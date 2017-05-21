package de.teamlapen.lib.lib.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 1.10
 *
 * @author maxanier
 */
public class ItemStackUtil {

    /**
     * Increase the stack size by the given amount
     */
    public static @Nullable
    ItemStack grow(@Nonnull ItemStack stack, int amount) {
        stack.stackSize += amount;
        if (stack.stackSize <= 0) return null;
        return stack;
    }

    public static @Nullable
    ItemStack decr(@Nonnull ItemStack stack) {
        return grow(stack, -1);
    }

    public static int getCount(@Nullable ItemStack stack) {
        return stack == null ? 0 : stack.stackSize;
    }

    public static @Nullable
    ItemStack setCount(@Nonnull ItemStack stack, int count) {
        stack.stackSize = count;
        return stack.stackSize > 0 ? stack : null;
    }

    public static boolean isEmpty(@Nullable ItemStack stack) {
        return stack == null || stack.stackSize <= 0;
    }

    public static @Nullable
    ItemStack makeEmpty(@Nullable ItemStack stack) {
        if (stack != null) stack.stackSize = 0;
        return null;
    }

    public static @Nullable
    ItemStack getEmptyStack() {
        return null;
    }

    public static @Nullable
    ItemStack loadFromNBT(@Nonnull NBTTagCompound nbt) {
        return ItemStack.loadItemStackFromNBT(nbt);
    }

    /**
     * Can be used in {@link IInventory#decrStackSize(int, int)}
     */
    public static @Nullable
    ItemStack decrIInventoryStackSize(IInventory inv, int slot, int amt) {
        ItemStack stack = inv.getStackInSlot(slot);
        if (!ItemStackUtil.isEmpty(stack)) {
            if (ItemStackUtil.getCount(stack) <= amt) {
                inv.setInventorySlotContents(slot, ItemStackUtil.getEmptyStack());
            } else {
                stack = stack.splitStack(amt);
                if (ItemStackUtil.isEmpty(stack)) {
                    inv.setInventorySlotContents(slot, ItemStackUtil.getEmptyStack());
                }
            }
            return stack;
        }
        return ItemStackUtil.getEmptyStack();
    }

    /**
     * On 1.11 checks if stack is not null
     */
    public static boolean isValid(ItemStack stack) {
        return true;
    }

    /**
     * Checks if stackA contains stackB
     * True if A !=null and B == null
     */
    public static boolean doesStackContain(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
        return (stackB == null || stackA != null && (areStacksEqualIgnoreAmount(stackA, stackB) && stackA.stackSize >= stackB.stackSize));
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if both ItemStacks are equal. ignores stack size
     */
    public static boolean areStacksEqualIgnoreAmount(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
        if (stackA == null && stackB == null) return true;
        if (stackA == null || stackB == null) return false;
        if (stackA.getItem() != stackB.getItem()) return false;
        if (stackA.getItemDamage() != stackB.getItemDamage()) return false;
        return ItemStack.areItemStackTagsEqual(stackA, stackB);
    }


}