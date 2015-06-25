package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.IMinionLord;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowBoss extends EntityAIBase {
	/** The child that is following its parent. */
	IMinion minion;
	IMinionLord boss;
	double speed;
	private int timer;
	/**
	 * Min dist for execution
	 */
	private final int MINDIST = 200;
	/**
	 * Max dist for execution
	 */
	private final int MAXDIST = 600;
	
	/**
	 * Min dist for teleport
	 */
	private final int TELEPORT_DIST = 2500;

	public EntityAIFollowBoss(IMinion minion, double speed) {
		this.minion = minion;
		this.speed = speed;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting() {
		if (!this.boss.isTheEntityAlive()) {
			boss = null;
			return false;
		} else {
			double d0 = this.boss.getTheDistanceSquared(minion.getRepresentingEntity());
			return d0 >= MINDIST && d0 <= MAXDIST;
		}
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		this.boss = null;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		boss = minion.getLord();
		if (boss == null) {
			return false;
		} else {
			double d0 = this.boss.getTheDistanceSquared(minion.getRepresentingEntity());
			return d0 >= MINDIST && d0 <= MAXDIST;
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.timer = 0;
	}

	/**
	 * Updates the task
	 */
	@Override
	public void updateTask() {
		if (--this.timer <= 0) {
			this.timer = 10;
			minion.getRepresentingEntity().getNavigator().tryMoveToEntityLiving(this.boss.getRepresentingEntity(), this.speed);
			if(this.minion.getRepresentingEntity().getDistanceSqToEntity(boss.getRepresentingEntity())>TELEPORT_DIST){
				EntityLivingBase lord=boss.getRepresentingEntity();
				int x=MathHelper.floor_double(lord.posX)-4;
				int z=MathHelper.floor_double(lord.posZ)-4;
				int y=MathHelper.floor_double(lord.boundingBox.minY);
				
				for (int dx = 0; dx <= 4; ++dx)
                {
                    for (int dz = 0; dz <= 4; ++dz)
                    {
                        if ((dx < 1 || dz < 1 || dx > 3 || dz > 3) && World.doesBlockHaveSolidTopSurface(lord.worldObj, x + dx, y - 1, z + dz) && !lord.worldObj.getBlock(x + dx, y, z + dz).isNormalCube() && !lord.worldObj.getBlock(x + dx, y + 1, z + dz).isNormalCube())
                        {
                            minion.getRepresentingEntity().setLocationAndAngles((double)((float)(x + dx) + 0.5F), (double)y, (double)((float)(z + dz) + 0.5F), MathHelper.wrapAngleTo180_float(lord.rotationYaw+180F), MathHelper.wrapAngleTo180_float(lord.rotationPitch+180F));
                            minion.getRepresentingEntity().getNavigator().clearPathEntity();
                            return;
                        }
                    }
                }
			}
		}
	}
}
