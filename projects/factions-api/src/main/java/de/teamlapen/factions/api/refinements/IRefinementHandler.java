package de.teamlapen.factions.api.refinements;

import de.teamlapen.factions.api.FactionApi;
import de.teamlapen.factions.api.items.IRefinementItem;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IRefinementHandler<T extends IRefinementPlayer<T>> {

    static <T extends IRefinementPlayer<T>> Optional<IRefinementHandler<T>> get(Player player) {
        return FactionApi.factionPlayerHandler(player).getRefinementHandler();
    }

    NonNullList<ItemStack> getRefinementItems();

    void damageRefinements();

    /**
     * Equip the refinement set from the given stack to the appropriate slot
     * If no set is present, or it is from the wrong faction, the old set for the slot will be removed, but no new set will be added
     *
     * @return Whether the item was equipped
     */
    boolean equipRefinement(ItemStack stack);

    void removeRefinement(IRefinementItem.AccessorySlotType slot);

    boolean isRefinementEquipped(Holder<IRefinement> refinement);

    void updateItems();
    /**
     * remove all equipped refinements
     */
    void resetRefinements();

    /**
     * Reset all skills but reactivate the root skill of the faction
     */
    void reset();
}
