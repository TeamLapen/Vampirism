package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Should be implemented by all items that are supposed to be used by only a specific faction.
 */
public interface IFactionExclusiveItem extends IItemProvider {

    @Deprecated
    default void addFactionPoisonousToolTip(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
        addFactionExclusiveToolTips(stack, worldIn, tooltip, flagIn, player);
    }

    default void addFactionExclusiveToolTips(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
        addOilDescTooltip(stack, worldIn, tooltip, flagIn, player);
        tooltip.add(StringTextComponent.EMPTY);
        tooltip.add(new TranslationTextComponent("text.vampirism.faction_specifics").withStyle(TextFormatting.GRAY));
        TextFormatting color = TextFormatting.GRAY;
        IFaction<?> faction = getExclusiveFaction();

        if (player != null) {
            color = VampirismAPI.factionRegistry().getFaction(player) == faction ? TextFormatting.DARK_GREEN : TextFormatting.DARK_RED;
        }
        tooltip.add(new StringTextComponent(" ").append(faction.getName()).append(new TranslationTextComponent("text.vampirism.faction_only")).withStyle(color));
    }

    /**
     * In case a faction specific item is applied with oil, this will add the oil specific tooltip at the correct position.
     * Otherwise, the default oil tooltip handler {@link de.teamlapen.vampirism.client.core.ClientEventHandler#onItemToolTip(net.minecraftforge.event.entity.player.ItemTooltipEvent)} would put it at the wrong position.
     * <p>
     * This must produce the same tooltip line as the default handler.
     */
    default void addOilDescTooltip(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
        if (!stack.hasTag()) return;
        CompoundNBT tag = stack.getTag().getCompound("applied_oil");
        if (tag.contains("oil")) {
            IOil oil = OilRegistry.getOilRegistry().getValue(new ResourceLocation(tag.getString("oil")));
            int duration = tag.getInt("duration");
            if (oil instanceof IApplicableOil && duration > 0) {
                ((IApplicableOil) oil).getToolTipLine(stack, ((IApplicableOil) oil), duration, flagIn).ifPresent(tooltip::add);
            }
        }
    }

    /**
     * @return The faction this item is meant for
     */
    @Nonnull
    IFaction<?> getExclusiveFaction();
}
