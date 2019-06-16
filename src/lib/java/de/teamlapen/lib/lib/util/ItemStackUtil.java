package de.teamlapen.lib.lib.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemStackUtil {

    /**
     * Can be used in {@link IInventory#decrStackSize(int, int)}
     */
    public static @Nonnull
    ItemStack decrIInventoryStackSize(IInventory inv, int slot, int amt) {
        ItemStack stack = inv.getStackInSlot(slot);
        if (!stack.isEmpty()) {
            if (stack.getCount() <= amt) {
                inv.setInventorySlotContents(slot, ItemStack.EMPTY);
            } else {
                stack = stack.split(amt);
                if (stack.isEmpty()) {
                    inv.setInventorySlotContents(slot, ItemStack.EMPTY);
                }
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Checks if stackA contains stackB
     * True if A !=null and B == null
     */
    public static boolean doesStackContain(@Nonnull ItemStack stackA, @Nonnull ItemStack stackB) {
        return (stackB.isEmpty() || !stackA.isEmpty() && (areStacksEqualIgnoreAmount(stackA, stackB) && stackA.getCount() >= stackB.getCount()));
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if both ItemStacks are equal. ignores stack size
     */
    public static boolean areStacksEqualIgnoreAmount(@Nonnull ItemStack stackA, @Nonnull ItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) return true;
        if (stackA.isEmpty() || stackB.isEmpty()) return false;
        if (stackA.getItem() != stackB.getItem()) return false;
        if (stackA.getDamage() != stackB.getDamage()) return false;
        return ItemStack.areItemStackTagsEqual(stackA, stackB);
    }
}
