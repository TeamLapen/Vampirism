package de.teamlapen.vampirism.entity.minions;

import java.util.UUID;

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
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
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
		this.tasks.addTask(4, new EntityAIFollowBoss(this, 1.0D));
		this.tasks.addTask(5, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
		this.targetTasks.addTask(2, new EntityAIDefendLord(this));

		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				IMinionLord lord=getLord();
				if (lord != null && lord.getRepresentingEntity().equals(entity)) {
					return false;
				}
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() <= BALANCE.VAMPIRE_FRIENDLY_LEVEL || VampirePlayer.get((EntityPlayer) entity).isVampireLord();
				}
				return false;
			}

		}));
		// Search for villagers
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityVillager) {
					return !VampireMob.get((EntityVillager) entity).isVampire();
				}
				return false;
			}

		}));

		this.targetTasks.addTask(8, new EntityAIHurtByTarget(this, false));
		
		activeCommand=this.getDefaultCommand();
		activeCommand.onActivated(this);
	}

	public void activateMinionCommand(IMinionCommand command){
		if(command==null)return;
		this.activeCommand.onDeactivated(this);
		this.activeCommand=command;
		this.activeCommand.onActivated(this);
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
	public EntityCreature getRepresentingEntity() {
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
	
	protected abstract IMinionCommand getDefaultCommand();

}
