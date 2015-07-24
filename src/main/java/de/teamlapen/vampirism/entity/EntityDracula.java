package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampireLordData;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import org.eclipse.jdt.annotation.NonNull;

/** @author Mistadon */
public class EntityDracula extends DefaultVampire implements IBossDisplayData {
	// TODO Sounds

	private static final int DISAPPEAR_DELAY = 200;
	private int disappearDelay;
	private final int maxTeleportDistanceX = 16;
	private final int maxTeleportDistanceY = 16;
	private final int maxTeleportDistanceZ = 16;
	private boolean inCastle;
	private int damageCounter=0;
	private final static String TAG = "Dracula";

	public EntityDracula(World par1World) {
		super(par1World);
		this.tasks.addTask(2,new EntityAIMoveTowardsRestriction(this,1.0D));
		this.tasks.addTask(12, new EntityAIWander(this, 0.7));
		this.tasks.addTask(13, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.DRACULA_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.DRACULA_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.DRACULA_MOVEMENT_SPEED);
	}

	/**
	 * Starts a countdown at whichs end the entity will be fake teleported and killed
	 */
	public void makeDisappear() {
		if (this.disappearDelay == 0) {
			this.disappearDelay = DISAPPEAR_DELAY;
		}
	}

	@Override
	public void onDeath(DamageSource src) {
		if (src.getSourceOfDamage() instanceof EntityPlayer) {
			boolean flag=VampireLordData.get(this.worldObj).makeLord((EntityPlayer) src.getSourceOfDamage());
			if(flag){
				VampireLordData.get(this.worldObj).onDraculaDied();
			}
			else{
				VampireLordData.get(this.worldObj).setRegenerateCastleDim(true);
			}
			Logger.d(TAG,"Dracula (%s) was killed by a player",this);
		}
		else{
			//If Dracula died in a castle area, but was not killed by a player, try to respawn him with low health otherwise regenerate the world
			CastlePositionData.Position pos=CastlePositionData.get(worldObj).findPosAtChunk(chunkCoordX,chunkCoordZ);
			if(pos!=null){
				EntityDracula drac= (EntityDracula) EntityList.createEntityByName(REFERENCE.ENTITY.DRACULA_NAME,worldObj);
				boolean flag=Helper.spawnEntityInWorld(worldObj, AxisAlignedBB.getBoundingBox(pos.getLowerMainCastle().chunkXPos<<4,pos.getHeight(),pos.getLowerMainCastle().chunkXPos<<4,pos.getUpperMainCastle().chunkXPos<<4,pos.getHeight()+10,pos.getUpperMainCastle().chunkZPos<<4),drac,10);
				if(flag){
					drac.makeCastleLord(pos);
					drac.setHealth(10);
					Logger.i(TAG, "Dracula (%s) died inside a castle, but could be respawned",this);
				}
				else{
					drac.setDead();
					VampireLordData.get(this.worldObj).setRegenerateCastleDim(true);
					Logger.w(TAG,"Dracula (%s) died inside a castle and could not be respawned -> regenerate dimension",this);
				}
			}
			else{
				Logger.w(TAG,"Dracula (%s) died outside of a castle",this);
			}
		}
	}

	@Override public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
		damageCounter+=p_70097_2_*5;
		return super.attackEntityFrom(p_70097_1_, p_70097_2_);
	}

	@Override
	public void onLivingUpdate() {

		this.isJumping = false;

		if (this.entityToAttack != null) {
			this.faceEntity(this.entityToAttack, 100.0F, 100.0F);
			if(this.rand.nextInt(300)==0){
				this.addPotionEffect(new PotionEffect(Potion.invisibility.id,60));
			}
		}
		if(this.isInWater()||this.handleLavaMovement()){
			this.teleportRandomly();
		}

		if (!this.worldObj.isRemote && this.isEntityAlive()) {
			if (disappearDelay > 0) {
				if (--disappearDelay == 1) {
					this.teleportAway();
				}
			}
			if(damageCounter>0)damageCounter--;
			if(damageCounter>50){
				if(this.teleportRandomly()){
					damageCounter=0;
				}
			}
			if (this.entityToAttack != null) {
				if (this.entityToAttack instanceof EntityPlayer && this.shouldAttackPlayer((EntityPlayer) this.entityToAttack)) {
					//					if (this.entityToAttack.getDistanceSqToEntity(this) < 16.0D){
					//						this.teleportRandomly();
					//						if(rand.nextBoolean()){
					//							this.summonBats();
					//						}
					//					}

				}
			}
		}

		super.onLivingUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.disappearDelay = nbt.getInteger("ddelay");
	}

	/**
	 * Checks to see if dracula should be attacking this player
	 */
	private boolean shouldAttackPlayer(EntityPlayer p_70821_1_) {
		Vec3 vec3 = p_70821_1_.getLook(1.0F).normalize();
		Vec3 vec31 = Vec3.createVectorHelper(this.posX - p_70821_1_.posX, this.boundingBox.minY + this.height / 2.0F - (p_70821_1_.posY + p_70821_1_.getEyeHeight()), this.posZ - p_70821_1_.posZ);
		double d0 = vec31.lengthVector();
		vec31 = vec31.normalize();
		double d1 = vec3.dotProduct(vec31);
		return d1 > 1.0D - 0.025D / d0 && p_70821_1_.canEntityBeSeen(this);
	}

	/** Teleports dracula randomly */
	private boolean teleportRandomly() {
		double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceX;
		double d1 = this.posY + (this.rand.nextInt(maxTeleportDistanceY) - maxTeleportDistanceY * 0.5D);
		double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceZ;
		if(this.isWithinHomeDistance(MathHelper.floor_double(d0),MathHelper.floor_double(d1),MathHelper.floor_double(d2))){
			if(Helper.teleportTo(this, d0, d1, d2, true)){
				if(rand.nextInt(5)==0){
					summonBats();
				}
				return true;
			}
		}
		return false;
	}

	/** Teleports dracula to the given entity */
	private boolean teleportToEntity(Entity e) {
		Vec3 vec3 = Vec3.createVectorHelper(this.posX - e.posX, this.boundingBox.minY + this.height / 2.0F - e.posY + e.getEyeHeight(), this.posZ - e.posZ);
		vec3 = vec3.normalize();
		double d0 = 16.0D;
		double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
		double d2 = this.posY + (this.rand.nextInt(16) - 8) - vec3.yCoord * d0;
		double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;
		return Helper.teleportTo(this, d1, d2, d3, true);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("ddelay", disappearDelay);
	}

	/**
	 * Called when the entity is spawned in a castle as lord
	 */
	public void makeCastleLord(@NonNull CastlePositionData.Position pos){
		ChunkCoordIntPair lc=pos.getLowerMainCastle();
		this.setHomeArea(lc.chunkXPos << 4 + 15, (int) this.posY + 1, lc.chunkZPos << 4 + 15, 16);
		inCastle=true;
	}

	private void summonBats(){
		for (int i = 0; i < BALANCE.VP_SKILLS.SUMMON_BAT_COUNT; i++) {
			Entity e = EntityList.createEntityByName(REFERENCE.ENTITY.BLINDING_BAT_NAME, worldObj);
			((EntityBlindingBat)e).restrictLiveSpan();
			e.copyLocationAndAnglesFrom(this);
			worldObj.spawnEntityInWorld(e);
		}
	}
}
