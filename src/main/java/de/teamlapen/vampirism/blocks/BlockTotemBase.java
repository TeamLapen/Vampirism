package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BlockTotemBase extends VampirismBlock {

    private final static String regName = "totem_base";

    public BlockTotemBase() {
        super(regName, Properties.create(Material.ROCK).hardnessAndResistance(40, 2000).sound(SoundType.STONE));

    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest, IFluidState fluid) {
        IBlockState up = world.getBlockState(pos.up());
        if (up.getBlock().equals(ModBlocks.totem_top)) {
            if (!up.getBlock().removedByPlayer(up, world, pos.up(), player, willHarvest, fluid)) {
                return false;
            }
            ModBlocks.totem_top.dropBlockAsItemWithChance(state, world, pos, 1, 0); //Manually drop top, because block is only destroyed not harvested by #removedByPlayer
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
