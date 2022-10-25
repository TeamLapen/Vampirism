package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Item's implementing this can only be used by players that match the requirements.
 * Currently only affects {@link PlayerEntity#attackTargetEntityWithCurrentItem(Entity)} and {@link PlayerEntity#setActiveHand(Hand)}
 */
public interface IFactionLevelItem<T extends IFactionPlayer<?>> { //TODO 1.17 extend IFactionExclusiveItem and modify

    @OnlyIn(Dist.CLIENT)
    @Deprecated
    default void addFactionLevelToolTip(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
        addFactionToolTips(stack, worldIn, tooltip, flagIn, player);
    }

    default void addFactionToolTips(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
        LazyOptional<IFactionPlayerHandler> playerHandler = player != null && player.isAlive() ? VampirismAPI.getFactionPlayerHandler(player) : LazyOptional.empty();

        addFactionLevelOilDescTooltip(stack, worldIn, tooltip, flagIn, player);
        tooltip.add(StringTextComponent.EMPTY);
        tooltip.add(new TranslationTextComponent("text.vampirism.faction_specifics").withStyle(TextFormatting.GRAY));
        TextFormatting color = TextFormatting.GRAY;
        IFaction<?> faction = getUsingFaction(stack);

        if (player != null) {
            color = VampirismAPI.factionRegistry().getFaction(player) == faction ? TextFormatting.DARK_GREEN : TextFormatting.DARK_RED;
        }
        tooltip.add(new StringTextComponent(" ").append(faction.getName()).append(new TranslationTextComponent("text.vampirism.faction_only")).withStyle(color));

        boolean correctFaction = playerHandler.map(f -> f.isInFaction(getUsingFaction(stack))).orElse(false);
        int minLevel = getMinLevel(stack);
        if (minLevel > 1) {
            tooltip.add(new TranslationTextComponent(" Required Level:").append(" ").append(String.valueOf(minLevel)).withStyle(correctFaction ? TextFormatting.DARK_GREEN : TextFormatting.DARK_RED));
        }
        ISkill requiredSkill = getRequiredSkill(stack);
        if (requiredSkill != null) {
            tooltip.add(new StringTextComponent(" ").append(new TranslationTextComponent("text.vampirism.required_skill").append(requiredSkill.getName())).withStyle(correctFaction && playerHandler.map(IFactionPlayerHandler::getCurrentFactionPlayer).flatMap(p -> p.map(d-> d.getSkillHandler().isSkillEnabled(requiredSkill))).orElse(false) ? TextFormatting.DARK_GREEN : TextFormatting.DARK_RED));
        }
    }

    /**
     * In case a faction specific item is applied with oil, this will add the oil specific tooltip at the correct position.
     * Otherwise, the default oil tooltip handler {@link de.teamlapen.vampirism.client.core.ClientEventHandler#onItemToolTip(net.minecraftforge.event.entity.player.ItemTooltipEvent)} would put it at the wrong position.
     * <p>
     * This must produce the same tooltip line as the default handler.
     */
    default void addFactionLevelOilDescTooltip(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
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
