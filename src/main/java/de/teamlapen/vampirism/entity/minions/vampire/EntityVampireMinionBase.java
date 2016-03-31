package de.teamlapen.vampirism.entity.minions.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinion;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMinion;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.minions.ai.MinionAIHurtByTarget;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for small vampire minions
 */
public abstract class EntityVampireMinionBase extends EntityVampireBase implements IVampireMinion {


    /**
     * Datawatcher id for oldVampireTexture
     * Used for the visual transition from normal vampire to players minion
     */
    private final int ID_TEXTURE = 16;


    private IMinionCommand activeCommand;
    private boolean wantsBlood = false;


    public EntityVampireMinionBase(World world) {
        super(world, true);
        // this.setSize(0.5F, 1.1F);
        //this.func_110163_bv(); TODO check if this was relevant
        this.tasks.addTask(6, new EntityAIAttackOnCollide(this, 1.0, false));
        this.tasks.addTask(15, new EntityAIWander(this, 0.7));
        this.tasks.addTask(16, new EntityAIWatchClosest(this, EntityPlayer.class, 10));

        this.targetTasks.addTask(8, new MinionAIHurtByTarget(this, false));
        getDataWatcher().addObject(ID_TEXTURE, -1);
        activeCommand = this.createDefaultCommand();
        activeCommand.onActivated();
    }

    @Override
    public boolean wantsBlood() {
        return wantsBlood;
    }

    public void setWantsBlood(boolean wantsBlood) {
        this.wantsBlood = wantsBlood;
    }

    @Override
    public void onKillEntity(EntityLivingBase entity) {


        if (this.getLord() != null && this.getLord() instanceof EntityVampireBaron) {
            ((EntityVampireBaron) this.getLord()).onKillEntity(entity);
        } else {
            super.onKillEntity(entity);
        }

    }

    @Override
    public void activateMinionCommand(IMinionCommand command) {
        if (command == null)
            return;
        this.activeCommand.onDeactivated();
        this.activeCommand = command;
        this.activeCommand.onActivated();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Balance.mobProps.VAMPIRE_MINION_MAX_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(Balance.mobProps.VAMPIRE_MINION_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(Balance.mobProps.VAMPIRE_MINION_MOVEMENT_SPEED);
    }

    @Override
    public void copyDataFromOld(Entity entityIn) {
        super.copyDataFromOld(entityIn);
        if (entityIn instanceof EntityVampireMinionBase) {
            this.copyDataFromMinion((EntityVampireMinionBase) entityIn);
        }
    }


    /**
     * Copies vampire minion data
     *
     * @param from
     */
    protected void copyDataFromMinion(EntityVampireMinionBase from) {
        this.setOldVampireTexture(from.getOldVampireTexture());
        this.setLord(from.getLord());
        this.activateMinionCommand(from.getActiveCommand());
    }

    public IMinionCommand getActiveCommand() {
        return this.activeCommand;
    }


    @Override
    public float getBlockPathWeight(BlockPos pos) {
        float i = 0.5F - this.worldObj.getLightBrightness(pos);
        if (i > 0)
            return i;
        return 0.01F;
    }

    /**
     * Has to return the command which is activated on default
     *
     * @return
     */
    protected abstract
    @Nonnull
    IMinionCommand createDefaultCommand();

    public int getOldVampireTexture() {
        return getDataWatcher().getWatchableObjectInt(ID_TEXTURE);
    }

    public void setOldVampireTexture(int oldVampireTexture) {
        getDataWatcher().updateObject(ID_TEXTURE, oldVampireTexture);
    }

    @Override
    public boolean isChild() {
        return true;
    }

    @Override
    public boolean canAttackClass(Class p_70686_1_) {
        //if (EntityPortalGuard.class.equals(p_70686_1_)) return false;//TODO
        return super.canAttackClass(p_70686_1_);
    }

    @Override
    public void onLivingUpdate() {
        if (getOldVampireTexture() != -1 && this.ticksExisted > 50) {
            setOldVampireTexture(-1);
        }
        if (getOldVampireTexture() != -1 && worldObj.isRemote) {
            UtilLib.spawnParticlesAroundEntity(this, EnumParticleTypes.SPELL_WITCH, 1.0F, 3);
        }
        if (!this.worldObj.isRemote && !this.dead) {

            List<EntityItem> list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D));
            Iterator<EntityItem> iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = iterator.next();

                if (!entityitem.isDead && entityitem.getEntityItem() != null) {
                    ItemStack itemstack = entityitem.getEntityItem();
                    if (activeCommand.shouldPickupItem(itemstack)) {
                        ItemStack stack1 = this.getEquipmentInSlot(0);
                        if (stack1 != null) {
                            this.entityDropItem(stack1, 0.0F);
                        }
                        this.setCurrentItemOrArmor(0, itemstack);
                        entityitem.setDead();
                    }

                }
            }
        }
        if (Balance.mobProps.VAMPIRE_MINION_REGENERATE_SECS >= 0 && this.ticksExisted % (Balance.mobProps.VAMPIRE_MINION_REGENERATE_SECS * 20) == 0 && (this.getLastAttackerTime() == 0 || this.getLastAttackerTime() - ticksExisted > 100)) {
            this.heal(2F);
        }
        super.onLivingUpdate();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        IMinionCommand command = this.getCommand(nbt.getInteger("command_id"));
        if (command != null) {
            this.activateMinionCommand(command);
        }
        if (nbt.hasKey("CustomName", 8) && nbt.getString("CustomName").length() > 0) {
            this.tryToSetName(nbt.getString("CustomName"), null);
        }
    }

    /**
     * Does not nothing, since minions should not be named normaly. Use {@link #tryToSetName(String, EntityPlayer)} instead
     */
    @Override
    public void setCustomNameTag(String s) {

    }

    /**
     * Replaces {@link #setCustomNameTag(String)}.
     *
     * @param name
     * @param player If this isn't null, checks if the player is the minions lord
     * @return success
     */
    public boolean tryToSetName(String name, @Nullable EntityPlayer player) {
        if (player == null || MinionHelper.isLordSafe(this, player)) {
            super.setCustomNameTag(name);
            return true;
        }
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("command_id", getActiveCommand().getId());
    }


    @Override
    public boolean writeToNBTOptional(NBTTagCompound nbt) {
        if (this instanceof ISaveableMinion) {
            return false;
        }
        return super.writeToNBTOptional(nbt);
    }


    @Override
    public int getTalkInterval() {
        return 2000;
    }
}
