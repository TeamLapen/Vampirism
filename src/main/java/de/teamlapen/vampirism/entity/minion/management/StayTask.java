package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.DefaultMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;


public class StayTask extends DefaultMinionTask<StayTask.Desc> {


    @Nullable
    @Override
    public Desc activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, IMinionInventory inventory) {
        BlockPos pos = minion != null ? minion.getRepresentingEntity().getPosition() : (lord != null ? lord.getPosition() : null);
        return pos == null ? null : new Desc(pos);
    }

    @Override
    public void deactivateTask(Desc desc) {

    }

    @Override
    public Desc readFromNBT(CompoundNBT nbt) {
        BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("pos"));
        return new Desc(pos);
    }

    public static class Desc implements IMinionTask.IMinionTaskDesc {
        public final BlockPos position;

        public Desc(BlockPos pos) {
            this.position = pos;
        }


        @Override
        public IMinionTask<?> getTask() {
            return MinionTasks.stay;
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            nbt.put("pos", NBTUtil.writeBlockPos(position));
        }
    }

}
