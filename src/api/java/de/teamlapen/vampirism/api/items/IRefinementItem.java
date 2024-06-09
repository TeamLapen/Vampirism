package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * represents an accessory item
 *
 * @implSpec should only be implemented by {@link net.minecraft.world.item.Item}s
 */
public interface IRefinementItem extends IFactionExclusiveItem {

    /**
     * Gets the refinement set that is applied to this refinement item
     *
     * @param stack the refinement item stack
     * @return the applied refinement set
     */
    @Nullable IRefinementSet getRefinementSet(ItemStack stack);

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
    boolean applyRefinementSet(ItemStack stack, IRefinementSet set);

    Holder<? extends IPlayableFaction<?>> getExclusiveFaction(@NotNull ItemStack stack);

    /**
     * Slots types for {@link IRefinementItem}
     * <br>
     * (like {@link net.minecraft.world.entity.EquipmentSlot} for other items)
     */
    enum AccessorySlotType implements StringRepresentable {
        AMULET("amulet",0),
        RING("ring", 1),
        OBI_BELT("obi_belt",2);

        private final String name;
        /**
         * slot index
         */
        private final int slot;

        AccessorySlotType(String name, int slot) {
            this.name = name;
            this.slot = slot;
        }

        /**
         * @return slot index
         */
        public int getSlot() {
            return slot;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
