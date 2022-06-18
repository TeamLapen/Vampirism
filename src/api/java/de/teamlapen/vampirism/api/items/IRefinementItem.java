package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * represents an accessory item
 *
 * @implSpec should only be implemented by {@link Item}s
 */
public interface IRefinementItem extends IFactionExclusiveItem {

    /**
     * Gets the refinement set that is applied to this refinement item
     *
     * @param stack the refinement item stack
     * @return the applied refinement set
     */
    IRefinementSet getRefinementSet(ItemStack stack);

    /**
     * Gets the accessory slot where this item can be equipped
     *
     * @return accessory slot
     */
    AccessorySlotType getSlotType();

    /**
     * Apply refinement set to the given stack.
     * Note: Not all refinements can be applied to all accessory slot types
     *
     * @return Whether the set was successfully applied
     */
    default boolean applyRefinementSet(ItemStack stack, IRefinementSet set) {//TODO 1.17 remove default implementation
        return false;
    }

    @Nonnull
    default IFaction<?> getExclusiveFaction() { //TODO 1.17 remove
        return VReference.VAMPIRE_FACTION;
    }

    /**
     * Slots types for {@link IRefinementItem}
     * <br>
     * (like {@link net.minecraft.inventory.EquipmentSlotType} for other items)
     */
    enum AccessorySlotType {
        AMULET(0), RING(1), OBI_BELT(2);

        /**
         * slot index
         */
        private final int slot;

        AccessorySlotType(int slot) {
            this.slot = slot;
        }

        /**
         * @return slot index
         */
        public int getSlot() {
            return slot;
        }
    }
}
