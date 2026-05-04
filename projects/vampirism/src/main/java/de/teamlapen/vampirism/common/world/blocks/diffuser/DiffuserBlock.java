package de.teamlapen.vampirism.common.world.blocks.diffuser;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.DiffuserBlockEntity;
import de.teamlapen.faction.common.world.blocks.base.BaseContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class DiffuserBlock extends BaseContainerBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 3, 15), Block.box(2, 3, 2, 14, 12, 14));

    private final Supplier<BlockEntityType<? extends DiffuserBlockEntity>> blockEntityType;

    public DiffuserBlock(Properties properties, Supplier<BlockEntityType<? extends DiffuserBlockEntity>> blockEntityType) {
        super(properties);
        this.blockEntityType = blockEntityType;
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected abstract MapCodec<? extends DiffuserBlock> codec();

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                getBlockEntity(level, pos).ifPresent(blockEntity -> serverPlayer.openMenu(blockEntity, blockEntity::writeExtraData));
                onSuccessfullyOpened(state, level, pos, serverPlayer, hitResult);
            }
            return InteractionResult.CONSUME;
        }
    }

    protected void onSuccessfullyOpened(BlockState state, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hitResult) {}

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        getBlockEntity(level, pos).ifPresent(blockEntity -> {
            if (state.getValue(LIT)) {
                for (int i = 0; i < blockEntity.getParticleNumber(level, pos, state, blockEntity); i++) {
                    double x = pos.getX() - 0.15 + level.getRandom().nextDouble() * 1.3;
                    double y = pos.getY() + 4 / 16d + level.getRandom().nextDouble() / 3;
                    double z = pos.getZ() - 0.15 + level.getRandom().nextDouble() * 1.3;

                    level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.02, 0.0);
                }
            }
        });
    }

    @Nullable
    @Override
    public abstract DiffuserBlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player) {
            getBlockEntity(level, pos).ifPresent(entity -> entity.setOwned(player));
        }
    }

    protected Optional<DiffuserBlockEntity> getBlockEntity(BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DiffuserBlockEntity diffuser) {
            return Optional.of(diffuser);
        }
        return Optional.empty();
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        getBlockEntity(level, pos).ifPresent(blockEntity -> blockEntity.onTouched(player));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        getBlockEntity(level, pos).ifPresent(blockEntity1 -> blockEntity1.onTouched(player));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, this.blockEntityType.get(), DiffuserBlockEntity::serverTick);
    }
}
