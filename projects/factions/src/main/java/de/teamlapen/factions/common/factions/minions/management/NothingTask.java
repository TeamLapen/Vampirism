package de.teamlapen.factions.common.factions.minions.management;

import de.teamlapen.factions.api.factions.lord.ILordPlayer;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.api.world.entities.minion.INoGlobalCommandTask;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.factions.minions.MinionData;
import org.jetbrains.annotations.Nullable;

public class NothingTask extends SimpleMinionTask implements INoGlobalCommandTask<IMinionTask.NoDesc<MinionData>, MinionData> {

    public NothingTask() {
        super(FactionMinionTasks.NOTHING);
    }

    @Override
    public boolean isAvailable(@Nullable ILordPlayer<?> player) {
        return false;
    }

}
