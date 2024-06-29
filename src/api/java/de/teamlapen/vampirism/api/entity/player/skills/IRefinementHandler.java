package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IRefinementPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.api.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IRefinementHandler<T extends IRefinementPlayer<T>> {

    static <T extends IRefinementPlayer<T>> Optional<IRefinementHandler<T>> get(Player player) {
        return VampirismAPI.factionPlayerHandler(player).getRefinementHandler();
    }

    NonNullList<ItemStack> getRefinementItems();

    void damageRefinements();

    /**
     * Equip the refinement set from the given stack to the appropriate slot
     * If no set is present, or it is from the wrong faction, the old set for the slot will be removed, but no new set will be added
     *
     * @return Whether the item was equipped
     */
    boolean equipRefinementItem(ItemStack stack);

    void removeRefinementItem(IRefinementItem.AccessorySlotType slot);

    @Deprecated
    default boolean isRefinementEquipped(IRefinement refinement) {
        return isRefinementEquipped(RegUtil.holder(refinement));
    }

    boolean isRefinementEquipped(Holder<IRefinement> refinement);

    /**
     * remove all equipped refinements
     */
    void resetRefinements();

    /**
     * Reset all skills but reactivate the root skill of the faction
     */
    void reset();
}
