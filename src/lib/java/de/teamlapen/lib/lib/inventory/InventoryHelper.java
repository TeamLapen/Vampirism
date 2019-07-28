package de.teamlapen.lib.lib.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryHelper {


    /**
     * Checks if the given inventory contains at least the given amount of tileInventory in the respective slots.
     *
     * @param inventory
     * @param items     Has to have the same size as the inventory
     * @param amounts   Has to have the same size as the inventory
     * @return Null if all tileInventory are present otherwise an itemstack which represents the missing tileInventory
     */
    public static ItemStack checkItems(NonNullList<ItemStack> inventory, Item[] items, int[] amounts) {
        if (inventory.size() < amounts.length || items.length != amounts.length) {
            throw new IllegalArgumentException("There has to be one itemstack and amount value for each item");
        }
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = inventory.get(i);
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
    public static void removeItems(NonNullList<ItemStack> inventory, int[] amounts) {
        if (inventory.size() < amounts.length) {
            throw new IllegalArgumentException("There has to be one itemstack value for each amount");
        }
        for (int i = 0; i < amounts.length; i++) {
            ItemStackHelper.getAndSplit(inventory, i, amounts[i]);
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
