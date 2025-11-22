package de.teamlapen.factions.common.minions.management;

import de.teamlapen.factions.api.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.entities.minion.IMinionTask;
import de.teamlapen.factions.common.minions.MinionData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleMinionTask extends DefaultMinionTask<IMinionTask.NoDesc<MinionData>, MinionData> {


    @Nullable
    @Override
    public NoDesc<MinionData> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, MinionData data) {
        this.triggerAdvancements(lord);
        return new NoDesc<>(this);
    }

    @Override
    public void deactivateTask(NoDesc<MinionData> desc) {

    }

    @Override
    public @NotNull NoDesc<MinionData> load(ValueInput input) {
        return new NoDesc<>(this);
    }
}
