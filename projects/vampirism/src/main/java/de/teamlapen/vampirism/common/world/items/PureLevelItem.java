package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class PureLevelItem extends Item implements BaseDisplayItemGenerator.CreativeTabItemProvider {

    public PureLevelItem(Properties properties) {
        super(properties.component(ModDataComponents.PURE_LEVEL, PureLevel.LOW));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int level = stack.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.EMPTY).level() ;
        tooltipComponents.accept(Component.translatable("tooltip.vampirism.purity", level + 1).withStyle(level == 5 ? ChatFormatting.DARK_PURPLE : ChatFormatting.DARK_RED));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(PureLevel.pureBlood(this, 0));
        for (int i = 1; i < 6; i++) {
            output.accept(PureLevel.pureBlood(this, i), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }
}
