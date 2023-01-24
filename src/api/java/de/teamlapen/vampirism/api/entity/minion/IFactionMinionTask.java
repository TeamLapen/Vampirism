package de.teamlapen.vampirism.api.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import org.jetbrains.annotations.Nullable;

public interface IFactionMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends IMinionTask<T, Q> {

    @Nullable
    IFaction<?> getFaction();
}
