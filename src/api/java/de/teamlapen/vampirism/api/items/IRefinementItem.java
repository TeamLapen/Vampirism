package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import net.minecraft.world.item.ItemStack;

/**
 * only extended by Item
 */
public interface IRefinementItem extends IFactionExclusiveItem {

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

    /**
     * Apply refinement set to the given stack.
     * Note: Not all refinements can be applied to all accessory slot types
     *
     * @return Whether the set was successfully applied
     */
    boolean applyRefinementSet(ItemStack stack, IRefinementSet set);

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
