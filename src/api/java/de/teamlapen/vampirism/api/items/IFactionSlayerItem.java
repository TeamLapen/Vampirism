package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;

/**
 * Item deals extra damage to a specific faction. Currently only has an effect if wielded by a player
 */
public interface IFactionSlayerItem extends ItemLike {

    /**
     * @param stack The used item stack
     * @return Damage modifier for attacking the specific faction
     */
    float getDamageMultiplierForFaction(@Nonnull ItemStack stack);

    /**
     * @return Modify damage for this faction
     */
    IFaction<?> getSlayedFaction();
}
