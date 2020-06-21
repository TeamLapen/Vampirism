package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

/**
 * 1.14
 *
 * @author maxanier
 */
public interface IMinionInventory extends IInventory {
    /**
     * Passed item stack count is reduced accordingly
     *
     * @param stack Is modified
     */
    void addItemStack(@Nonnull ItemStack stack);

    int getAvailableSize();

    NonNullList<ItemStack> getInventoryArmor();

    NonNullList<ItemStack> getInventoryHands();
}
