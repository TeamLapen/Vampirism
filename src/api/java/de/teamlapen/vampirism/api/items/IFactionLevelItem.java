package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
public interface IFactionLevelItem<T extends IFactionPlayer> { //TODO 1.17 extend IFactionExclusiveItem and modify

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

    @SuppressWarnings("RedundantCast")
    @OnlyIn(Dist.CLIENT)
    default void addFactionLevelToolTip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn, @Nullable PlayerEntity player) {
        TextFormatting factionC = TextFormatting.DARK_RED;
        TextFormatting levelC = TextFormatting.DARK_RED;
        TextFormatting skillC = TextFormatting.DARK_RED;

        LazyOptional<IFactionPlayerHandler> playerHandler = player != null && player.isAlive()?VampirismAPI.getFactionPlayerHandler(player):LazyOptional.empty();

        IPlayableFaction usingFaction = getUsingFaction(stack);
        ISkill requiredSkill = getRequiredSkill(stack);
        int reqLevel = getMinLevel(stack);
        if ((Boolean) playerHandler.map(p -> p.isInFaction(usingFaction)).orElse(false)) {
            factionC = TextFormatting.GREEN;
            if (playerHandler.map(IFactionPlayerHandler::getCurrentLevel).orElse(0) >= reqLevel) {
                levelC = TextFormatting.GREEN;
            }
            if ((Boolean)playerHandler.map(IFactionPlayerHandler::getCurrentFactionPlayer).flatMap(a -> a.map(b -> b.getSkillHandler().isSkillEnabled(requiredSkill))).orElse(false)){
                skillC = TextFormatting.GREEN;
            }
        }



        if (usingFaction == null && getMinLevel(stack) == 0)return;
        IFormattableTextComponent string = new StringTextComponent("").append(usingFaction == null ? new TranslationTextComponent("text.vampirism.all") : usingFaction.getNamePlural()).mergeStyle(factionC);
        if(getMinLevel(stack) > 0) {
            string.append(new StringTextComponent("@" + getMinLevel(stack)).mergeStyle(levelC));
        }
        tooltip.add(string);
        ISkill reqSkill = this.getRequiredSkill(stack);
        if (reqSkill != null) {
            tooltip.add(new TranslationTextComponent("text.vampirism.required_skill", reqSkill.getName()).mergeStyle(skillC));
        }
    }
}
