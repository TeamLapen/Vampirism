package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.data.BlockStateGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implements some basic horizontal rotation functionality. <br>
 * Don't forget to use {@code horizontalBlock} in {@link BlockStateGenerator} so the model is actually rotated
 * If your subclass adds additional states: <br>
 * - Add {@code FACING} to the defaultState in the constructor
 * {@code this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));} <br>
 * - Add {@code FACING} to {@link Block#createBlockStateDefinition(StateDefinition.Builder)}
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
        this(properties, Shapes.block());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Nonnull
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nonnull
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
