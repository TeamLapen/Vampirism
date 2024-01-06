package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.blocks.AlchemicalFireBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Simple placer item for alchemical fire. Also used in recipes
 *
 * @author maxanier
 */
public class AlchemicalFireItem extends Item {
    public AlchemicalFireItem() {
        super(new Properties());
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.vampirism.item_alchemical_fire.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.vampirism.item_alchemical_fire.desc2").withStyle(ChatFormatting.GRAY));
    }

    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());

        if (ctx.getPlayer() != null && !ctx.getPlayer().mayUseItemAt(pos, ctx.getClickedFace(), ctx.getItemInHand())) {
            return InteractionResult.FAIL;
        } else {
            if (ctx.getLevel().isEmptyBlock(pos)) {
                ctx.getLevel().playSound(ctx.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, ctx.getPlayer().getRandom().nextFloat() * 0.4F + 0.8F);
                ctx.getLevel().setBlock(pos, ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState().setValue(AlchemicalFireBlock.AGE, 15), 11);
                ctx.getItemInHand().shrink(1);
            }

            return InteractionResult.SUCCESS;
        }
    }

}
