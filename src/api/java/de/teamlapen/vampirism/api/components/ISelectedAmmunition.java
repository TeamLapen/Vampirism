package de.teamlapen.vampirism.api.components;

import net.minecraft.world.item.Item;

/**
 * Stores the currently selected ammunition of an item if it has any.
 */
public interface ISelectedAmmunition {

    /**
     * @return The currently selected ammunition item
     */
    Item item();
}
