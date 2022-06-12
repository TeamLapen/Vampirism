package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Should be implemented by all items that are supposed to be used by only a specific faction.
 */
public interface IFactionExclusiveItem extends IItemProvider {

    @OnlyIn(Dist.CLIENT)
    default void addFactionPoisonousToolTip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
        IFaction<?> faction = player != null ? VampirismAPI.factionRegistry().getFaction(player) : null;
        if (faction == null ? !VReference.HUNTER_FACTION.equals(getExclusiveFaction()) : faction != getExclusiveFaction()) {
            tooltip.add(new TranslationTextComponent("text.vampirism.poisonous_to_non", getExclusiveFaction().getNamePlural()).withStyle(TextFormatting.DARK_RED));
        }
    }

    /**
     * @return The faction this item is meant for
     */
    @Nonnull
    IFaction<?> getExclusiveFaction();
}
