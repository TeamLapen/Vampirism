package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

/**
 * Faction registry.
 * Register all extended properties that extend {@link IFactionPlayer} here
 * Currently only used for managing IPlayerEventListeners.
 */
public interface IFactionRegistry {
    /**
     * Registers a faction
     * Call before PostInit
     */
    void addFaction(Faction faction);

    /**
     * @return All factions after post init
     */
    Faction[] getFactions();

    /**
     * @return All playable factions after post init
     */
    PlayableFaction[] getPlayableFactions();
}
