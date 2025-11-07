package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class MinionUpgradeItem extends Item {

    private final int minLevel;
    private final int maxLevel;
    private final Holder<? extends IFaction<?>> faction;

    public MinionUpgradeItem(int minLevel, int maxLevel, Holder<? extends IFaction<?>> faction, Properties properties) {
        super(properties);
        this.faction = faction;
        this.maxLevel = maxLevel;
        this.minLevel = minLevel;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        tooltip.accept(Component.translatable("item.vampirism.minion_upgrade_item.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("text.vampirism.for_to_levels", minLevel + 1, maxLevel + 1).withStyle(ChatFormatting.GRAY));
    }

    public Holder<? extends IFaction<?>> getFaction() {
        return faction;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMinLevel() {
        return minLevel;
    }
}
