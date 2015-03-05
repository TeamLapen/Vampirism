package de.teamlapen.vampirism.entity;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.BALANCE;

public class EntityDracula extends EntityMob implements IBossDisplayData {
	// TODO Sounds

	private int teleportDelay;
	private final int maxTeleportDistanceX = 16;
	private final int maxTeleportDistanceY = 16;
	private final int maxTeleportDistanceZ = 16;

	public EntityDracula(World par1World) {
		super(par1World);

		this.getNavigator().setAvoidsWater(true);
		this.setSize(0.6F, 1.8F);

		// Attack player
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIBreakDoor(this));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this,
				EntityPlayer.class, 1.1, true));

		// Attack vampire hunter
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this,
				EntityVampireHunter.class, 1.0, true));

		// Attack villager
		this.tasks.addTask(3, new EntityAIAttackOnCollide(this,
				EntityVillager.class, 0.9, true));

		// Avoids Vampire Hunters
		this.tasks.addTask(3, new EntityAIAvoidEntity(this,
				EntityVampireHunter.class,
				BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.2));

		// Low priority tasks
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.tasks.addTask(6, new EntityAIWander(this, 0.7));
		this.tasks.addTask(9, new EntityAILookIdle(this));

		// Search for players
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
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
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			float brightness = this.getBrightness(1.0F);
			boolean canSeeSky = this.worldObj.canBlockSeeTheSky(
					MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.posY),
					MathHelper.floor_double(this.posZ));
			if (brightness > 0.5F) {
				if (this.worldObj.isDaytime() && canSeeSky) {
					this.attackEntityFrom(VampirismMod.sunDamage, 0.5F);
				}
			}
		}
		if (this.worldObj.isRemote)
			BossStatus.setBossStatus(this, true);

		if (!this.worldObj.isRemote && this.isEntityAlive()) {
			if (this.entityToAttack != null) {
				if (this.entityToAttack instanceof EntityPlayer) {
					if (this.entityToAttack.getDistanceSqToEntity(this) < 16.0D) {
						this.teleportRandomly();
					}

					this.teleportDelay = 0;
				} else if (this.entityToAttack.getDistanceSqToEntity(this) > 256.0D
						&& this.teleportDelay++ >= 30
						&& this.teleportToEntity(this.entityToAttack)) {
					this.teleportDelay = 0;
				}
			} else {
				this.teleportDelay = 0;
			}
		}

		super.onLivingUpdate();
	}

	/** Teleports dracula to the given entity */
	private boolean teleportToEntity(Entity e) {
		Vec3 vec3 = Vec3.createVectorHelper(this.posX - e.posX,
				this.boundingBox.minY + (double) (this.height / 2.0F) - e.posY
						+ (double) e.getEyeHeight(), this.posZ - e.posZ);
		vec3 = vec3.normalize();
		double d0 = 16.0D;
		double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D
				- vec3.xCoord * d0;
		double d2 = this.posY + (double) (this.rand.nextInt(16) - 8)
				- vec3.yCoord * d0;
		double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D
				- vec3.zCoord * d0;
		return this.teleportTo(d1, d2, d3);
	}

	/** Teleports dracula randomly */
	private boolean teleportRandomly() {
		double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceX;
		double d1 = this.posY + (double) (this.rand.nextInt(maxTeleportDistanceY) - maxTeleportDistanceY * 0.5D);
		double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * maxTeleportDistanceZ;
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
				double d6 = (double) l / ((double) short1 - 1.0D);
				float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float f1 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				double d7 = d3 + (this.posX - d3) * d6
						+ (this.rand.nextDouble() - 0.5D) * (double) this.width
						* 2.0D;
				double d8 = d4 + (this.posY - d4) * d6 + this.rand.nextDouble()
						* (double) this.height;
				double d9 = d5 + (this.posZ - d5) * d6
						+ (this.rand.nextDouble() - 0.5D) * (double) this.width
						* 2.0D;
				this.worldObj.spawnParticle("portal", d7, d8, d9, (double) f,
						(double) f1, (double) f2);
			}

			// TODO different sound (bang?)
			this.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal",
					1.0F, 1.0F);
			this.playSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}

}
