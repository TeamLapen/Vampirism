package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
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
    private static final VoxelShape NORTH = makeShape();
    private static final VoxelShape EAST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape SOUTH = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape WEST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

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
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        if (newState.getBlock() != state.getBlock()) {
            BlockPos main = pos;
            Direction dir = state.get(FACING);
            int p = state.get(POSITION);
            if (p == 0) {
                dir = dir.getOpposite();
            } else if (p == 1) {
                main = pos.offset(dir.rotateY());
            } else if (p == 2) {
                main = pos.offset(dir.rotateY()).offset(dir.getOpposite());
            } else if (p == 3) {
                main = pos.offset(dir);
                dir = dir.getOpposite();
            }
            BlockPos cur = main;
            if (cur != pos) world.destroyBlock(cur, true);
            cur = main.offset(dir);
            if (cur != pos) world.destroyBlock(cur, true);
            cur = main.offset(dir.rotateYCCW());
            if (cur != pos) world.destroyBlock(cur, true);
            cur = main.offset(dir).offset(dir.rotateYCCW());
            if (cur != pos) world.destroyBlock(cur, true);
        }

    }

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader blockReader, BlockPos blockPos, ISelectionContext context) {
        switch (blockState.get(FACING)) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
        }
        return NORTH;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION);
    }

    private static VoxelShape makeShape() {
        return VoxelShapes.or(
                Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
                Block.makeCuboidShape(0.5, 1, 0, 1.4, 1.45, 16),
                Block.makeCuboidShape(0.9, 1.4, 0, 1.8, 1.85, 16),
                Block.makeCuboidShape(1.3, 1.8, 0, 2.2, 2.25, 16),
                Block.makeCuboidShape(1.7, 2.2, 0, 2.6, 2.65, 16),
                Block.makeCuboidShape(2.1, 2.6, 0, 3.0, 3.05, 16),
                Block.makeCuboidShape(2.5, 3.0, 0, 3.4, 3.45, 16),
                Block.makeCuboidShape(2.9, 3.4, 0, 3.8, 3.85, 16),
                Block.makeCuboidShape(3.3, 3.8, 0, 4.2, 4.25, 16),
                Block.makeCuboidShape(3.7, 4.2, 0, 4.6, 4.65, 16),
                Block.makeCuboidShape(4.1, 4.6, 0, 5.0, 5.05, 16),
                Block.makeCuboidShape(4.5, 5.0, 0, 5.4, 5.45, 16),
                Block.makeCuboidShape(4.9, 5.4, 0, 5.8, 5.85, 16),
                Block.makeCuboidShape(5.3, 5.8, 0, 6.2, 6.25, 16),
                Block.makeCuboidShape(5.7, 6.2, 0, 6.6, 6.65, 16),
                Block.makeCuboidShape(6.1, 6.6, 0, 7.0, 7.05, 16),
                Block.makeCuboidShape(6.5, 7.0, 0, 7.4, 7.45, 16),
                Block.makeCuboidShape(6.9, 7.4, 0, 7.8, 7.85, 16),
                Block.makeCuboidShape(7.3, 7.8, 0, 8.2, 8.25, 16),
                Block.makeCuboidShape(7.7, 8.2, 0, 8.6, 8.65, 16),
                Block.makeCuboidShape(8.1, 8.6, 0, 9.0, 9.05, 16),
                Block.makeCuboidShape(8.5, 9.0, 0, 9.4, 9.45, 16),
                Block.makeCuboidShape(8.9, 9.4, 0, 9.8, 9.85, 16),
                Block.makeCuboidShape(9.3, 9.8, 0, 10.2, 10.25, 16),
                Block.makeCuboidShape(9.7, 10.2, 0, 10.6, 10.65, 16),
                Block.makeCuboidShape(10.1, 10.6, 0, 11.0, 11.05, 16),
                Block.makeCuboidShape(10.5, 11.0, 0, 11.4, 11.45, 16),
                Block.makeCuboidShape(10.9, 11.4, 0, 11.8, 11.85, 16),
                Block.makeCuboidShape(11.3, 11.8, 0, 12.2, 12.25, 16),
                Block.makeCuboidShape(11.7, 12.2, 0, 12.6, 12.65, 16),
                Block.makeCuboidShape(12.1, 12.6, 0, 13.0, 13.05, 16),
                Block.makeCuboidShape(12.5, 13.0, 0, 13.4, 13.45, 16),
                Block.makeCuboidShape(12.9, 13.4, 0, 13.8, 13.85, 16),
                Block.makeCuboidShape(13.3, 13.8, 0, 14.2, 14.25, 16),
                Block.makeCuboidShape(13.7, 14.2, 0, 14.6, 14.65, 16),
                Block.makeCuboidShape(14.1, 14.6, 0, 15.0, 15.05, 16),
                Block.makeCuboidShape(14.5, 15.0, 0, 15.4, 15.45, 16),
                Block.makeCuboidShape(14.9, 15.4, 0, 15.8, 15.85, 16),
                Block.makeCuboidShape(15, 15, 0, 16, 16, 16)
        );
    }
}
