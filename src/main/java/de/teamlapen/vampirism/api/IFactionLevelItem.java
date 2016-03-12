package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Item's implementing this can only be used by players that match the requirements.
 * Currently only affects {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity)} and {@link EntityPlayer#setItemInUse(ItemStack, int)}
 */
public interface IFactionLevelItem<T extends IFactionPlayer> {
    /**
     * Can be used to check for an activated skill or something else
     *
     * @param player
     * @param stack  The item stack
     * @return
     */
    boolean canUse(T player, ItemStack stack);

    /**
     * @return The level the player has to be to use this item
     */
    int getMinLevel();

    /**
     * @return The faction that can use this item
     */
    IPlayableFaction<T> getUsingFaction();
}
