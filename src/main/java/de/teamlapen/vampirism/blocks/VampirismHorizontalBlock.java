package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.data.BlockStateGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

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
    public VampirismHorizontalBlock(String regName, Block.Properties properties, VoxelShape shape) {
        super(regName, properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));

        NORTH = shape;
        EAST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.NINETY);
        SOUTH = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
    }

    public VampirismHorizontalBlock(String regName, Block.Properties properties) {
        this(regName, properties, Shapes.block());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
