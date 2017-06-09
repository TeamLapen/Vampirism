package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.item.ItemStack;

/**
 * Item deals extra damage to a specific faction
 */
public interface IFactionSlayerItem {

    /**
     * @param stack The used item stack
     * @return Damage modifier for attacking the specific faction
     */
    float getDamageMultiplierForFaction(ItemStack stack);

    /**
     * @return Modify damage for this faction
     */
    IFaction getSlayedFaction();
}
