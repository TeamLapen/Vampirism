package de.teamlapen.faction.common.factions.minions.management;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.factions.minions.MinionData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class SimpleMinionTask extends DefaultMinionTask<IMinionTask.NoDesc<MinionData>, MinionData> {

    private final Codec<NoDesc<MinionData>> codec;

    public SimpleMinionTask(Supplier<? extends SimpleMinionTask> typeSupplier) {
        this.codec = NoDesc.codec(typeSupplier);
    }

    @Nullable
    @Override
    public NoDesc<MinionData> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, @NonNull MinionData data) {
        this.triggerAdvancements(lord);
        return new NoDesc<>(this);
    }

    @Override
    public void deactivateTask(@NonNull NoDesc<MinionData> desc) {

    }

    @Override
    public @NotNull Codec<NoDesc<MinionData>> descriptionCodec() {
        return this.codec;
    }

    @Override
    public @NotNull NoDesc<MinionData> load(@NotNull ValueInput input) {
        return new NoDesc<>(this);
    }
}
