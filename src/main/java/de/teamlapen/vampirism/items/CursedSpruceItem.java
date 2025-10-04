package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.blocks.CursedSpruceBlock;
import de.teamlapen.vampirism.core.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CursedSpruceItem extends BlockItem {

    public CursedSpruceItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state != null && state.getBlock() instanceof CursedSpruceBlock && context.getItemInHand().has(ModDataComponents.ACTIVE)) {
            state = state.setValue(CursedSpruceBlock.ACTIVE, true);
        }

        return state;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (stack.has(ModDataComponents.ACTIVE)) {
            tooltipComponents.add(Component.translatable("text.vampirism.active").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
