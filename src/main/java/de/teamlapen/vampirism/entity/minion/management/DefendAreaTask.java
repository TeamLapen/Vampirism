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

import static de.teamlapen.vampirism.entity.minion.management.DefendAreaTask.Desc;


public class DefendAreaTask extends DefaultMinionTask<Desc> {


    @Override
    public Desc activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, IMinionInventory inventory) {
        BlockPos pos = minion != null ? minion.getRepresentingEntity().getPosition() : (lord != null ? lord.getPosition() : null);
        return pos == null ? null : new Desc(pos, 10);
    }


    @Override
    public void deactivateTask(Desc desc) {

    }

    @Override
    public Desc readFromNBT(CompoundNBT nbt) {
        BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("center"));
        int dist = nbt.getInt("radius");
        return new Desc(pos, dist);
    }

    public static class Desc implements IMinionTask.IMinionTaskDesc {

        public final BlockPos center;
        public final int distance;

        public Desc(BlockPos center, int distance) {
            this.center = center;
            this.distance = distance;
        }

        @Override
        public IMinionTask<?> getTask() {
            return MinionTasks.defend_area;
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            nbt.put("center", NBTUtil.writeBlockPos(center));
            nbt.putInt("radius", distance);
        }
    }
}
