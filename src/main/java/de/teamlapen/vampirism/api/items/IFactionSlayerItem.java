package de.teamlapen.vampirism.api.items;

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
    float getDamageMultiplierForFaction(ItemStack stack);

    /**
     * Modify damage for this faction
     *
     * @return
     */
    IFaction getSlayedFaction();
}
