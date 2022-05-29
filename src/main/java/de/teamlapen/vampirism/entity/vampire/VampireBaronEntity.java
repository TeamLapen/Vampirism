package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.vampire.IVampireBaron;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.AttackRangedDarkBloodGoal;
import de.teamlapen.vampirism.entity.goals.FleeGarlicVampireGoal;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public class VampireBaronEntity extends VampireBaseEntity implements IVampireBaron {
    public static final int MAX_LEVEL = 4;
    private final static Logger LOGGER = LogManager.getLogger(VampireBaronEntity.class);
    private static final DataParameter<Integer> LEVEL = EntityDataManager.defineId(VampireBaronEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> ENRAGED = EntityDataManager.defineId(VampireBaronEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LADY = EntityDataManager.defineId(VampireBaronEntity.class, DataSerializers.BOOLEAN);
    private final static int ENRAGED_TRANSITION_TIME = 15;

    public static boolean spawnPredicateBaron(EntityType<? extends VampireBaronEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return (ModBiomes.VAMPIRE_FOREST.get().getRegistryName().equals(Helper.getBiomeId(world, blockPos)) || ModBiomes.VAMPIRE_FOREST_HILLS.get().getRegistryName().equals(Helper.getBiomeId(world, blockPos))) && world.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
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

    public VampireBaronEntity(EntityType<? extends VampireBaronEntity> type, World world) {
        super(type, world, true);
        this.garlicResist = EnumStrength.MEDIUM;
        this.hasArms = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("level", getLevel());
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
            if (getLevel() > 3 && this.random.nextInt(9) == 0) {
                this.addEffect(new EffectInstance(Effects.INVISIBILITY, 60));
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
    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReasonIn) {
        int i = MathHelper.floor(this.getBoundingBox().minY);
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
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag && entity instanceof LivingEntity) {
            float tm = 1f;
            int mr = 1;
            if (entity instanceof PlayerEntity) {
                float pld = (this.getLevel() + 1) - VampirismPlayerAttributes.get((PlayerEntity) entity).vampireLevel / 3f;
                tm = pld + 1;
                mr = pld < 1.5f ? 1 : (pld < 3 ? 2 : 3);
                if (VampirismPlayerAttributes.get((PlayerEntity) entity).getHuntSpecial().fullHunterCoat != null) {
                    tm *= 0.5F;
                }
            }
            if (entity instanceof VampireBaronEntity) {
                ((LivingEntity) entity).addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 40, 5));
            }
            ((LivingEntity) entity).addEffect(new EffectInstance(Effects.WEAKNESS, (int) (200 * tm), random.nextInt(mr)));
            ((LivingEntity) entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, (int) (100 * tm), random.nextInt(mr)));
            attackDecisionCounter = 0;
        }
        return flag;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
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
    public int getLevel() {
        return getEntityData().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getEntityData().set(LEVEL, level);
            this.updateEntityAttributes(false);
            float hp = this.getHealth() / this.getMaxHealth();
            this.setHealth(this.getMaxHealth() * hp);
            this.setCustomName(getTypeName().plainCopy().append(new TranslationTextComponent("entity.vampirism.vampire_baron.level", level + 1)));
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
        return (int) (BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER * this.getLevel() / (float) this.getMaxLevel() * 2f);
    }

    @Override
    public int getMaxHeadYRot() {
        return 5; //Don't move the head too far
    }

    @Override
    public int getMaxLevel() {
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
    public boolean hurt(DamageSource damageSource, float amount) {
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
    public void killed(ServerWorld world, LivingEntity entity) {
        super.killed(world, entity);
        if (entity instanceof VampireBaronEntity) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        setLevel(MathHelper.clamp(nbt.getInt("level"), 0, MAX_LEVEL));
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
    public int suggestLevel(Difficulty d) {
        int avg = Math.round(((d.avgPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int max = Math.round(((d.maxPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int min = Math.round(((d.minPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * (MAX_LEVEL));

        switch (random.nextInt(7)) {
            case 0:
                return min;
            case 1:
                return max + 1;
            case 2:
                return avg;
            case 3:
                return avg + 1;
            case 4:
            case 5:
                return random.nextInt(MAX_LEVEL + 1);
            default:
                return random.nextInt(max + 2 - min) + min;
        }
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
    protected int getExperienceReward(PlayerEntity player) {
        return 20 + 5 * getLevel();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new FleeGarlicVampireGoal(this, 0.9F, false));
        this.goalSelector.addGoal(5, new BaronAIAttackMelee(this, 1.0F));
        this.goalSelector.addGoal(6, new BaronAIAttackRanged(this, 60, 64, 6, 4));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, PlayerEntity.class, 6.0F, 0.6, 0.7F, input -> input != null && !isLowerLevel(input)));//Works only partially. Pathfinding somehow does not find escape routes
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 0.2));
        this.goalSelector.addGoal(9, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, input -> input != null && isLowerLevel(input)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, VampireBaronEntity.class, true, false));
    }

    protected void updateEntityAttributes(boolean aggressive) {
        if (aggressive) {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(
                    BalanceMobProps.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow((BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL - 1) / 5 + 1, (getLevel())));
        } else {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(5D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(
                    BalanceMobProps.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()) / 3);
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_BARON_MAX_HEALTH * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
        this.getAttribute(Attributes.ATTACK_DAMAGE)
                .setBaseValue(BalanceMobProps.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
    }

    private boolean isLowerLevel(LivingEntity player) {
        if (player instanceof PlayerEntity) {
            int playerLevel = FactionPlayerHandler.getOpt((PlayerEntity) player).map(FactionPlayerHandler::getCurrentLevel).orElse(0);
            return (playerLevel - 8) / 2F - VampireBaronEntity.this.getLevel() <= 0;
        }
        return false;
    }

    private class BaronAIAttackMelee extends MeleeAttackGoal {

        BaronAIAttackMelee(CreatureEntity creature, double speedIn) {
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
