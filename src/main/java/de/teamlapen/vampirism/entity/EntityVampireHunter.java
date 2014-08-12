package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.MobProperties;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityVampireHunter extends BasicMob {

	public EntityVampireHunter(World p_i1738_1_) {
		super(p_i1738_1_);
		
		this.applyEntityAttributes();
		
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this,EntityVampire.class,2*MobProperties.vampireHunter_movementSpeed,false));
	}
	
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MobProperties.vampire_maxHealth);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(MobProperties.vampireHunter_attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MobProperties.vampireHunter_movementSpeed);
	}
	@Override
	protected Item getDropItem(){
		return null;
		
	}

}
