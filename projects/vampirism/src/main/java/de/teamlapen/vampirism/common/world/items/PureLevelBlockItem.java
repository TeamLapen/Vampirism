package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class PureLevelBlockItem extends BlockItem {

    public PureLevelBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(stack.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.EMPTY).getPurityTooltip());
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }
}
