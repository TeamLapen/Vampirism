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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SieveBlock extends VampirismBlockContainer {

    public static final BooleanProperty PROPERTY_ACTIVE = BooleanProperty.create("active");
    protected static final VoxelShape sieveShape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.box(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.box(5, 2, 5, 11, 12, 11);
        VoxelShape d = Block.box(3, 6, 3, 13, 9, 13);
        VoxelShape e = Block.box(1, 12, 1, 15, 14, 15);
        VoxelShape f = Block.box(0, 14, 0, 16, 16, 16);

        return VoxelShapes.or(a, b, c, d, e, f);
    }

    public SieveBlock() {
        super(Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(PROPERTY_ACTIVE, false));
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new SieveTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return sieveShape;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ACTIVE);
    }
}
