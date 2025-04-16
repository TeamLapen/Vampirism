package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.InjectionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Block which represents the top and the bottom part of the "Injection Chair" used for injections
 */
public class MedChairBlock extends VampirismHorizontalBlock {

    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);

    private static final @NotNull VoxelShape SHAPE_TOP = box(2, 6, 0, 14, 16, 16);
    private static final @NotNull VoxelShape SHAPE_BOTTOM = box(1, 1, 0, 15, 10, 16);
    private final VoxelShape NORTH1;
    private final @NotNull VoxelShape EAST1;
    private final @NotNull VoxelShape SOUTH1;
    private final @NotNull VoxelShape WEST1;
    private final VoxelShape NORTH2;
    private final @NotNull VoxelShape EAST2;
    private final @NotNull VoxelShape SOUTH2;
    private final @NotNull VoxelShape WEST2;

    public MedChairBlock(Properties properties) {
        super(properties.mapColor(MapColor.METAL).pushReaction(PushReaction.DESTROY).strength(1).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, EnumPart.BOTTOM));
        NORTH1 = SHAPE_BOTTOM;
        EAST1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.NINETY);
        SOUTH1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
        NORTH2 = SHAPE_TOP;
        EAST2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.NINETY);
        SOUTH2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (player.isAlive()) {
            if (handleInjections(player, level, stack, pos)) {
                player.awardStat(ModStats.INTERACT_WITH_INJECTION_CHAIR.get());
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().removeItem(stack);
                }
            }
        } else if (level.isClientSide) {
            player.displayClientMessage(Component.translatable("text.vampirism.need_item_to_use", Component.translatable(ModItems.INJECTION_GARLIC.get().getDescriptionId())), true);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    private boolean handleInjections(@NotNull Player player, @NotNull Level level, @NotNull ItemStack stack, @NotNull BlockPos pos) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();

        if (stack.getItem() instanceof InjectionItem injectionItem) {
            return injectionItem.handleInjection(level, pos, player, handler, faction);
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
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        boolean main = state.getValue(PART) == EnumPart.BOTTOM;
        return switch (state.getValue(FACING)) {
            case NORTH -> main ? NORTH1 : NORTH2;
            case EAST -> main ? EAST1 : EAST2;
            case SOUTH -> main ? SOUTH1 : SOUTH2;
            case WEST -> main ? WEST1 : WEST2;
            default -> NORTH1;
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos neighborPos = context.getClickedPos().relative(facing);
        return context.getLevel().getBlockState(neighborPos).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, facing.getOpposite()) : null;
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && player.isCreative()) {
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
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(getOtherBlockDirection(state));
            BlockState otherState = state.setValue(PART, EnumPart.TOP);
            otherState = otherState.setValue(FACING, otherState.getValue(FACING));
            level.setBlock(blockpos, otherState, 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }

    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull LevelReader level, @NotNull ScheduledTickAccess scheduledTickAccess, @NotNull BlockPos pos, @NotNull Direction direction, @NotNull BlockPos neighborPos, @NotNull BlockState neighborState, @NotNull RandomSource random) {
        if (direction == getOtherBlockDirection(state)) {
            return neighborState.getBlock() == this && neighborState.getValue(PART) != state.getValue(PART) ? state : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    private @NotNull Direction getOtherBlockDirection(@NotNull BlockState blockState) {
        return blockState.getValue(PART) == EnumPart.BOTTOM ? blockState.getValue(FACING).getOpposite() : blockState.getValue(FACING);
    }
}
