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
    public boolean canUse() {
        boolean flag = super.canUse();
        if (flag) {
            LivingEntity entitylivingbase = this.mob.getTarget();
            if (entitylivingbase != null) {
                double distance = this.mob.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
                if (distance <= this.getAttackReachSqr(entitylivingbase)) {
                    return true;
                }
            }
            boolean avoidSun = false;
            if (mob.getNavigation() instanceof GroundPathNavigator) {
                avoidSun = ((GroundPathNavigator) mob.getNavigation()).avoidSun;
            }

            if (avoidSun) {

                Path path = this.path;
                if (mob.getCommandSenderWorld().canSeeSkyFromBelowWater(new BlockPos(MathHelper.floor(this.mob.getX()), (int) (this.mob.getBoundingBox().minY + 0.5D), MathHelper.floor(this.mob.getZ())))) {
                    return false;
                }

                for (int j = 0; j < path.getNodeCount(); ++j) {
                    PathPoint pathpoint2 = path.getNode(j);

                    if (this.mob.getCommandSenderWorld().canSeeSkyFromBelowWater(new BlockPos(pathpoint2.x, pathpoint2.y, pathpoint2.z))) {
                        path.truncateNodes(Math.max(j - 1, 0));
                        return path.getNodeCount() > 1;
                    }

                }

            }

        }
        return flag;
    }
}
