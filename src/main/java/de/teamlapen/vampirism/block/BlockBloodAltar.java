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
		this.setBlockTextureName("vampirism:iconBloodAltarTier");
	}

	private void activateAltar(EntityPlayer player, ItemStack item, TileEntityBloodAltar te) {
		if (!te.isOccupied()) {
			te.startVampirismRitual(player, item);
		} else {
			Logger.i(TAG, "Altar already used");
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBloodAltar();
	}


	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
		Logger.i(TAG, "Altar right-click detected");
		if (!world.isRemote) {
			ItemStack item = null;
			try {
				item = player.inventory.getCurrentItem();
			} catch (NullPointerException e) {
				Logger.i(TAG, "No item in hand");
				// e.printStackTrace();
				return false;
			}

			if (item != null && item.getItem() instanceof ItemVampiresFear) {
				Logger.i(TAG, "Activating Altar");
				TileEntityBloodAltar te = (TileEntityBloodAltar) world.getTileEntity(par2, par3, par4);
				activateAltar(player, item, te);
			}
			return true;
		} else
			Logger.e(TAG, "World remote!");
		return false;
	}
}
