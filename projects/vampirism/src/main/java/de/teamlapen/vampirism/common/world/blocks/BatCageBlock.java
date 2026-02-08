package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.blockentity.BatCageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BatCageBlock extends BaseEntityBlock {

    public static final MapCodec<BatCageBlock> CODEC = simpleCodec(BatCageBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty CONTAINS_BAT = BooleanProperty.create("contains_bat");

    private static final VoxelShape SHAPE = Shapes.join(Block.box(1, 0, 1, 15, 12, 15), Block.box(3, 12, 3, 13, 16, 13), BooleanOp.OR);

    public BatCageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(CONTAINS_BAT, false));
    }

    public static ItemStack createBatCageStack(@Nullable BlockEntity blockEntity) {
        CompoundTag entityTag = null;
        if (blockEntity instanceof BatCageBlockEntity batCageBlockEntity) {
            entityTag = batCageBlockEntity.getEntityTag();
        }
        return createBatCageStack(entityTag);
    }

    public static ItemStack createBatCageStack(@Nullable CompoundTag entityTag) {
        ItemStack stack = new ItemStack(ModBlocks.BAT_CAGE.get());
        if (entityTag != null) {
            stack.set(ModDataComponents.HELD_ENTITY, entityTag);
        }
        return stack;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BatCageBlockEntity(pPos, pState);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        popResource(level, pos, createBatCageStack(blockEntity));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!stack.isEmpty()) {
            getBlockEntity(level, pos).ifPresent(blockEntity -> {
                CompoundTag tag = stack.get(ModDataComponents.HELD_ENTITY.get());
                if (tag != null) {
                    blockEntity.setEntityTag(tag.copy());
                    blockEntity.setChanged();
                }
            });
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return createBatCageStack(getBlockEntity(level, pos).orElse(null));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONTAINS_BAT);
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.setValue(FACING, pMirror.mirror(pState.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(CONTAINS_BAT, context.getItemInHand().has(ModDataComponents.HELD_ENTITY));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return getBlockEntity(level, pos).map(blockEntity -> blockEntity.hasEntity() ? 15 : 0).orElse(0);
    }

    public Optional<BatCageBlockEntity> getBlockEntity(LevelReader level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BatCageBlockEntity blockEntity) {
            return Optional.of(blockEntity);
        }

        return Optional.empty();
    }
}
