package de.teamlapen.vampirism.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.ai.IMinionLord;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.DifficultyCalculator.Difficulty;
import de.teamlapen.vampirism.util.DifficultyCalculator.IAdjustableLevel;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;

public class EntityVampireLord extends DefaultVampire implements ISyncable, IMinionLord, IAdjustableLevel {

	
	private final static int MAX_LEVEL=5;

	protected int level = 0;
	
	private boolean prevAttacking=false;

	public EntityVampireLord(World par1World) {
		super(par1World);

		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityVampireHunter.class, BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.2));
		this.tasks.addTask(6, new EntityAIWander(this, 0.2));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this,EntityPlayer.class,0,false));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.applyEntityAttributes(false);
	}
	
	protected void applyEntityAttributes(boolean aggressive){
		if(aggressive){
			this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20D);
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(Math.min(4.5D,BALANCE.MOBPROP.VAMPIRE_LORD_MOVEMENT_SPEED*Math.pow(BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL, level-1)));
		}
		else{
			this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(5D);
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_LORD_MOVEMENT_SPEED*Math.pow(BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL, level-1)/3);
		}
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_LORD_MAX_HEALTH*Math.pow(BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL, level-1));
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_LORD_ATTACK_DAMAGE*Math.pow(BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL, level-1));
	}

	@Override
	public NBTTagCompound getJoinWorldSyncData() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeEntityToNBT(nbt);
		return nbt;
	}

	public int getLevel() {
		return this.level;
	}

	/**
	 * @return The number of near minions
	 */
	protected int getMinionCount() {
		return this.worldObj.getEntitiesWithinAABB(EntityVampireMinion.class, this.boundingBox.expand(22, 17, 22)).size();
	}

	@Override
	public EntityLivingBase getMinionTarget() {
		return this.getAttackTarget();
	}

	@Override
	public void loadPartialUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("level")) {
			this.level = nbt.getInteger("level");
		}

	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		boolean flag=super.attackEntityAsMob(entity);
		if(flag&&entity instanceof EntityLivingBase){
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.weakness.id,200,rand.nextInt(1)+1));
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,100,rand.nextInt(1)+1));
		}
		return flag;
	}

	@Override
	public void onDeath(DamageSource s) {
		if (this.recentlyHit > 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
			if (level > 0 && level < 6) {
				this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, level - 1), 0.3F);
			} else if (level > 5) {
				this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, 4), 0.3F);
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		if(!prevAttacking &&this.getAttackTarget()!=null){
			prevAttacking=true;
			this.applyEntityAttributes(true);
		}
		if(prevAttacking &&this.getAttackTarget()==null){
			prevAttacking=false;
			this.applyEntityAttributes(false);
		}
		if (!worldObj.isRemote&&shouldSpawnMinion()) {
			int i=0;
			if(this.recentlyHit>0){
				i=this.rand.nextInt(3);
			}
			IMinion m=null;
			
			if(i==1){
				EntityLiving e=(EntityLiving) EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_MINION_NAME,this.worldObj);
				e.copyLocationAndAnglesFrom(this);
				worldObj.spawnEntityInWorld(e);
				m=(IMinion) e;
			}
			else if(i==2&&this.getAttackTarget()!=null){
				m=(IMinion) Helper.spawnEntityBehindEntity(this.getAttackTarget(),REFERENCE.ENTITY.VAMPIRE_MINION_NAME);
			}
			if(m==null){
				m= (IMinion) Helper.spawnEntityInWorld(worldObj, this.boundingBox.expand(19, 14, 19), REFERENCE.ENTITY.VAMPIRE_MINION_NAME, 3);
			}
			if (m != null) {
				m.setLord(this);
			}
		}
		if (!this.worldObj.isRemote && this.worldObj.isDaytime()) {
			if (this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
				this.teleportAway();
			}
		}
		super.onLivingUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.loadPartialUpdate(nbt);
	}

	/**
	 * Sets the vampire lords level and updates his nametag
	 * @param l
	 * @param sync whether to sync the level with the client or not
	 */
	public void setLevel(int l, boolean sync) {
		if (l > 0 && l != level) {
			this.level = l;
			float hp=this.getHealth()/this.getMaxHealth();
			this.applyEntityAttributes(false);
			this.setHealth(this.getMaxHealth()*hp);
			if (sync && !this.worldObj.isRemote) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("level", level);
				Helper.sendPacketToPlayersAround(new UpdateEntityPacket(this, nbt), this);
			}

		}
	}

	/**
	 * Decides if a new minion should be spawned.
	 * Therefore randomly checks the existing minion count
	 * @return
	 */
	protected boolean shouldSpawnMinion() {
		if (this.ticksExisted%40 == 0) {
			int count = getMinionCount();
			if (count < level+1) {
				return true;
			}
			if (recentlyHit > 0 && count < 2+level) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Fakes a teleportation and actually just kills the entity
	 */
	protected void teleportAway() {
		this.setInvisible(true);
		short short1 = 128;
		for (int l = 0; l < short1; ++l) {
			double d6 = l / (short1 - 1.0D);
			float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
			float f1 = (this.rand.nextFloat() - 0.5F) * 0.2F;
			float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
			double d7 = this.posX + (50) * d6 + (this.rand.nextDouble() - 0.5D) * this.width * 2.0D;
			double d8 = this.posY + (10) * d6 + this.rand.nextDouble() * this.height;
			double d9 = this.posZ + (50) * d6 + (this.rand.nextDouble() - 0.5D) * this.width * 2.0D;
			this.worldObj.spawnParticle("portal", d7, d8, d9, f, f1, f2);
		}

		this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.endermen.portal", 1.0F, 1.0F);
		this.playSound("mob.endermen.portal", 1.0F, 1.0F);

		this.setDead();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("level", level);
	}
	
	@Override
	public String getCommandSenderName(){
		return super.getCommandSenderName()+" "+VampirismMod.proxy.translateToLocal("text.vampirism:entity_level")+" "+level;
	}
	
	@Override
	public boolean getAlwaysRenderNameTagForRender(){
		return true;
	}

	@Override
	public void setLevel(int level) {
		this.setLevel(level, false);
		
	}

	@Override
	public int suggestLevel(Difficulty d) {
		int avg=Math.round((d.avgLevel-4)/2);
		int max=Math.round((d.maxLevel-4)/2);
		int min=Math.round((d.minLevel-4)/2);
		
		switch(rand.nextInt(6)){
		case 0: return min;
		case 1: return max+1;
		case 2: return avg;
		case 3: return avg+1;
		default: return rand.nextInt(max+2-min)+min;
		}
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	@Override
	public double getDistanceSquared(Entity e) {
		return this.getDistanceSqToEntity(e);
	}

	@Override
	public Entity getRepresentingEntity() {
		return this;
	}
	

}
