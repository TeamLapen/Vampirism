package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableList;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StandingCandleStickBlock extends CandleStickBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final ImmutableList<Vec3> PARTICLE_OFFSET = ImmutableList.of(new Vec3(0.5D, 0.8D, 0.5D));

    private static final VoxelShape SHAPE = makeShape();
    private static final VoxelShape SHAPE_WITH_CANDLE = makeShapeWithCandle();

    public StandingCandleStickBlock(@Nullable Supplier<? extends Block> emptyBlock, @NotNull Supplier<Item> candle, Properties pProperties) {
        super(emptyBlock, candle, pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(LIT, false));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ModItems.CANDLE_STICK.get().getDefaultInstance();
    }
    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).isSolid();
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
        return pFacing == Direction.DOWN && !this.canSurvive(pState, pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState pState, @NotNull Rotation pRot) {
        return pState.setValue(FACING, pState.getValue(FACING).getOpposite());
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, @NotNull Mirror pMirror) {
        return pState.setValue(FACING, pMirror.mirror(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return isEmpty() ? SHAPE : SHAPE_WITH_CANDLE;
    }

    @Override
    protected @NotNull Iterable<Vec3> getParticleOffsets(@NotNull BlockState pState) {
        return PARTICLE_OFFSET;
    }

    @Override
    protected BlockState getFilledState(BlockState sourceState, Block block) {
        return super.getFilledState(sourceState, block).setValue(FACING, sourceState.getValue(FACING));
    }

    @Override
    protected BlockState getEmptyState(BlockState sourceState, Block block) {
        return super.getEmptyState(sourceState, block).setValue(FACING, sourceState.getValue(FACING));
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(5f / 16, 0, 5f / 16, 11f / 16, 1f / 16, 11f / 16));
        shape = Shapes.or(shape, Shapes.box(7f / 16, 1f / 16, 7f / 16, 9f / 16, 3f / 16, 9f / 16));
        shape = Shapes.or(shape, Shapes.box(6f / 16, 3f / 16, 6f / 16, 10f / 16, 5f / 16, 10f / 16));
        return shape;
    }

    private static VoxelShape makeShapeWithCandle() {
        VoxelShape shape = makeShape();
        shape = Shapes.or(shape, Shapes.box(7f / 16, 4f / 16, 7f / 16, 9f / 16, 11f / 16, 9f / 16));
        return shape;
    }
}
