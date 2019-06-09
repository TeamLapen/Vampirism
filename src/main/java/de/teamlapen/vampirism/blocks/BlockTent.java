package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Part of a 2x2 block tent
 * Position property contains the position within the 4 block arrangement
 */
public class BlockTent extends VampirismBlock {
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    private static final String name = "tent";

    public BlockTent() {
        this(name);
    }

    protected BlockTent(String name) {
        super(name, Properties.create(Material.CLOTH).hardnessAndResistance(0.6f).sound(SoundType.CLOTH));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH).with(POSITION, 0));
    }


    @Override
    public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        if (newState.getBlock() != state.getBlock()) {
            EnumFacing dir = state.get(FACING);
            int p = state.get(POSITION);
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
            world.removeBlock(pos);
            world.removeBlock(pos.offset(dir));
            world.removeBlock(pos.offset(dir.rotateYCCW()));
            world.removeBlock(pos.offset(dir).offset(dir.rotateYCCW()));
        }

    }



    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }


    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING, POSITION);
    }
}
