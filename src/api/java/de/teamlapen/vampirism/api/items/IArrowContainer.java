package de.teamlapen.vampirism.api.items;

import net.minecraft.world.item.ItemStack;

import java.util.Collection;

/**
 * This item is not an arrow itself, but holds arrows
 */
public interface IArrowContainer {

    /**
     * @return contained arrows
     */
    Collection<ItemStack> getArrows(ItemStack stack);
}
