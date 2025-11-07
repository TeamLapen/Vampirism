package de.teamlapen.vampirism.common.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    public @NotNull Desc load(ValueInput input) {
        return new Desc(input);
    }

    public static class Desc implements IMinionTask.IMinionTaskDesc<MinionData> {
        public final BlockPos position;

        public Desc(BlockPos pos) {
            this.position = pos;
        }

        private Desc(ValueInput input) {
            this.position = input.read("pos", BlockPos.CODEC).orElseThrow();
        }


        @Override
        public @NotNull IMinionTask<?, MinionData> getTask() {
            return MinionTasks.STAY.get();
        }


        @Override
        public void serialize(@NotNull ValueOutput output) {
            output.store("pos", BlockPos.CODEC, position);
        }
    }

}
