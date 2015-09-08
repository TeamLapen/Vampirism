package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.Helper18;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
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
		Helper18.setCanSwim(this,true);
		this.experienceValue = 20;
		this.setSize(0.8F, 2.0F);
		this.clearAITasks();
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(2, new EntityAIFleeSun(this, 0.9F));
		this.tasks.addTask(5, new EntityAISwimming(this));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, null));
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


	@Override
	public float func_180484_a(BlockPos pos) {
		return 0.1F;
	}


	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	/**
	 * Ghost do not make any step sounds
	 */
	@Override
	protected void playStepSound(BlockPos p_180429_1_, Block p_180429_2_) {
		return;
	}
}
