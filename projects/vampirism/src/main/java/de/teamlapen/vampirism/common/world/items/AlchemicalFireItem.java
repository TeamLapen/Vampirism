package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.world.blocks.AlchemicalFireBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Consumer;

/**
 * Simple placer item for alchemical fire. Also used in recipes
 *
 * @author maxanier
 */
public class AlchemicalFireItem extends Item {
    
    public AlchemicalFireItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        tooltip.accept(Component.translatable("item.vampirism.item_alchemical_fire.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.vampirism.item_alchemical_fire.desc2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        if (context.getPlayer() != null && !context.getPlayer().mayUseItemAt(pos, context.getClickedFace(), context.getItemInHand())) {
            return InteractionResult.FAIL;
        } else {
            if (context.getPlayer() != null && context.getLevel().isEmptyBlock(pos) ) {
                context.getLevel().playSound(context.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, context.getPlayer().getRandom().nextFloat() * 0.4F + 0.8F);
                context.getLevel().setBlock(pos, ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState().setValue(AlchemicalFireBlock.AGE, 15), 11);
                context.getItemInHand().shrink(1);
            }

            return InteractionResult.SUCCESS;
        }
    }
}
