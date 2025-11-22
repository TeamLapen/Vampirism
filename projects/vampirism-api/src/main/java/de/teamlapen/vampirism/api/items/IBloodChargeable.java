package de.teamlapen.vampirism.api.items;

import net.minecraft.world.item.ItemStack;

public interface IBloodChargeable {

    /**
     * @param stack Stack of this item
     * @return If the itemstack can be charged
     */
    boolean canBeCharged(ItemStack stack);

    /**
     * Try to charge the given itemstack
     *
     * @param stack  Stack of this item
     * @param amount The max amount blood in mB to be charged
     * @return The actual amount charged
     */
    int charge(ItemStack stack, int amount);

    /**
     * Gets the charge percentage of the item
     *
     * @param stack Stack of this item
     * @return the charge percentage of the item
     */
    default float getChargePercentage(ItemStack stack) {
        return canBeCharged(stack) ? 0 : 1;
    }
}
