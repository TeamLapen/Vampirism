package de.teamlapen.lib.lib.util;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackUtil {

    /**
     * Can be used in {@link Container#removeItem(int, int)}
     */
    public static @NotNull
    ItemStack decrIInventoryStackSize(Container inv, int slot, int amt) {
        ItemStack stack = inv.getItem(slot);
        if (!stack.isEmpty()) {
            if (stack.getCount() <= amt) {
                inv.setItem(slot, ItemStack.EMPTY);
            } else {
                stack = stack.split(amt);
                if (stack.isEmpty()) {
                    inv.setItem(slot, ItemStack.EMPTY);
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
    public static boolean doesStackContain(@NotNull ItemStack stackA, @NotNull ItemStack stackB) {
        return (stackB.isEmpty() || !stackA.isEmpty() && (areStacksEqualIgnoreAmount(stackA, stackB) && stackA.getCount() >= stackB.getCount()));
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if both ItemStacks are equal. ignores stack size
     */
    public static boolean areStacksEqualIgnoreAmount(@NotNull ItemStack stackA, @NotNull ItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) return true;
        if (stackA.isEmpty() || stackB.isEmpty()) return false;
        if (stackA.getItem() != stackB.getItem()) return false;
        if (stackA.getDamageValue() != stackB.getDamageValue()) return false;
        return ItemStack.tagMatches(stackA, stackB);
    }

    public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.tagMatches(stack1, stack2);
    }
}
