package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.AlchemicalFireBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Simple placer item for alchemical fire. Also used in recipes
 *
 * @author maxanier
 */
public class AlchemicalFireItem extends Item {
    public AlchemicalFireItem() {
        super(new Properties().tab(VampirismMod.creativeTab));
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslatableComponent("item.vampirism.item_alchemical_fire.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.vampirism.item_alchemical_fire.desc2").withStyle(ChatFormatting.GRAY));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());

        if (ctx.getPlayer() != null && !ctx.getPlayer().mayUseItemAt(pos, ctx.getClickedFace(), ctx.getItemInHand())) {
            return InteractionResult.FAIL;
        } else {
            if (ctx.getLevel().isEmptyBlock(pos)) {
                ctx.getLevel().playSound(ctx.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, ctx.getPlayer().getRandom().nextFloat() * 0.4F + 0.8F);
                ctx.getLevel().setBlock(pos, ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState().setValue(AlchemicalFireBlock.AGE, 15), 11);
            }

            return InteractionResult.SUCCESS;
        }
    }

}
