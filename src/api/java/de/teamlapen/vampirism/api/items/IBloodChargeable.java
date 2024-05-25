package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismDataComponents;
import de.teamlapen.vampirism.api.components.IBloodCharged;
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
        IBloodCharged bloodCharge = stack.get(VampirismDataComponents.BLOOD_CHARGED.get());
        return bloodCharge != null ? bloodCharge.charged() : 0;
    }
}
