package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

import static de.teamlapen.vampirism.entity.minion.management.DefendAreaTask.Desc;


public class DefendAreaTask extends DefaultMinionTask<Desc, MinionData> {


    @Override
    public Desc activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, MinionData inventory) {
        this.triggerAdvancements(lord);
        BlockPos pos = minion != null ? minion.getRepresentingEntity().blockPosition() : (lord != null ? lord.blockPosition() : null);
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

    public static class Desc implements IMinionTask.IMinionTaskDesc<MinionData> {

        public final BlockPos center;
        public final int distance;

        public Desc(BlockPos center, int distance) {
            this.center = center;
            this.distance = distance;
        }

        @Override
        public IMinionTask<?, MinionData> getTask() {
            return MinionTasks.DEFEND_AREA.get();
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            nbt.put("center", NBTUtil.writeBlockPos(center));
            nbt.putInt("radius", distance);
        }
    }
}
