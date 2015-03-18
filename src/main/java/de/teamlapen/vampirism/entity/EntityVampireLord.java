package de.teamlapen.vampirism.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

public class EntityVampireLord extends DefaultVampire implements ISyncable{

	protected int level=1;
	
	public EntityVampireLord(World par1World) {
		super(par1World);

		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityVampireHunter.class, BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.2));
		this.tasks.addTask(6, new EntityAIWander(this, 0.2));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.addAttackingTargetTasks(2);
		if(!par1World.isRemote){
			this.setLevel(4,false);
		}

	}
	
	public void setLevel(int l,boolean sync){
		if(l>0&&l!=level){
			this.level=l;
			this.setCustomNameTag("Vampire Lord Level "+level);
			if(sync&&!this.worldObj.isRemote){
				NBTTagCompound nbt=new NBTTagCompound();
				nbt.setInteger("level", level);
				Helper.sendPacketToPlayersAround(new UpdateEntityPacket(this,nbt), this);
			}
			
		}
	}
	
	public int getLevel(){
		return this.level;
	}
	
	@Override
	public boolean hasCustomNameTag(){
		return true;
	}
	

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MOVEMENT_SPEED);
	}

	@Override
	public void onDeath(DamageSource s) {
		if (this.recentlyHit>0&&this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
			if(level>0&&level<6){
				this.entityDropItem(new ItemStack(ModItems.pureBlood,1,level-1), 0.3F);
			}
			else if(level>5){
				this.entityDropItem(new ItemStack(ModItems.pureBlood,1,4),0.3F);
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		if(!this.worldObj.isRemote&&this.worldObj.isDaytime()){
			if(this.worldObj.canBlockSeeTheSky(
					MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.posY),
					MathHelper.floor_double(this.posZ))){
				this.teleportAway();
			}
		}
		super.onLivingUpdate();
	}
	
	/**
	 * Fakes a teleportation and actually just kills the entity
	 */
	protected void teleportAway(){
		this.setInvisible(true);
		short short1 = 128;
		for (int l = 0; l < short1; ++l) {
			double d6 = l / (short1 - 1.0D);
			float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
			float f1 = (this.rand.nextFloat() - 0.5F) * 0.2F;
			float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
			double d7 = this.posX + (50) * d6
					+ (this.rand.nextDouble() - 0.5D) * this.width * 2.0D;
			double d8 = this.posY + (10) * d6 + this.rand.nextDouble()
					* this.height;
			double d9 = this.posZ + (50) * d6
					+ (this.rand.nextDouble() - 0.5D) * this.width * 2.0D;
			this.worldObj.spawnParticle("portal", d7, d8, d9, f, f1, f2);
		}

		this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.endermen.portal",
				1.0F, 1.0F);
		this.playSound("mob.endermen.portal", 1.0F, 1.0F);
		
		this.setDead();
	}

	@Override
	public void loadPartialUpdate(NBTTagCompound nbt) {
		if(nbt.hasKey("level")){
			this.level=nbt.getInteger("level");
		}
		
	}
	
	 @Override
	 public void writeEntityToNBT(NBTTagCompound nbt) {
		 super.writeEntityToNBT(nbt);
		 nbt.setInteger("level", level);
	 }
	 
		@Override
		public void readEntityFromNBT(NBTTagCompound nbt) {
			super.readEntityFromNBT(nbt);
			this.loadPartialUpdate(nbt);
		}

		@Override
		public NBTTagCompound getJoinWorldSyncData() {
			NBTTagCompound nbt=new NBTTagCompound();
			this.writeEntityToNBT(nbt);
			return nbt;
		}


}
