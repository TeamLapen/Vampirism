package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismDataComponents;
import de.teamlapen.vampirism.api.components.IAppliedOilContent;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Should be implemented by all items that are supposed to be used by only a specific faction.
 */
public interface IFactionExclusiveItem extends ItemLike {

    default void addFactionToolTips(@NotNull ItemStack stack, @Nullable Item.TooltipContext context, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        addOilDescTooltip(stack, context, tooltip, flagIn, player);
        ChatFormatting color = ChatFormatting.GRAY;
        IFaction<?> faction = getExclusiveFaction(stack);

        if (faction != null) {
            if (player != null) {
                color = VampirismAPI.factionRegistry().getFaction(player) == faction ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED;
            }
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("text.vampirism.faction_exclusive", faction.getName().copy().withStyle(color)));
        }
    }

    /**
     * In case a faction specific item is applied with oil, this will add the oil specific tooltip at the correct position.
     * Otherwise, the default oil tooltip handler {@link de.teamlapen.vampirism.client.core.ClientEventHandler#onItemToolTip(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent)} would put it at the wrong position.
     * <p>
     * This must produce the same tooltip line as the default handler.
     */
    @SuppressWarnings("JavadocReference")
    default void addOilDescTooltip(@NotNull ItemStack stack, @Nullable Item.TooltipContext context, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        IAppliedOilContent appliedOil = stack.get(VampirismDataComponents.APPLIED_OIL.get());
        if (appliedOil != null) {
            if (appliedOil.duration() > 0) {
                appliedOil.oil().value().getToolTipLine(stack, appliedOil.oil().value(), appliedOil.duration(), flagIn).ifPresent(tooltip::add);
            }
        }
    }

    /**
     * @return The faction that can use this item or null if any
     */
    @Nullable
    IFaction<?> getExclusiveFaction(@NotNull ItemStack stack);
}
