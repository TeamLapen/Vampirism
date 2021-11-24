package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import net.minecraft.item.ItemStack;

/**
 * only extended by Item
 */
public interface IRefinementItem extends IFactionExclusiveItem {

    IRefinementSet getRefinementSet(ItemStack stack);

    AccessorySlotType getSlotType();

    /**
     * Apply refinement set to the given stack.
     * Note: Not all refinements can be applied to all accessory slot types
     *
     * @return Whether the set was successfully applied
     */
    boolean applyRefinementSet(ItemStack stack, IRefinementSet set);

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
