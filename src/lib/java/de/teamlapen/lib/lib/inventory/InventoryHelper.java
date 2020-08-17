package de.teamlapen.lib.lib.inventory;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
import java.util.function.BiPredicate;

public class InventoryHelper {


    /**
     * Checks if the given inventory contains at least the given amount of tileInventory in the respective slots.
     *
     * @param inventory
     * @param items           Has to have the same size as the inventory
     * @param amounts         Has to have the same size as the inventory
     * @param compareFunction Used to determine if the first items can be used in place of the second one (most simple -> equals)
     * @return Null if all tileInventory are present otherwise an itemstack which represents the missing tileInventory
     */
    public static ItemStack checkItems(IInventory inventory, Item[] items, int[] amounts, BiPredicate<Item, Item> compareFunction) {
        if (inventory.getSizeInventory() < amounts.length || items.length != amounts.length) {
            throw new IllegalArgumentException("There has to be one itemstack and amount value for each item");
        }
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            int actual = (!stack.isEmpty() && compareFunction.test(stack.getItem(), items[i])) ? stack.getCount() : 0;
            if (actual < amounts[i]) {
                return new ItemStack(items[i], amounts[i] - actual);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack checkItems(IInventory inventory, Item[] items, int[] amounts) {
        return checkItems(inventory, items, amounts, Object::equals);
    }

    /**
     * Removes the given amount from the corresponding slot in the given inventory
     *
     * @param inventory
     * @param amounts   Has to have the same size as the inventory
     */
    public static void removeItems(IInventory inventory, int[] amounts) {
        if (inventory.getSizeInventory() < amounts.length) {
            throw new IllegalArgumentException("There has to be one itemstack value for each amount");
        }
        for (int i = 0; i < amounts.length; i++) {
            inventory.decrStackSize(i, amounts[i]);
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

    /**
     * Write the given inventory as new ListNBT "inventory" to given tag
     */
    public static void writeInventoryToTag(CompoundNBT tag, Inventory inventory) {
        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                listnbt.add(itemstack.write(new CompoundNBT()));
            }
        }
        tag.put("inventory", listnbt);
    }

    /**
     * Write the given inventory from ListNBT "inventory" in the given tag
     */
    public static void readInventoryFromTag(CompoundNBT tag, Inventory inventory) {
        ListNBT listnbt = tag.getList("inventory", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));
            if (!itemstack.isEmpty()) {
                inventory.addItem(itemstack);
            }
        }

    }

    public static boolean canMergeStacks(ItemStack stack1, ItemStack stack2, int invLimit) {
        return !stack1.isEmpty() && ItemStackUtil.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < invLimit;
    }

    /**
     * Try add stack to given slot. Tries to merge. DOES NOT check mergeability
     *
     * @param inv
     * @param slot
     * @param addStack is Modified to remove the added items
     */
    public static void addStackToSlotWithoutCheck(IInventory inv, int slot, ItemStack addStack) {

        int newCount = addStack.getCount();
        ItemStack existingStack = inv.getStackInSlot(slot);


        int oldCount = existingStack.getCount();

        int addAmount = Math.min(newCount, Math.min(inv.getInventoryStackLimit() - oldCount, addStack.getMaxStackSize() - oldCount));
        if (addAmount == 0) {
            return;
        }
        if (existingStack.isEmpty()) {
            //If stack in inventory is empty, add a 0 count stack with the item and nbt information. It will be grown afterwards
            existingStack = addStack.copy();
            existingStack.setCount(0);
            if (addStack.hasTag()) {
                existingStack.setTag(addStack.getTag().copy());
            }

            inv.setInventorySlotContents(slot, existingStack);
        }
        existingStack.grow(addAmount);
        addStack.shrink(addAmount);
    }

    /**
     * Find the slot the given stack should be added to. First checks if there already is a stack it can be merged into, then looks for empty slots.  Prefers lower index slots.
     *
     * @param invLimit The maximum item count per slot in the inventory
     * @return the slot id or -1 if none found
     */
    public static int getFirstSuitableSlotToAdd(NonNullList<ItemStack> inventory, ItemStack stack, int invLimit) {
        if (!stack.isDamaged() && stack.isStackable()) {
            for (int i = 0; i < inventory.size(); ++i) {
                if (InventoryHelper.canMergeStacks(inventory.get(i), stack, invLimit)) {
                    return i;
                }
            }
        }
        for (int i = 0; i < inventory.size(); ++i) {
            if (inventory.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

}
