package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CastleBricksBlock extends VampirismBlock {
    private final EnumVariant variant;

    public CastleBricksBlock(EnumVariant variant) {
        super(Properties.of(Material.STONE).strength(2, 10).sound(SoundType.STONE));
        this.variant = variant;

    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if (!CastleStairsBlock.isStairs(state) && variant == EnumVariant.DARK_BRICK_BLOODY) {
            if (rand.nextInt(180) == 0) {
                world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.AMBIENT_CASTLE.get(), SoundCategory.AMBIENT, 0.8F, 1.0F, false);
            }

        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.appendHoverText(stack, world, tooltip, advanced);
        tooltip.add(new TranslationTextComponent("block.vampirism.castle_block" + (variant == EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
    }

    public EnumVariant getVariant() {
        return variant;
    }


    public enum EnumVariant implements IStringSerializable {
        DARK_BRICK("dark_brick"),
        PURPLE_BRICK("purple_brick"),
        DARK_BRICK_BLOODY("dark_brick_bloody"),
        NORMAL_BRICK("normal_brick"),
        DARK_STONE("dark_stone");

        private final String name;

        EnumVariant(String name) {
            this.name = name;
        }

        public String getName() {
            return this.getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
