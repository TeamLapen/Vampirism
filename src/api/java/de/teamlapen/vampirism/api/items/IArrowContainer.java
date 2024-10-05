package de.teamlapen.vampirism.api.items;

import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * This item is not an arrow itself, but holds arrows
 */
public interface IArrowContainer {

    /**
     * @return contained arrows
     */
    Collection<ItemStack> getArrows(ItemStack container);

    /**
     * Adds an arrow to the container
     * @return true if the arrow was added
     */
    boolean addArrow(ItemStack container, ItemStack arrow);

    /**
     * Adds multiple arrows to the container.
     * @param arrowStacks The arrows to add. Added arrows are removed from the list, if the list supports it (e.g. ArrayList)
     */
    void addArrows(ItemStack container, List<ItemStack> arrowStacks);

    /**
     * Removes all arrows from the container and returns them
     */
    Collection<ItemStack> getAndRemoveArrows(ItemStack container);

    /**
     * Remove all arrows from the container
     */
    void removeArrows(ItemStack container);

    /**
     * Removes a specific arrow from the container
     * @return true if the arrow was removed
     */
    boolean removeArrow(ItemStack container, ItemStack arrow);

    /**
     * @return true if the container is destroyed when it is unloaded
     */
    boolean isDiscardedOnUse(ItemStack container);

    /**
     * @return the maximum amount of arrows that can be stored in the container
     */
    int getMaxArrows(ItemStack container);

    /**
     * @return true if the container can be refilled with arrows
     */
    boolean canBeRefilled(ItemStack container);

    /**
     * @return true if the container can contain the given arrow
     */
    boolean canContainArrow(ItemStack container, ItemStack arrow);
}
