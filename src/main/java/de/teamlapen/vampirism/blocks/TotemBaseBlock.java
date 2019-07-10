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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;


public class TotemBaseBlock extends VampirismBlock {

    private final static String regName = "totem_base";

    public TotemBaseBlock() {
        super(regName, Properties.create(Material.ROCK).hardnessAndResistance(40, 2000).sound(SoundType.STONE));

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

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
