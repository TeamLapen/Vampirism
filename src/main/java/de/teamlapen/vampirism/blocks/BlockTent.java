package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        super(name, Material.CLOTH);
        this.setCreativeTab(null);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POSITION, 0));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        EnumFacing dir = state.getValue(FACING);
        int p = state.getValue(POSITION);
        if (p == 0) {
            dir = dir.getOpposite();
        } else if (p == 1) {
            pos = pos.offset(dir.rotateY());
        } else if (p == 2) {
            pos = pos.offset(dir.rotateY()).offset(dir.getOpposite());
        } else if (p == 3) {
            pos = pos.offset(dir);
            dir = dir.getOpposite();
        }
        worldIn.setBlockToAir(pos);
        worldIn.setBlockToAir(pos.offset(dir));
        worldIn.setBlockToAir(pos.offset(dir.rotateYCCW()));
        worldIn.setBlockToAir(pos.offset(dir).offset(dir.rotateYCCW()));


    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
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
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, POSITION);
    }
}
