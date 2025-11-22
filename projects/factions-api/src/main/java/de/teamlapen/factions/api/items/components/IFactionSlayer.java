package de.teamlapen.factions.api.items.components;

import de.teamlapen.factions.api.factions.IFaction;
import net.minecraft.core.HolderSet;

/**
 * Can be paired with {@link IFactionRestriction}
 */
public interface IFactionSlayer {

    /**
     * Factions that can be slayed by this slayer.
     */
    HolderSet<IFaction<?>> slayedFactions();

    /**
     * Damage multiplier for this slayer.
     */
    float multiplier();
}
