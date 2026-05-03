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

    private final VoxelShape NORTH_MAIN, EAST_MAIN, SOUTH_MAIN, WEST_MAIN;
    private final VoxelShape NORTH_SUB, EAST_SUB, SOUTH_SUB, WEST_SUB;

    private final boolean vertical;

    public BaseSplitBlock(Properties properties, VoxelShape mainShape, VoxelShape subShape, boolean vertical) {
        super(properties);
        this.vertical = vertical;
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, Part.MAIN));

        NORTH_MAIN = mainShape;
        EAST_MAIN  = ShapeUtil.rotateY(mainShape, ShapeUtil.RotationAmount.NINETY);
        SOUTH_MAIN = ShapeUtil.rotateY(mainShape, ShapeUtil.RotationAmount.HUNDRED_EIGHTY);
        WEST_MAIN  = ShapeUtil.rotateY(mainShape, ShapeUtil.RotationAmount.TWO_HUNDRED_SEVENTY);

        NORTH_SUB = subShape;
        EAST_SUB  = ShapeUtil.rotateY(subShape, ShapeUtil.RotationAmount.NINETY);
        SOUTH_SUB = ShapeUtil.rotateY(subShape, ShapeUtil.RotationAmount.HUNDRED_EIGHTY);
        WEST_SUB  = ShapeUtil.rotateY(subShape, ShapeUtil.RotationAmount.TWO_HUNDRED_SEVENTY);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == Part.MAIN ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean main = state.getValue(PART).isMain();
        return switch (state.getValue(FACING)) {
            case NORTH -> main ? NORTH_MAIN : NORTH_SUB;
            case EAST  -> main ? EAST_MAIN  : EAST_SUB;
            case SOUTH -> main ? SOUTH_MAIN : SOUTH_SUB;
            case WEST  -> main ? WEST_MAIN  : WEST_SUB;
            default -> NORTH_MAIN;
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == getOtherBlockDirection(state)) {
            return neighborState.getBlock() == this && neighborState.getValue(PART) != state.getValue(PART) ? state : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
        }
    }

    protected Direction getOtherBlockDirection(BlockState state) {
        if (vertical) {
            return state.getValue(PART) == Part.MAIN ? Direction.UP : Direction.DOWN;
        }
        return state.getValue(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection();
        BlockPos subPos = context.getClickedPos().relative(this.vertical ? Direction.UP : direction);

        if (!level.getBlockState(subPos).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(subPos)) {
            return null;
        }

        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide()) return;

        BlockPos subPos = pos.relative(getOtherBlockDirection(state));
        BlockState subState = getSubStateForPlacement(level, subPos, state);

        level.setBlock(subPos, subState, Block.UPDATE_ALL);
        state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
    }

    protected BlockState getSubStateForPlacement(Level level, BlockPos subPos, BlockState mainState) {
        BlockState subState = mainState.setValue(PART, Part.SUB);

        if (!this.vertical) {
            subState = subState.setValue(FACING, subState.getValue(FACING).getOpposite());
        }

        return subState;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative() && state.getValue(PART).isSub()) {
            BlockPos mainPos = pos.relative(getOtherBlockDirection(state));
            BlockState mainState = level.getBlockState(mainPos);

            if (mainState.getBlock() == this && mainState.getValue(PART).isMain()) {
                level.setBlock(mainPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, mainPos, Block.getId(mainState));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public boolean isVertical() {
        return this.vertical;
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
