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

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Checks paths of {@link EntityAIAttackMelee} for sunny parts. Uses reflection twice each tick TODO
 */
public class EntityAIAttackMeleeNoSun extends EntityAIAttackMelee {

    /**
     * Caches accessor for {@link PathNavigateGround#shouldAvoidSun}
     */
    private @Nullable
    Field field_shouldAvoidSun = null;
    /**
     * Caches accessor for {@link EntityAIAttackMelee#entityPathEntity}
     */
    private @Nullable
    Field field_entityPathEntity = null;

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
                    if (field_shouldAvoidSun == null) {
                        field_shouldAvoidSun = ReflectionHelper
                                .findField(PathNavigateGround.class, "shouldAvoidSun", SRGNAMES.PathNavigateGround_shouldAvoidSun);
                    }
                    avoidSun = (boolean) field_shouldAvoidSun.get(attacker.getNavigator());
                }
            } catch (Exception e) {
                VampirismMod.log.e("AttackMeleeNoSun", e, "Failed to check for 'shouldAvoidSun' (%s)", SRGNAMES.PathNavigateGround_shouldAvoidSun);
                avoidSun = false;
            }
            if (avoidSun) {
                try {
                    if (field_entityPathEntity == null) {
                        field_entityPathEntity = ReflectionHelper
                                .findField(EntityAIAttackMelee.class, "entityPathEntity", SRGNAMES.EntityAIAttackMelee_entityPathEntity);
                    }
                    Path path = (Path) field_entityPathEntity.get(this);
                    if (attacker.getEntityWorld().canSeeSky(new BlockPos(MathHelper.floor(this.attacker.posX), (int) (this.attacker.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.attacker.posZ)))) {
                        return false;
                    }

                    for (int j = 0; j < path.getCurrentPathLength(); ++j) {
                        PathPoint pathpoint2 = path.getPathPointFromIndex(j);

                        if (this.attacker.getEntityWorld().canSeeSky(new BlockPos(pathpoint2.xCoord, pathpoint2.yCoord, pathpoint2.zCoord))) {
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
