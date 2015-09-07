package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockChurchAltar extends BasicBlockContainer {
	public static class TileEntityChurchAltar extends TileEntity {

	}

	public static final String name = "churchAltar";

	public BlockChurchAltar() {
		super(Material.rock, name);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityChurchAltar();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.openGui(VampirismMod.instance,GuiHandler.ID_CONVERT_BACK,worldIn,pos.getX(),pos.getY(),pos.getZ());
		return true;
	}

}
