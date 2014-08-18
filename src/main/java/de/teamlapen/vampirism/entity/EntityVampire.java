package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.MobProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;


public class EntityVampire extends EntityZombie {
	//TODO Sounds

	public EntityVampire(World par1World) {
		super(par1World);

		// Avoids Vampire Hunters TODO Distance (3rd argument)
		this.tasks.addTask(1, new EntityAIAvoidEntity(this,
				EntityVampireHunter.class, 10.0F,
				MobProperties.vampire_movementSpeed,
				MobProperties.vampire_movementSpeed * 1.5));
		// Attacks player
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this,
				EntityPlayer.class, MobProperties.vampire_movementSpeed * 2,
				true));
		// Attacks villagers
		this.tasks.addTask(3, new EntityAIAttackOnCollide(this,
				EntityVillager.class,
				MobProperties.vampire_movementSpeed * 1.5, true));
		// Moves through villages
		this.tasks
				.addTask(4, new EntityAIMoveThroughVillage(this, 1.0D, false));
		// Avoids sun
		this.tasks.addTask(10, new EntityAIFleeSun(this,
				MobProperties.vampire_movementSpeed * 1.5));
	}
	
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setBaseValue(MobProperties.vampire_maxHealth);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
				.setBaseValue(MobProperties.vampire_attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setBaseValue(MobProperties.vampire_movementSpeed);
	}


	
	//Overrides methods that are not needed, but declared in EntityZombie.class
	@Override
	protected void convertToVillager() {}
}
