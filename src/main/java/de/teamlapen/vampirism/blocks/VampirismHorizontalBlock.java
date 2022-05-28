package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.data.BlockStateGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Implements some basic horizontal rotation functionality.
 * Don't forget to use `horizontalBlock` in {@link BlockStateGenerator} so the model is actually rotated
 * If your subclass adds additional states:
 * - Add FACING to the defaultState in the constructor
 * this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
 * - Add FACING to {@link Block#fillStateContainer(StateContainer.Builder)}
 */
public class VampirismHorizontalBlock extends VampirismBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final VoxelShape NORTH;
    private final VoxelShape EAST;
    private final VoxelShape SOUTH;
    private final VoxelShape WEST;

    /**
     * @param shape Shape (collision box) for a north facing placement. Rotated shapes are derived from this
     */
    public VampirismHorizontalBlock(Block.Properties properties, VoxelShape shape) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));

        NORTH = shape;
        EAST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.NINETY);
        SOUTH = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
    }

    public VampirismHorizontalBlock(Block.Properties properties) {
        this(properties, VoxelShapes.block());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
