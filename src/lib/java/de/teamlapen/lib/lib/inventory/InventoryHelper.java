package de.teamlapen.lib.lib.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Helper method for Inventories respectively {@link InventorySlot.IInventorySlotInventory}
 */
public class InventoryHelper {


    /**
     * Checks if the given inventory contains at least the given amount of items in the respective slots.
     * TODO 1.13 probably has to be modified/removed since meta data does not exist
     *
     * @param inventory
     * @param items     Has to have the same size as the inventory
     * @param amounts   Has to have the same size as the inventory
     * @return Null if all items are present otherwise an itemstack which represents the missing items
     */
    public static ItemStack checkItems(InventorySlot.IInventorySlotInventory inventory, Item[] items, int[] amounts) {
        if (inventory.getSizeInventory() != amounts.length || items.length != amounts.length) {
            throw new IllegalArgumentException("There has to be one amount and meta value for each slot");
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            int actual = (!stack.isEmpty() && stack.getItem().equals(items[i])) ? stack.getCount() : 0;
            if (actual < amounts[i]) {
                return new ItemStack(items[i], amounts[i] - actual);
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


    @Nonnull
    public static LazyOptional<Pair<IItemHandler, TileEntity>> tryGetItemHandler(IBlockReader world, BlockPos pos, @Nullable Direction side) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock().hasTileEntity(state)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).map(capability -> ImmutablePair.of(capability, tile));

            }
        }
        return LazyOptional.empty();
    }


}
