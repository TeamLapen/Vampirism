package de.teamlapen.vampirism.common.world.entity.dracula;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import de.teamlapen.faction.api.world.entities.IEntityLeader;
import de.teamlapen.faction.common.world.entities.IEntityEventReceiver;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.entity.ai.memory.HurtByEntities;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaAiSystem;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Dracula extends PathfinderMob implements IDraculaAnimations, IEntityLeader, IEntityEventReceiver {

    public static final EntityDataAccessor<DraculaState> FIGHT_STAGE = SynchedEntityData.defineId(Dracula.class, ModEntities.DRACULA_STATE.get());
    public static final EntityDataAccessor<Long> TRANSFORMATION_START = SynchedEntityData.defineId(Dracula.class, EntityDataSerializers.LONG);
    /** Entity ids of the current blood siphon victims; empty while not channeling. Used client side to render the siphon beams. */
    public static final EntityDataAccessor<List<Integer>> SIPHON_TARGETS = SynchedEntityData.defineId(Dracula.class, ModEntities.INT_LIST.get());

    private final List<Pair<Long, Float>> recentDamage = new ArrayList<>();
    private long mistStartTime = -1;
    private long mistCooldownUntil = -1;
    /** Duration of mist form in ticks. Public so the client renderer can compute a fade-out envelope. */
    public static final int MIST_DURATION = 5 * 20;
    private static final int MIST_COOLDOWN = 30 * 20;
    /** Amount of extra max health per additional player in the fight */
    private static final float HEALTH_SCALE_PER_PLAYER = 0.5f;
    private int fightScalePlayers = 1;

    public Dracula(EntityType<? extends Dracula> type, Level level) {
        super(type, level);
    }

    /**
     * Starts the boss fight. Dracula enters the passive stage and scales with the players present in the dimension.
     */
    public void startFight(int playerCount) {
        this.fightScalePlayers = Math.max(1, playerCount);
        this.setState(DraculaState.PASSIVE);
        this.updateAttributes(this.getStage());
        this.setHealth(this.getMaxHealth());
    }

    public float healthScale() {
        return 1.0f + HEALTH_SCALE_PER_PLAYER * (this.fightScalePlayers - 1);
    }

    public int getFightScalePlayers() {
        return this.fightScalePlayers;
    }

    /** Debug command hook: jumps directly to the given fight state, skipping the transformation. */
    public void debugSetState(DraculaState state) {
        this.setState(state);
        this.setTransformationStart(-1);
        this.updateAttributes(this.getStage());
        this.setHealth(this.getMaxHealth());
        updateEvent();
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DRACULA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.DRACULA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DRACULA_DEATH.get();
    }

    public FightStage getStage() {
        return getState().stage;
    }

    @Override
    public void tick() {
        super.tick();
        tickTransformation();
        if (this.getState() == DraculaState.MIST) {
            tickMistForm();
        }
    }

    private void tickMistForm() {
        if (this.level().isClientSide()) return;
        if (this.mistStartTime == -1) this.mistStartTime = this.level().getGameTime();
        if (this.level().getGameTime() - this.mistStartTime > MIST_DURATION) {
            this.setState(DraculaState.RAGED);
            this.mistStartTime = -1;
            return;
        }

    }

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable() || this.isTransforming() || this.getState() == DraculaState.MIST || this.hasActiveInvulnerableAction();
    }

    /** True while Dracula is channeling a phase 2 flying needle/sword attack, during which he should not be able to be hit. */
    private boolean hasActiveInvulnerableAction() {
        Brain<Dracula> brain = this.getBrain();
        return brain.hasMemoryValue(ModMemoryTypes.FLYING_NEEDLE_ACTIVE.get()) || brain.hasMemoryValue(ModMemoryTypes.FLYING_SWORD_ACTIVE.get());
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource damageSource) {
        if (super.isInvulnerableTo(level, damageSource)) return true;
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        DraculaState state = this.getState();
        if (state.isTransforming || state == DraculaState.MIST || this.hasActiveInvulnerableAction()) return true;
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && (state == DraculaState.PASSIVE || state == DraculaState.DEFAULT)) {
            if (damageSource.getDirectEntity() instanceof AbstractArrow arrow) {
                arrow.setDeltaMovement(arrow.getDeltaMovement().scale(-1.0));
                arrow.setYRot(arrow.getYRot() + 180.0f);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();

        if (this.level() instanceof ServerLevel level){
            if (this.deathTime % 5 == 0 && level.getGameRules().get(GameRules.MOB_DROPS)) {
                int award = net.neoforged.neoforge.event.EventHooks.getExperienceDrop(this, null, Mth.floor((float) 1000 * 0.08F));
                ExperienceOrb.award((ServerLevel) this.level(), this.position(), award);
            }
        }
    }

    protected void updateEvent() {
        DraculaFightData.getOpt(this.level()).ifPresent(x -> x.getEvent().update(this));
    }

    protected void addPlayerToEvent(ServerPlayer player) {
        DraculaFightData.getOpt(this.level()).ifPresent(x -> x.getEvent().addPlayer(player));
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    //<editor-fold desc="Data">

    private boolean isTransforming() {
        return this.getState().isTransforming;
    }

    public long getTransformationStart() {
        return this.entityData.get(TRANSFORMATION_START);
    }

    private void setTransformationStart(long transformationStart) {
        this.entityData.set(TRANSFORMATION_START, transformationStart);
    }

    public DraculaState getState() {
        return this.entityData.get(FIGHT_STAGE);
    }
    private void setState(DraculaState stage) {
        this.entityData.set(FIGHT_STAGE, stage);
    }

    public List<Integer> getSiphonTargets() {
        return this.entityData.get(SIPHON_TARGETS);
    }

    public void setSiphonTargets(List<Integer> targets) {
        this.entityData.set(SIPHON_TARGETS, List.copyOf(targets));
    }

    public void clearSiphonTargets() {
        if (!this.getSiphonTargets().isEmpty()) {
            this.entityData.set(SIPHON_TARGETS, List.of());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FIGHT_STAGE, DraculaState.DEFAULT);
        builder.define(TRANSFORMATION_START, -1L);
        builder.define(SIPHON_TARGETS, List.of());
    }

    //</editor-fold>

    //<editor-fold desc="Attribute Values">

    private static double createMaxHealth(FightStage stage) {
        return switch (stage) {
            case PHASE_2 -> 100;
            case PHASE_3 -> 200;
            default -> 50;
        };
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static double createMovementSpeed(FightStage stage) {
        return switch (stage) {
            case PHASE_3 -> 0.75d;
            default -> 0.7d;
        };
    }

    private static double createKnockbackResistance(FightStage stage) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (stage) {
            default-> 1d;
        };
    }

    private static double createAttackKnockback(FightStage stage) {
        return switch (stage) {
            case PHASE_2 -> 0.5d;
            case PHASE_3 -> 2d;
            default -> 0;
        };
    }

    private static double createAttackDamage(FightStage stage) {
        return switch (stage) {
            case NONE, PHASE_1 -> 10d;
            case PHASE_2 -> 15d;
            case PHASE_3 -> 20d;
        };
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static double createExplosionKnockbackResistance(FightStage stage) {
        return switch (stage) {
            case PHASE_3 -> 0.2d;
            default -> 1;
        };
    }

    @SuppressWarnings("SameReturnValue")
    private static double createArmor(FightStage stage) {
        return switch (stage) {
            case PHASE_2 -> 40;
            case PHASE_3 -> 40;
            default -> 40;
        };
    }

    private static double createArmorToughness(FightStage stage) {
        return switch (stage) {
            case PHASE_2 -> 15;
            case PHASE_3 -> 6;
            default -> 15;
        };
    }

    @SuppressWarnings("DataFlowIssue")
    private void updateAttributes(FightStage fightStage) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(createMaxHealth(fightStage) * healthScale());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(createAttackDamage(fightStage));
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(createAttackKnockback(fightStage));
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(createKnockbackResistance(fightStage));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(createMovementSpeed(fightStage));
        this.getAttribute(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE).setBaseValue(createExplosionKnockbackResistance(fightStage));
        this.getAttribute(Attributes.ARMOR).setBaseValue(createArmor(fightStage));
        this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(createArmorToughness(fightStage));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, createMaxHealth(FightStage.NONE))
                .add(Attributes.ATTACK_DAMAGE, createAttackDamage(FightStage.NONE))
                .add(Attributes.ATTACK_KNOCKBACK, createAttackKnockback(FightStage.NONE))
                .add(Attributes.KNOCKBACK_RESISTANCE, createKnockbackResistance(FightStage.NONE))
                .add(Attributes.MOVEMENT_SPEED, createMovementSpeed(FightStage.NONE))
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, createKnockbackResistance(FightStage.NONE))
                .add(Attributes.BURNING_TIME, 0.1f)
                .add(Attributes.ARMOR, createArmor(FightStage.NONE))
                .add(Attributes.ARMOR_TOUGHNESS, createArmorToughness(FightStage.NONE))
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    //</editor-fold>

    //<editor-fold desc="Animation">

    private final AnimationState attackAnimationState = new AnimationState();
    private final AnimationState transformationAnimationState = new AnimationState();
    private final AnimationState mistAnimationState = new AnimationState();
    private IDraculaAnimations.Animation attackAnimationType = IDraculaAnimations.Animation.NONE;

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (FIGHT_STAGE.equals(key)) {
            if (this.getState().isTransforming) {
                this.transformationAnimationState.start(this.tickCount);
            } else {
                this.transformationAnimationState.stop();
            }
            if (this.getState() == DraculaState.MIST) {
                this.mistAnimationState.start(this.tickCount);
            } else {
                this.mistAnimationState.stop();
            }
        }
    }

    public void copyTransformationAnimationTo(AnimationState state) {
        state.copyFrom(this.transformationAnimationState);
    }

    /** Client-observed mist-form start, used by the renderer to drive the volumetric mist's fade in/out envelope. */
    public void copyMistAnimationTo(AnimationState state) {
        state.copyFrom(this.mistAnimationState);
    }

    public void triggerAnim(Animation... animations) {
        if (animations.length == 0) {
            return;
        }

        this.attackAnimationType = animations[this.random.nextInt(animations.length)];
        this.attackAnimationState.start(this.tickCount);
        sendEvent(this.attackAnimationType.animationId());
    }

    public void copyAttackAnimationTo(AnimationState state) {
        state.copyFrom(this.attackAnimationState);
    }

    public IDraculaAnimations.Animation getAttackAnimationType() {
        return attackAnimationType;
    }

    @Override
    public void onEvent(Identifier event) {
        Animation animation = Animation.BY_ID.get(event);
        if (animation != null) {
            this.attackAnimationType = animation;
            this.attackAnimationState.start(this.tickCount);
        }
    }

    //</editor-fold>


    //<editor-fold desc="Serialization">

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setState(input.read("fight_stage", DraculaState.CODEC).orElse(DraculaState.DEFAULT));
        setTransformationStart(input.getLongOr("transformation_start", -1));
        this.fightScalePlayers = Math.max(1, input.getIntOr("fight_scale_players", 1));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("fight_stage", DraculaState.CODEC, getState());
        if (getTransformationStart() != -1) {
            output.putLong("transformation_start", getTransformationStart());
        }
        output.putInt("fight_scale_players", this.fightScalePlayers);
    }

    //</editor-fold>

    //<editor-fold desc="Brain">

    @SuppressWarnings("unchecked")
    @Override
    public Brain<Dracula> getBrain() {
        return (Brain<Dracula>) super.getBrain();
    }

    @Override
    protected Brain<? extends LivingEntity> makeBrain(Brain.Packed packedBrain) {
        return DraculaAiSystem.AI.brainProvider().makeBrain(this, packedBrain);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        if (!this.isTransforming()) {
            DraculaAiSystem.AI.tick(level, this);
        }
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        if (!this.isTransforming()) {
            super.aiStep();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Transformation">

    protected void tickTransformation() {
        if (!this.isTransforming() || !(this.level() instanceof ServerLevel serverLevel)) return;


        var percentage = ((serverLevel.getGameTime() - getTransformationStart()) / (float) getState().transformTime);


        setHealth(Math.max(1, getMaxHealth() * percentage));

        if (serverLevel.getGameTime() % 4 == 0) {
            serverLevel.sendParticles(new DustColorTransitionOptions(0x750014, 0x46011a, 1.5f), this.getX(), this.getY() + this.random.nextDouble() * this.getBbHeight(), this.getZ(), 4, 0.6, 0.4, 0.6, 0.02);
        }

        if (percentage >= 1) {
            finishTransformation();
        }
        updateEvent();
    }

    private void finishTransformation() {
        var stage = switch (this.getStage()) {
            case PHASE_2 -> DraculaState.RANGED;
            case PHASE_3 -> DraculaState.RAGED;
            default -> throw new IllegalStateException("Unexpected value: " + this.getStage());
        };
        this.setState(stage);
        this.setTransformationStart(-1);

        this.level().playSound(null, this.blockPosition(), ModSounds.DRACULA_TRANSFORM_END.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1.0, this.getZ(), 4, 0.5, 0.5, 0.5, 0);
        }
        knockbackEntities();
    }

    private void knockbackEntities() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10.0)).forEach(entity -> {
                if (entity != this) {
                    entity.knockback(1.0, entity.getX() - this.getX(), entity.getZ() - this.getZ());
                }
            });
        }
    }

    private void startTransformation() {
        var nextStage = switch (this.getStage()) {
            case PHASE_1 -> DraculaState.TRANSFORMING_TO_RANGED;
            case PHASE_2 -> DraculaState.TRANSFORMING_TO_RAGED;
            default -> throw new IllegalStateException("Unexpected value: " + this.getStage());
        };

        teleportToTransformationMarker();
        this.setTransformationStart(this.level().getGameTime());
        this.setState(nextStage);
        updateAttributes(getStage());
        if (level() instanceof ServerLevel serverLevel) {
            DraculaAiSystem.AI.stop(serverLevel, this);
        }
        this.level().playSound(null, this.blockPosition(), ModSounds.DRACULA_TRANSFORM.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
        DraculaFightData.getOpt(this.level()).ifPresent(data -> data.sendMessage(Component.translatable("message.vampirism.dracula.transformation")));
        updateEvent();
    }

    /**
     * Teleports to a random spawn marker so the transformation happens at a set piece location.
     * Falls back to transforming in place if no (other) marker exists.
     */
    private void teleportToTransformationMarker() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        List<Marker> markers = DraculaFightData.findMarkers(serverLevel, DraculaFightData.DRACULA_SPAWN_MARKER);
        markers.removeIf(marker -> marker.distanceToSqr(this) < 16);
        if (markers.isEmpty()) return;
        Marker target = markers.get(this.random.nextInt(markers.size()));

        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.5, 1.0, 0.5, 0.02);
        serverLevel.playSound(null, this.blockPosition(), ModSounds.DRACULA_TELEPORT.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
        this.getNavigation().stop();
        this.teleportTo(target.getX(), target.getY(), target.getZ());
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY() + 1.0, target.getZ(), 30, 0.5, 1.0, 0.5, 0.02);
        serverLevel.playSound(null, target.blockPosition(), ModSounds.DRACULA_TELEPORT.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
    }

    @Override
    public void setHealth(float health) {
        if (this.getState() != DraculaState.RAGED && this.damageContainers != null && !this.damageContainers.empty()) {
            DamageContainer peek = this.damageContainers.peek();
            if (!peek.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && health <= 0) {
                health = 1;
                startTransformation();
            }
        }

        super.setHealth(health);
    }

    @Override
    public void heal(float healAmount) {
        super.heal(healAmount);
        updateEvent();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float p_376610_) {
        if(super.hurtServer(level, source, p_376610_)) {
            HurtByEntities hurtByEntities = getBrain().getMemory(ModMemoryTypes.HURT_BY_ENTITIES.get()).orElseGet(HurtByEntities::empty);
            getBrain().setMemory(ModMemoryTypes.HURT_BY_ENTITIES.get(), hurtByEntities.hurtBy(level,this, source));
            return true;
        }
        return false;
    }

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource damageSource, float amount) {
        DraculaState state = this.getState();
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
            if (state == DraculaState.RANGED) {
                amount *= 0.5f;
            } else if (state == DraculaState.RAGED) {
                if (damageSource.getDirectEntity() instanceof AbstractArrow) {
                    amount *= 1.5f;
                }
            }
        }
        super.actuallyHurt(level, damageSource, amount);
        if (damageSource.getEntity() instanceof ServerPlayer player) {
            addPlayerToEvent(player);
        }
        if (getState() == DraculaState.DEFAULT) {
            setState(DraculaState.PASSIVE);
        }

        if (this.getState() == DraculaState.PASSIVE && damageSource.getEntity() instanceof LivingEntity attacker) {
            double d0 = attacker.getX() - this.getX();
            double d1 = attacker.getZ() - this.getZ();
            attacker.knockback(0.5D, d0, d1);
        }

        if (this.getState() == DraculaState.RAGED) {
            this.recentDamage.add(Pair.of(level.getGameTime(), amount));
            this.checkMistFormTrigger();
        }
        updateEvent();
    }

    private void checkMistFormTrigger() {
        long gameTime = this.level().getGameTime();
        if (gameTime < this.mistCooldownUntil) return;
        this.recentDamage.removeIf(p -> gameTime - p.getFirst() > 100);
        float total = 0;
        for (Pair<Long, Float> p : this.recentDamage) {
            total += p.getSecond();
        }
        if (total > this.getMaxHealth() * 0.2f) {
            this.setState(DraculaState.MIST);
            this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            this.mistStartTime = gameTime;
            this.mistCooldownUntil = gameTime + MIST_COOLDOWN;
            this.recentDamage.clear();
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        DraculaFightData.getOpt(level()).ifPresent  (x -> x.draculaDied(this));
    }

    //</editor-fold>

    //<editor-fold desc="Entity Leader">

    @Override
    public void decreaseFollowerCount() {
    }

    @Override
    public int getFollowingCount() {
        return 0;
    }

    @Override
    public int getMaxFollowerCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean increaseFollowerCount() {
        return true;
    }

    @Override
    public LivingEntity asEntity() {
        return this;
    }


    //</editor-fold>
}
