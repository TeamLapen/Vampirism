package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Similar to {@link net.minecraft.world.entity.player.Inventory}
 * Manages the different inventories of a minion (armor, held item, main inventory)
 */
public interface IMinionInventory extends Container {
    /**
     * Passed item stack count is reduced accordingly
     *
     * @param stack Is modified
     */
    void addItemStack(@NotNull ItemStack stack);

    /**
     * @return The number of available main inventory slots. Must be 9, 12 or 15
     */
    int getAvailableSize();

    NonNullList<ItemStack> getInventoryArmor();

    NonNullList<ItemStack> getInventoryHands();

    List<NonNullList<ItemStack>> getAllInventorys();
}
