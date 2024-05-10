package de.teamlapen.vampirism.api.components;

import org.jetbrains.annotations.Range;

/**
 * Interface for components that have a blood bottle level.
 * Should only be used by the blood bottle item.
 */
public interface IBottleBlood {

    @Range(from = 0, to = 9)
    int blood();
}
