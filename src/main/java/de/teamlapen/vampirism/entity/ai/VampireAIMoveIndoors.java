package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampireBase;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

/**
 * Had to simply copy {@link EntityAIMoveIndoors} since all variables are private
 */
public class VampireAIMoveIndoors extends EntityAIBase {
    private final EntityVampireBase vampire;
    private VillageDoorInfo doorInfo;
    private int insidePosX = -1;
    private int insidePosZ = -1;

    public VampireAIMoveIndoors(EntityVampireBase p_i1637_1_) {
        vampire = p_i1637_1_;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos pos=vampire.getPosition();
        if ((this.vampire.worldObj.isDaytime() || this.vampire.worldObj.isRaining()) && !this.vampire.worldObj.provider.getHasNoSky()) {
            if (this.vampire.getRNG().nextInt(50) != 0) {
                return false;
            } else if (this.insidePosX != -1 && this.vampire.getDistanceSq((double) this.insidePosX, this.vampire.posY, (double) this.insidePosZ) < 4.0D) {
                return false;
            } else {
                Village village = this.vampire.worldObj.villageCollectionObj.getNearestVillage(pos, 14);

                if (village == null) {
                    return false;
                } else {
                    this.doorInfo = village.getNearestDoor(pos);
                    return this.doorInfo != null;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return !this.vampire.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.insidePosX = -1;
        BlockPos pos=this.doorInfo.getInsideBlockPos();
        if (this.vampire.getDistanceSq(pos) > 256.0D) {
            Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.vampire, 14, 3, new Vec3((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D));

            if (vec3 != null) {
                this.vampire.getNavigator().tryMoveToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord, 1.0D);
            }
        } else {
            this.vampire.getNavigator().tryMoveToXYZ((double) pos.getX()+ 0.5D, (double) pos.getY(),(double) pos.getZ() + 0.5D, 1.0D);
        }
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        Logger.t("Stop move indoors");
        this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
        this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
        this.doorInfo = null;
    }
}
