package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * Part of the Altar of Infusion structure
 */
public class AltarTipBlock extends VampirismBlock {
    private final static String name = "altar_tip";
    protected static final VoxelShape tipShape = makeShape();

    public AltarTipBlock() {
        super(name, Properties.create(Material.IRON).hardnessAndResistance(1f));
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 1;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return tipShape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(3, 0, 3, 13, 3, 13);
        VoxelShape b = Block.makeCuboidShape(4, 3, 4, 12, 4, 12);
        VoxelShape c = Block.makeCuboidShape(5, 4, 5, 11, 5, 11);
        VoxelShape d = Block.makeCuboidShape(6, 5, 6, 10, 6, 10);
        VoxelShape e = Block.makeCuboidShape(7, 6, 7, 9, 7, 9);
        return VoxelShapes.or(a, b, c, d, e);
    }
}
