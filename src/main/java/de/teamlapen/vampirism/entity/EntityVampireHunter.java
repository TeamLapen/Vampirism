package de.teamlapen.vampirism.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import de.teamlapen.vampirism.util.MobProperties;

public class EntityVampireHunter extends MobVampirism {

	public EntityVampireHunter(World p_i1738_1_) {
		super(p_i1738_1_);

		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVampire.class, 2 * MobProperties.vampireHunter_movementSpeed, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 2 * MobProperties.vampireHunter_movementSpeed, false));

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVampire.class, 0, true));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MobProperties.vampire_maxHealth);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(MobProperties.vampireHunter_attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MobProperties.vampireHunter_movementSpeed);
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

	@Override
	protected Item getDropItem() {
		return null;
	}

	@Override
	public void onDeath(DamageSource s) {
	}

	@Override
	protected void onDeathUpdate() {
		this.setInvisible(true);
		this.worldObj.spawnParticle("depthsuspend", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		this.worldObj.spawnParticle("mobSpellAmbient", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		super.onDeathUpdate();
	}

}
