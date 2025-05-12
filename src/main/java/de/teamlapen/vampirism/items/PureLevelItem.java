package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.PureLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PureLevelItem extends Item implements ModDisplayItemGenerator.CreativeTabItemProvider {

    public PureLevelItem(Properties properties) {
        super(properties.component(ModDataComponents.PURE_LEVEL, PureLevel.LOW));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("text.vampirism.purity", stack.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.EMPTY).level() + 1).withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(PureLevel.pureBlood(this, 0));
        for (int i = 1; i < 5; i++) {
            output.accept(PureLevel.pureBlood(this, i), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }
}
