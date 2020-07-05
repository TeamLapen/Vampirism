package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;

import javax.annotation.Nonnull;

/**
 * Should be implemented by all items that are supposed to be used by only a specific faction.
 */
public interface IFactionExclusiveItem {

    /**
     * @return The faction this item is meant for
     */
    @Nonnull
    IFaction<?> getExclusiveFaction();
}
