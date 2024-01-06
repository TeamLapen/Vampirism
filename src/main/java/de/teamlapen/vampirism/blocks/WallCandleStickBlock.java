package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class WallCandleStickBlock extends CandleStickBlock {
    public static final MapCodec<WallCandleStickBlock> CODEC = RecordCodecBuilder.mapCodec(inst ->
            candleStickParts(inst).apply(inst, WallCandleStickBlock::new)
    );
    private static final Map<Direction, Iterable<Vec3>> PARTICLE_OFFSET = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, ImmutableList.of(new Vec3(0.5D, 0.86D, 0.75)));
        put(Direction.WEST, ImmutableList.of(new Vec3(0.75, 0.86D, 0.5D)));
        put(Direction.SOUTH, ImmutableList.of(new Vec3(0.5D, 0.86D, 0.25D)));
        put(Direction.EAST, ImmutableList.of(new Vec3(0.25, 0.86D, 0.5D)));
    }};
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, UtilLib.rotateShape(makeShape(), UtilLib.RotationAmount.HUNDRED_EIGHTY));
        put(Direction.WEST, UtilLib.rotateShape(makeShape(), UtilLib.RotationAmount.NINETY));
        put(Direction.SOUTH, makeShape());
        put(Direction.EAST, UtilLib.rotateShape(makeShape(), UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY));
    }};
    private static final Map<Direction, VoxelShape> SHAPES_WITH_CANDLE = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, UtilLib.rotateShape(makeShapeWithCandle(), UtilLib.RotationAmount.HUNDRED_EIGHTY));
        put(Direction.WEST, UtilLib.rotateShape(makeShapeWithCandle(), UtilLib.RotationAmount.NINETY));
        put(Direction.SOUTH, makeShapeWithCandle());
        put(Direction.EAST, UtilLib.rotateShape(makeShapeWithCandle(), UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY));
    }};
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private WallCandleStickBlock(Block emptyBlock, Item candle, Properties pProperties) {
        this(() -> emptyBlock, () -> candle, pProperties);
    }

    public WallCandleStickBlock(@Nullable Supplier<? extends Block> emptyBlock, @NotNull Supplier<Item> candle, Properties pProperties) {
        super(emptyBlock, candle, pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(LIT, false));
    }

    @Override
    protected BlockState getFilledState(BlockState sourceState, Block block) {
        return super.getFilledState(sourceState, block).setValue(FACING, sourceState.getValue(FACING));
    }

    @Override
    protected BlockState getEmptyState(BlockState sourceState, Block block) {
        return super.getEmptyState(sourceState, block).setValue(FACING, sourceState.getValue(FACING));
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        return pLevel.getBlockState(pPos.relative(pState.getValue(FACING).getOpposite())).isSolid();
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState();
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        LevelReader levelreader = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Direction[] adirection = pContext.getNearestLookingDirections();

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

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
        return pFacing.getOpposite() == pState.getValue(FACING) && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.setValue(FACING, pMirror.mirror(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return isEmpty() ? SHAPES.get(pState.getValue(FACING)) : SHAPES_WITH_CANDLE.get(pState.getValue(FACING));
    }


    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return ModItems.CANDLE_STICK.get().getDefaultInstance();
    }

    @Override
    protected @NotNull MapCodec<? extends AbstractCandleBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Iterable<Vec3> getParticleOffsets(@NotNull BlockState pState) {
        return PARTICLE_OFFSET.get(pState.getValue(FACING));
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(6f / 16, 0, 0, 10f / 16, 4f / 16, 1f / 16));
        shape = Shapes.or(shape, Shapes.box(7f / 16, 1f / 16, 1f / 16, 9f / 16, 3f / 16, 5f / 16));
        shape = Shapes.or(shape, Shapes.box(7f / 16, 1f / 16, 3f / 16, 9f / 16, 4f / 16, 5f / 16));
        shape = Shapes.or(shape, Shapes.box(6f / 16, 4f / 16, 2f / 16, 10f / 16, 6f / 16, 6f / 16));
        return shape;
    }

    private static VoxelShape makeShapeWithCandle() {
        VoxelShape shape = makeShape();
        shape = Shapes.or(shape, Shapes.box(7f / 16, 6f / 16, 3f / 16, 9f / 16, 12f / 16, 5f / 16));
        return shape;
    }
}
