package de.teamlapen.lib.lib.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

/**
 * Used for inventory slot description as well as for storing the actual inventory
 * Allows setting a item selector to restrict the item types that can co in here
 *
 * @author Maxanier
 */
public class InventorySlot {
    public final IItemSelector itemSelector;
    public final int xDisplay, yDisplay;
    public ItemStack stack = ItemStack.EMPTY;

    public InventorySlot(final Class<? extends Item> cls, int xDisplay, int yDisplay) {
        this(item -> cls.isInstance(item.getItem()), xDisplay, yDisplay);
    }

    public InventorySlot(IItemSelector selector, int xDisplay, int yDisplay) {
        itemSelector = selector;
        this.xDisplay = xDisplay;
        this.yDisplay = yDisplay;
    }

    public InventorySlot(int xDisplay, int yDisplay) {
        this((IItemSelector) null, xDisplay, yDisplay);
    }

    public InventorySlot(final Item item, int xDisplay, int yDisplay) {
        this(stack -> item.equals(stack.getItem()), xDisplay, yDisplay);
    }

    public interface IItemSelector {
        /**
         * @param item
         * @return whether the item is allowed or not
         */
        boolean isItemAllowed(@Nonnull ItemStack item);
    }

    /**
     * Interface for accessing the inventory slots of tiles
     */
    public interface IInventorySlotInventory extends IInventory {
        NonNullList<InventorySlot> getSlots();

        InventorySlot getSlot(int index);
    }
}