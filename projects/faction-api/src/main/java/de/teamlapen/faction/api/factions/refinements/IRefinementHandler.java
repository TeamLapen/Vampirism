package de.teamlapen.faction.api.factions.refinements;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IRefinementHandler extends IRefinementAccess {

    static Optional<IRefinementHandler> get(Player player) {
        return FactionsApi.factionPlayerHandler(player).getExtension(IRefinementHandler.class);
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

    void updateItems();
    /**
     * remove all equipped refinements
     */
    void resetRefinements();

    /**
     * Reset all skills but reactivate the root skill of the faction
     */
    void reset();

    @Override
    default Optional<IRefinementHandler> handler() {
        return Optional.of(this);
    }
}
