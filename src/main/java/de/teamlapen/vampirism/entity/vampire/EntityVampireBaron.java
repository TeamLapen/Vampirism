package de.teamlapen.vampirism.entity.vampire;

import com.google.common.base.Predicate;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampireBaron;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMinion;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeGarlic;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minions.SaveableMinionHandler;
import de.teamlapen.vampirism.items.ItemHunterCoat;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public class EntityVampireBaron extends EntityVampireBase implements IVampireBaron {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityVampireBaron.class, DataSerializers.VARINT);
    private final SaveableMinionHandler<IVampireMinion.Saveable> minionHandler;
    private final int MAX_LEVEL = 4;

    private boolean prevAttacking = false;

    public EntityVampireBaron(World world) {
        super(world, true);
        minionHandler = new SaveableMinionHandler<>(this);
        this.setSize(0.6F, 1.8F);


        this.garlicResist = EnumStrength.MEDIUM;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = super.attackEntityAsMob(entity);
        if (flag && entity instanceof EntityLivingBase) {
            float tm = 1f;
            int mr = 1;
            if (entity instanceof EntityPlayer) {
                float pld = (this.getLevel() + 1) - VampirePlayer.get((EntityPlayer) entity).getLevel() / 3f;
                tm = pld + 1;
                mr = pld < 1.5f ? 1 : (pld < 3 ? 2 : 3);
                if (ItemHunterCoat.isFullyEquipped((EntityPlayer) entity)) {
                    tm *= 0.5F;
                }
            }
            if (entity instanceof EntityVampireBaron) {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 5));
            }
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) (200 * tm), rand.nextInt(mr)));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) (100 * tm), rand.nextInt(mr)));
        }
        return flag;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.getEntityBoundingBox().minY);
        //Only spawn on the surface
        if (i < 60) return false;
//        CastlePositionData data = CastlePositionData.get(world);
//        if (data.isPosAt(MathHelper.floor_double(posX), MathHelper.floor_double(posZ))) {
//            return false;
//        }
        BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);

        return ModBlocks.cursedEarth.equals(world.getBlockState(blockpos.down()).getBlock()) && super.getCanSpawnHere();
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
    public EntityLivingBase getMinionTarget() {
        return this.getAttackTarget();
    }

    @Nonnull
    @Override
    public String getName() {
        return super.getName() + " " + I18n.translateToLocal("text.vampirism.entity_level") + " " + (getLevel() + 1);
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Override
    public ISaveableMinionHandler<IVampireMinion.Saveable> getSaveableMinionHandler() {
        return minionHandler;
    }

    @Override
    public double getTheDistanceSquared(Entity e) {
        return this.getDistanceSqToEntity(e);
    }

    @Override
    public UUID getThePersistentID() {
        return this.getPersistentID();
    }

    @Override
    public boolean isTheEntityAlive() {
        return this.isEntityAlive();
    }

    @Override
    public void onDeath(@Nonnull DamageSource s) {
        super.onDeath(s);
        if (this.recentlyHit > 0 && this.world.getGameRules().getBoolean("doMobLoot")) {
            if (getLevel() >= 0 && getLevel() < 5) {
                this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, getLevel()), 0.3F);
            } else if (getLevel() > 5) {
                this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, 4), 0.3F);
            }
        }
    }

    @Override
    public void onKillEntity(EntityLivingBase entity) {
        super.onKillEntity(entity);
        if (entity instanceof EntityVampireBaron) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void onLivingUpdate() {
        if (!prevAttacking && this.getAttackTarget() != null) {
            prevAttacking = true;
            updateEntityAttributes(true);
        }
        if (prevAttacking && this.getAttackTarget() == null) {
            prevAttacking = false;
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
                EntityLiving e = (EntityLiving) EntityList.createEntityByName(ModEntities.VAMPIRE_MINION_SAVEABLE_NAME, this.world);
                if (e == null) {
                    VampirismMod.log.w("VampireBaron", "Failed to create saveable minion");
                } else {
                    e.copyLocationAndAnglesFrom(this);
                    world.spawnEntity(e);
                    m = (IVampireMinion.Saveable) e;
                }

            } else if (i == 2 && this.getAttackTarget() != null) {
                m = (IVampireMinion.Saveable) UtilLib.spawnEntityBehindEntity(this.getAttackTarget(), ModEntities.VAMPIRE_MINION_SAVEABLE_NAME);
            }
            if (m == null) {
                m = (IVampireMinion.Saveable) UtilLib.spawnEntityInWorld(world, this.getEntityBoundingBox().expand(19, 4, 19), ModEntities.VAMPIRE_MINION_SAVEABLE_NAME, 3);
            }
            if (m != null) {
                m.setLord(this);
            }
        }
        if (!this.world.isRemote && this.isGettingSundamage()) {
            this.teleportAway();

        }
        super.onLivingUpdate();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setLevel(Math.min(nbt.getInteger("level"), 1));
        minionHandler.loadMinions(nbt.getTagList("minions", 10));
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
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("level", getLevel());
        nbt.setTag("minions", minionHandler.getMinionsToSave());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes(false);
    }

    @Override
    protected float calculateFireDamage(float amount) {
        return (float) (amount * Balance.mobProps.VAMPIRE_BARON_FIRE_VULNERABILITY);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(LEVEL, -1);
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 20 + 5 * getLevel();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new VampireAIFleeGarlic(this, 0.9F, false));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0F, false));
        this.tasks.addTask(6, new EntityAIAvoidEntity<>(this, EntityPlayer.class, new Predicate<EntityPlayer>() {
            @Override
            public boolean apply(@Nullable EntityPlayer input) {
                return input != null && !isLowerLevel(input);
            }
        }, 6.0F, 0.6, 0.7F));//TODO Works only partially. Pathfinding somehow does not find escape routes
        this.tasks.addTask(7, new EntityAIWander(this, 0.2));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, new Predicate<EntityPlayer>() {
            @Override
            public boolean apply(@Nullable EntityPlayer input) {
                return input != null && isLowerLevel(input);
            }
        }));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVampireBaron.class, true, false));
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
            if (recentlyHit > 0 && count < 2 + getLevel()) {
                return true;
            }
        }
        return false;
    }

    protected void updateEntityAttributes(boolean aggressive) {
        if (aggressive) {
            this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20D);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(
                    Balance.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow((Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL - 1) / 3 + 1, (getLevel())));
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(5D);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(
                    Balance.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()) / 3);
        }
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_BARON_MAX_HEALTH * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
                .setBaseValue(Balance.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
    }

    private boolean isLowerLevel(EntityPlayer player) {
        int playerLevel = FactionPlayerHandler.get(player).getCurrentLevel();
        return (playerLevel - 8) / 2F - EntityVampireBaron.this.getLevel() <= 0;
    }
}
