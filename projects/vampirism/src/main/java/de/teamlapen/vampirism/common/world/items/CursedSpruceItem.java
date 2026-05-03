package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.blocks.CursedSpruceBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class CursedSpruceItem extends BlockItem {

    public CursedSpruceItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state != null && state.getBlock() instanceof CursedSpruceBlock && context.getItemInHand().has(ModDataComponents.ACTIVE)) {
            state = state.setValue(CursedSpruceBlock.ACTIVE, true);
        }

        return state;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
        if (stack.has(ModDataComponents.ACTIVE)) {
            tooltipComponents.accept(Component.translatable("tooltip.vampirism.active").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
