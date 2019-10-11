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
import net.minecraft.util.BlockRenderLayer;
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
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
                Block.makeCuboidShape(1, 1, 0, 2, 2, 16),
                Block.makeCuboidShape(2, 2, 0, 3, 3, 16),
                Block.makeCuboidShape(3, 3, 0, 4, 4, 16),
                Block.makeCuboidShape(4, 4, 0, 5, 5, 16),
                Block.makeCuboidShape(5, 5, 0, 6, 6, 16),
                Block.makeCuboidShape(6, 6, 0, 7, 7, 16),
                Block.makeCuboidShape(7, 7, 0, 8, 8, 16),
                Block.makeCuboidShape(8, 8, 0, 9, 9, 16),
                Block.makeCuboidShape(9, 9, 0, 10, 10, 16),
                Block.makeCuboidShape(10, 10, 0, 11, 11, 16),
                Block.makeCuboidShape(11, 11, 0, 12, 12, 16),
                Block.makeCuboidShape(12, 12, 0, 13, 13, 16),
                Block.makeCuboidShape(13, 13, 0, 14, 14, 16),
                Block.makeCuboidShape(14, 14, 0, 15, 15, 16),
                Block.makeCuboidShape(15, 15, 0, 16, 16, 16));
    }
}
