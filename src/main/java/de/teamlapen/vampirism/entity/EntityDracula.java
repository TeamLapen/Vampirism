package de.teamlapen.vampirism.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import de.teamlapen.vampirism.util.BALANCE;
/**@author Mistadon */
public class EntityDracula extends EntityVampire implements IBossDisplayData {
	// TODO Sounds

	private static final int DISAPPEAR_DELAY = 200;
	private int teleportDelay;
	private int disappearDelay;
	private final int maxTeleportDelay = 30;
	private final int maxTeleportDistanceX = 16;
	private final int maxTeleportDistanceY = 16;
	private final int maxTeleportDistanceZ = 16;

	public EntityDracula(World par1World) {
		super(par1World);

		this.tasks.addTask(3, new EntityAIAvoidEntity(this,
				EntityVampireHunter.class,
				BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.2));

		this.tasks.addTask(6, new EntityAIWander(this, 0.7));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this,
				EntityPlayer.class, 0, true));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setBaseValue(BALANCE.MOBPROP.DRACULA_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
				.setBaseValue(BALANCE.MOBPROP.DRACULA_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setBaseValue(BALANCE.MOBPROP.DRACULA_MOVEMENT_SPEED);
	}

	@Override
	public void onLivingUpdate() {
		if (this.worldObj.isDaytime() && !this.worldObj.isRemote) {
			float f = this.getBrightness(1.0F);

			if (f > 0.5F
					&& this.worldObj.canBlockSeeTheSky(
							MathHelper.floor_double(this.posX),
							MathHelper.floor_double(this.posY),
							MathHelper.floor_double(this.posZ))
					&& this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				this.entityToAttack = null;
				this.teleportRandomly();
			}
		}

		if (this.isBurning()) {
			this.entityToAttack = null;
			this.teleportRandomly();
		}

		this.isJumping = false;

		if (this.entityToAttack != null)
			this.faceEntity(this.entityToAttack, 100.0F, 100.0F);

		if (!this.worldObj.isRemote && this.isEntityAlive()) {
			if(disappearDelay>0){
				if(--disappearDelay==1){
					this.teleportAway();
				}
			}
			if (this.entityToAttack != null) {
				if (this.entityToAttack instanceof EntityPlayer
						&& this.shouldAttackPlayer((EntityPlayer) this.entityToAttack)) {
					if (this.entityToAttack.getDistanceSqToEntity(this) < 16.0D)
						this.teleportRandomly();
					this.teleportDelay = 0;
					
				} else if (this.entityToAttack.getDistanceSqToEntity(this) > 256.0D
						&& this.teleportDelay++ >= maxTeleportDelay
						&& this.teleportToEntity(this.entityToAttack)) {
					this.teleportDelay = 0;
				}
			} else
				this.teleportDelay = 0;
		}

		super.onLivingUpdate();
	}

	/** Teleports dracula to the given entity */
	private boolean teleportToEntity(Entity e) {
		Vec3 vec3 = Vec3.createVectorHelper(
				this.posX - e.posX,
				this.boundingBox.minY + this.height / 2.0F - e.posY
						+ e.getEyeHeight(), this.posZ - e.posZ);
		vec3 = vec3.normalize();
		double d0 = 16.0D;
		double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D
				- vec3.xCoord * d0;
		double d2 = this.posY + (this.rand.nextInt(16) - 8) - vec3.yCoord * d0;
		double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D
				- vec3.zCoord * d0;
		return this.teleportTo(d1, d2, d3);
	}

	/** Teleports dracula randomly */
	private boolean teleportRandomly() {
		double d0 = this.posX + (this.rand.nextDouble() - 0.5D)
				* maxTeleportDistanceX;
		double d1 = this.posY
				+ (this.rand.nextInt(maxTeleportDistanceY) - maxTeleportDistanceY * 0.5D);
		double d2 = this.posZ + (this.rand.nextDouble() - 0.5D)
				* maxTeleportDistanceZ;
		return this.teleportTo(d0, d1, d2);
	}

	/** Teleports dracula to the given coordinates */
	protected boolean teleportTo(double p_70825_1_, double p_70825_3_,
			double p_70825_5_) {
		EnderTeleportEvent event = new EnderTeleportEvent(this, p_70825_1_,
				p_70825_3_, p_70825_5_, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		double d3 = this.posX;
		double d4 = this.posY;
		double d5 = this.posZ;
		this.posX = event.targetX;
		this.posY = event.targetY;
		this.posZ = event.targetZ;
		boolean flag = false;
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.posY);
		int k = MathHelper.floor_double(this.posZ);

		if (this.worldObj.blockExists(i, j, k)) {
			boolean flag1 = false;

			while (!flag1 && j > 0) {
				Block block = this.worldObj.getBlock(i, j - 1, k);
				if (block.getMaterial().blocksMovement())
					flag1 = true;
				else {
					--this.posY;
					--j;
				}
			}

			if (flag1) {
				this.setPosition(this.posX, this.posY, this.posZ);

				if (this.worldObj.getCollidingBoundingBoxes(this,
						this.boundingBox).isEmpty()
						&& !this.worldObj.isAnyLiquid(this.boundingBox))
					flag = true;
			}
		}

		if (!flag) {
			this.setPosition(d3, d4, d5);
			return false;
		} else {
			short short1 = 128;

			for (int l = 0; l < short1; ++l) {
				double d6 = l / (short1 - 1.0D);
				float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float f1 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				double d7 = d3 + (this.posX - d3) * d6
						+ (this.rand.nextDouble() - 0.5D) * this.width * 2.0D;
				double d8 = d4 + (this.posY - d4) * d6 + this.rand.nextDouble()
						* this.height;
				double d9 = d5 + (this.posZ - d5) * d6
						+ (this.rand.nextDouble() - 0.5D) * this.width * 2.0D;
				this.worldObj.spawnParticle("portal", d7, d8, d9, f, f1, f2);
			}

			// TODO different sound (bang?)
			this.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal",
					1.0F, 1.0F);
			this.playSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("ddelay", disappearDelay);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.disappearDelay=nbt.getInteger("ddelay");
	}

	/**
	 * Checks to see if dracula should be attacking this player
	 */
	private boolean shouldAttackPlayer(EntityPlayer p_70821_1_) {
		Vec3 vec3 = p_70821_1_.getLook(1.0F).normalize();
		Vec3 vec31 = Vec3.createVectorHelper(this.posX - p_70821_1_.posX,
				this.boundingBox.minY + this.height / 2.0F
						- (p_70821_1_.posY + p_70821_1_.getEyeHeight()),
				this.posZ - p_70821_1_.posZ);
		double d0 = vec31.lengthVector();
		vec31 = vec31.normalize();
		double d1 = vec3.dotProduct(vec31);
		return d1 > 1.0D - 0.025D / d0 && p_70821_1_.canEntityBeSeen(this);
	}
	
	/**
	 * Starts a countdown at whichs end the entity will be fake teleported and killed
	 */
	public void makeDisappear(){
		if(this.disappearDelay==0){
			this.disappearDelay=DISAPPEAR_DELAY;
		}
	}
}
