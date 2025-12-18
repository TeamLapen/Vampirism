package de.teamlapen.factions.common.factions.minions.management;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.factions.minions.MinionData;
import net.minecraft.core.BlockPos;
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
    public @NotNull Codec<Desc> descriptionCodec() {
        return Desc.CODEC;
    }

    @Override
    public @NotNull Desc load(@NotNull ValueInput input) {
        return new Desc(input);
    }

    public record Desc(BlockPos position) implements IMinionTaskDesc<MinionData> {

        private static final Codec<Desc> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BlockPos.CODEC.fieldOf("position").forGetter(d -> d.position)
        ).apply(inst, Desc::new));

        private Desc(ValueInput input) {
            this(input.read("pos", BlockPos.CODEC).orElseThrow());
        }


        @Override
        public @NotNull IMinionTask<?, MinionData> getTask() {
            return FactionMinionTasks.STAY.get();
        }


        @Override
        public void serialize(@NotNull ValueOutput output) {
            output.store("pos", BlockPos.CODEC, position);
        }
    }

}
