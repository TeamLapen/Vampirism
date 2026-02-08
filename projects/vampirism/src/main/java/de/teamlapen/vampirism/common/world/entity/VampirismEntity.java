package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.world.entities.IEntityWithHome;
import de.teamlapen.faction.api.world.entities.extensions.ILivingEntity;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.util.SpawnUtil;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.IVampirismEntity;
import de.teamlapen.vampirism.common.core.ModParticles;
import de.teamlapen.vampirism.common.particles.GenericParticleOptions;
import de.teamlapen.vampirism.common.tags.ModBiomeTags;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for most vampirism mobs
 */
public abstract class VampirismEntity extends PathfinderMob implements IEntityWithHome, IVampirismEntity, ILivingEntity {

    public static boolean spawnPredicateVampireFog(@NotNull LevelAccessor world, @NotNull BlockPos blockPos) {
        return world.getBiome(blockPos).is(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME) || (world instanceof Level && LevelFog.get((Level) world).isInsideArtificialVampireFogArea(blockPos));
    }

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return PathfinderMob.createLivingAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.ATTACK_KNOCKBACK);
    }

    private final @NotNull Goal moveTowardsRestriction;
    protected boolean hasArms = true;
    protected boolean peaceful = false;
    /**
     * Whether the home should be saved to nbt or not
     */
    protected boolean saveHome = false;
    @Nullable
    private AABB home;
    private boolean moveTowardsRestrictionAdded = false;
    private int moveTowardsRestrictionPrio = -1;
    /**
     * Counter which reaches zero every 70 to 120 ticks
     */
    private int randomTickDivider;
    private boolean doImobConversion = false;

    public VampirismEntity(@NotNull EntityType<? extends VampirismEntity> type, @NotNull Level world) {
        super(type, world);
        moveTowardsRestriction = new MoveTowardsRestrictionGoal(this, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (saveHome && home != null) {
            int[] h = {(int) home.minX, (int) home.minY, (int) home.minZ, (int) home.maxX, (int) home.maxY, (int) home.maxZ};
            output.putIntArray("home", h);
            if (moveTowardsRestrictionAdded && moveTowardsRestrictionPrio > -1) {
                output.putInt("homeMovePrio", moveTowardsRestrictionPrio);
            }
        }
    }

    @Nullable
    @Override
    public AABB getHome() {
        return home;
    }

    @Override
    public void aiStep() {
        if (hasArms) {
            this.updateSwingTime();
        }
        super.aiStep();
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor worldIn, @NotNull EntitySpawnReason spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    public boolean isWithinHomeDistance(double x, double y, double z) {
        if (home != null) {
            return home.contains(new Vec3(x, y, z));
        }
        return true;
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        input.getIntArray("home").ifPresent(h -> {
            this.setHome(new AABB(h[0], h[1], h[2], h[3], h[4], h[5]));
            input.getInt("homeMovePrio").ifPresent(x -> this.setMoveTowardsRestriction(x, true));
        });
    }

    @Override
    public void setHome(@Nullable AABB home) {
        this.home = home;
        if (home != null) {
            int posX, posY, posZ;
            posX = (int) (home.minX + (home.maxX - home.minX) / 2);
            posY = (int) (home.minY + (home.maxY - home.minY) / 2);
            posZ = (int) (home.minZ + (home.maxZ - home.minZ) / 2);
            super.setHomeTo(new BlockPos(posX, posY, posZ), (int) home.getSize());
        } else {
            super.setHomeTo(new BlockPos(0, 0, 0), -1);
        }
    }

    @Override
    public void setHomeArea(@NotNull BlockPos pos, int r) {
        this.setHome(new AABB(Vec3.atLowerCornerOf(pos.offset(-r, -r, -r)), Vec3.atLowerCornerWithOffset(pos.offset(r, r, r), 1, 1, 1)));
    }

    @Override
    public void tick() {
        super.tick();
        this.checkImobConversion();
        if (!this.level().isClientSide() && !peaceful && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (--this.randomTickDivider <= 0) {
            this.randomTickDivider = 70 + random.nextInt(50);
            onRandomTick();
        }
    }

    protected void disableImobConversion() {
        this.doImobConversion = false;
    }

    /**
     * Removes the MoveTowardsRestriction task
     */
    protected void disableMoveTowardsRestriction() {
        if (moveTowardsRestrictionAdded) {
            this.goalSelector.removeGoal(moveTowardsRestriction);
            moveTowardsRestrictionAdded = false;
        }
    }

    protected void enableImobConversion() {
        if (this instanceof IFactionEntity) {
            this.doImobConversion = true;
        } else {
            throw new IllegalStateException("Can only do IMob conversion for IFactionEntity");
        }
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.HOSTILE_DEATH;
    }

    @NotNull
    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.HOSTILE_SMALL_FALL, SoundEvents.HOSTILE_BIG_FALL);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.HOSTILE_HURT;
    }

    /**
     * @param iMob Whether we want the iMob or non iMob variant
     * @return Must be LivingEntity type
     */
    protected @NotNull EntityType<?> getIMobTypeOpt(boolean iMob) {
        return this.getType();
    }

    @NotNull
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @NotNull
    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    protected boolean isLowLightLevel(@NotNull LevelAccessor iWorld) {
        //copy of Monster#isDarkEnoughToSSpawn, but not requiring server level
        BlockPos blockpos = new BlockPos((int) this.getX(), (int) this.getBoundingBox().minY, (int) this.getZ());
        if (iWorld.getBrightness(LightLayer.SKY, blockpos) > this.random.nextInt(32)) {
            return false;
        } else if (iWorld.getBrightness(LightLayer.BLOCK, blockpos) > 0) {
            return false;
        } else {
            int i = iWorld.getMaxLocalRawBrightness(blockpos);

            if (iWorld instanceof Level && ((Level) iWorld).isThundering()) {
                i = iWorld.getMaxLocalRawBrightness(blockpos);
            }

            return i <= this.random.nextInt(8);
        }
    }

    @Override
    public @NotNull LivingEntity asEntity() {
        return this;
    }

    /**
     * Called every 70 to 120 ticks during {@link Mob#customServerAiStep(ServerLevel)}
     */
    @SuppressWarnings("EmptyMethod")
    protected void onRandomTick() {

    }

    protected void setDontDropEquipment() {
    }

    /**
     * Add the MoveTowardsRestriction task with the given priority.
     * Overrides prior priorities if existent
     * e
     *
     * @param prio   Priority of the task
     * @param active If the task should be active or not
     */
    protected void setMoveTowardsRestriction(int prio, boolean active) {
        if (moveTowardsRestrictionAdded) {
            if (active && moveTowardsRestrictionPrio == prio) {
                return;
            }
            this.goalSelector.removeGoal(moveTowardsRestriction);
            moveTowardsRestrictionAdded = false;
        }
        if (active) {
            goalSelector.addGoal(prio, moveTowardsRestriction);
            moveTowardsRestrictionAdded = true;
            moveTowardsRestrictionPrio = prio;
        }

    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }

    /**
     * Fakes a teleportation and actually just kills the entity
     */
    protected void teleportAway() {
        this.setInvisible(true);
        ModParticles.spawnParticlesServer(this.level(), new GenericParticleOptions(VIdentifier.mc("effect_6"), 10, 0x0A0A0A, 0.6F), this.getX(), this.getY(), this.getZ(), 20, 1, 1, 1, 0);
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
        this.discard();
    }

    private void checkImobConversion() {
        if (doImobConversion && !this.level().isClientSide()) {
            if (this.tickCount % 256 == 0 && this.isAlive()) {
                boolean current = this instanceof Enemy;
                boolean convert = false;
                de.teamlapen.faction.common.config.ServerConfig.IMobOptions opt = FactionConfig.server().entityIMob.get();
                if (ServerLifecycleHooks.getCurrentServer().isDedicatedServer()) {
                    convert = (opt == de.teamlapen.faction.common.config.ServerConfig.IMobOptions.ALWAYS_IMOB) != current;
                } else {
                    if (opt == de.teamlapen.faction.common.config.ServerConfig.IMobOptions.SMART) {
                        Player player = FactionsMod.proxy.getClientPlayer();
                        if (player != null && player.isAlive()) {
                            Holder<? extends IPlayableFaction<?>> f = FactionPlayerHandler.get(player).getFaction();
                            Holder<IFaction<?>> thisFaction = (Holder<IFaction<?>>) ((IFactionEntity) this).getFaction();

                            boolean hostile = IFaction.isNeutral(f) ? thisFaction.is(de.teamlapen.faction.api.tags.FactionTags.HOSTILE_TOWARDS_NEUTRAL) : !thisFaction.equals(f);
                            convert = hostile != current;

                        }
                    } else {
                        convert = (opt == de.teamlapen.faction.common.config.ServerConfig.IMobOptions.ALWAYS_IMOB) != current;
                    }
                }
                if (convert) {
                    EntityType<?> t = getIMobTypeOpt(!current);
                    SpawnUtil.createEntity(t, this.level(), EntitySpawnReason.CONVERSION).ifPresent(newEntity -> {
                        TagValueOutput withContext = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.registryAccess());
                        this.saveWithoutId(withContext);
                        newEntity.load(TagValueInput.create(ProblemReporter.DISCARDING, this.registryAccess(), withContext.buildResult()));
                        newEntity.setUUID(Mth.createInsecureUUID(this.random));
                        assert newEntity instanceof LivingEntity;
                        SpawnUtil.replaceEntity(this, (LivingEntity) newEntity);
                    });

                }
            }
        }
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }
}