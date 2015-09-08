package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

/**
 * Avoid vampire player task.
 * 
 * @see EntityAIAvoidEntity for reference
 * @author Max
 *
 */
public class EntityAIAvoidVampirePlayer extends EntityAIBase {

	private EntityPlayer closestPlayer;
	private EntityCreature entity;
	private float distance;
	private double farSpeed;
	private double nearSpeed;
	private int avoidLevel;
	private PathNavigate entityPathNavigate;
	private PathEntity entityPathEntity;

	public EntityAIAvoidVampirePlayer(EntityCreature e, float minDist, double farSpeed, double nearSpeed, int avoidLevel) {
		this.distance = minDist;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		this.entityPathNavigate = e.getNavigator();
		this.avoidLevel = avoidLevel;
		entity = e;
		this.setMutexBits(1);
	}

	@Override
	public boolean continueExecuting() {
		return !this.entityPathNavigate.noPath();
	}

	@Override
	public void resetTask() {
		this.closestPlayer = null;
	}

	@Override
	public boolean shouldExecute() {
		this.closestPlayer = this.entity.worldObj.getClosestPlayerToEntity(entity, this.distance);
		if (this.closestPlayer == null) {
			return false;
		}
		VampirePlayer p = VampirePlayer.get(closestPlayer);
		if (p.getLevel() < avoidLevel) {
			return false;
		}
		Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vec3(this.closestPlayer.posX, this.closestPlayer.posY, this.closestPlayer.posZ));

		if (vec3 == null) {
			return false;
		} else if (this.closestPlayer.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestPlayer.getDistanceSqToEntity(this.entity)) {
			return false;
		} else {
			this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
			return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3);
		}
	}

	@Override
	public void startExecuting() {
		this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
	}

	@Override
	public void updateTask() {
		if (this.entity.getDistanceSqToEntity(this.closestPlayer) < 49.0D) {
			this.entity.getNavigator().setSpeed(this.nearSpeed);
		} else {
			this.entity.getNavigator().setSpeed(this.farSpeed);
		}
	}

}
