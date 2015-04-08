package de.teamlapen.vampirism.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.item.ItemVampiresFear;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.util.Logger;

public class BlockBloodAltar extends BasicBlockContainer {
	private final String TAG = "BlockBloodAltar";
	public static final String name = "bloodAltar";

	public BlockBloodAltar() {
		super(Material.rock, name);
		this.setHardness(70.0F);
		this.setResistance(4000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBloodAltar();
	}


	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (!world.isRemote) {
			ItemStack item = null;
			try {
				item = player.inventory.getCurrentItem();
			} catch (NullPointerException e) {
			}

			TileEntityBloodAltar te = (TileEntityBloodAltar) world.getTileEntity(par2, par3, par4);
			te.onActivated(player, item);
		} 
		return true;
	}
	
	
}
