package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampireLordData;
import de.teamlapen.vampirism.entity.ai.DraculaAIHeal;
import de.teamlapen.vampirism.entity.minions.EntitySaveableVampireMinion;
import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import org.eclipse.jdt.annotation.NonNull;

import java.util.List;

public class EntityDracula extends DefaultVampireWithMinion implements IBossDisplayData {
	// TODO Sounds

	private static final int DISAPPEAR_DELAY = 200;
	private static final int HOME_RADIUS = 19;
	private int disappearDelay;
	private final int maxTeleportDistanceX = 16;
	private final int maxTeleportDistanceY = 20;
	private final int maxTeleportDistanceZ = 16;
	private int damageCounter=0;
	private final static String TAG = "Dracula";

	public EntityDracula(World par1World) {
		super(par1World);
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(4, new DraculaAIHeal(this));
		this.tasks.addTask(12, new EntityAIWander(this, 0.7));
		this.tasks.addTask(13, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.isImmuneToFire = true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.DRACULA_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.DRACULA_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.DRACULA_MOVEMENT_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32F);
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
			CastlePositionData.Position pos=CastlePositionData.get(worldObj).findPosAtChunk(chunkCoordX,chunkCoordZ,true);
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
		damageCounter += p_70097_2_ * 2;
		return super.attackEntityFrom(p_70097_1_, p_70097_2_);
	}

	@Override
	public void onLivingUpdate() {

		this.isJumping = false;

		if (this.entityToAttack != null) {

			if(this.rand.nextInt(300)==0){
				this.faceEntity(this.entityToAttack, 100.0F, 100.0F);
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
			if (damageCounter > 40) {
				if(this.teleportRandomly()){
					damageCounter=0;
				}
			}
			if (this.entityToAttack != null) {
				if (this.entityToAttack instanceof EntityPlayer) {
					if (getMinionHandler().getMinionCount() < 4 && rand.nextInt(80) == 0) {
						EntitySaveableVampireMinion entity = (EntitySaveableVampireMinion) Helper.spawnEntityBehindEntity((EntityLivingBase) this.entityToAttack, REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME);
						if (entity != null) {
							entity.setLord(this);
							minionInHomeDist(entity);
						}
					}

				}
			}

			if (this.ticksExisted % 100 == 0 && this.hasHome() && this.getMinionHandler().getMinionCount() < 1) {
				ChunkCoordinates pos = this.getHomePosition();
				EntityVampireMinion minion = (EntityVampireMinion) Helper.spawnEntityInWorld(worldObj, AxisAlignedBB.getBoundingBox(pos.posX - 16, pos.posY - 2, pos.posZ - 16, pos.posX + 16, pos.posY + 10, pos.posZ + 16), REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME, 3);
				if (minion != null) {
					minion.setLord(this);
				}
			}
		}

		if (this.getHealth() > DraculaAIHeal.THRESHOLD * 2 && BALANCE.MOBPROP.DRACULA_REGENERATE_SECS >= 0 && this.ticksExisted % (BALANCE.MOBPROP.DRACULA_REGENERATE_SECS * 20) == 0 && (this.getLastAttackerTime() == 0 || this.getLastAttackerTime() - ticksExisted > 100)) {
			this.heal(5F);
		}

		super.onLivingUpdate();
	}

	private boolean minionInHomeDist(EntityCreature minion) {
		if (this.hasHome()) {
			minion.setHomeArea(this.getHomePosition().posX, this.getHomePosition().posY, this.getHomePosition().posZ, HOME_RADIUS);
			if (!minion.isWithinHomeDistanceCurrentPosition()) {
				minion.setDead();
				return false;
			}
		}
		return true;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.disappearDelay = nbt.getInteger("ddelay");
	}


	/** Teleports dracula randomly */
	private boolean teleportRandomly() {
		double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceX;
		double d1 = this.posY + (this.rand.nextInt((int) (maxTeleportDistanceY * 1.5)) - maxTeleportDistanceY * 0.5D);
		if (this.hasHome() && this.getHomePosition().posY - 6 > d1) d1 = this.posY;
		double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceZ;
		if(this.isWithinHomeDistance(MathHelper.floor_double(d0),MathHelper.floor_double(d1),MathHelper.floor_double(d2))){
			if(Helper.teleportTo(this, d0, d1, d2, true)){
				if (rand.nextInt(10) == 0) {
					this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 60));
					this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 60, 2));
					this.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60));
					summonBats();
				}
				return true;
			}
		}
		if (rand.nextBoolean()) {
			this.teleportRandomly();
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
		Logger.d(TAG, "Set draculas home pos");
		ChunkCoordIntPair lc=pos.getLowerMainCastle();
		this.setHomeArea((lc.chunkXPos << 4) + 15, (int) this.posY + 5, (lc.chunkZPos << 4) + 15, HOME_RADIUS);
		Logger.t("Home at %d %d %d with r %s", this.getHomePosition().posX, this.getHomePosition().posY, this.getHomePosition().posZ, this.func_110174_bM());

	}

	@Deprecated
	public void createTestGlass(){
		if(!this.hasHome())return;
		Logger.t("galas");
		for(int x= (int) (posX-100);x<posX+100;x++){
			for( int y= (int) (posY-100);y<posY+100;y++){
				for(int z= (int) (posZ-100);z<posZ+100;z++){
					if(this.isWithinHomeDistance(x,y,z)){
						this.worldObj.setBlock(x,y,z, Blocks.glass);
					}

				}
			}
		}
	}

	private void summonBats(){
		for (int i = 0; i < BALANCE.VP_SKILLS.SUMMON_BAT_COUNT; i++) {
			Entity e = EntityList.createEntityByName(REFERENCE.ENTITY.BLINDING_BAT_NAME, worldObj);
			((EntityBlindingBat)e).restrictLiveSpan();
			e.copyLocationAndAnglesFrom(this);
			worldObj.spawnEntityInWorld(e);
		}
	}

	public void freezeSkill() {
		List l = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(10, 5, 10), this.getMinionHandler().getLivingBaseSelectorExludingMinions());
		for (Object o : l) {
			EntityLivingBase e = (EntityLivingBase) o;
			e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 10));
			e.addPotionEffect(new PotionEffect(Potion.resistance.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 10));
			e.addPotionEffect(new PotionEffect(Potion.jump.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 128));
			Helper.spawnParticlesAroundEntity(e, "snowshovel", 1.5, 40);
		}
	}

	@Override
	public int getMaxMinionCount() {
		return 100;
	}
}
