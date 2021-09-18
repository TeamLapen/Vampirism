package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import net.minecraft.world.item.ItemStack;

public interface IRefinementItem {

    /**
     * Gets {@link IRefinementSet} from a {@link IRefinementItem} stack
     *
     * @param stack stack of a {@link IRefinementItem}
     * @return {@link IRefinementSet} of the stack
     */
    IRefinementSet getRefinementSet(ItemStack stack);

    /**
     * @return slot this IRefinementItem can be equipped
     */
    AccessorySlotType getSlotType();

    enum AccessorySlotType {
        AMULET(0), RING(1), OBI_BELT(2);

        /**
         * slot index
         */
        private final int slot;

        AccessorySlotType(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }
}
