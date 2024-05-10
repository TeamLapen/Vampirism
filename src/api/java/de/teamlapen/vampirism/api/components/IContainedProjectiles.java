package de.teamlapen.vampirism.api.components;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Used to store projectiles in an item. Such as an {@link de.teamlapen.vampirism.api.items.IArrowContainer}
 */
public interface IContainedProjectiles {

    /**
     * @return The contained projectiles
     */
    List<ItemStack> getProjectiles();
}
