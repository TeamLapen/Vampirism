package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

/**
 * Inventory for the hunter table. Simply sets the inventory slots up and handles item (temporary) storage
 */
public class HunterTableInventory extends SimpleInventory {

    public HunterTableInventory(InventorySlot.IItemSelector[] items) {
        super(new InventorySlot[]{
                new InventorySlot(items[0], 15, 28), new InventorySlot(items[1], 42, 28), new InventorySlot(items[2], 69, 28), new InventorySlot(items[3], 96, 28)
        });

    }


    @Override
    public ITextComponent getName() {
        return new TextComponentTranslation(ModBlocks.hunter_table.getTranslationKey());
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;//TODO not null?
    }
}
