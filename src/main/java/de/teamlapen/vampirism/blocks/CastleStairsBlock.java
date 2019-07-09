package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
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

public class CastleStairsBlock extends StairsBlock {
    private final static String REGNAME_BASE = "castle_stairs_";
    private final CastleBricksBlock.EnumVariant variant;

    public CastleStairsBlock(BlockState state, String name) {
        super(state, Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
        setRegistryName(REFERENCE.MODID, REGNAME_BASE + name);
        variant = ((CastleBricksBlock) state.getBlock()).getVariant();
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader player, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(new TranslationTextComponent(getTranslationKey() + (variant == CastleBricksBlock.EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).applyTextStyle(TextFormatting.ITALIC));

    }

}
