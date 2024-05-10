package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.annotations.FloatRange;

import java.util.Map;
import java.util.UUID;

/**
 * Item component to store the player training for a sword
 */
public interface ISwordTraining {

    /**
     * @return A map of player UUIDs to their training level. The level is a float between 0 and 1
     */
    Map<UUID, @FloatRange(from = 0, to = 1) Float> training();
}
