package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

import javax.annotation.Nullable;

/**
 * Interface for the player lord related data.
 * Attached to all players as capability
 */
public interface ILordPlayer {

    @Nullable
    IPlayableFaction<?> getLordFaction();

    int getLordLevel();
}
