package de.teamlapen.lib.lib.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

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
            int actual = stack.isEmpty() ? 0 : (!stack.getItem().equals(items[i]) ? 0 : (meta[i] == OreDictionary.WILDCARD_VALUE || stack.getMetadata() == meta[i] || (meta[i] < 0 && stack.getMetadata() >= (-meta[i]))) ? stack.getCount() : 0);
            if (actual < amounts[i]) {
                return new ItemStack(items[i], amounts[i] - actual, meta[i] == OreDictionary.WILDCARD_VALUE ? 0 : meta[i] < 0 ? -meta[i] : meta[i]);
            }
        }
        return ItemStack.EMPTY;
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
            if (!stack.isEmpty() && amounts[i] > 0) {
                stack.grow(-amounts[i]);
                if (stack.isEmpty()) {
                    inventory.removeStackFromSlot(i);
                }
            }
        }
    }


    @Nullable
    public static IItemHandler tryGetItemHandler(IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().hasTileEntity(state)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
                    return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                }
            }
        }
        return null;
    }


}
