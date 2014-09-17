package de.teamlapen.vampirism.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.util.Logger;

public class BlockBloodAltar extends BlockVampirism {

	public BlockBloodAltar() {
		super(Material.rock);
		this.setBlockName("bloodAltar");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		Logger.i("BlockBloodAltar", "createTileEntity called");
		return new TileEntityBloodAltar();
	}

	@Override
	// And this tell it that you can see through this block, and neighbor blocks
	// should be rendered.
	public boolean isOpaqueCube() {
		return false;
	}
}
