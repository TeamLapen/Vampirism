package de.teamlapen.vampirism.blocks;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CastleSlabBlock extends SlabBlock {
    private final CastleBricksBlock.EnumVariant variant;

    public CastleSlabBlock(CastleBricksBlock.EnumVariant variant) {
        super(Properties.of(Material.STONE).strength(2, 10).sound(SoundType.STONE));
        this.variant = variant;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader player, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.appendHoverText(stack, player, tooltip, advanced);
        tooltip.add(new TranslationTextComponent("block.vampirism.castle_block" + (variant == CastleBricksBlock.EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));

    }

    public CastleBricksBlock.EnumVariant getVariant() {
        return variant;
    }
}
