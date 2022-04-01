package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.vampire.IVampireBaron;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.AttackRangedDarkBloodGoal;
import de.teamlapen.vampirism.entity.goals.FleeGarlicVampireGoal;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public class VampireBaronEntity extends VampireBaseEntity implements IVampireBaron {
    public static final int MAX_LEVEL = 4;
    private final static Logger LOGGER = LogManager.getLogger(VampireBaronEntity.class);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(VampireBaronEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENRAGED = SynchedEntityData.defineId(VampireBaronEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LADY = SynchedEntityData.defineId(VampireBaronEntity.class, EntityDataSerializers.BOOLEAN);
    private final static int ENRAGED_TRANSITION_TIME = 15;

    public static boolean spawnPredicateBaron(EntityType<? extends VampireBaronEntity> entityType, LevelAccessor world, MobSpawnType spawnReason, BlockPos blockPos, Random random) {
        return world.getBiome(blockPos).is(ModTags.Biomes.IS_VAMPIRE_BIOME) && world.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }

    public static AttributeSupplier.Builder getAttributeBuilder() {
        return VampireBaseEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.VAMPIRE_BARON_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, 5);
    }

    /**
     * Used for ranged vs melee attack decision
     */
    private int attackDecisionCounter = 0;
    /**
     * Whether to prefer ranged attack
     */
    private boolean rangedAttack = false;
    private boolean prevAttacking = false;
    /**
     * Store the approximate count of entities that are following this advanced vampire.
     * Not guaranteed to be exact and not saved to nbt
     */
    private int followingEntities = 0;
    private int enragedTransitionTime = 0;

    public VampireBaronEntity(EntityType<? extends VampireBaronEntity> type, Level world) {
        super(type, world, true);
        this.garlicResist = EnumStrength.MEDIUM;
        this.hasArms = true;
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("level", getEntityLevel());
        nbt.putBoolean("lady", isLady());
    }

    @Override
    public void aiStep() {
        if (!prevAttacking && this.getTarget() != null) {
            prevAttacking = true;
            updateEntityAttributes(true);
        }
        if (prevAttacking && this.getTarget() == null) {
            prevAttacking = false;
            this.rangedAttack = false;
            this.attackDecisionCounter = 0;
            updateEntityAttributes(false);
        }

        if (!this.level.isClientSide && this.isGettingSundamage(level)) {
            this.teleportAway();

        }
        if (!this.level.isClientSide && this.getTarget() != null && this.tickCount % 128 == 0) {
            if (rangedAttack) {
                if (this.random.nextInt(2) == 0 && this.navigation.createPath(this.getTarget(), 0) != null) {
                    rangedAttack = false;
                }
            } else {
                if (attackDecisionCounter > 4 || this.random.nextInt(6) == 0) {
                    rangedAttack = true;
                    attackDecisionCounter = 0;
                }
            }
            if (getEntityLevel() > 3 && this.random.nextInt(9) == 0) {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60));
            }
        }
        if (this.level.isClientSide()) {
            if (isEnraged() && enragedTransitionTime < ENRAGED_TRANSITION_TIME) {
                enragedTransitionTime++;
            } else if (!isEnraged() && enragedTransitionTime > 0) {
                enragedTransitionTime--;
            }
        }
        super.aiStep();
    }

    @Override
    public boolean checkSpawnRules(@Nonnull LevelAccessor worldIn, @Nonnull MobSpawnType spawnReasonIn) {
        int i = Mth.floor(this.getBoundingBox().minY);
        //Only spawn on the surface
        if (i < 60) return false;
//        CastlePositionData data = CastlePositionData.get(world);
//        if (data.isPosAt(MathHelper.floor_double(posX), MathHelper.floor_double(posZ))) {
//            return false;
//        }
        BlockPos blockpos = new BlockPos(this.getX(), this.getBoundingBox().minY, this.getZ());
        return worldIn.getBlockState(blockpos.below()).is(ModTags.Blocks.CURSEDEARTH) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    public void decreaseFollowerCount() {
        followingEntities = Math.max(0, followingEntities - 1);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag && entity instanceof LivingEntity) {
            float tm = 1f;
            int mr = 1;
            if (entity instanceof Player) {
                float pld = (this.getEntityLevel() + 1) - VampirismPlayerAttributes.get((Player) entity).vampireLevel / 3f;
                tm = pld + 1;
                mr = pld < 1.5f ? 1 : (pld < 3 ? 2 : 3);
                if (VampirismPlayerAttributes.get((Player) entity).getHuntSpecial().fullHunterCoat != null) {
                    tm *= 0.5F;
                }
            }
            if (entity instanceof VampireBaronEntity) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 5));
            }
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int) (200 * tm), random.nextInt(mr)));
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (100 * tm), random.nextInt(mr)));
            attackDecisionCounter = 0;
        }
        return flag;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.getEntityData().set(LADY, this.getRandom().nextBoolean());
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * @return float between 0 and 1 representing the transition progress
     */
    @OnlyIn(Dist.CLIENT)
    public float getEnragedProgress() {
        return enragedTransitionTime / (float) ENRAGED_TRANSITION_TIME;
    }

    @Override
    public int getFollowingCount() {
        return followingEntities;
    }

    @Override
    public int getEntityLevel() {
        return getEntityData().get(LEVEL);
    }

    @Override
    public void setEntityLevel(int level) {
        if (level >= 0) {
            getEntityData().set(LEVEL, level);
            this.updateEntityAttributes(false);
            float hp = this.getHealth() / this.getMaxHealth();
            this.setHealth(this.getMaxHealth() * hp);
            this.setCustomName(getTypeName().plainCopy().append(new TranslatableComponent("entity.vampirism.vampire_baron.level", level + 1)));
        } else {
            this.setCustomName(null);
        }
    }

    @Override
    public int getMaxHeadXRot() {
        return 5; //Turn around slowly
    }

    @Override
    public int getMaxFollowerCount() {
        return (int) (BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER * this.getEntityLevel() / (float) this.getMaxEntityLevel() * 2f);
    }

    @Override
    public int getMaxHeadYRot() {
        return 5; //Don't move the head too far
    }

    @Override
    public int getMaxEntityLevel() {
        return MAX_LEVEL;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public int getPortalWaitTime() {
        return 500;
    }

    @Override
    public boolean increaseFollowerCount() {
        if (followingEntities < getMaxFollowerCount()) {
            followingEntities++;
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float amount) {
        attackDecisionCounter++;
        return super.hurt(damageSource, amount);
    }

    public boolean isEnraged() {
        return getEntityData().get(ENRAGED);
    }

    public boolean isLady() {
        return getEntityData().get(LADY);
    }

    public void setLady(boolean lady) {
        getEntityData().set(LADY, lady);
    }

    @Override
    public void killed(@Nonnull ServerLevel world, @Nonnull LivingEntity entity) {
        super.killed(world, entity);
        if (entity instanceof VampireBaronEntity) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setEntityLevel(Mth.clamp(nbt.getInt("level"), 0, MAX_LEVEL));
        this.getEntityData().set(LADY, nbt.getBoolean("lady"));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        this.getEntityData().set(ENRAGED, target != null);
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public int suggestEntityLevel(Difficulty d) {
        int avg = Math.round(((d.avgPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int max = Math.round(((d.maxPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int min = Math.round(((d.minPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * (MAX_LEVEL));

        return switch (random.nextInt(7)) {
            case 0 -> min;
            case 1 -> max + 1;
            case 2 -> avg;
            case 3 -> avg + 1;
            case 4, 5 -> random.nextInt(MAX_LEVEL + 1);
            default -> random.nextInt(max + 2 - min) + min;
        };
    }

    @Override
    protected float calculateFireDamage(float amount) {
        return (float) (amount * BalanceMobProps.mobProps.VAMPIRE_BARON_FIRE_VULNERABILITY);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(LEVEL, -1);
        getEntityData().define(ENRAGED, false);
        getEntityData().define(LADY, false);
    }

    @Override
    protected int getExperienceReward(@Nonnull Player player) {
        return 20 + 5 * getEntityLevel();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new FleeGarlicVampireGoal(this, 0.9F, false));
        this.goalSelector.addGoal(5, new BaronAIAttackMelee(this, 1.0F));
        this.goalSelector.addGoal(6, new BaronAIAttackRanged(this, 60, 64, 6, 4));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Player.class, 6.0F, 0.6, 0.7F, input -> input != null && !isLowerLevel(input)));//Works only partially. Pathfinding somehow does not find escape routes
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.2));
        this.goalSelector.addGoal(9, new LookAtClosestVisibleGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isLowerLevel));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, VampireBaronEntity.class, true, false));
    }

    protected void updateEntityAttributes(boolean aggressive) {
        if (aggressive) {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(
                    BalanceMobProps.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow((BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL - 1) / 5 + 1, (getEntityLevel())));
        } else {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(5D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(
                    BalanceMobProps.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getEntityLevel()) / 3);
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_BARON_MAX_HEALTH * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getEntityLevel()));
        this.getAttribute(Attributes.ATTACK_DAMAGE)
                .setBaseValue(BalanceMobProps.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getEntityLevel()));
    }

    private boolean isLowerLevel(LivingEntity player) {
        if (player instanceof Player) {
            int playerLevel = FactionPlayerHandler.getOpt((Player) player).map(FactionPlayerHandler::getCurrentLevel).orElse(0);
            return (playerLevel - 8) / 2F - VampireBaronEntity.this.getEntityLevel() <= 0;
        }
        return false;
    }

    private class BaronAIAttackMelee extends MeleeAttackGoal {

        BaronAIAttackMelee(PathfinderMob creature, double speedIn) {
            super(creature, speedIn, false);
        }

        @Override
        public boolean canContinueToUse() {
            return !VampireBaronEntity.this.rangedAttack && super.canContinueToUse();
        }

        @Override
        public boolean canUse() {
            return !VampireBaronEntity.this.rangedAttack && super.canUse();
        }
    }

    private class BaronAIAttackRanged extends AttackRangedDarkBloodGoal {

        BaronAIAttackRanged(VampireBaronEntity entity, int cooldown, int maxDistance, float damage, float indirectDamage) {
            super(entity, cooldown, maxDistance, damage, indirectDamage);
        }

        @Override
        public boolean canUse() {
            return VampireBaronEntity.this.getTarget() != null && (VampireBaronEntity.this.rangedAttack || !VampireBaronEntity.this.isPathFinding());
        }
    }
}
