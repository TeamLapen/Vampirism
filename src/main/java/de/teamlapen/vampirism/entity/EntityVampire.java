package de.teamlapen.vampirism.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.util.BALANCE;

public class EntityVampire extends DefaultVampire {
	// TODO Sounds

	public EntityVampire(World par1World) {
		super(par1World);
		// Avoids Vampire Hunters
		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityVampireHunter.class, BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.2));
		// Low priority tasks
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 0.6, false));
		this.tasks.addTask(6, new EntityAIWander(this, 0.7));
		this.tasks.addTask(9, new EntityAILookIdle(this));

		// Search for players
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.addAttackingTargetTasks(2);

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
		if (s.getEntity() != null && s.getEntity() instanceof EntityPlayer) {
			this.dropItem(ModItems.vampireFang, 1);
		}
	}

}
