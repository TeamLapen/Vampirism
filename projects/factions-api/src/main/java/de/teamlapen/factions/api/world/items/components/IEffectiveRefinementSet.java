package de.teamlapen.factions.api.world.items.components;

import de.teamlapen.factions.api.factions.refinements.IRefinementSet;
import de.teamlapen.factions.api.world.items.IRefinementItem;

/**
 * Interface for an active refinement set. Used by {@link IRefinementItem} to store the applied refinements.
 */
public interface IEffectiveRefinementSet {

    /**
     * @return The applied refinement set
     */
    IRefinementSet set();
}
