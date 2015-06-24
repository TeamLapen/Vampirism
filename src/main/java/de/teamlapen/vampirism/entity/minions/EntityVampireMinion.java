package de.teamlapen.vampirism.entity.minions;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.jdt.annotation.NonNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.DefaultVampire;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendLord;
import de.teamlapen.vampirism.entity.ai.EntityAIFollowBoss;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;

/**
 * Base class for all vampire minions. Handles conversion and commands
 * @author Max
 *
 */
public abstract class EntityVampireMinion extends DefaultVampire implements IMinion, ISyncable {

	/**
	 * Used for the visual transition from normal vampire to players minion
	 */
	private int oldVampireTexture = -1;
	
	private IMinionCommand activeCommand;
	
	@SideOnly(Side.CLIENT)
	private int activeCommandId;

	public EntityVampireMinion(World world) {
		super(world);
		this.setSize(0.3F, 0.6F);
		this.func_110163_bv();
		this.tasks.addTask(6, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
		this.tasks.addTask(15, new EntityAIWander(this,0.7));
		this.tasks.addTask(16, new EntityAIWatchClosest(this,EntityPlayer.class,10));

		this.targetTasks.addTask(8, new EntityAIHurtByTarget(this, false));
		
		activeCommand=this.getDefaultCommand();
		activeCommand.onActivated();
	}

	public void activateMinionCommand(IMinionCommand command){
		if(command==null)return;
		this.activeCommand.onDeactivated();
		this.activeCommand=command;
		this.activeCommand.onActivated();
	}
	
	public IMinionCommand getActiveCommand(){
		return this.activeCommand;
	}
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MINION_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MINION_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MINION_MOVEMENT_SPEED);
	}

	@Override
	public void copyDataFrom(Entity from, boolean p) {
		super.copyDataFrom(from, p);
		if (from instanceof EntityVampireMinion) {
			EntityVampireMinion m = (EntityVampireMinion) from;
			this.copyDataFromMinion(m);
		}
	}

	/**
	 * Copies vampire minion data
	 * @param from
	 */
	protected void copyDataFromMinion(EntityVampireMinion from) {
		this.setOldVampireTexture(from.getOldVampireTexture());
	}

	@Override
	public float getBlockPathWeight(int p_70783_1_, int p_70783_2_, int p_70783_3_) {
		float i = 0.5F - this.worldObj.getLightBrightness(p_70783_1_, p_70783_2_, p_70783_3_);
		if (i > 0)
			return i;
		return 0.01F;
	}

	public int getOldVampireTexture() {
		return oldVampireTexture;
	}

	@Override
	public @NonNull EntityCreature getRepresentingEntity() {
		return this;
	}

	@Override
	public boolean isChild() {
		return true;
	}

	/**
	 * Vampire minions have no right to complain about sun damage ;)
	 */
	@Override
	public boolean isValidLightLevel() {
		return true;
	}

	/**
	 * Can be used by child classes to write info to {@link #loadUpdateFromNBT(NBTTagCompound)}
	 * 
	 * @param nbt
	 */
	protected void loadPartialUpdateFromNBT(NBTTagCompound nbt) {

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("oldvampire")) {
			this.oldVampireTexture = nbt.getInteger("oldvampire");
		}
		this.activeCommandId=nbt.getInteger("active_command_id");
		loadPartialUpdateFromNBT(nbt);
	}

	@Override
	public void onLivingUpdate() {
		if (oldVampireTexture != -1 && this.ticksExisted > 50) {
			oldVampireTexture = -1;
		}
		if (oldVampireTexture != -1 && worldObj.isRemote) {
			Helper.spawnParticlesAroundEntity(this, "witchMagic", 1.0F, 3);
		}
		if(!this.worldObj.isRemote&&!this.dead){
			List list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityItem entityitem = (EntityItem)iterator.next();

                if (!entityitem.isDead && entityitem.getEntityItem() != null)
                {
                    ItemStack itemstack = entityitem.getEntityItem();
                    if(activeCommand.shouldPickupItem(itemstack)){
                    	ItemStack stack1=this.getEquipmentInSlot(0);
                    	if(stack1!=null){
                    		this.entityDropItem(stack1, 0.0F);
                    	}
                    	this.setCurrentItemOrArmor(0, itemstack);
                        entityitem.setDead();
                    }

                }
            }
		}
		super.onLivingUpdate();
	}

	public void setOldVampireTexture(int oldVampireTexture) {
		this.oldVampireTexture = oldVampireTexture;
	}

	@Override
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		nbt.setInteger("oldvampire", oldVampireTexture);
		nbt.setInteger("active_command_id", activeCommand.getId());
		writeUpdateToNBT(nbt);
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound nbt) {
		if (shouldBeSavedWithLord()) {
			return false;
		}
		return super.writeToNBTOptional(nbt);
	}

	/**
	 * Can be used by child classes to write info to {@link #writeFullUpdateToNBT(NBTTagCompound)}
	 * 
	 * @param nbt
	 */
	protected void writeUpdateToNBT(NBTTagCompound nbt) {

	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("command_id", getActiveCommand().getId());
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		super.readEntityFromNBT(nbt);
		IMinionCommand command=this.getCommand(nbt.getInteger("command_id"));
		if(command!=null){
			this.activateMinionCommand(command);
		}
	}
	
	/**
	 * Has to return the command which is activated on default
	 * @return
	 */
	protected abstract @NonNull IMinionCommand getDefaultCommand();

}
