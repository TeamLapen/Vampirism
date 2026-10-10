package de.teamlapen.faction.api.world.entities.minion.tasks;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public interface IFactionMinionTask<T extends IMinionTask.IMinionTaskState> extends IMinionTask<T> {

    /**
     * @return The faction that is required to use this task. Null if no faction is required
     */
    @Nullable
    Holder<? extends IFaction<?>> getFaction();
}
