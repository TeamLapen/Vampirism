package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;


public class StayTask extends DefaultMinionTask<StayTask.Desc, MinionData> {


    @Nullable
    @Override
    public Desc activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, MinionData inventory) {
        this.triggerAdvancements(lord);
        BlockPos pos = minion != null ? minion.getRepresentingEntity().blockPosition() : (lord != null ? lord.blockPosition() : null);
        return pos == null ? null : new Desc(pos);
    }

    @Override
    public void deactivateTask(Desc desc) {

    }

    @Override
    public Desc readFromNBT(CompoundTag nbt) {
        BlockPos pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
        return new Desc(pos);
    }

    public static class Desc implements IMinionTask.IMinionTaskDesc<MinionData> {
        public final BlockPos position;

        public Desc(BlockPos pos) {
            this.position = pos;
        }


        @Override
        public IMinionTask<?, MinionData> getTask() {
            return MinionTasks.STAY.get();
        }

        @Override
        public void writeToNBT(CompoundTag nbt) {
            nbt.put("pos", NbtUtils.writeBlockPos(position));
        }
    }

}
