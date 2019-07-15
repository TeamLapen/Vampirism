/*
package de.teamlapen.vampirism.entity.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.gen.feature.Feature;

import java.util.EnumSet;

*/
/**
 * Same as vanilla {@link EntityAIMoveIndoors}, but executes during day and not night
 * <p>
 * Returns whether an in-progress EntityAIBase should continue executing
 * <p>
 * Resets the task
 * <p>
 * Execute a one shot task or start executing a continuous task
 * <p>
 * Returns whether an in-progress EntityAIBase should continue executing
 * <p>
 * Resets the task
 * <p>
 * Execute a one shot task or start executing a continuous task
 *//*

public class MoveIndoorsDayGoal extends Goal {
    private final CreatureEntity entityObj;
    private VillageDoorInfo doorInfo;
    private int insidePosX = -1;
    private int insidePosZ = -1;

    public MoveIndoorsDayGoal(CreatureEntity entityObjIn) {
        this.entityObj = entityObjIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    */
/**
 * Returns whether an in-progress EntityAIBase should continue executing
 *//*

    public boolean continueExecuting() {
        return !this.entityObj.getNavigator().noPath();
    }

    */
/**
 * Resets the task
 *//*

    public void resetTask() {
        this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
        this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
        this.doorInfo = null;
    }

    @Override
    public boolean shouldExecute() {
        BlockPos blockpos = new BlockPos(this.entityObj);

        if (this.entityObj.getEntityWorld().isDaytime() && this.entityObj.getEntityWorld().dimension.hasSkyLight()) {
            if (this.entityObj.getRNG().nextInt(50) != 0) {
                return false;
            } else if (this.insidePosX != -1 && this.entityObj.getDistanceSq((double) this.insidePosX, this.entityObj.posY, (double) this.insidePosZ) < 4.0D) {
                return false;
            } else {
                entityObj.getEntityWorld().findNearestStructure("Village",entityObj.getPosition(), 2,true);
                Feature.VILLAGE.isPositionInStructure()
            }
        } else {
            return false;
        }
    }

    */
/**
 * Execute a one shot task or start executing a continuous task
 *//*

    public void startExecuting() {
        this.insidePosX = -1;
        Vec3d pos = new Vec3d(this.doorInfo.getInsideBlockPos());

        if (this.entityObj.getDistanceSq(pos) > 256.0D) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, pos.add(0.5D,0,0.5D));

            if (vec3d != null) {
                this.entityObj.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            }
        } else {
            this.entityObj.getNavigator().tryMoveToXYZ(pos.x + 0.5D, pos.y, pos.z + 0.5D, 1.0D);
        }
    }
}
*/
