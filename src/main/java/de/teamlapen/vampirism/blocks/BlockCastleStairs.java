package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCastleStairs extends BlockStairs {
    private final static String REGNAME_BASE = "castle_stairs_";
    private final BlockCastleBlock.EnumVariant variant;

    public BlockCastleStairs(IBlockState state, String name) {
        super(state, Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
        setRegistryName(REFERENCE.MODID, REGNAME_BASE + name);
        variant = ((BlockCastleBlock) state.getBlock()).getVariant();
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader player, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(new TextComponentTranslation(getTranslationKey() + (variant == BlockCastleBlock.EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).applyTextStyle(TextFormatting.ITALIC));

    }

}
