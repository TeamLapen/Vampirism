package de.teamlapen.vampirism.api.items.components;

import org.jetbrains.annotations.Range;

/**
 * Interface for components that have a blood bottle level.
 * Should only be used by the blood bottle item.
 */
public interface IBottleBlood {

    int MAX_VALUE = 5;
    int MULTIPLIER = 50;

    @Range(from = 0, to = MAX_VALUE)
    int blood();
}
