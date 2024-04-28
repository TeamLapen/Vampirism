package de.teamlapen.vampirism.blocks;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public abstract class CandleStickBlock extends AbstractCandleBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> state.getValue(LIT) ? 6 : 0;

    protected static <T extends CandleStickBlock> Products.P3<RecordCodecBuilder.Mu<T>, Block, Item, Properties> candleStickParts(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("empty_block").forGetter(i -> i.emptyBlock.get()),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("candle").forGetter(i -> i.candle.get()),
                propertiesCodec()
        );
    }

    private final Map<ResourceLocation, Supplier<Block>> fullHolderByContent = Maps.newHashMap();
    @Nullable
    protected final Supplier<? extends Block> emptyBlock;
    @NotNull
    protected final Supplier<Item> candle;

    protected CandleStickBlock(@Nullable Supplier<? extends Block> emptyBlock, @NotNull Supplier<Item> candle, Properties pProperties) {
        super(pProperties);
        this.emptyBlock = emptyBlock;
        this.candle = candle;
    }

    public void addCandle(ResourceLocation candle, Supplier<Block> holder) {
        if (candle == null) {
            throw new IllegalArgumentException("Cannot add plant to non-empty candle mount");
        }
        this.fullHolderByContent.put(candle, holder);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
        Item item = stack.getItem();
        if (isEmpty()) {
            Block orDefault = this.fullHolderByContent.getOrDefault(BuiltInRegistries.ITEM.getKey(item), () -> Blocks.AIR).get();
            if (orDefault != Blocks.AIR) {
                pLevel.setBlock(pPos, getFilledState(pState, orDefault), 3);
                if (!pPlayer.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        } else if (pPlayer.getAbilities().mayBuild && pPlayer.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            if (pState.getValue(LIT)) {
                extinguish(pPlayer, pState, pLevel, pPos);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            } else {
                if (this.emptyBlock != null) {
                    pLevel.setBlock(pPos, this.getEmptyState(pState, this.emptyBlock.get()), 3);
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.addItem(this.candle.get().getDefaultInstance());
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    protected BlockState getFilledState(BlockState sourceState, Block block) {
        return block.defaultBlockState().setValue(WATERLOGGED, sourceState.getValue(WATERLOGGED)).setValue(LIT, sourceState.getValue(LIT));
    }

    protected BlockState getEmptyState(BlockState sourceState, Block block) {
        return block.defaultBlockState().setValue(WATERLOGGED, sourceState.getValue(WATERLOGGED)).setValue(LIT, sourceState.getValue(LIT));
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT, WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull FluidState pFluidState) {
        if (!pState.getValue(WATERLOGGED) && pFluidState.getType() == Fluids.WATER) {
            BlockState blockstate = pState.setValue(WATERLOGGED, Boolean.TRUE);
            if (pState.getValue(LIT)) {
                extinguish((Player) null, blockstate, pLevel, pPos);
            } else {
                pLevel.setBlock(pPos, blockstate, 3);
            }

            pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean canBeLit(@NotNull BlockState pState) {
        return !pState.getValue(WATERLOGGED) && !this.isEmpty() && super.canBeLit(pState);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        return Block.canSupportCenter(pLevel, pPos.below(), Direction.UP);
    }

    public static boolean canLight(@NotNull BlockState pState) {
        return pState.is(BlockTags.CANDLES, (p_152810_) -> p_152810_.hasProperty(LIT) && p_152810_.hasProperty(WATERLOGGED)) && !pState.getValue(LIT) && !pState.getValue(WATERLOGGED);
    }

    @NotNull
    public Supplier<@Nullable Item> getCandle() {
        return candle;
    }

    public boolean isEmpty() {
        return this.candle.get() == null;
    }
}
