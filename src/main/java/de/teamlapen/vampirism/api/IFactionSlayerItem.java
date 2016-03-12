package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.item.ItemStack;

/**
 * Item deals extra damage to a specific faction
 */
public interface IFactionSlayerItem {

    /**
     * @param stack
     * @return Damage modifier for attacking the specific faction
     */
    float getDamageMultiplier(ItemStack stack);

    /**
     * Modify damage for this faction
     *
     * @return
     */
    IFaction getSlayedFaction();
}
