package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.AlchemicalFireBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("item.vampirism.item_alchemical_fire.desc1").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.vampirism.item_alchemical_fire.desc2").withStyle(TextFormatting.GRAY));
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());

        if (ctx.getPlayer() != null && !ctx.getPlayer().mayUseItemAt(pos, ctx.getClickedFace(), ctx.getItemInHand())) {
            return ActionResultType.FAIL;
        } else {
            if (ctx.getLevel().isEmptyBlock(pos)) {
                ctx.getLevel().playSound(ctx.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, ctx.getPlayer().getRandom().nextFloat() * 0.4F + 0.8F);
                ctx.getLevel().setBlock(pos, ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState().setValue(AlchemicalFireBlock.AGE, 15), 11);
            }

            return ActionResultType.SUCCESS;
        }
    }

}
