package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.tileEntity.TileEntityCoffinSec;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 
 * @author Moritz
 *
 */
public class BlockCoffinSec extends BasicBlockContainer {

	private static final String TAG = "BlockCoffinSec";
	public static final String name = "coffinSec";
	
	
	public BlockCoffinSec() {
		super(Material.rock, name);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block,
			int par) {
		TileEntityCoffinSec te = (TileEntityCoffinSec) world.getTileEntity(x,
				y, z);
		if (te == null)
			return;
		world.setBlockToAir(te.primary_x, te.primary_y, te.primary_z);
		world.removeTileEntity(te.primary_x, te.primary_y, te.primary_z);
		world.removeTileEntity(x, y, z);
	}

	/**
	 * Checks if the primary block still exists
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z,
			Block block) {
		TileEntityCoffinSec tileEntity = (TileEntityCoffinSec) world
				.getTileEntity(x, y, z);
		if (tileEntity != null) {
			if (!(world.getBlock(tileEntity.primary_x, tileEntity.primary_y,
					tileEntity.primary_z) instanceof BlockCoffin)) {
				Logger.i(TAG, "Removing secondary coffin block");
				world.setBlockToAir(x, y, z);
				world.removeTileEntity(x, y, z);
			}
		}
	}

	// This block is supposed to be invisible
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i,
			int j, int k, int l) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		// TODO Auto-generated method stub
		return null;
	}
}
