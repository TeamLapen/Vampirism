package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.ai.DraculaAIHeal;
import de.teamlapen.vampirism.entity.minions.EntitySaveableVampireMinion;
import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import org.eclipse.jdt.annotation.NonNull;

import java.util.List;

public class EntityDracula extends DefaultVampireWithMinion implements IBossDisplayData {
	private static final int DISAPPEAR_DELAY = 200;
	private static final int TELEPORT_THRESHOLD = 30;
	private static final int TELEPORT_DELAY = 80;
	private AxisAlignedBB castle;
	private int disappearDelay;
	private int teleportDelay = 0;
	private final int maxTeleportDistanceX = 16;
	private final int maxTeleportDistanceY = 3;
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
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
		this.experienceValue = 100;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.DRACULA_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.DRACULA_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.DRACULA_MOVEMENT_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(50F);
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
	protected void attackedEntityAsMob(EntityLivingBase entity) {
		if (rand.nextInt(3) == 0) {
			entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));
			entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 50));
			if (rand.nextBoolean()) {
				entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 120));
			}
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
				Logger.w(TAG, "Dracula (%s) died outside of a castle", this);
			}
		}
	}

	@Override
	public boolean isWithinHomeDistanceCurrentPosition() {
		return super.isWithinHomeDistanceCurrentPosition();
	}

	@Override
	public boolean hasHome() {
		return castle != null;
	}

	@Override
	public boolean isWithinHomeDistance(int p_110176_1_, int p_110176_2_, int p_110176_3_) {
		if (castle != null) {
			return castle.isVecInside(Vec3.createVectorHelper(p_110176_1_, p_110176_2_, p_110176_3_));
		}
		return true;
	}

	@Override
	public ChunkCoordinates getHomePosition() {
		if (castle == null) {
			return null;
		}
		return new ChunkCoordinates((int) castle.minX + 16, (int) castle.minY + 3, (int) (castle.minZ + 16));
	}

	@Override
	public void onKillEntity(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			this.restoreOnPlayerKill((EntityPlayer) entity);
		}
	}

	public void restoreOnPlayerKill(EntityPlayer player) {
		this.setHealth(this.getMaxHealth());
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("player_id", this.getEntityId());
		data.setBoolean("direct", true);
		VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(0, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ), MathHelper.floor_double(posZ), 40, data));

		for (int x = (int) (this.posX - 25); x < this.posX + 25; x++) {
			for (int y = (int) (this.posY - 5); y < this.posY + 10; y++) {
				for (int z = (int) (this.posZ - 25); z < this.posZ + 25; z++) {
					if (ModBlocks.bloodAltar2.equals(this.worldObj.getBlock(x, y, z))) {
						((TileEntityBloodAltar2) worldObj.getTileEntity(x, y, z)).removeBlood(TileEntityBloodAltar2.MAX_BLOOD);
						((TileEntityBloodAltar2) worldObj.getTileEntity(x, y, z)).addBlood(rand.nextInt(TileEntityBloodAltar2.MAX_BLOOD));
					}
				}
			}
		}
	}

	@Override public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
		if (this.getHealth() < this.getMaxHealth() * 0.8) {
			damageCounter += p_70097_2_ * 2;
			if (damageCounter > TELEPORT_THRESHOLD && !this.worldObj.isRemote) {
				if (rand.nextInt(3) == 0) {
					damageCounter = 0;
				} else if (this.teleportRandomly()) {
					damageCounter = 0;
				} else {
				}
			}
		}
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


		if (!this.worldObj.isRemote && this.isEntityAlive()) {
			if (disappearDelay > 0) {
				if (--disappearDelay == 1) {
					this.teleportAway();
				}
			}
			if (teleportDelay > 0) {
				teleportDelay--;
			}
			if (this.isInWater() || this.handleLavaMovement()) {
				this.teleportRandomly();
			}
			if (this.ticksExisted % 60 == 0) {
				//Logger.t("Damage %d",damageCounter);
				if (damageCounter > 30) {
					if (this.teleportRandomly()) {
						damageCounter = 0;
					} else {
					}
				} else {
					damageCounter=0;
				}
			}
			if (this.entityToAttack != null) {
				if (this.entityToAttack instanceof EntityPlayer) {
					if (getMinionHandler().getMinionCount() < 4 && rand.nextInt(80) == 0) {
						EntitySaveableVampireMinion entity = (EntitySaveableVampireMinion) Helper.spawnEntityBehindEntity((EntityLivingBase) this.entityToAttack, REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME);
						if (entity != null) {
							entity.setLord(this);
							entity.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 20000, 2));
							minionInHomeDist(entity);
						}
					}

				}
			}

			if (this.ticksExisted % 100 == 0 && this.hasHome() && this.getMinionHandler().getMinionCount() < 1) {
				EntityVampireMinion minion = (EntityVampireMinion) Helper.spawnEntityInWorld(worldObj, castle, REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME, 3);
				if (minion != null) {
					minion.setLord(this);
					minion.addPotionEffect(new PotionEffect(Potion.resistance.id, 20000, 3));
				}
			}
		}

		if (this.getHealth() > DraculaAIHeal.THRESHOLD * 2 && BALANCE.MOBPROP.DRACULA_REGENERATE_SECS >= 0 && this.ticksExisted % (BALANCE.MOBPROP.DRACULA_REGENERATE_SECS * 20) == 0 && (this.getLastAttackerTime() == 0 || this.getLastAttackerTime() - ticksExisted > 100)) {
			this.heal(5F);
		}

		if (!this.worldObj.isRemote && this.isGettingSundamage()) {
			this.makeDisappear();
		}
		super.onLivingUpdate();
	}

	private boolean minionInHomeDist(EntityCreature minion) {
		if (this.hasHome()) {

			if (isWithinHomeDistance(MathHelper.floor_double(minion.posX), MathHelper.floor_double(minion.posY), MathHelper.floor_double(minion.posZ))) {
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
		if (teleportDelay > 0) return false;
		double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceX;
		double d1 = this.posY + (this.rand.nextInt((int) (maxTeleportDistanceY * 1.5)) - maxTeleportDistanceY * 0.5D);
		double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceZ;
		if(this.isWithinHomeDistance(MathHelper.floor_double(d0),MathHelper.floor_double(d1),MathHelper.floor_double(d2))){
			if(Helper.teleportTo(this, d0, d1, d2, true)){
				if (rand.nextInt(10) == 0) {
					this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 60));
					this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 60, 2));
					this.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 2));
					summonBats();
				}
				teleportDelay = TELEPORT_DELAY;
				return true;
			}
		}
		if (rand.nextBoolean()) {
			this.teleportRandomly();
		}
		return false;
	}

//	/** Teleports dracula to the given entity */
//	private boolean teleportToEntity(Entity e) {
//		Vec3 vec3 = Vec3.createVectorHelper(this.posX - e.posX, this.boundingBox.minY + this.height / 2.0F - e.posY + e.getEyeHeight(), this.posZ - e.posZ);
//		vec3 = vec3.normalize();
//		double d0 = 16.0D;
//		double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
//		double d2 = this.posY + (this.rand.nextInt(16) - 8) - vec3.yCoord * d0;
//		double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;
//		return Helper.teleportTo(this, d1, d2, d3, true);
//	}

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
		ChunkCoordIntPair uc = pos.getUpperMainCastle();
		this.castle = AxisAlignedBB.getBoundingBox(lc.chunkXPos << 4, pos.getHeight() - 1, lc.chunkZPos << 4, (uc.chunkXPos << 4) + 15, pos.getHeight() + 5, (uc.chunkZPos << 4) + 15);
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
			if (o instanceof EntityBlindingBat) continue;
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
