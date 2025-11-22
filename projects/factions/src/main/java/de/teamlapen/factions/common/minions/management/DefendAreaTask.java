package de.teamlapen.factions.common.minions.management;


import de.teamlapen.factions.api.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.entities.minion.IMinionTask;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.minions.MinionData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.teamlapen.factions.common.minions.management.DefendAreaTask.Desc;


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
    public @NotNull Desc load(ValueInput input) {
        return new Desc(input);
    }


    public static class Desc implements IMinionTask.IMinionTaskDesc<MinionData> {

        public final BlockPos center;
        public final int distance;

        public Desc(BlockPos center, int distance) {
            this.center = center;
            this.distance = distance;
        }

        private Desc(ValueInput input) {
            this.center = input.read("center", BlockPos.CODEC).orElseThrow();
            this.distance = input.getIntOr("radius", 0);
        }

        @Override
        public @NotNull IMinionTask<?, MinionData> getTask() {
            return FactionMinionTasks.DEFEND_AREA.get();
        }

        @Override
        public void serialize(@NotNull ValueOutput output) {
            output.store("center", BlockPos.CODEC, center);
            output.putInt("radius", distance);
        }
    }
}
