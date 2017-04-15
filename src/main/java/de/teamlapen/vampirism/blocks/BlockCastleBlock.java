/*
 * Copyright (c) 2016. @maxanier
 * Published under LGPLv3
 *
 */

package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;


public class BlockCastleBlock extends VampirismBlock {
    public static final PropertyEnum<BlockCastleBlock.EnumType> VARIANT = PropertyEnum.create("variant", BlockCastleBlock.EnumType.class);
    private static final String name = "castleBlock";

    public BlockCastleBlock() {
        super(name, Material.ROCK);
        this.setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.DARK_BRICK));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumType type : EnumType.values()) {
            list.add(new ItemStack(itemIn, 1, type.getMetadata()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(VARIANT) == EnumType.DARK_BRICK_BLOODY) {
            if (rand.nextInt(180) == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.ambient_castle, SoundCategory.AMBIENT, 0.8F, 1.0F, false);
            }

        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    public enum EnumType implements IStringSerializable {
        DARK_BRICK(0, "dark_brick"),
        PURPLE_BRICK(1, "purple_brick"),
        DARK_BRICK_BLOODY(2, "dark_brick_bloody");

        /**
         * Array of the Block's BlockStates
         */
        private static final EnumType[] META_LOOKUP = new EnumType[values().length];

        static {
            for (EnumType blockstone$enumtype : values()) {
                META_LOOKUP[blockstone$enumtype.getMetadata()] = blockstone$enumtype;
            }
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        /**
         * The BlockState's metadata.
         */
        private final int meta;
        /**
         * The EnumType's name.
         */
        private final String name;
        private final String unlocalizedName;

        EnumType(int metaIn, String nameIn) {
            this.meta = metaIn;
            this.name = nameIn;
            this.unlocalizedName = nameIn;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }

    }


}
