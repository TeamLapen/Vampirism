package de.teamlapen.vampirism.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
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
import java.util.function.Supplier;

public class CastleStairsBlock extends StairsBlock {
    private final static String REGNAME_BASE = "castle_stairs";
    private final CastleBricksBlock.EnumVariant variant;

    public CastleStairsBlock(Supplier<BlockState> state, CastleBricksBlock.EnumVariant variant) {
        super(state, Properties.of(Material.STONE).strength(2, 10).sound(SoundType.STONE));
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
