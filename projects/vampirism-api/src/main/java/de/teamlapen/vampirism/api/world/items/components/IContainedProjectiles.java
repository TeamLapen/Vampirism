package de.teamlapen.vampirism.api.world.items.components;

import net.minecraft.world.item.ItemStackTemplate;

/**
 * Used to store projectiles in an item, such as an arrow container.
 */
public interface IContainedProjectiles {

    ItemStackTemplate projectiles();
}
