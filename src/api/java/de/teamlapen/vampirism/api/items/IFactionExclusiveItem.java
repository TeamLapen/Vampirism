package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Should be implemented by all items that are supposed to be used by only a specific faction.
 */
public interface IFactionExclusiveItem extends ItemLike {

    @OnlyIn(Dist.CLIENT)
    default void addFactionPoisonousToolTip(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        IFaction<?> faction = player != null ? VampirismAPI.factionRegistry().getFaction(player) : null;
        if (faction == null ? !VReference.HUNTER_FACTION.equals(getExclusiveFaction(stack)) : faction != getExclusiveFaction(stack)) {
            tooltip.add(Component.translatable("text.vampirism.poisonous_to_non", getExclusiveFaction(stack).getNamePlural()).withStyle(ChatFormatting.DARK_RED));
        }
    }

    /**
     * @return The faction that can use this item or null if any
     */
    @Nullable
    IFaction<?> getExclusiveFaction(@NotNull ItemStack stack);
}
