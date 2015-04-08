package de.teamlapen.vampirism.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar1;

public class BlockBloodAltar1 extends BasicBlockContainer {
	private final String TAG = "BlockBloodAltar";
	public static final String name = "bloodAltar";

	public BlockBloodAltar1() {
		super(Material.rock, name);
		this.setHardness(8.0F);
		this.setHarvestLevel("pickaxe", 3);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBloodAltar1();
	}

	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (!world.isRemote) {
			ItemStack item = null;
			try {
				item = player.inventory.getCurrentItem();
			} catch (NullPointerException e) {
			}

			TileEntityBloodAltar1 te = (TileEntityBloodAltar1) world.getTileEntity(par2, par3, par4);
			te.onActivated(player, item);
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		TileEntityBloodAltar1 te = (TileEntityBloodAltar1) world.getTileEntity(x, y, z);
		te.dropSword();
		super.breakBlock(world, x, y, z, par5, par6);
	}

}
