package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
            boolean avoidSun = true;
            try {
                if (attacker.getNavigator() instanceof PathNavigateGround) {
                    avoidSun = ReflectionHelper.getPrivateValue(PathNavigateGround.class, (PathNavigateGround) attacker.getNavigator(), "shouldAvoidSun", SRGNAMES.PathNavigateGround_shouldAvoidSun);
                }
            } catch (Exception e) {
                VampirismMod.log.e("AttackMeleeNoSun", e, "Failed to check for 'shouldAvoidSun'");
                avoidSun = false;
            }
            if (avoidSun) {
                try {

                    Path path = ReflectionHelper.getPrivateValue(EntityAIAttackMelee.class, this, "entityPathEntity", SRGNAMES.EntityAIAttackMelee_entityPathEntity);

                    if (attacker.worldObj.canSeeSky(new BlockPos(MathHelper.floor_double(this.attacker.posX), (int) (this.attacker.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(this.attacker.posZ)))) {
                        return false;
                    }

                    for (int j = 0; j < path.getCurrentPathLength(); ++j) {
                        PathPoint pathpoint2 = path.getPathPointFromIndex(j);

                        if (this.attacker.worldObj.canSeeSky(new BlockPos(pathpoint2.xCoord, pathpoint2.yCoord, pathpoint2.zCoord))) {
                            path.setCurrentPathLength(j - 1);
                            return path.getCurrentPathLength() > 1;
                        }

                    }
                } catch (Exception e) {
                    VampirismMod.log.e("AttackMeleeNoSun", e, "Failed to retrieve path from EntityAIAttackMelee");
                }
            }

        }
        return flag;
    }
}
