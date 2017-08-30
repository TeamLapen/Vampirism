package de.teamlapen.lib.lib.inventory;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Helper method for Inventories respectively {@link InventorySlot.IInventorySlotInventory}
 */
public class InventoryHelper {


    /**
     * Checks if the given inventory contains at least the given amount of items in the respective slots.
     *
     * @param inventory
     * @param items     Has to have the same size as the inventory
     * @param amounts   Has to have the same size as the inventory
     * @param meta      Has to have the same size as the inventory. If any metadata is ok, set this to {@link OreDictionary#WILDCARD_VALUE}. If any value above (including)  a specific one is ok, set this to -(minLevel)
     * @return Null if all items are present otherwise an itemstack which represents the missing items
     */
    public static ItemStack checkItems(InventorySlot.IInventorySlotInventory inventory, Item[] items, int[] amounts, int[] meta) {
        if (inventory.getSizeInventory() != amounts.length || amounts.length != meta.length || items.length != amounts.length) {
            throw new IllegalArgumentException("There has to be one amount and meta value for each slot");
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            int actual = ItemStackUtil.isEmpty(stack) ? 0 : (!stack.getItem().equals(items[i]) ? 0 : (meta[i] == OreDictionary.WILDCARD_VALUE || stack.getMetadata() == meta[i] || (meta[i] < 0 && stack.getMetadata() >= (-meta[i]))) ? ItemStackUtil.getCount(stack) : 0);
            if (actual < amounts[i]) {
                return new ItemStack(items[i], amounts[i] - actual, meta[i] == OreDictionary.WILDCARD_VALUE ? 0 : meta[i] < 0 ? -meta[i] : meta[i]);
            }
        }
        return ItemStackUtil.getEmptyStack();
    }

    /**
     * Removes the given amount from the corresponding slot in the given inventory
     *
     * @param inventory
     * @param amounts   Has to have the same size as the inventory
     */
    public static void removeItems(InventorySlot.IInventorySlotInventory inventory, int[] amounts) {
        if (inventory.getSizeInventory() != amounts.length) {
            throw new IllegalArgumentException("There has to be one amount and meta value for each slot");
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!ItemStackUtil.isEmpty(stack) && amounts[i] > 0) {
                stack = ItemStackUtil.grow(stack, -amounts[i]);
                if (ItemStackUtil.isEmpty(stack)) {
                    inventory.removeStackFromSlot(i);
                }
            }
        }
    }


}
