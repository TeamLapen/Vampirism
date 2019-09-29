package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
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

    private static final String regName = "castle_slab";
    private final CastleBricksBlock.EnumVariant variant;

    public CastleSlabBlock(CastleBricksBlock.EnumVariant variant) {
        super(Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
        this.variant = variant;

        this.setRegistryName(REFERENCE.MODID, regName + "_" + variant.getName());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader player, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(new TranslationTextComponent("block.vampirism.castle_block" + (variant == CastleBricksBlock.EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).applyTextStyle(TextFormatting.ITALIC));

    }

    public CastleBricksBlock.EnumVariant getVariant() {
        return variant;
    }
}
