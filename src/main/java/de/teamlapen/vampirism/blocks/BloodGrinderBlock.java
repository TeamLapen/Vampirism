package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.BloodGrinderBlockEntity;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.fluids.BloodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BloodGrinderBlock extends BaseEntityBlock {

    public static final MapCodec<BloodGrinderBlock> CODEC = simpleCodec(BloodGrinderBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty GRINDING = BooleanProperty.create("grinding");
    public static final BooleanProperty HAS_FILTER = BooleanProperty.create("has_filter");

    public BloodGrinderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(GRINDING, false).setValue(HAS_FILTER, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodGrinderBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, ModBlockEntities.BLOOD_GRINDER.get(), BloodGrinderBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);

        Optional<BloodGrinderBlockEntity> optionalBlockEntity = getBlockEntity(level, pos);
        if (optionalBlockEntity.isPresent()) {
            BloodGrinderBlockEntity blockEntity = optionalBlockEntity.get();

            if (BloodGrinderBlockEntity.isFilter(heldItem) && blockEntity.filterStack.isEmpty()) {
                blockEntity.filterStack = heldItem.copyWithCount(1);
                if (!player.getAbilities().instabuild) heldItem.shrink(1);
                blockEntity.updateFilterState(level, pos);
                blockEntity.setChanged();

                return InteractionResult.SUCCESS;
            }
        }

        return BloodHelper.handleFluidItemBlockInteraction(stack, level, pos, player, hand, hitResult.getDirection()) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return getBlockEntity(level, pos).filter(blockEntity -> !blockEntity.filterStack.isEmpty()).map(blockEntity -> {
            if (!player.getAbilities().instabuild) {
                if (!player.getInventory().add(blockEntity.filterStack)) {
                    player.drop(blockEntity.filterStack, false);
                }
            }
            blockEntity.filterStack = ItemStack.EMPTY;
            blockEntity.updateFilterState(level, pos);
            blockEntity.setChanged();

            return (InteractionResult) InteractionResult.SUCCESS;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if ((state.hasBlockEntity() && !state.is(newState.getBlock())) || !newState.hasBlockEntity()) {
            getBlockEntity(level, pos).ifPresent(blockEntity -> Containers.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, blockEntity.inputStack, blockEntity.filterStack)));
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    protected Optional<BloodGrinderBlockEntity> getBlockEntity(BlockGetter level, BlockPos pos) {
        BlockEntity blockEntityOpt = level.getBlockEntity(pos);
        if (blockEntityOpt instanceof BloodGrinderBlockEntity blockEntity) {
            return Optional.of(blockEntity);
        }
        return Optional.empty();
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide) return;

        boolean wasPowered = state.getValue(POWERED);
        if (wasPowered != level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
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
        builder.add(FACING, POWERED, GRINDING, HAS_FILTER);
    }
}
