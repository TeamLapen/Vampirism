package de.teamlapen.vampirism.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

/**
 * Just a simple dummy class, which makes it possible to spawn bitten mobs
 */
public class EntityDummyBittenAnimal extends EntityLiving {
	public EntityDummyBittenAnimal(World p_i1595_1_) {
		super(p_i1595_1_);
	}

	@Override public void onEntityUpdate() {
		String entity;
		int rand=this.rand.nextInt(3);
		switch (rand){
		case 0:entity="Pig";break;
		case 1:entity="Sheep";break;
		default:entity="Cow";
		}
		Entity entity1= EntityList.createEntityByName(entity,worldObj);
		if(entity1!=null){
			entity1.copyLocationAndAnglesFrom(this);
			VampireMob.get((EntityCreature) entity1).makeVampire();
			worldObj.spawnEntityInWorld(entity1);
		}
		this.setDead();

	}
}
