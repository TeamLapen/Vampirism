package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.SieveTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SieveBlock extends VampirismBlockContainer {

    public static final BooleanProperty PROPERTY_ACTIVE = BooleanProperty.create("active");
    private final static String regName = "blood_sieve";
    protected static final VoxelShape sieveShape = makeShape();

    public SieveBlock() {
        super(regName, Properties.create(Material.WOOD).hardnessAndResistance(2.5f).sound(SoundType.WOOD));
        this.setDefaultState(this.getStateContainer().getBaseState().with(PROPERTY_ACTIVE, false));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new SieveTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return sieveShape;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ACTIVE);
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.makeCuboidShape(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.makeCuboidShape(5, 2, 5, 11, 12, 11);
        VoxelShape d = Block.makeCuboidShape(3, 6, 3, 13, 9, 13);
        VoxelShape e = Block.makeCuboidShape(1, 12, 1, 15, 14, 15);
        VoxelShape f = Block.makeCuboidShape(0, 14, 0, 16, 16, 16);

        return VoxelShapes.or(a, b, c, d, e, f);
    }
}
