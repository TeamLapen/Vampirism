package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.world.blocks.base.BaseContainerBlock;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.blockentity.AltarInfusionBlockEntity;
import de.teamlapen.vampirism.common.world.heritage.HeritageData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public class AltarInfusionBlock extends BaseContainerBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<AltarInfusionBlock> CODEC = simpleCodec(AltarInfusionBlock::new);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(5, 0, 5, 11, 4, 11),
            Block.box(1, 4, 1, 15, 7, 15),
            Block.box(13, 7, 1, 15, 13, 3),
            Block.box(13, 7, 13, 15, 13, 15),
            Block.box(1, 7, 13, 3, 13, 15),
            Block.box(1, 7, 1, 3, 13, 3)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AltarInfusionBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);

        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AltarInfusionBlockEntity(blockPos, blockState);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(FactionItems.OBLIVION_POTION.get()) && HeritageData.get(player).getMembership().isPresent()) {
            if (level.isClientSide()) {
                VampirismMod.proxy.displayHeritageRunAwayScreen();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof AltarInfusionBlockEntity blockEntity) || level.isClientSide()) return InteractionResult.SUCCESS;

        if (!Helper.isVampire(player)) {
            player.sendOverlayMessage(FactionRestriction.getFactionRestrictionMessage(ModFactions.VAMPIRE.get()));
            return InteractionResult.SUCCESS;
        }

        AltarInfusionBlockEntity.Result activationResult = blockEntity.tryActivate(player);
        if (activationResult == AltarInfusionBlockEntity.Result.STILL_RUNNING) {
            player.sendOverlayMessage(activationResult.getMessage());
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            if (activationResult == AltarInfusionBlockEntity.Result.SUCCESS) {
                player.awardStat(ModStats.ALTAR_INFUSION_RITUALS_PERFORMED.get());
                blockEntity.startRitual(player);
                return InteractionResult.SUCCESS;
            } else if (activationResult != AltarInfusionBlockEntity.Result.MISSING_ITEMS) {
                player.sendOverlayMessage(activationResult.getMessage());
                return InteractionResult.SUCCESS;
            }
        }

        player.openMenu(blockEntity);
        player.awardStat(ModStats.INTERACT_WITH_ALTAR_OF_INFUSION.get());

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.ALTAR_INFUSION.get(), AltarInfusionBlockEntity::tick);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}