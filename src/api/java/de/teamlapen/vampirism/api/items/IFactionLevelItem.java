package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Item's implementing this can only be used by players that match the requirements.
 * Currently, only affects {@link Player#attack(Entity)} and {@link Player#startUsingItem(InteractionHand)}
 */
public interface IFactionLevelItem<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends IFactionExclusiveItem {

    @Override
    default void addFactionToolTips(@NotNull ItemStack stack, Item.@Nullable TooltipContext worldIn, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        IFactionPlayerHandler playerHandler = player != null ? VampirismAPI.factionPlayerHandler(player) : null;

        IFactionExclusiveItem.super.addFactionToolTips(stack, worldIn, tooltip, flagIn, player);

        boolean correctFaction = playerHandler != null && playerHandler.isInFaction(getExclusiveFaction(stack));
        int minLevel = getMinLevel(stack);
        if (minLevel > 1) {
            tooltip.add(Component.literal(" ").append(Component.translatable("text.vampirism.required_level", String.valueOf(minLevel))).withStyle(correctFaction ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
        }
        Holder<ISkill<?>> requiredSkill = requiredSkill(stack);
        if (requiredSkill != null) {
            tooltip.add(Component.literal(" ").append(Component.translatable("text.vampirism.required_skill", requiredSkill.value().getName())).withStyle(correctFaction && playerHandler.getCurrentSkillPlayer().map(p -> p.getSkillHandler().isSkillEnabled(requiredSkill)).orElse(false) ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
        }
    }



    /**
     * @return The level the player has to be to use this item
     */
    int getMinLevel(@NotNull ItemStack stack);

    /**
     * @return The skill required to use this or null if none
     */
    @Nullable
    default Holder<ISkill<?>> requiredSkill(@NotNull ItemStack stack) {
        var req = getRequiredSkill(stack);
        return req == null ? null : RegUtil.holder(req);
    }

    /**
     * use @link {@link #requiredSkill(ItemStack)} instead
     */
    @Deprecated(forRemoval = true)
    @Nullable
    default ISkill<T> getRequiredSkill(@NotNull ItemStack stack) {
        return null;
    }
}
