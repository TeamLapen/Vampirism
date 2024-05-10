package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;

/**
 * Interface for an active refinement set. Used by {@link de.teamlapen.vampirism.api.items.IRefinementItem} to store the applied refinements.
 */
public interface IEffectiveRefinementSet {

    /**
     * @return The applied refinement set
     */
    IRefinementSet set();
}
