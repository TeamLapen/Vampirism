package de.teamlapen.factions.common.minions.management;

import de.teamlapen.factions.api.entities.minion.IMinionTask;
import de.teamlapen.factions.api.entities.minion.INoGlobalCommandTask;
import de.teamlapen.factions.api.factions.ILordPlayer;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.minions.MinionData;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NothingTask extends SimpleMinionTask implements INoGlobalCommandTask<IMinionTask.NoDesc<MinionData>, MinionData> {

    @Override
    public boolean isAvailable(@Nullable ILordPlayer<?> player) {
        return false;
    }
}
