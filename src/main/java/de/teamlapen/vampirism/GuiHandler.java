package de.teamlapen.vampirism;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import de.teamlapen.vampirism.client.gui.GUIBloodAltarTier4;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import de.teamlapen.vampirism.util.Logger;

/**
 * Gui handler used to open GUI's
 */
public class GuiHandler implements IGuiHandler {
	public final static int ID_ALTAR_4=0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID==ID_ALTAR_4){
			TileEntityBloodAltarTier4 tile=(TileEntityBloodAltarTier4)world.getTileEntity(x, y, z);
			return tile.getNewInventoryContainer(player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID==ID_ALTAR_4){
			TileEntityBloodAltarTier4 tile=(TileEntityBloodAltarTier4)world.getTileEntity(x, y, z);
			return new GUIBloodAltarTier4(player.inventory,tile);
		}
		return null;
	}

}
