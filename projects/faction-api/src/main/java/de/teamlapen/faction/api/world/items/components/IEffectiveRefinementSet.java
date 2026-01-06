package de.teamlapen.faction.api.world.items.components;

import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.world.items.IRefinementItem;

/**
 * Interface for an active refinement set. Used by {@link IRefinementItem} to store the applied refinements.
 */
public interface IEffectiveRefinementSet {

    /**
     * @return The applied refinement set
     */
    IRefinementSet set();
}
