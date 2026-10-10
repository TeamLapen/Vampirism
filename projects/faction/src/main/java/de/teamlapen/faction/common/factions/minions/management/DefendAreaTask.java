package de.teamlapen.faction.common.factions.minions.management;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.tasks.MinionTaskProperties;
import de.teamlapen.faction.common.factions.minions.MinionData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import static de.teamlapen.faction.common.factions.minions.management.DefendAreaTask.State;


public class DefendAreaTask extends MinionTask<State> {

    public DefendAreaTask(MinionTaskProperties properties) {
        super(properties, State.CODEC);
    }

    @Override
    public @NonNull ActivateResult<State> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, @NonNull IMinionData inventory) {
        this.triggerAdvancements(lord);
        BlockPos pos = minion != null ? minion.asEntity().blockPosition() : (lord != null ? lord.blockPosition() : null);
        return pos == null ? ActivateResult.failed() : new ActivateResult<>(new State(pos, 10));
    }


    @Override
    public void deactivateTask(DefendAreaTask.@NonNull State desc) {

    }

    @Nullable
    @Override
    public Codec<State> stateCodec() {
        return State.CODEC;
    }

    public record State(BlockPos center, int distance) implements IMinionTaskState {

            public static Codec<State> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    BlockPos.CODEC.fieldOf("center").forGetter(d -> d.center),
                    Codec.INT.fieldOf("radius").forGetter(d -> d.distance)
            ).apply(inst, State::new));

    }
}
