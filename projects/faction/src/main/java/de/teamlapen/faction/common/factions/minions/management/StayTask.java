package de.teamlapen.faction.common.factions.minions.management;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.tasks.MinionTaskProperties;
import de.teamlapen.faction.common.factions.minions.MinionData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;


public class StayTask extends MinionTask<MinionData, StayTask.State> {

    public StayTask(MinionTaskProperties properties) {
        super(properties, State.CODEC);
    }

    @Override
    public @NonNull ActivateResult<StayTask.@NonNull State> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, @NonNull MinionData inventory) {
        this.triggerAdvancements(lord);
        BlockPos pos = minion != null ? minion.asEntity().blockPosition() : (lord != null ? lord.blockPosition() : null);
        return pos == null ? ActivateResult.failed() : new ActivateResult<>(new State(pos));
    }

    @Override
    public void deactivateTask(StayTask.@NonNull State desc) {

    }

    @Override
    public @NotNull Codec<State> stateCodec() {
        return State.CODEC;
    }

    public record State(BlockPos position) implements IMinionTaskState {

        private static final Codec<State> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BlockPos.CODEC.fieldOf("position").forGetter(d -> d.position)
        ).apply(inst, State::new));

    }

}
