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

import static de.teamlapen.vampirism.entity.minion.management.DefendAreaTask.Desc;


public class DefendAreaTask extends DefaultMinionTask<Desc, MinionData> {


    @Override
    public Desc activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, MinionData inventory) {
        this.triggerAdvancements(lord);
        BlockPos pos = minion != null ? minion.asEntity().blockPosition() : (lord != null ? lord.blockPosition() : null);
        return pos == null ? null : new Desc(pos, 10);
    }


    @Override
    public void deactivateTask(Desc desc) {

    }

    @Override
    public @NotNull Desc readFromNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        BlockPos pos = NbtUtils.readBlockPos(nbt, "center").orElseThrow();
        int dist = nbt.getInt("radius");
        return new Desc(pos, dist);
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class Desc implements IMinionTask.IMinionTaskDesc<MinionData> {

        public final BlockPos center;
        public final int distance;

        public Desc(BlockPos center, int distance) {
            this.center = center;
            this.distance = distance;
        }

        @Override
        public @NotNull IMinionTask<?, MinionData> getTask() {
            return MinionTasks.DEFEND_AREA.get();
        }

        @Override
        public void writeToNBT(@NotNull CompoundTag nbt) {
            nbt.put("center", NbtUtils.writeBlockPos(center));
            nbt.putInt("radius", distance);
        }
    }
}
