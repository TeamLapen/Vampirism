package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Similar to {@link PlayerInventory}
 * Manages the different inventories of a minion (armor, held item, main inventory)
 */
public interface IMinionInventory extends IInventory {
    /**
     * Passed item stack count is reduced accordingly
     *
     * @param stack Is modified
     */
    void addItemStack(@Nonnull ItemStack stack);

    /**
     * @return The number of available main inventory slots. Must be 9, 12 or 15
     */
    int getAvailableSize();

    NonNullList<ItemStack> getInventoryArmor();

    NonNullList<ItemStack> getInventoryHands();

    List<NonNullList<ItemStack>> getAllInventorys();
}
