package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Item deals extra damage to a specific faction
 */
public interface IFactionSlayerItem {

    /**
     * @param stack The used item stack
     * @return Damage modifier for attacking the specific faction
     */
    float getDamageMultiplierForFaction(@Nonnull ItemStack stack);

    /**
     * @return Modify damage for this faction
     */
    IFaction getSlayedFaction();
}
