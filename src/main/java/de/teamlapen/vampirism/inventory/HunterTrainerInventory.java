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
        super(new InventorySlot[]{new InventorySlot(Items.IRON_INGOT, 27, 26), new InventorySlot(Items.GOLD_INGOT, 57, 26), new InventorySlot(ModItems.hunterIntel, 86, 26)});
    }

    @Override
    public String getName() {
        return "entity." + ModEntities.HUNTER_TRAINER + ".name";
    }
}
