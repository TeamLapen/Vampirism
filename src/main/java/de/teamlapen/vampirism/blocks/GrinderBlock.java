package de.teamlapen.vampirism.blocks;

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
    private final static String regName = "blood_grinder";
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    protected static final VoxelShape grinderShape = makeShape();//TODO 1.14 make shape for all directions


    public GrinderBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(5).sound(SoundType.METAL));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));

    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new BloodGrinderTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return grinderShape;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) return true;
        player.openContainer(world.getTileEntity(pos) instanceof BloodGrinderTileEntity ? (BloodGrinderTileEntity) world.getTileEntity(pos) : null);
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
        VoxelShape b = Block.makeCuboidShape(1, 1, 1, 15, 3, 15);

        VoxelShape b1 = Block.makeCuboidShape(2, 3, 2, 3, 4, 3);
        VoxelShape b2 = Block.makeCuboidShape(13, 3, 2, 14, 4, 3);
        VoxelShape b3 = Block.makeCuboidShape(2, 3, 13, 3, 4, 14);
        VoxelShape b4 = Block.makeCuboidShape(13, 3, 14, 13, 4, 14);

        VoxelShape c = Block.makeCuboidShape(5, 3, 5, 11, 7, 11);

        VoxelShape d1 = Block.makeCuboidShape(5, 7, 2, 11, 12, 14);
        VoxelShape d2 = Block.makeCuboidShape(2, 7, 5, 14, 12, 11);
        VoxelShape d3 = Block.makeCuboidShape(3, 7, 4, 13, 12, 12);
        VoxelShape d4 = Block.makeCuboidShape(4, 7, 3, 12, 12, 13);
        VoxelShape d5 = VoxelShapes.or(d1, d2);
        VoxelShape d6 = VoxelShapes.or(d3, d4);
        VoxelShape d = VoxelShapes.or(d5, d6);

        VoxelShape e = Block.makeCuboidShape(8, 12, 8, 12, 16, 12);

        VoxelShape e1 = Block.makeCuboidShape(4, 12, 5, 7, 13, 6);
        VoxelShape e2 = Block.makeCuboidShape(5, 12, 4, 6, 13, 7);
        VoxelShape e3 = VoxelShapes.or(e1, e2);
        VoxelShape e4 = VoxelShapes.or(e, e3);

        VoxelShape f = VoxelShapes.or(a, b);
        VoxelShape g = VoxelShapes.or(c, d);

        VoxelShape g1 = VoxelShapes.or(b1, b2);
        VoxelShape g2 = VoxelShapes.or(b3, b4);
        VoxelShape g3 = VoxelShapes.or(g1, g2);
        VoxelShape g4 = VoxelShapes.or(g, g3);

        VoxelShape h = VoxelShapes.or(f, g4);

        VoxelShape i = VoxelShapes.or(h, e4);
        return i;
    }

}
