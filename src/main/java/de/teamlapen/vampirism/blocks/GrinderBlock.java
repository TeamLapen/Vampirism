package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.tileentity.BloodGrinderTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GrinderBlock extends VampirismBlockContainer {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
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
        VoxelShape d5 = VoxelShapes.or(d1, d2);
        VoxelShape d6 = VoxelShapes.or(d3, d4);
        VoxelShape d = VoxelShapes.or(d5, d6);

        VoxelShape e = Block.box(8, 12, 8, 12, 16, 12);

        VoxelShape e1 = Block.box(4, 12, 5, 7, 13, 6);
        VoxelShape e2 = Block.box(5, 12, 4, 6, 13, 7);
        VoxelShape e3 = VoxelShapes.or(e1, e2);
        VoxelShape e4 = VoxelShapes.or(e, e3);

        VoxelShape f = VoxelShapes.or(a, b);
        VoxelShape g = VoxelShapes.or(c, d);

        VoxelShape g1 = VoxelShapes.or(b1, b2);
        VoxelShape g2 = VoxelShapes.or(b3, b4);
        VoxelShape g3 = VoxelShapes.or(g1, g2);
        VoxelShape g4 = VoxelShapes.or(g, g3);

        VoxelShape h = VoxelShapes.or(f, g4);

        return VoxelShapes.or(h, e4);
    }

    public GrinderBlock() {
        super(Properties.of(Material.METAL).strength(5).sound(SoundType.METAL).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));

    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
        }
        return NORTH;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new BloodGrinderTileEntity();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) return ActionResultType.SUCCESS;
        player.openMenu(world.getBlockEntity(pos) instanceof BloodGrinderTileEntity ? (BloodGrinderTileEntity) world.getBlockEntity(pos) : null);
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, World worldIn, BlockPos pos) {
        dropInventoryTileEntityItems(worldIn, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
