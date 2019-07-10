package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * Part of a 2x2 block tent
 * Position property contains the position within the 4 block arrangement
 */
public class TentBlock extends VampirismBlock {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    private static final String name = "tent";

    public TentBlock() {
        this(name);
    }

    protected TentBlock(String name) {
        super(name, Properties.create(Material.WOOL).hardnessAndResistance(0.6f).sound(SoundType.CLOTH));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(POSITION, 0));
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        if (newState.getBlock() != state.getBlock()) {
            Direction dir = state.get(FACING);
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
            world.removeBlock(pos, isMoving);
            world.removeBlock(pos.offset(dir), isMoving);
            world.removeBlock(pos.offset(dir.rotateYCCW()), isMoving);
            world.removeBlock(pos.offset(dir).offset(dir.rotateYCCW()), isMoving);
        }

    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION);
    }
}
