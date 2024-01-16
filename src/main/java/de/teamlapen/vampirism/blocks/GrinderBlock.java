package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blockentity.BloodGrinderBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrinderBlock extends VampirismBlockContainer {
    public static final MapCodec<GrinderBlock> CODEC = simpleCodec(GrinderBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SOUTH = makeShape();
    private static final VoxelShape WEST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape NORTH = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape EAST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

    private static @NotNull VoxelShape makeShape() {
        VoxelShape a = Block.box(0, 0, 0, 16, 1, 16);
        VoxelShape b = Block.box(1, 1, 1, 15, 3, 15);

        VoxelShape b1 = Block.box(2, 3, 2, 3, 4, 3);
        VoxelShape b2 = Block.box(13, 3, 2, 14, 4, 3);
        VoxelShape b3 = Block.box(2, 3, 13, 3, 4, 14);
        VoxelShape b4 = Block.box(13, 3, 14, 13, 4, 14);

        VoxelShape c = Block.box(5, 3, 5, 11, 7, 11);

        VoxelShape d1 = Block.box(5, 7, 2, 11, 12, 14);
        VoxelShape d2 = Block.box(2, 7, 5, 14, 12, 11);
        VoxelShape d3 = Block.box(3, 7, 4, 13, 12, 12);
        VoxelShape d4 = Block.box(4, 7, 3, 12, 12, 13);
        VoxelShape d5 = Shapes.or(d1, d2);
        VoxelShape d6 = Shapes.or(d3, d4);
        VoxelShape d = Shapes.or(d5, d6);

        VoxelShape e = Block.box(8, 12, 8, 12, 16, 12);

        VoxelShape e1 = Block.box(4, 12, 5, 7, 13, 6);
        VoxelShape e2 = Block.box(5, 12, 4, 6, 13, 7);
        VoxelShape e3 = Shapes.or(e1, e2);
        VoxelShape e4 = Shapes.or(e, e3);

        VoxelShape f = Shapes.or(a, b);
        VoxelShape g = Shapes.or(c, d);

        VoxelShape g1 = Shapes.or(b1, b2);
        VoxelShape g2 = Shapes.or(b3, b4);
        VoxelShape g3 = Shapes.or(g1, g2);
        VoxelShape g4 = Shapes.or(g, g3);

        VoxelShape h = Shapes.or(f, g4);

        return Shapes.or(h, e4);
    }

    public GrinderBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));

    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @NotNull
    @Override
    public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BloodGrinderBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        player.awardStat(ModStats.interact_with_blood_grinder.get());
        player.openMenu(world.getBlockEntity(pos) instanceof BloodGrinderBlockEntity ? (BloodGrinderBlockEntity) world.getBlockEntity(pos) : null);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, @NotNull Level worldIn, BlockPos pos) {
        dropInventoryTileEntityItems(worldIn, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level p_153212_, @NotNull BlockState p_153213_, @NotNull BlockEntityType<T> p_153214_) {
        return p_153212_.isClientSide() ? null : createTickerHelper(p_153214_, ModTiles.GRINDER.get(), BloodGrinderBlockEntity::serverTick);
    }
}
