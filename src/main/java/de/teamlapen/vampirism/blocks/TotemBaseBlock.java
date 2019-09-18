package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;


public class TotemBaseBlock extends VampirismBlock {
    private static final VoxelShape shape = makeShape();
    private final static String regName = "totem_base";

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.makeCuboidShape(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.makeCuboidShape(3, 2, 3, 13, 3, 13);

        VoxelShape d1 = Block.makeCuboidShape(4, 3, 4, 7, 16, 7);
        VoxelShape d2 = Block.makeCuboidShape(9, 3, 4, 12, 16, 7);
        VoxelShape d3 = Block.makeCuboidShape(4, 3, 9, 7, 16, 12);
        VoxelShape d4 = Block.makeCuboidShape(9, 3, 9, 12, 16, 12);

        VoxelShape e = Block.makeCuboidShape(5, 3, 5, 11, 16, 11);

        return VoxelShapes.or(a, b, c, d1, d2, d3, d4, e);
    }

    public TotemBaseBlock() {
        super(regName, Properties.create(Material.ROCK).hardnessAndResistance(40, 2000).sound(SoundType.STONE));

    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        BlockState up = world.getBlockState(pos.up());
        if (up.getBlock().equals(ModBlocks.totem_top)) {
            if (!up.getBlock().removedByPlayer(up, world, pos.up(), player, willHarvest, fluid)) {
                return false;
            }
            Block.spawnDrops(state, world, pos);
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
