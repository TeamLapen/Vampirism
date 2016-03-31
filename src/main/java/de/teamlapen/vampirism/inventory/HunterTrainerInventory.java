package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.init.Items;

/**
 * Simple inventory for the Hunter Trainer
 */
public class HunterTrainerInventory extends SimpleInventory {
    public HunterTrainerInventory() {
        super(new InventorySlot[]{new InventorySlot(Items.iron_ingot, 27, 26), new InventorySlot(Items.gold_ingot, 57, 26), new InventorySlot(ModItems.hunterIntel, 86, 26)});
    }

    @Override
    public String getName() {
        return "entity." + ModEntities.HUNTER_TRAINER + ".name";
    }
}
