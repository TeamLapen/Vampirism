package de.teamlapen.vampirism.api.world.items.components;

import de.teamlapen.factions.api.annotations.FloatRange;

/**
 * Interface for components that have a blood charge level.
 */
public interface IBloodCharged {

    /**
     * The percentage of the charge level.
     */
    @FloatRange(from = 0, to = 1)
    float charged();
}
