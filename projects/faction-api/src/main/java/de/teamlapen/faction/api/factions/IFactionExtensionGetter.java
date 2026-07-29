package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.factions.refinements.IRefinementAccess;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;

import java.util.Optional;

public interface IFactionExtensionGetter {

    default IRefinementAccess getRefinementHandler() {
        return getExtension(IRefinementHandler.class).map(IRefinementAccess::from).orElse(IRefinementAccess.EMPTY);
    }

    default Optional<ITaskManager> getTaskManager() {
        return getExtension(ITaskManager.class);
    }

    /**
     * Resolves the extension of the given type registered by the player's current faction, if any.
     *
     * @see FactionProperties#extension(Class, net.minecraft.core.Holder)
     */
    <TInterface> Optional<TInterface> getExtension(Class<TInterface> type);
}
