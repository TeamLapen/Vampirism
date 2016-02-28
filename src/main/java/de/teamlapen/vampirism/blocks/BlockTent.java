package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;

/**
 * Part of a 2x2 block tent
 * Position property contains the position within the 4 block arrangement
 */
public class BlockTent extends VampirismBlock {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger POSITION = PropertyInteger.create("position", 0, 3);
    private static final String name = "tent";

    public BlockTent() {
        this(name);
    }

    protected BlockTent(String name) {
        super(name, Material.cloth);
        this.setCreativeTab(null);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POSITION, 0));
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        meta += state.getValue(POSITION) << 2;
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int dir = meta & 3;
        int pos = (meta >> 2);
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(dir)).withProperty(POSITION, pos);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING, POSITION);
    }
}
