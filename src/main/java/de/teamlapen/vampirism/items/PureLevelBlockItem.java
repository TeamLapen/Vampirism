package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.PureLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class PureLevelBlockItem extends BlockItem {

    public PureLevelBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("text.vampirism.purity", stack.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.EMPTY).level() + 1).withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
