package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.mixin.accessor.GroundPathNavigationAccessor;
import de.teamlapen.vampirism.mixin.accessor.MeleeAttackGoalAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;

/**
 * Checks paths of {@link MeleeAttackGoal} for sunny parts.
 */
public class AttackMeleeNoSunGoal extends MeleeAttackGoal {


    public AttackMeleeNoSunGoal(@NotNull PathfinderMob creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    public boolean canUse() {
        boolean flag = super.canUse();
        if (flag) {
            LivingEntity entitylivingbase = this.mob.getTarget();
            if (entitylivingbase != null) {
                if (this.mob.isWithinMeleeAttackRange(entitylivingbase)) {
                    return true;
                }
            }
            boolean avoidSun = false;
            if (mob.getNavigation() instanceof GroundPathNavigation) {
                avoidSun = ((GroundPathNavigationAccessor) mob.getNavigation()).getAvoidSun();
            }

            if (avoidSun) {

                Path path = ((MeleeAttackGoalAccessor) this).getPath();
                if (mob.getCommandSenderWorld().canSeeSkyFromBelowWater(new BlockPos(Mth.floor(this.mob.getX()), (int) (this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getZ())))) {
                    return false;
                }

                for (int j = 0; j < path.getNodeCount(); ++j) {
                    Node pathpoint2 = path.getNode(j);

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
