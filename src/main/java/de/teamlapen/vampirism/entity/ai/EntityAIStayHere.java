package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIStayHere extends EntityAIBase {

	private double posX, posY, posZ;
	private final int maxDist;
	private final float yaw;
	private final EntityCreature creature;

	/**
	 *
	 * @param creature
	 * @param maxDist Maximal distance the creature is allowed to be away from the position
	 * @param yaw Yaw. -1 if not controlled
	 */
	public EntityAIStayHere(EntityCreature creature,int maxDist,float yaw) {
		this.creature = creature;
		this.posX = creature.posX;
		this.posY = creature.posY;
		this.posZ = creature.posZ;
		this.setMutexBits(1);
		this.maxDist=maxDist*maxDist;
		this.yaw=yaw;
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
		if (creature.getDistanceSq(posX, posY, posZ) > maxDist) {
			creature.getNavigator().getPathToXYZ(posX, posY, posZ);
		}
		if(yaw>=0){
			creature.setRotationYawHead(yaw);
		}
	}

}
