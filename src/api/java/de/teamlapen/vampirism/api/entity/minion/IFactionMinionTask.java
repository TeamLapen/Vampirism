package de.teamlapen.vampirism.api.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public interface IFactionMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends IMinionTask<T, Q> {

    /**
     *
     * @return The faction that is required to use this task. Null if no faction is required
     */
    @Nullable
    Holder<? extends IFaction<?>> getFaction();
}
