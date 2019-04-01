package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BlockTotemBase extends VampirismBlock {

    private final static String regName = "totem_base";

    public BlockTotemBase() {
        super(regName, Material.ROCK);
        this.setHardness(40.0F);
        this.setResistance(2000.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        IBlockState up = world.getBlockState(pos.up());
        if (up.getBlock().equals(ModBlocks.totem_top)) {
            if (!up.getBlock().removedByPlayer(up, world, pos.up(), player, willHarvest)) {
                return false;
            }
            ModBlocks.totem_top.dropBlockAsItem(world, pos, state, 0); //Manually drop top, because block is only destroyed not harvested by #removedByPlayer
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
}
