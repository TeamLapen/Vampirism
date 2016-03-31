package de.teamlapen.vampirism.entity.minions.ai;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MinionAIFollowLord extends EntityAIBase {
    /**
     * Min dist for execution
     */
    private final int MINDIST = 200;
    /**
     * Min dist for teleport
     */
    private final int TELEPORT_DIST = 2500;
    /**
     * The child that is following its parent.
     */
    IMinion minion;
    EntityCreature minionEntity;
    IMinionLord boss;
    double speed;
    private int timer;

    public MinionAIFollowLord(IMinion minion, double speed) {
        this.minion = minion;
        minionEntity = MinionHelper.entity(minion);
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
            double d0 = this.boss.getTheDistanceSquared(minionEntity);
            return d0 >= MINDIST;
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
            double d0 = this.boss.getTheDistanceSquared(minionEntity);
            return d0 >= MINDIST;
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
            minionEntity.getNavigator().tryMoveToEntityLiving(this.boss.getRepresentingEntity(), this.speed);
            if (this.minionEntity.getDistanceSqToEntity(boss.getRepresentingEntity()) > TELEPORT_DIST) {
                EntityLivingBase lord = boss.getRepresentingEntity();
                BlockPos pos = lord.getPosition().add(-4, 0, -4);


                for (int dx = 0; dx <= 4; ++dx) {
                    for (int dz = 0; dz <= 4; ++dz) {
                        BlockPos pos1 = pos.add(dx, 0, dz);
                        if ((dx < 1 || dz < 1 || dx > 3 || dz > 3) && World.doesBlockHaveSolidTopSurface(lord.worldObj, pos1.down())
                                && !lord.worldObj.getBlockState(pos1).getBlock().isNormalCube() && !lord.worldObj.getBlockState(pos.up()).getBlock().isNormalCube()) {
                            minionEntity.setLocationAndAngles(pos1.getX() + 0.5F, pos1.getY() + 0.1, pos1.getZ() + 0.5F,
                                    MathHelper.wrapAngleTo180_float(lord.rotationYaw + 180F), MathHelper.wrapAngleTo180_float(lord.rotationPitch + 180F));
                            minionEntity.getNavigator().clearPathEntity();
                            return;
                        }
                    }
                }
            }
        }
    }
}