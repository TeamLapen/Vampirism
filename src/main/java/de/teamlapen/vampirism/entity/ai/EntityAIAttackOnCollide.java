package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * No use atm, use vanilla instead
 * Clone of vanilla {@link net.minecraft.entity.ai.EntityAIAttackOnCollide} with a few adjustments and compatible with {@link EntityVampirism}
 */
@Deprecated
public class EntityAIAttackOnCollide extends EntityAIBase {

    World worldObj;

    final EntityVampirism attacker;

    int attackTick;

    final boolean longMemory;

    final Class classTarget;

    PathEntity pathEntity;

    private int tick1;
    private double oldTargetX, oldTargetY, oldTargetZ;

    private int failedPathFindingPenalty;

    public EntityAIAttackOnCollide(EntityVampirism attacker, Class classTarget, boolean longMemory) {
        this.attacker = attacker;
        this.classTarget = classTarget;
        this.longMemory = longMemory;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isEntityAlive()) {
            return false;
        } else if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())) {
            return false;
        } else {
            if (--this.tick1 <= 0) {
                this.pathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
                this.tick1 = 4 + this.attacker.getRNG().nextInt(7);
                return this.pathEntity != null;
            } else {
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY), MathHelper.floor_double(entitylivingbase.posZ))));
    }

    public void startExecuting() {
        this.attacker.getNavigator().setPath(this.pathEntity, 1.0F);
        this.tick1 = 0;
    }

    public void resetTask() {
        this.attacker.getNavigator().clearPathEntity();
    }


    public void updateTask() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
        double d1 = (double) (this.attacker.width * 2.0F * this.attacker.width * 2.0F + entitylivingbase.width);
        --this.tick1;

        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.tick1 <= 0 && (this.oldTargetX == 0.0D && this.oldTargetY == 0.0D && this.oldTargetZ == 0.0D || entitylivingbase.getDistanceSq(this.oldTargetX, this.oldTargetY, this.oldTargetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F)) {
            this.oldTargetX = entitylivingbase.posX;
            this.oldTargetY = entitylivingbase.boundingBox.minY;
            this.oldTargetZ = entitylivingbase.posZ;
            this.tick1 = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

            if (this.attacker.getNavigator().getPath() != null) {
                PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
                    failedPathFindingPenalty = 0;
                } else {
                    failedPathFindingPenalty += 10;
                }
            } else {
                failedPathFindingPenalty += 10;
            }

            if (d0 > 1024.0D) {
                this.tick1 += 10;
            } else if (d0 > 256.0D) {
                this.tick1 += 5;
            }

            if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, 1.0F)) {
                this.tick1 += 15;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);

        if (d0 <= d1 && this.attackTick <= 20) {
            this.attackTick = 20;

            if (this.attacker.getHeldItem() != null) {
                this.attacker.swingItem();
            }

            this.attacker.attackEntityAsMob(entitylivingbase);
        }
    }
}
