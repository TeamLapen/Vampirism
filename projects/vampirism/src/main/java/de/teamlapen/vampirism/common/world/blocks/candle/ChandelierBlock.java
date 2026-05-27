package de.teamlapen.vampirism.common.world.blocks.candle;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ChandelierBlock extends CandleHolderBlock {
    public static final MapCodec<ChandelierBlock> CODEC = RecordCodecBuilder.mapCodec(inst ->
            candleStickParts(inst).apply(inst, ChandelierBlock::new)
    );

    private final static VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    private static final ImmutableList<Vec3> PARTICLE_OFFSET = ImmutableList.of(
            new Vec3(3 / 16d, 11.75 / 16d, 3 / 16d),
            new Vec3(13 / 16d, 11.75 / 16d, 3 / 16d),
            new Vec3(13 / 16d, 11.75 / 16d, 13 / 16d),
            new Vec3(3 / 16d, 11.75 / 16d, 13 / 16d)
    );

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected ChandelierBlock(Optional<Block> emptyBlock, Optional<Item> candle, Properties properties) {
        super(emptyBlock, candle, properties);
    }

    public ChandelierBlock(@Nullable Supplier<? extends Block> emptyBlock, Supplier<@Nullable Item> candle, Properties properties) {
        super(emptyBlock, candle, properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return canSupportCenter(worldIn, pos.above(), Direction.DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource randomSource) {
        return direction == Direction.UP && !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends AbstractCandleBlock> codec() {
        return CODEC;
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return PARTICLE_OFFSET;
    }

    @Override
    public int getNumberOfCandles() {
        return 4;
    }

    @Override
    public int getLitLightLevel() {
        return 15;
    }

    @Override
    public String getDescriptionKey() {
        return BuiltInRegistries.ITEM.getKey(ModBlocks.CHANDELIER.asItem()).getPath() + (emptyBlock != null ? ".filled" : "");
    }
}
