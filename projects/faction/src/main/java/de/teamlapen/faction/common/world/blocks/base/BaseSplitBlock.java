package de.teamlapen.faction.common.world.blocks.base;


import de.teamlapen.faction.common.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class BaseSplitBlock extends Block {

    public static final EnumProperty<Direction> FACING = HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    
    private final VoxelShape NORTH1;
    private final VoxelShape EAST1;
    private final VoxelShape SOUTH1;
    private final VoxelShape WEST1;
    private final VoxelShape NORTH2;
    private final VoxelShape EAST2;
    private final VoxelShape SOUTH2;
    private final VoxelShape WEST2;
    private final boolean vertical;

    public BaseSplitBlock(Properties properties, VoxelShape mainShape, VoxelShape subShape, boolean vertical) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, Part.MAIN));
        NORTH1 = mainShape;
        EAST1 = ShapeUtil.rotateY(NORTH1, ShapeUtil.RotationAmount.NINETY);
        SOUTH1 = ShapeUtil.rotateY(NORTH1, ShapeUtil.RotationAmount.HUNDRED_EIGHTY);
        WEST1 = ShapeUtil.rotateY(NORTH1, ShapeUtil.RotationAmount.TWO_HUNDRED_SEVENTY);
        NORTH2 = subShape;
        EAST2 = ShapeUtil.rotateY(NORTH2, ShapeUtil.RotationAmount.NINETY);
        SOUTH2 = ShapeUtil.rotateY(NORTH2, ShapeUtil.RotationAmount.HUNDRED_EIGHTY);
        WEST2 = ShapeUtil.rotateY(NORTH2, ShapeUtil.RotationAmount.TWO_HUNDRED_SEVENTY);
        this.vertical = vertical;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == Part.MAIN ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean main = state.getValue(PART) == Part.MAIN;
        return switch (state.getValue(FACING)) {
            case NORTH -> main ? NORTH1 : NORTH2;
            case EAST -> main ? EAST1 : EAST2;
            case SOUTH -> main ? SOUTH1 : SOUTH2;
            case WEST -> main ? WEST1 : WEST2;
            default -> NORTH1;
        };
    }

    /*
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos relativePos = context.getClickedPos().relative(this.vertical ? Direction.UP : direction);
        return context.getLevel().getBlockState(relativePos).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, direction) : null;
    }
     */

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos relativePos = context.getClickedPos().relative(this.vertical ? Direction.UP : direction);
        return context.getLevel().getBlockState(relativePos).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, direction.getOpposite()) : null;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            Part part = state.getValue(PART);
            if (part == Part.SUB) {
                BlockPos blockpos = pos.relative(getOtherBlockDirection(state));
                BlockState neighborState = level.getBlockState(blockpos);
                if (neighborState.getBlock() == this && neighborState.getValue(PART) == Part.MAIN) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockpos, Block.getId(neighborState));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide()) {
            BlockPos blockpos = pos.relative(getOtherBlockDirection(state));
            BlockState neighborState = state.setValue(PART, Part.SUB);
            if (!this.vertical) {
                neighborState = neighborState.setValue(FACING, neighborState.getValue(FACING).getOpposite());
            }
            level.setBlockAndUpdate(blockpos, neighborState);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == getOtherBlockDirection(state)) {
            return neighborState.getBlock() == this && neighborState.getValue(PART) != state.getValue(PART) ? state : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    protected Direction getOtherBlockDirection(BlockState state) {
        if (vertical) {
            return state.getValue(PART) == Part.MAIN ? Direction.UP : Direction.DOWN;
        }
        return state.getValue(FACING);
    }

    public enum Part implements StringRepresentable {
        MAIN("main"),
        SUB("sub");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isMain() {
            return this == MAIN;
        }

        public boolean isSub() {
            return this == SUB;
        }
    }
}
