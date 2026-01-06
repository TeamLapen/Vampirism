package de.teamlapen.faction.common.factions.minions.management;

import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.INoGlobalCommandTask;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.factions.minions.MinionData;
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
