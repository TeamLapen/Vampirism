package de.teamlapen.lib.lib.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * 1.11
 *
 * @author maxanier
 */
public class ItemStackUtil {

    /**
     * Increase the stack size by the given amount
     */
    public static @Nonnull
    ItemStack grow(@Nonnull ItemStack stack, int amount) {
        stack.grow(amount);
        return stack;
    }

    public static @Nonnull
    ItemStack decr(@Nonnull ItemStack stack) {
        return grow(stack, -1);
    }

    public static int getCount(@Nonnull ItemStack stack) {
        return stack.getCount();
    }

    public static @Nonnull
    ItemStack setCount(@Nonnull ItemStack stack, int count) {
        stack.setCount(count);
        return stack;
    }

    public static boolean isEmpty(@Nonnull ItemStack stack) {
        return stack.isEmpty();
    }

    public static @Nonnull
    ItemStack makeEmpty(@Nonnull ItemStack stack) {
        stack.setCount(0);
        return ItemStack.EMPTY;
    }

    public static @Nonnull
    ItemStack getEmptyStack() {
        return ItemStack.EMPTY;
    }

    public static @Nonnull
    ItemStack loadFromNBT(@Nonnull NBTTagCompound nbt) {
        return new ItemStack(nbt);
    }

    /**
     * Can be used in {@link IInventory#decrStackSize(int, int)}
     */
    public static @Nonnull
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
        return stack != null;
    }

    /**
     * Checks if stackA contains stackB
     * True if A !=null and B == null
     */
    public static boolean doesStackContain(@Nonnull ItemStack stackA, @Nonnull ItemStack stackB) {
        return !stackA.isEmpty() && (stackB.isEmpty() || (areStacksEqualIgnoreAmount(stackA, stackB) && stackA.getCount() >= stackB.getCount()));
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if both ItemStacks are equal. ignores stack size
     */
    public static boolean areStacksEqualIgnoreAmount(@Nonnull ItemStack stackA, @Nonnull ItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) return true;
        if (stackA.isEmpty() || stackB.isEmpty()) return false;
        if (stackA.getItem() != stackB.getItem()) return false;
        if (stackA.getItemDamage() != stackB.getItemDamage()) return false;
        return ItemStack.areItemStackTagsEqual(stackA, stackB);
    }
}
