package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIStayHere extends EntityAIBase {

	private double posX, posY, posZ;
	private final EntityCreature creature;

	public EntityAIStayHere(EntityCreature creature) {
		this.creature = creature;
		this.posX = creature.posX;
		this.posY = creature.posY;
		this.posZ = creature.posZ;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}

	public void updatePosition(double x, double y, double z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}

	@Override
	public void updateTask() {
		if (creature.getDistanceSq(posX, posY, posZ) > 16) {
			creature.getNavigator().getPathToXYZ(posX, posY, posZ);
		}
	}

}
