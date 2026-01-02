package de.teamlapen.factions.common.world.blocks.base;

import de.teamlapen.factions.common.util.ShapeUtil;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Implements some basic horizontal rotation functionality. <br>
 * Don't forget to use {@code horizontalBlock} in the mod's block model data provider so the model is actually rotated
 * If your subclass adds additional states: <br>
 * - Add {@code FACING} to the defaultState in the constructor
 * {@code this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));} <br>
 * - Add {@code FACING} to {@link Block#createBlockStateDefinition(StateDefinition.Builder)}
 */
public class BaseHorizontalBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Map<Direction, VoxelShape> shapes;

    /**
     * @param shape Shape (collision box) for a north facing placement. Rotated shapes are derived from this
     */
    public BaseHorizontalBlock(Properties properties, VoxelShape shape) {
        super(properties);
        this.shapes = ShapeUtil.getShapesRotatedFromNorth(shape);

        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    public BaseHorizontalBlock(Properties properties) {
        this(properties, Shapes.block());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
