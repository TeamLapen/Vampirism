package de.teamlapen.factions.api.items.components;

import de.teamlapen.factions.api.items.IRefinementItem;
import de.teamlapen.factions.api.refinements.IRefinementSet;

/**
 * Interface for an active refinement set. Used by {@link IRefinementItem} to store the applied refinements.
 */
public interface IEffectiveRefinementSet {

    /**
     * @return The applied refinement set
     */
    IRefinementSet set();
}
