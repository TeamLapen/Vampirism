package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.annotations.FloatRange;

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
