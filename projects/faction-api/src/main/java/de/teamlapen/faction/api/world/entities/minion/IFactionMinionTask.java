package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public interface IFactionMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends IMinionTask<T, Q> {

    /**
     * @return The faction that is required to use this task. Null if no faction is required
     */
    @Nullable
    Holder<? extends IFaction<?>> getFaction();
}
