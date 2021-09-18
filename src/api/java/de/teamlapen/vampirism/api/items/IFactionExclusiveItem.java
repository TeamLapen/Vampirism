package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Should be implemented by all items that are supposed to be used by only a specific faction.
 */
public interface IFactionExclusiveItem {

    @OnlyIn(Dist.CLIENT)
    default void addFactionPoisonousToolTip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        IFaction<?> faction = player != null ? VampirismAPI.factionRegistry().getFaction(player) : null;
        if (faction == null ? !VReference.HUNTER_FACTION.equals(getExclusiveFaction()) : faction != getExclusiveFaction()) {
            tooltip.add(new TranslatableComponent("text.vampirism.poisonous_to_non", getExclusiveFaction().getNamePlural()).withStyle(ChatFormatting.DARK_RED));
        }
    }

    /**
     * @return The faction this item is meant for
     */
    @Nonnull
    IFaction<?> getExclusiveFaction();
}
