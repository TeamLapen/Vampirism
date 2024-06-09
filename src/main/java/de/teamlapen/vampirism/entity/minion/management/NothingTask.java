package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.minion.INoGlobalCommandTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NothingTask extends SimpleMinionTask implements INoGlobalCommandTask<IMinionTask.NoDesc<MinionData>, MinionData> {

    @Override
    public boolean isAvailable(@NotNull Holder<? extends IPlayableFaction<?>> faction, @Nullable ILordPlayer player) {
        return false;
    }
}
