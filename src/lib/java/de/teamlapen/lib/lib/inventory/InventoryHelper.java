package de.teamlapen.lib.lib.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;

public class InventoryHelper {


    /**
     * Checks if the given inventory contains at least the given amount of tileInventory in the respective slots.
     *
     * @param items           Has to have the same size as the inventory
     * @param amounts         Has to have the same size as the inventory
     * @param compareFunction Used to determine if the first items can be used in place of the second one (most simple -> equals)
     * @return Null if all tileInventory are present otherwise an itemstack which represents the missing tileInventory
     */
    public static @NotNull ItemStack checkItems(@NotNull Container inventory, Item @NotNull [] items, int @NotNull [] amounts, @NotNull BiPredicate<Item, Item> compareFunction) {
        if (inventory.getContainerSize() < amounts.length || items.length != amounts.length) {
            throw new IllegalArgumentException("There has to be one itemstack and amount value for each item");
        }
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = inventory.getItem(i);
            int actual = (!stack.isEmpty() && compareFunction.test(stack.getItem(), items[i])) ? stack.getCount() : 0;
            if (actual < amounts[i]) {
                return new ItemStack(items[i], amounts[i] - actual);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack checkItems(@NotNull Container inventory, Item @NotNull [] items, int @NotNull [] amounts) {
        return checkItems(inventory, items, amounts, Object::equals);
    }

    /**
     * Removes the given amount from the corresponding slot in the given inventory
     *
     * @param amounts Has to have the same size as the inventory
     */
    public static void removeItems(@NotNull Container inventory, int... amounts) {
        if (inventory.getContainerSize() < amounts.length) {
            throw new IllegalArgumentException("There has to be one itemstack value for each amount");
        }
        for (int i = 0; i < amounts.length; i++) {
            inventory.removeItem(i, amounts[i]);
        }
    }

    @NotNull
    public static Optional<Pair<net.neoforged.neoforge.items.IItemHandler, BlockEntity>> tryGetItemHandler(@NotNull Level world, @NotNull BlockPos pos, @Nullable Direction side) {
        BlockState state = world.getBlockState(pos);
        if (state.hasBlockEntity()) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile != null) {
                return Optional.ofNullable(world.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, tile, side)).map(s -> ImmutablePair.of(s, tile));
            }
        }
        return Optional.empty();
    }


    public static boolean canMergeStacks(@NotNull ItemStack stack1, @NotNull ItemStack stack2, int invLimit) {
        return !stack1.isEmpty() && ItemStack.isSameItemSameComponents(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < Math.min(invLimit, stack1.getMaxStackSize());
    }

    /**
     * Try to add stack to given slot. Tries to merge. DOES NOT check mergeability
     *
     * @param addStack is Modified to remove the added items
     */
    public static void addStackToSlotWithoutCheck(@NotNull Container inv, int slot, @NotNull ItemStack addStack) {

        int newCount = addStack.getCount();
        ItemStack existingStack = inv.getItem(slot);


        int oldCount = existingStack.getCount();

        int addAmount = Math.min(newCount, Math.min(inv.getMaxStackSize() - oldCount, addStack.getMaxStackSize() - oldCount));
        if (addAmount == 0) {
            return;
        }
        if (existingStack.isEmpty()) {
            //If stack in inventory is empty, add a 0 count stack with the item and nbt information. It will be grown afterwards
            existingStack = addStack.copy();
            existingStack.setCount(0);
            existingStack.applyComponents(addStack.getComponents());

            inv.setItem(slot, existingStack);
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
    public static int getFirstSuitableSlotToAdd(@NotNull NonNullList<ItemStack> inventory, @NotNull ItemStack stack, int invLimit) {
        return getFirstSuitableSlotToAdd(inventory, inventory.size(), stack, invLimit);
    }

    public static int getFirstSuitableSlotToAdd(@NotNull NonNullList<ItemStack> inventory, int inventorySize, @NotNull ItemStack stack, int invLimit) {
        assert inventory.size() >= inventorySize;
        if (!stack.isDamaged() && stack.isStackable()) {
            for (int i = 0; i < inventorySize; ++i) {
                if (InventoryHelper.canMergeStacks(inventory.get(i), stack, invLimit)) {
                    return i;
                }
            }
        }
        for (int i = 0; i < inventorySize; ++i) {
            if (inventory.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }


    public static boolean removeItemFromInventory(@NotNull Container inventory, @NotNull ItemStack item) {
        int i = item.getCount();

        for (int j = 0; j < inventory.getContainerSize(); ++j) {
            ItemStack itemstack = inventory.getItem(j);
            if (itemstack.getItem().equals(item.getItem())) {
                if (itemstack.getCount() >= i) {
                    itemstack.shrink(i);
                    return true;
                } else {
                    int l = itemstack.getCount();
                    itemstack.shrink(i);
                    i -= l;
                }
            }
        }

        return i <= 0;
    }

    public static @Nullable ItemStack getFirst(@NotNull Container inventory, Item set) {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack = inventory.getItem(i);
            if (set == itemstack.getItem() && itemstack.getCount() > 0) {
                return itemstack;
            }
        }

        return null;
    }

}
