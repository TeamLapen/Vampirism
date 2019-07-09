package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampireBaron;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMinion;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.ai.AttackRangedDarkBloodGoal;
import de.teamlapen.vampirism.entity.ai.FleeGarlicVampireGoal;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minions.SaveableMinionHandler;
import de.teamlapen.vampirism.entity.minions.vampire.VampireMinionSaveableEntity;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public class VampireBaronEntity extends VampireBaseEntity implements IVampireBaron {
    private final static Logger LOGGER = LogManager.getLogger(VampireBaronEntity.class);
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(VampireBaronEntity.class, DataSerializers.VARINT);
    private final SaveableMinionHandler<IVampireMinion.Saveable> minionHandler;
    private final int MAX_LEVEL = 4;

    /**
     * Used for ranged vs melee attack decision
     */
    private int attackDecisionCounter = 0;

    /**
     * Whether to prefer ranged attack
     */
    private boolean rangedAttack = false;

    private boolean prevAttacking = false;

    public VampireBaronEntity(EntityType<? extends VampireBaronEntity> type, World world) {
        super(type, world, true);
        minionHandler = new SaveableMinionHandler<>(this);


        this.garlicResist = EnumStrength.MEDIUM;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = super.attackEntityAsMob(entity);
        if (flag && entity instanceof LivingEntity) {
            float tm = 1f;
            int mr = 1;
            if (entity instanceof PlayerEntity) {
                float pld = (this.getLevel() + 1) - VampirePlayer.get((PlayerEntity) entity).getLevel() / 3f;
                tm = pld + 1;
                mr = pld < 1.5f ? 1 : (pld < 3 ? 2 : 3);
                if (HunterCoatItem.isFullyEquipped((PlayerEntity) entity)) {
                    tm *= 0.5F;
                }
            }
            if (entity instanceof VampireBaronEntity) {
                ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.STRENGTH, 40, 5));
            }
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.WEAKNESS, (int) (200 * tm), rand.nextInt(mr)));
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, (int) (100 * tm), rand.nextInt(mr)));
            attackDecisionCounter = 0;
        }
        return flag;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        attackDecisionCounter++;
        return super.attackEntityFrom(damageSource, amount);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public boolean canSpawn(IWorld worldIn, boolean fromSpawner) {
        int i = MathHelper.floor(this.getBoundingBox().minY);
        //Only spawn on the surface
        if (i < 60) return false;
//        CastlePositionData data = CastlePositionData.get(world);
//        if (data.isPosAt(MathHelper.floor_double(posX), MathHelper.floor_double(posZ))) {
//            return false;
//        }
        BlockPos blockpos = new BlockPos(this.posX, this.getBoundingBox().minY, this.posZ);

        return ModBlocks.cursed_earth.equals(world.getBlockState(blockpos.down()).getBlock()) && super.canSpawn(worldIn, fromSpawner);
    }

    @Override
    public long getLastComebackCall() {
        return 0;
    }

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getDataManager().set(LEVEL, level);
            this.updateEntityAttributes(false);
            float hp = this.getHealth() / this.getMaxHealth();
            this.setHealth(this.getMaxHealth() * hp);
            LOGGER.info("Lev %s", level);
        }
    }

    @Override
    public int getMaxInPortalTime() {
        return 500;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getMaxMinionCount() {
        return 15;
    }

    @Override
    public LivingEntity getMinionTarget() {
        return this.getAttackTarget();
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        return super.getName().appendText(" " + new TranslationTextComponent("text.vampirism.entity_level") + " " + (getLevel() + 1));
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public ISaveableMinionHandler<IVampireMinion.Saveable> getSaveableMinionHandler() {
        return minionHandler;
    }

    @Override
    public double getTheDistanceSquared(Entity e) {
        return this.getDistanceSq(e);
    }

    @Override
    public UUID getThePersistentID() {
        return this.entityUniqueID;
    }

    @Override
    public boolean isTheEntityAlive() {
        return this.isAlive();
    }

    @Override
    public void livingTick() {
        if (!prevAttacking && this.getAttackTarget() != null) {
            prevAttacking = true;
            updateEntityAttributes(true);
        }
        if (prevAttacking && this.getAttackTarget() == null) {
            prevAttacking = false;
            this.rangedAttack = false;
            this.attackDecisionCounter = 0;
            updateEntityAttributes(false);
        }
        this.getSaveableMinionHandler().checkMinions();
        if (!world.isRemote && shouldSpawnMinion()) {
            int i = 0;
            if (this.recentlyHit > 0) {
                i = this.rand.nextInt(3);
            }
            IVampireMinion.Saveable m = null;

            if (i == 1) {
                MobEntity e = new VampireMinionSaveableEntity(world);
                if (e == null) {
                    LOGGER.warn("Failed to create saveable minion");
                } else {
                    e.copyLocationAndAnglesFrom(this);
                    world.spawnEntity(e);
                    m = (IVampireMinion.Saveable) e;
                }

            } else if (i == 2 && this.getAttackTarget() != null) {
                m = (IVampireMinion.Saveable) UtilLib.spawnEntityBehindEntity(this.getAttackTarget(), ModEntities.vampire_minion_s, SpawnReason.NATURAL);
            }
            if (m == null) {
                m = (IVampireMinion.Saveable) UtilLib.spawnEntityInWorld(world, this.getBoundingBox().grow(19, 4, 19), ModEntities.vampire_minion_s, 3, Collections.emptyList()); //Do not avoid player here. Already using spawnBehind sometimes
            }
            if (m != null) {
                m.setLord(this);
            }
        }
        if (!this.world.isRemote && this.isGettingSundamage()) {
            this.teleportAway();

        }
        if (!this.world.isRemote && this.getAttackTarget() != null && this.ticksExisted % 128 == 0) {
            if (rangedAttack) {
                if (this.rand.nextInt(2) == 0 && this.navigator.getPathToEntityLiving(this.getAttackTarget()) != null) {
                    rangedAttack = false;
                }
            } else {
                if (attackDecisionCounter > 4 || this.rand.nextInt(6) == 0) {
                    rangedAttack = true;
                    attackDecisionCounter = 0;
                }
            }
            if (getLevel() > 3 && this.rand.nextInt(9) == 0) {
                this.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 60));
            }
        }
        super.livingTick();
    }

    @Override
    public void onKillEntity(LivingEntity entity) {
        super.onKillEntity(entity);
        if (entity instanceof VampireBaronEntity) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        setLevel(MathHelper.clamp(nbt.getInt("level"), 0, MAX_LEVEL));
        minionHandler.loadMinions(nbt.getList("minions", 10));
    }

    @Override
    public int suggestLevel(Difficulty d) {
        int avg = Math.round(((d.avgPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int max = Math.round(((d.maxPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int min = Math.round(((d.minPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * (MAX_LEVEL));

        switch (rand.nextInt(7)) {
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
                return rand.nextInt(MAX_LEVEL + 1);
            default:
                return rand.nextInt(max + 2 - min) + min;
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("level", getLevel());
        nbt.put("minions", minionHandler.getMinionsToSave());
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.updateEntityAttributes(false);
    }

    @Override
    protected float calculateFireDamage(float amount) {
        return (float) (amount * Balance.mobProps.VAMPIRE_BARON_FIRE_VULNERABILITY);
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(LEVEL, -1);
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 20 + 5 * getLevel();
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.VAMPIRE_BARON;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new FleeGarlicVampireGoal(this, 0.9F, false));
        this.tasks.addTask(5, new BaronAIAttackMelee(this, 1.0F));
        this.tasks.addTask(6, new BaronAIAttackRanged(this, 60, 64, 6, 4));
        this.tasks.addTask(6, new AvoidEntityGoal<>(this, PlayerEntity.class, 6.0F, 0.6, 0.7F, input -> input != null && !isLowerLevel((PlayerEntity) input)));//TODO Works only partially. Pathfinding somehow does not find escape routes
        this.tasks.addTask(7, new RandomWalkingGoal(this, 0.2));
        this.tasks.addTask(9, new LookAtGoal(this, PlayerEntity.class, 10.0F));
        this.tasks.addTask(10, new LookRandomlyGoal(this));

        this.targetTasks.addTask(1, new HurtByTargetGoal(this, false));
        this.targetTasks.addTask(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, input -> input != null && isLowerLevel(input)));
        this.targetTasks.addTask(3, new NearestAttackableTargetGoal<>(this, VampireBaronEntity.class, true, false));
    }

    /**
     * Decides if a new minion should be spawned. Therefore randomly checks the existing minion count
     */
    protected boolean shouldSpawnMinion() {
        if (this.ticksExisted % 30 == 7) {
            int count = getSaveableMinionHandler().getMinionCount();
            if (count < getLevel() + 1) {
                return true;
            }
            return recentlyHit > 0 && count < 2 + getLevel();
        }
        return false;
    }

    protected void updateEntityAttributes(boolean aggressive) {
        if (aggressive) {
            this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20D);
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(
                    Balance.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow((Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL - 1) / 5 + 1, (getLevel())));
        } else {
            this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(5D);
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(
                    Balance.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()) / 3);
        }
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_BARON_MAX_HEALTH * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
                .setBaseValue(Balance.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
    }

    private boolean isLowerLevel(PlayerEntity player) {
        int playerLevel = FactionPlayerHandler.get(player).getCurrentLevel();
        return (playerLevel - 8) / 2F - VampireBaronEntity.this.getLevel() <= 0;
    }

    private class BaronAIAttackMelee extends MeleeAttackGoal {

        BaronAIAttackMelee(CreatureEntity creature, double speedIn) {
            super(creature, speedIn, false);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !VampireBaronEntity.this.rangedAttack && super.shouldContinueExecuting();
        }

        @Override
        public boolean shouldExecute() {
            return !VampireBaronEntity.this.rangedAttack && super.shouldExecute();
        }
    }

    private class BaronAIAttackRanged extends AttackRangedDarkBloodGoal {

        BaronAIAttackRanged(VampireBaronEntity entity, int cooldown, int maxDistance, float damage, float indirectDamage) {
            super(entity, cooldown, maxDistance, damage, indirectDamage);
        }

        @Override
        public boolean shouldExecute() {
            return VampireBaronEntity.this.getAttackTarget() != null && (VampireBaronEntity.this.rangedAttack || !VampireBaronEntity.this.hasPath());
        }
    }
}
