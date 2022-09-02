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
    Collection<ItemStack> getArrows(ItemStack container);

    boolean addArrow(ItemStack container, ItemStack arrow);

    void addArrows(ItemStack container, Collection<ItemStack> arrowStacks);

    Collection<ItemStack> getAndRemoveArrows(ItemStack container);

    void removeArrows(ItemStack container);

    boolean removeArrow(ItemStack container, ItemStack arrow);

    boolean isDiscardedOnUse(ItemStack container);

    int getMaxArrows(ItemStack container);

    boolean canBeRefilled(ItemStack container);

    boolean canContainArrow(ItemStack container, ItemStack arrow);
}
