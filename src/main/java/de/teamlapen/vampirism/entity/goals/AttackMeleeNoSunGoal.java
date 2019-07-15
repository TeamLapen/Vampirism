package de.teamlapen.vampirism.entity.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Checks paths of {@link MeleeAttackGoal} for sunny parts.
 */
public class AttackMeleeNoSunGoal extends MeleeAttackGoal {


    public AttackMeleeNoSunGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    public boolean shouldExecute() {
        boolean flag = super.shouldExecute();
        if (flag) {
            LivingEntity entitylivingbase = this.attacker.getAttackTarget();
            if (entitylivingbase != null) {
                double distance = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getBoundingBox().minY, entitylivingbase.posZ);
                if (distance <= this.getAttackReachSqr(entitylivingbase)) {
                    return true;
                }
            }
            boolean avoidSun = false;
            if (attacker.getNavigator() instanceof GroundPathNavigator) {
                avoidSun = ((GroundPathNavigator) attacker.getNavigator()).shouldAvoidSun;
            }

            if (avoidSun) {

                Path path = this.path;
                if (attacker.getEntityWorld().canSeeSky(new BlockPos(MathHelper.floor(this.attacker.posX), (int) (this.attacker.getBoundingBox().minY + 0.5D), MathHelper.floor(this.attacker.posZ)))) {
                    return false;
                }

                for (int j = 0; j < path.getCurrentPathLength(); ++j) {
                    PathPoint pathpoint2 = path.getPathPointFromIndex(j);

                    if (this.attacker.getEntityWorld().canSeeSky(new BlockPos(pathpoint2.x, pathpoint2.y, pathpoint2.z))) {
                        path.setCurrentPathLength(j - 1);
                        return path.getCurrentPathLength() > 1;
                    }

                }

            }

        }
        return flag;
    }
}
