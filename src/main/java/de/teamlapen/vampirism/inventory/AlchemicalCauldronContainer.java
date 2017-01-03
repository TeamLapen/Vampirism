package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronContainer extends InventoryContainer {
    public AlchemicalCauldronContainer(InventoryPlayer invPlayer, InventorySlot.IInventorySlotInventory te) {
        super(invPlayer, te);
    }
}
