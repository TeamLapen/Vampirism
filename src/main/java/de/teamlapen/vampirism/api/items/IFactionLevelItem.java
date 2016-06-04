package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

/**
 * Item's implementing this can only be used by players that match the requirements.
 * Currently only affects {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity)} and {@link EntityPlayer#setActiveHand(EnumHand)}
 */
public interface IFactionLevelItem<T extends IFactionPlayer> {

    /**
     * @return The level the player has to be to use this item
     */
    int getMinLevel(ItemStack stack);

    /**
     *
     * @return The skill required to use this or null if none
     */
    @Nullable
    ISkill<T> getRequiredSkill(ItemStack stack);

    /**
     * @return The faction that can use this item or null if any
     */
    @Nullable
    IPlayableFaction<T> getUsingFaction(ItemStack stack);
}
