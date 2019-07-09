package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Item's implementing this can only be used by players that match the requirements.
 * Currently only affects {@link PlayerEntity#attackTargetEntityWithCurrentItem(Entity)} and {@link PlayerEntity#setActiveHand(Hand)}
 */
public interface IFactionLevelItem<T extends IFactionPlayer> {

    /**
     * @return The level the player has to be to use this item
     */
    int getMinLevel(@Nonnull ItemStack stack);

    /**
     * @return The skill required to use this or null if none
     */
    @Nullable
    ISkill getRequiredSkill(@Nonnull ItemStack stack);

    /**
     * @return The faction that can use this item or null if any
     */
    @Nullable
    IPlayableFaction<T> getUsingFaction(@Nonnull ItemStack stack);
}
