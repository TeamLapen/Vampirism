package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class BlockCastleBlock extends VampirismBlock {
    private static final String name = "castle_block";
    private final EnumVariant variant;

    public BlockCastleBlock(EnumVariant variant) {
        super(name + "_" + variant.getName(), Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
        this.variant = variant;

    }


    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(new TextComponentTranslation(getTranslationKey() + (variant == EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).applyTextStyle(TextFormatting.ITALIC));
    }


    @Override
    public void animateTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (!BlockCastleStairs.isBlockStairs(state) && variant == EnumVariant.DARK_BRICK_BLOODY) {
            if (rand.nextInt(180) == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.ambient_castle, SoundCategory.AMBIENT, 0.8F, 1.0F, false);
            }

        }
    }

    public EnumVariant getVariant() {
        return variant;
    }


    public enum EnumVariant implements IStringSerializable {
        DARK_BRICK(0, "dark_brick"),
        PURPLE_BRICK(1, "purple_brick"),
        DARK_BRICK_BLOODY(2, "dark_brick_bloody"),
        NORMAL_BRICK(3, "normal_brick"),
        DARK_STONE(4, "dark_stone");


        /**
         * The BlockState's metadata.
         */
        private final int meta;

        /**
         * The EnumType's name.
         */
        private final String name;
        private final String unlocalizedName;

        EnumVariant(int metaIn, String nameIn) {
            this.meta = metaIn;
            this.name = nameIn;
            this.unlocalizedName = nameIn;
        }


        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }

    }


}
