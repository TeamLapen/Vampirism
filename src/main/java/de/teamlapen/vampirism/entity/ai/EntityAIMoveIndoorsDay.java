package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

/**
 * Same as vanilla {@link EntityAIMoveIndoors}, but executes during day and not night
 */
public class EntityAIMoveIndoorsDay extends EntityAIBase {
    private final EntityCreature entityObj;
    private VillageDoorInfo doorInfo;
    private int insidePosX = -1;
    private int insidePosZ = -1;

    public EntityAIMoveIndoorsDay(EntityCreature entityObjIn) {
        this.entityObj = entityObjIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return !this.entityObj.getNavigator().noPath();
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
        this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
        this.doorInfo = null;
    }

    @Override
    public boolean shouldExecute() {
        BlockPos blockpos = new BlockPos(this.entityObj);

        if (this.entityObj.getEntityWorld().isDaytime() && this.entityObj.getEntityWorld().provider.hasSkyLight()) {
            if (this.entityObj.getRNG().nextInt(50) != 0) {
                return false;
            } else if (this.insidePosX != -1 && this.entityObj.getDistanceSq((double) this.insidePosX, this.entityObj.posY, (double) this.insidePosZ) < 4.0D) {
                return false;
            } else {
                Village village = this.entityObj.getEntityWorld().getVillageCollection().getNearestVillage(blockpos, 14);

                if (village == null) {
                    return false;
                } else {
                    this.doorInfo = village.getDoorInfo(blockpos);
                    return this.doorInfo != null;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.insidePosX = -1;
        BlockPos blockpos = this.doorInfo.getInsideBlockPos();
        int i = blockpos.getX();
        int j = blockpos.getY();
        int k = blockpos.getZ();

        if (this.entityObj.getDistanceSq(blockpos) > 256.0D) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, new Vec3d((double) i + 0.5D, (double) j, (double) k + 0.5D));

            if (vec3d != null) {
                this.entityObj.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            }
        } else {
            this.entityObj.getNavigator().tryMoveToXYZ((double) i + 0.5D, (double) j, (double) k + 0.5D, 1.0D);
        }
    }
}
