package de.teamlapen.factions.common.world.blocks;

import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.core.FactionStats;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.util.ShapeUtil;
import de.teamlapen.factions.api.world.items.InjectionItem;
import de.teamlapen.factions.common.world.blocks.base.BaseHorizontalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MedChairBlock extends BaseHorizontalBlock {

    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);

    private static final VoxelShape SHAPE_TOP = Block.box(2, 6, 0, 14, 16, 16);
    private static final VoxelShape SHAPE_BOTTOM = Block.box(1, 1, 0, 15, 10, 16);
    private final VoxelShape NORTH1;
    private final VoxelShape EAST1;
    private final VoxelShape SOUTH1;
    private final VoxelShape WEST1;
    private final VoxelShape NORTH2;
    private final VoxelShape EAST2;
    private final VoxelShape SOUTH2;
    private final VoxelShape WEST2;

    public MedChairBlock(BlockBehaviour.Properties properties) {
        super(properties.mapColor(MapColor.METAL).pushReaction(PushReaction.DESTROY).strength(1).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(BaseHorizontalBlock.FACING, Direction.NORTH).setValue(PART, EnumPart.BOTTOM));
        NORTH1 = SHAPE_BOTTOM;
        EAST1 = ShapeUtil.rotateY(NORTH1, ShapeUtil.RotationAmount.NINETY);
        SOUTH1 = ShapeUtil.rotateY(NORTH1, ShapeUtil.RotationAmount.HUNDRED_EIGHTY);
        WEST1 = ShapeUtil.rotateY(NORTH1, ShapeUtil.RotationAmount.TWO_HUNDRED_SEVENTY);
        NORTH2 = SHAPE_TOP;
        EAST2 = ShapeUtil.rotateY(NORTH2, ShapeUtil.RotationAmount.NINETY);
        SOUTH2 = ShapeUtil.rotateY(NORTH2, ShapeUtil.RotationAmount.HUNDRED_EIGHTY);
        WEST2 = ShapeUtil.rotateY(NORTH2, ShapeUtil.RotationAmount.TWO_HUNDRED_SEVENTY);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isAlive()) {
            if (stack.getItem() instanceof InjectionItem injectionItem) {
                if (handleInjections(stack, injectionItem, level, pos, player, hand)) {
                    return InteractionResult.SUCCESS_SERVER;
                }
            }
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    private boolean handleInjections(ItemStack stack, InjectionItem injectionItem, Level level, BlockPos pos, Player player, InteractionHand hand) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();

        if (injectionItem.handleInjection(level, pos, player, handler, faction)) {
            injectionItem.consumeInjectionItem(stack, player, hand);
            player.awardStat(FactionStats.INTERACT_WITH_INJECTION_CHAIR.get());

            return true;
        }

        return false;
    }

    public enum EnumPart implements StringRepresentable {
        TOP("top"),
        BOTTOM("bottom");

        public final String name;

        EnumPart(String name) {
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
    }

    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        boolean main = state.getValue(PART) == EnumPart.BOTTOM;
        return switch (state.getValue(BaseHorizontalBlock.FACING)) {
            case Direction.NORTH -> main ? NORTH1 : NORTH2;
            case Direction.EAST -> main ? EAST1 : EAST2;
            case Direction.SOUTH -> main ? SOUTH1 : SOUTH2;
            case Direction.WEST -> main ? WEST1 : WEST2;
            default -> NORTH1;
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos neighborPos = context.getClickedPos().relative(facing);
        return context.getLevel().getBlockState(neighborPos).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, facing.getOpposite()) : null;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            EnumPart part = state.getValue(PART);
            if (part == EnumPart.TOP) {
                BlockPos blockpos = pos.relative(getOtherBlockDirection(state));
                BlockState otherState = level.getBlockState(blockpos);
                if (otherState.getBlock() == this && otherState.getValue(PART) == EnumPart.BOTTOM) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(otherState));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (!level.isClientSide()) {
            BlockPos blockpos = pos.relative(getOtherBlockDirection(state));
            BlockState otherState = state.setValue(PART, EnumPart.TOP);
            otherState = otherState.setValue(BaseHorizontalBlock.FACING, otherState.getValue(BaseHorizontalBlock.FACING));
            level.setBlockAndUpdate(blockpos, otherState);
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
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    private Direction getOtherBlockDirection(BlockState blockState) {
        return blockState.getValue(PART) == EnumPart.BOTTOM ? blockState.getValue(BaseHorizontalBlock.FACING).getOpposite() : blockState.getValue(BaseHorizontalBlock.FACING);
    }
}
