package de.teamlapen.vampirism.common.world.blocks.candle;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class CandleHolderBlock extends AbstractCandleBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;

    protected static <T extends CandleHolderBlock> Products.P3<RecordCodecBuilder.Mu<T>, Optional<Block>, Optional<Item>, Properties> candleStickParts(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("empty_block").forGetter(i -> Optional.ofNullable(i.emptyBlock).map(Supplier::get)),
                BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("candle").forGetter(i -> Optional.ofNullable(i.candle.get())),
                propertiesCodec()
        );
    }

    private final Map<Identifier, Supplier<Block>> fullHolderByContent = Maps.newHashMap();
    protected final @Nullable Supplier<? extends Block> emptyBlock;
    protected final Supplier<@Nullable Item> candle;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected CandleHolderBlock(Optional<Block> emptyBlock, Optional<Item> candle, Properties properties) {
        this(emptyBlock.map(x -> (Supplier<Block>) () -> x).orElse(null), () -> candle.orElse(null), properties);
    }
    protected CandleHolderBlock(@Nullable Supplier<? extends Block> emptyBlock, Supplier<@Nullable Item> candle, Properties properties) {
        super(properties);
        this.emptyBlock = emptyBlock;
        this.candle = candle;
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(LIT, false));
    }

    public void addCandle(Identifier candle, Supplier<Block> holder) {
        this.fullHolderByContent.put(candle, holder);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Item item = stack.getItem();
        if (isEmpty()) {
            Block orDefault = this.fullHolderByContent.getOrDefault(BuiltInRegistries.ITEM.getKey(item), () -> Blocks.AIR).get();
            if (orDefault != Blocks.AIR) {
                if (stack.getCount() < getNumberOfCandles() && !player.isCreative()) {
                    player.sendOverlayMessage(Component.translatable("message.vampirism.candle_holder.not_enough_candles", getNumberOfCandles()));
                } else {
                    level.setBlock(pos, getFilledState(state, orDefault), Block.UPDATE_ALL);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(getNumberOfCandles());
                    }
                    level.playSound(player, pos, SoundType.CANDLE.getPlaceSound(), SoundSource.BLOCKS, (SoundType.CANDLE.getVolume() + 1.0F) / 2.0F, SoundType.CANDLE.getPitch() * 0.8F);
                }

                return InteractionResult.SUCCESS_SERVER;
            }
        } else if (player.getAbilities().mayBuild && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            if (state.getValue(LIT)) {
                extinguish(player, state, level, pos);
                return InteractionResult.SUCCESS_SERVER;
            } else {
                if (this.emptyBlock != null) {
                    level.setBlock(pos, this.getEmptyState(state, this.emptyBlock.get()), Block.UPDATE_ALL);
                    Item candle = this.candle.get();
                    if (!player.getAbilities().instabuild && candle != null) {
                        player.addItem(new ItemStack(candle, getNumberOfCandles()));
                    }
                    level.playSound(player, pos, SoundType.CANDLE.getBreakSound(), SoundSource.BLOCKS, (SoundType.CANDLE.getVolume() + 1.0F) / 2.0F, SoundType.CANDLE.getPitch() * 0.8F);
                }
            }
        }

        return InteractionResult.PASS;
    }

    public int getNumberOfCandles() {
        return 1;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIT) ? getLitLightLevel() : 0;
    }

    public int getLitLightLevel() {
        return 6;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    /**
     * Used in child methods for wall candle holders.
     */
    @Nullable
    public BlockState getStateForWallPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        LevelReader levelreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction[] adirection = context.getNearestLookingDirections();

        for (Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    protected BlockState getFilledState(BlockState sourceState, Block block) {
        return block.defaultBlockState().setValue(FACING, sourceState.getValue(FACING)).setValue(WATERLOGGED, sourceState.getValue(WATERLOGGED)).setValue(LIT, sourceState.getValue(LIT));
    }

    protected BlockState getEmptyState(BlockState sourceState, Block block) {
        return block.defaultBlockState().setValue(FACING, sourceState.getValue(FACING)).setValue(WATERLOGGED, sourceState.getValue(WATERLOGGED)).setValue(LIT, sourceState.getValue(LIT));
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource randomSource) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
            BlockState blockstate = state.setValue(WATERLOGGED, Boolean.TRUE);
            if (state.getValue(LIT)) {
                extinguish(null, blockstate, level, pos);
            } else {
                level.setBlock(pos, blockstate, 3);
            }

            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Required to prevent empty candle holders from being lit up.
     */
    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        ItemStack itemStack = context.getItemInHand();
        if (!itemStack.canPerformAction(itemAbility))
            return null;

        if (ItemAbilities.FIRESTARTER_LIGHT == itemAbility) {
            if (this.canBeLit(state)) {
                return state.setValue(BlockStateProperties.LIT, true);
            }
        }

        return null;
    }

    @Override
    protected boolean canBeLit(BlockState state) {
        return !state.getValue(WATERLOGGED) && !this.isEmpty() && super.canBeLit(state);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @NotNull
    public Supplier<@Nullable Item> getCandle() {
        return candle;
    }

    public boolean isEmpty() {
        return this.candle.get() == null;
    }

    public String getDescriptionKey() {
        return "";
    }

}
