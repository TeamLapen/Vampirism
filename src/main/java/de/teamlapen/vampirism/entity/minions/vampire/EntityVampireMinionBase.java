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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for small vampire minions
 * Does not count as entity for spawning algorithm
 */
public abstract class EntityVampireMinionBase extends EntityVampireBase implements IVampireMinion {


    /**
     * Datamanager key for oldVampireTexture
     * Used for the visual transition from normal vampire to players minion
     */
    private final static DataParameter<Integer> TEXTURE = EntityDataManager.createKey(EntityVampireMinionBase.class, DataSerializers.VARINT);


    private IMinionCommand activeCommand;
    private boolean wantsBlood = false;


    public EntityVampireMinionBase(World world) {
        super(world, false);
        // this.setSize(0.5F, 1.1F);
        //this.func_110163_bv(); TODO check if this was relevant

        activeCommand = this.createDefaultCommand();
        activeCommand.onActivated();
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
    public boolean canAttackClass(Class p_70686_1_) {
        //if (EntityPortalGuard.class.equals(p_70686_1_)) return false;//TODO
        return super.canAttackClass(p_70686_1_);
    }


    public IMinionCommand getActiveCommand() {
        return this.activeCommand;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        float i = 0.5F - this.world.getLightBrightness(pos);
        if (i > 0)
            return i;
        return 0.01F;
    }

    public int getOldVampireTexture() {
        return getDataManager().get(TEXTURE);
    }

    public void setOldVampireTexture(int oldVampireTexture) {
        getDataManager().set(TEXTURE, oldVampireTexture);
    }

    @Override
    public int getTalkInterval() {
        return 2000;
    }

    @Override
    public boolean isChild() {
        return true;
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        //Don't count as entity for spawning
        return !forSpawnCount && super.isCreatureType(type, forSpawnCount);
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
    public void onLivingUpdate() {
        if (getOldVampireTexture() != -1 && this.ticksExisted > 50) {
            setOldVampireTexture(-1);
        }
        if (getOldVampireTexture() != -1 && world.isRemote) {
            UtilLib.spawnParticlesAroundEntity(this, EnumParticleTypes.SPELL_WITCH, 1.0F, 3);
        }
        if (!this.world.isRemote && !this.dead) {

            List<EntityItem> list = this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D));

            for (EntityItem entityitem : list) {
                if (!entityitem.isDead && !(entityitem.getItem().isEmpty())) {
                    ItemStack itemstack = entityitem.getItem();
                    if (activeCommand.shouldPickupItem(itemstack)) {
                        ItemStack stack1 = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                        if (!stack1.isEmpty()) {
                            this.entityDropItem(stack1, 0.0F);
                        }
                        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack);
                        entityitem.setDead();
                    }

                }
            }
        }
        if (Balance.mobProps.VAMPIRE_MINION_REGENERATE_SECS >= 0 && this.ticksExisted % (Balance.mobProps.VAMPIRE_MINION_REGENERATE_SECS * 20) == 0 && (this.getLastAttackedEntityTime() == 0 || this.getLastAttackedEntityTime() - ticksExisted > 100)) {
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

    public void setWantsBlood(boolean wantsBlood) {
        this.wantsBlood = wantsBlood;
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
    public boolean wantsBlood() {
        return wantsBlood;
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
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_MINION_MAX_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_MINION_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.VAMPIRE_MINION_MOVEMENT_SPEED);
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

    /**
     * Has to return the command which is activated on default
     *
     * @return
     */
    protected abstract
    @Nonnull
    IMinionCommand createDefaultCommand();

    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(TEXTURE, -1);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(6, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(15, new EntityAIWander(this, 0.7));
        this.tasks.addTask(16, new EntityAIWatchClosest(this, EntityPlayer.class, 10));

        this.targetTasks.addTask(8, new MinionAIHurtByTarget(this, false));
    }
}
