package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import net.minecraft.item.ItemStack;

public interface IRefinementItem {

    IRefinementSet getRefinementSet(ItemStack stack);

    AccessorySlotType getSlotType();

    enum AccessorySlotType {
        AMULET(0), RING(1), OBI_BELT(2);

        private final int slot;

        AccessorySlotType(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }
}
