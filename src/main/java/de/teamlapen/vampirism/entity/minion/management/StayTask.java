package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class StayTask extends DefaultMinionTask<StayTask.Desc, MinionData> {


    @Nullable
    @Override
    public Desc activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, MinionData inventory) {
        this.triggerAdvancements(lord);
        BlockPos pos = minion != null ? minion.asEntity().blockPosition() : (lord != null ? lord.blockPosition() : null);
        return pos == null ? null : new Desc(pos);
    }

    @Override
    public void deactivateTask(Desc desc) {

    }

    @Override
    public @NotNull Desc readFromNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        BlockPos pos = NbtUtils.readBlockPos(nbt, "pos").orElseThrow();
        return new Desc(pos);
    }

    public static class Desc implements IMinionTask.IMinionTaskDesc<MinionData> {
        public final BlockPos position;

        public Desc(BlockPos pos) {
            this.position = pos;
        }


        @Override
        public @NotNull IMinionTask<?, MinionData> getTask() {
            return MinionTasks.STAY.get();
        }

        @Override
        public void writeToNBT(@NotNull CompoundTag nbt) {
            nbt.put("pos", NbtUtils.writeBlockPos(position));
        }
    }

}
