package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampireBaron;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMinion;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeGarlic;
import de.teamlapen.vampirism.entity.minions.SaveableMinionHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public class EntityVampireBaron extends EntityVampireBase implements IVampireBaron {
    private final SaveableMinionHandler<IVampireMinion.Saveable> minionHandler;
    private final int ID_LEVEL = 16;
    private final int MAX_LEVEL = 4;
    /**
     * True after the datawatcher has been initialized.
     */
    private boolean datawatcher_init = false;
    private boolean prevAttacking = false;

    public EntityVampireBaron(World world) {
        super(world, true);
        minionHandler = new SaveableMinionHandler<>(this);
        getDataWatcher().addObject(ID_LEVEL, -1);
        datawatcher_init = true;
        this.setSize(0.6F, 1.8F);


        this.garlicResist = EnumGarlicStrength.MEDIUM;


        this.tasks.addTask(4, new VampireAIFleeGarlic(this, 0.9F, false));
        this.tasks.addTask(5, new EntityAIAttackOnCollide(this, 1.0F, false));
        this.tasks.addTask(6, new EntityAIWander(this, 0.2));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVampireBaron.class, true, false));
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
            }
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.id, (int) (200 * tm), rand.nextInt(mr) + 1));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, (int) (100 * tm), rand.nextInt(mr) + 1));
        }
        return flag;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        int i = MathHelper.floor_double(this.getEntityBoundingBox().minY);
        //Only spawn on the surface
        if (i < 60) return false;
//        CastlePositionData data = CastlePositionData.get(worldObj);
//        if (data.isPosAt(MathHelper.floor_double(posX), MathHelper.floor_double(posZ))) {
//            return false;
//        }
        return super.getCanSpawnHere();
    }

    @Override
    public long getLastComebackCall() {
        return 0;
    }

    @Override
    public int getLevel() {
        return datawatcher_init ? getDataWatcher().getWatchableObjectInt(ID_LEVEL) : -1;
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            this.updateEntityAttributes(false);
            float hp = this.getHealth() / this.getMaxHealth();
            this.setHealth(this.getMaxHealth() * hp);
            getDataWatcher().updateObject(ID_LEVEL, level);
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

    @Override
    public String getName() {
        return super.getName() + " " + StatCollector.translateToLocal("text.vampirism.entity_level") + " " + (getLevel() + 1);
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Override
    public ISaveableMinionHandler getSaveableMinionHandler() {
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
    public void onDeath(DamageSource s) {
        super.onDeath(s);
        if (this.recentlyHit > 0 && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
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
        if (!worldObj.isRemote && shouldSpawnMinion()) {
            int i = 0;
            if (this.recentlyHit > 0) {
                i = this.rand.nextInt(3);
            }
            IVampireMinion.Saveable m = null;

            if (i == 1) {
                EntityLiving e = (EntityLiving) EntityList.createEntityByName(ModEntities.VAMPIRE_MINION_SAVEABLE_NAME, this.worldObj);
                e.copyLocationAndAnglesFrom(this);
                worldObj.spawnEntityInWorld(e);
                m = (IVampireMinion.Saveable) e;
            } else if (i == 2 && this.getAttackTarget() != null) {
                VampirismMod.log.t("Spawning entity behind player");
                m = (IVampireMinion.Saveable) UtilLib.spawnEntityBehindEntity(this.getAttackTarget(), ModEntities.VAMPIRE_MINION_SAVEABLE_NAME);
            }
            if (m == null) {
                m = (IVampireMinion.Saveable) UtilLib.spawnEntityInWorld(worldObj, this.getEntityBoundingBox().expand(19, 4, 19), ModEntities.VAMPIRE_MINION_SAVEABLE_NAME, 3);
            }
            if (m != null) {
                m.setLord(this);
            }
        }
        if (!this.worldObj.isRemote && this.isGettingSundamage()) {
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
        VampirismMod.log.t("Dif %d %d %d", min, max, avg);
        switch (rand.nextInt(6)) {
            case 0:
                return min;
            case 1:
                return max + 1;
            case 2:
                return avg;
            case 3:
                return avg + 1;
            case 4:
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
    protected int getExperiencePoints(EntityPlayer player) {
        return 20 + 5 * getLevel();
    }

    /**
     * Decides if a new minion should be spawned. Therefore randomly checks the existing minion count
     *
     * @return
     */
    protected boolean shouldSpawnMinion() {
        if (this.ticksExisted % 30 == 7) {
            int count = getSaveableMinionHandler().getMinionCount();
            if (count < getLevel() + 2) {
                return true;
            }
            if (recentlyHit > 0 && count < 4 + getLevel()) {
                return true;
            }
        }
        return false;
    }

    protected void updateEntityAttributes(boolean aggressive) {
        if (aggressive) {
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20D);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(
                    Balance.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow((Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL - 1) / 3 + 1, (getLevel())));
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(5D);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(
                    Balance.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()) / 3);
        }
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Balance.mobProps.VAMPIRE_BARON_MAX_HEALTH * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .setBaseValue(Balance.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE * Math.pow(Balance.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
    }
}
