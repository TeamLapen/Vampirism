package de.teamlapen.vampirism.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * 
 * @author WILLIAM
 *
 */
public class EntityGhost extends EntityMob {
	public EntityGhost(World world) {
		super(world);
		this.getNavigator().setCanSwim(true);
		this.experienceValue = 20;
		this.setSize(0.8F, 2.0F);
		this.clearAITasks();
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(2, new EntityAIFleeSun(this, 0.9F));
		this.tasks.addTask(5, new EntityAISwimming(this));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
	}

	/**
	 * Entity becomes invisible (5 sec) after being damaged.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float par2) {
		if (!super.attackEntityFrom(source, par2)) {
			return false;
		} else {
			addPotionEffect(new PotionEffect(Potion.invisibility.id, 20 * 5, 1));
		}
		return true;
	}

	@Override
	public boolean canDespawn() {
		return true;
	}

	/**
	 * Clears previous AI Tasks, so the ones defined above will actually perform.
	 */
	protected void clearAITasks() {
		tasks.taskEntries.clear();
		targetTasks.taskEntries.clear();
	}

	@Override
	protected void dropFewItems(boolean par1, int par2) {
//		int dropChance1 = this.rand.nextInt(10);
//		if (dropChance1 == 0) {
//			this.dropItem(ModItems.bloodBottle, 1);
//		}
//
//		int dropChance2 = this.rand.nextInt(5);
//		if (dropChance2 == 0) {
//			this.dropItem(ModItems.humanHeart, 1);
//		}
	}

	/**
	 * Testing purposes. Entities are having trouble attacking players; hopefully this will help out.
	 */
	@Override
	protected Entity findPlayerToAttack() {
		EntityPlayer entityplayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, 24.0D);
		return entityplayer != null && this.canEntityBeSeen(entityplayer) ? entityplayer : null;
	}

	/**
	 * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block. Args: x, y, z
	 */
	@Override
	public float getBlockPathWeight(int p_70783_1_, int p_70783_2_, int p_70783_3_) {
		return 0.1F;
		// return 0.5F - this.worldObj.getLightBrightness(p_70783_1_, p_70783_2_, p_70783_3_);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	/**
	 * Ghost do not make any step sounds
	 * @param p_145780_1_
	 * @param p_145780_2_
	 * @param p_145780_3_
	 * @param p_145780_4_
	 */
	@Override
	protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_){
		return;
	}
}
