package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Should be implemented by all items that are supposed to be used by only a specific faction.
 */
public interface IFactionExclusiveItem extends ItemLike {

    @Deprecated(since = "1.9", forRemoval = true)
    default void addFactionPoisonousToolTip(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        addFactionToolTips(stack, worldIn, tooltip, flagIn, player);
    }

    default void addFactionToolTips(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        addOilDescTooltip(stack, worldIn, tooltip, flagIn, player);
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("text.vampirism.faction_specifics").withStyle(ChatFormatting.GRAY));
        ChatFormatting color = ChatFormatting.GRAY;
        IFaction<?> faction = getExclusiveFaction(stack);

        if (faction != null) {
            if (player != null) {
                color = VampirismAPI.factionRegistry().getFaction(player) == faction ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED;
            }
            tooltip.add(Component.literal(" ").append(faction.getName()).append(Component.translatable("text.vampirism.faction_only")).withStyle(color));
        }
    }

    /**
     * In case a faction specific item is applied with oil, this will add the oil specific tooltip at the correct position.
     * Otherwise, the default oil tooltip handler {@link de.teamlapen.vampirism.client.core.ClientEventHandler#onItemToolTip(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent)} would put it at the wrong position.
     * <p>
     * This must produce the same tooltip line as the default handler.
     */
    @SuppressWarnings("JavadocReference")
    default void addOilDescTooltip(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag().getCompound("applied_oil");
        if (tag.contains("oil")) {
            IOil oil = VampirismRegistries.OILS.get().get(new ResourceLocation(tag.getString("oil")));
            int duration = tag.getInt("duration");
            if (oil instanceof IApplicableOil && duration > 0) {
                ((IApplicableOil) oil).getToolTipLine(stack, ((IApplicableOil) oil), duration, flagIn).ifPresent(tooltip::add);
            }
        }
    }

    /**
     * @return The faction that can use this item or null if any
     */
    @Nullable
    IFaction<?> getExclusiveFaction(@NotNull ItemStack stack);
}
