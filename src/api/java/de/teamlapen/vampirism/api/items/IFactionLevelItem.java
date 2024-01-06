package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Item's implementing this can only be used by players that match the requirements.
 * Currently, only affects {@link Player#attack(Entity)} and {@link Player#startUsingItem(InteractionHand)}
 */
public interface IFactionLevelItem<T extends IFactionPlayer<T>> extends IFactionExclusiveItem {

    @Deprecated(since = "1.9", forRemoval = true)
    default void addFactionLevelToolTip(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        addFactionToolTips(stack, worldIn, tooltip, flagIn, player);
    }

    @Override
    default void addFactionToolTips(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        Optional<IFactionPlayerHandler> playerHandler = player != null && player.isAlive() ? VampirismAPI.getFactionPlayerHandler(player) : Optional.empty();

        IFactionExclusiveItem.super.addFactionToolTips(stack, worldIn, tooltip, flagIn, player);

        boolean correctFaction = playerHandler.map(f -> f.isInFaction(getExclusiveFaction(stack))).orElse(false);
        int minLevel = getMinLevel(stack);
        if (minLevel > 1) {
            tooltip.add(Component.literal(" ").append(Component.translatable("text.vampirism.required_level", String.valueOf(minLevel))).withStyle(correctFaction ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
        }
        ISkill<T> requiredSkill = getRequiredSkill(stack);
        if (requiredSkill != null) {
            tooltip.add(Component.literal(" ").append(Component.translatable("text.vampirism.required_skill", requiredSkill.getName())).withStyle(correctFaction && playerHandler.map(IFactionPlayerHandler::getCurrentFactionPlayer).flatMap(p -> p.map(d-> d.getSkillHandler().isSkillEnabled(requiredSkill))).orElse(false) ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
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
    ISkill<T> getRequiredSkill(@NotNull ItemStack stack);
}
