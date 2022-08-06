package de.teamlapen.vampirism.api.items;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface IArrowContainer {

    /**
     * @return contained arrows
     */
    Collection<ItemStack> getArrows(ItemStack stack);
}
