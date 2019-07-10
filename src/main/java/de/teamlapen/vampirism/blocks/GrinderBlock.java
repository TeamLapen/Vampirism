package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.TileGrinder;
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
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GrinderBlock extends VampirismBlockContainer {
    private final static String regName = "blood_grinder";
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    protected static final VoxelShape grinderShape = VoxelShapes.combine(VoxelShapes.combine(Block.makeCuboidShape(0, 0, 0, 16, 3, 16), Block.makeCuboidShape(2, 7, 2, 14, 12, 14), IBooleanFunction.AND), VoxelShapes.combine(Block.makeCuboidShape(5, 3, 5, 11, 7, 11), Block.makeCuboidShape(8, 12, 4, 12, 16, 8), IBooleanFunction.AND), IBooleanFunction.AND);


    public GrinderBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(5).sound(SoundType.METAL));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));

    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileGrinder();
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
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
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
        //player.openGui(VampirismMod.instance, ModGuiHandler.ID_BLOOD_GRINDER, world, pos.getX(), pos.getY(), pos.getZ());//TODO 1.14
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

}
