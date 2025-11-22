package de.teamlapen.vampirism.common.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.blockentity.BloodGrinderBlockEntity;
import de.teamlapen.vampirism.common.blockentity.BloodSieveBlockEntity;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class BloodSieveBlock extends BaseEntityBlock {

    public static final MapCodec<BloodSieveBlock> CODEC = simpleCodec(BloodSieveBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_FILTER = BooleanProperty.create("has_filter");

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(4, 0, 4, 12, 1, 12),
            Block.box(4, 15, 4, 12, 16, 12),
            Block.box(5, 1, 5, 11, 15, 11),
            Block.box(2, 5, 2, 14, 11, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public BloodSieveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HAS_FILTER, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodSieveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.BLOOD_SIEVE.get(), BloodSieveBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);

        Optional<BloodSieveBlockEntity> optionalBlockEntity = getBlockEntity(level, pos);
        if (optionalBlockEntity.isPresent()) {
            BloodSieveBlockEntity blockEntity = optionalBlockEntity.get();

            if (BloodGrinderBlockEntity.isFilter(heldItem) && blockEntity.filterStack.isEmpty()) {
                blockEntity.filterStack = heldItem.copyWithCount(1);
                if (!player.getAbilities().instabuild) heldItem.shrink(1);
                blockEntity.updateFilterState(level, pos);
                blockEntity.setChanged();

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
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

    protected Optional<BloodSieveBlockEntity> getBlockEntity(BlockGetter level, BlockPos pos) {
        BlockEntity blockEntityOpt = level.getBlockEntity(pos);
        if (blockEntityOpt instanceof BloodSieveBlockEntity blockEntity) {
            return Optional.of(blockEntity);
        }
        return Optional.empty();
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
        builder.add(FACING, HAS_FILTER);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
