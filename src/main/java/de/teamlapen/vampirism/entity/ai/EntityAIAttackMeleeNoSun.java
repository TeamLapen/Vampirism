package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Checks paths of {@link EntityAIAttackMelee} for sunny parts. Uses reflection twice each tick TODO
 */
public class EntityAIAttackMeleeNoSun extends EntityAIAttackMelee {


    public EntityAIAttackMeleeNoSun(EntityCreature creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    public boolean shouldExecute() {
        boolean flag = super.shouldExecute();
        if (flag) {
            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
            if (entitylivingbase != null) {
                double distance = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
                if (distance <= this.getAttackReachSqr(entitylivingbase)) {
                    return true;
                }
            }
            boolean avoidSun = false;
            if (attacker.getNavigator() instanceof PathNavigateGround) {
                avoidSun = ((PathNavigateGround) attacker.getNavigator()).shouldAvoidSun;
            }

            if (avoidSun) {

                Path path = this.entityPathEntity;
                    if (attacker.getEntityWorld().canSeeSky(new BlockPos(MathHelper.floor(this.attacker.posX), (int) (this.attacker.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.attacker.posZ)))) {
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
