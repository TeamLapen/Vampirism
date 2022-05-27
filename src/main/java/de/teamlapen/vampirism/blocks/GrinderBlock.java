package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blockentity.BloodGrinderBlockEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrinderBlock extends VampirismBlockContainer {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SOUTH = makeShape();
    private static final VoxelShape WEST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape NORTH = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape EAST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

    private static VoxelShape makeShape() {
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

    public GrinderBlock() {
        super(Properties.of(Material.METAL).strength(5).sound(SoundType.METAL).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));

    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Nonnull
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new BloodGrinderBlockEntity(pos, state);
    }

    @Nonnull
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        player.openMenu(world.getBlockEntity(pos) instanceof BloodGrinderBlockEntity ? (BloodGrinderBlockEntity) world.getBlockEntity(pos) : null);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, Level worldIn, BlockPos pos) {
        dropInventoryTileEntityItems(worldIn, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, @Nonnull BlockState p_153213_, @Nonnull BlockEntityType<T> p_153214_) {
        return p_153212_.isClientSide() ? null : createTickerHelper(p_153214_, ModTiles.GRINDER.get(), BloodGrinderBlockEntity::serverTick);
    }
}
