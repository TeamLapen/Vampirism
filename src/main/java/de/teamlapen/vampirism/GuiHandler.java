package de.teamlapen.vampirism;

import de.teamlapen.vampirism.client.gui.GUIBloodAltar4;
import de.teamlapen.vampirism.client.gui.GUIConvertBack;
import de.teamlapen.vampirism.client.gui.GUIMinionControl;
import de.teamlapen.vampirism.client.gui.GUISelectSkill;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar4;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Gui handler used to open GUI's
 */
public class GuiHandler implements IGuiHandler {
	public final static int ID_ALTAR_4 = 0;
	public final static int ID_CONVERT_BACK = 1;
	public final static int ID_SKILL = 2;
	public final static int ID_MINION_CONTROL = 3;

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == ID_ALTAR_4) {
			TileEntityBloodAltar4 tile = (TileEntityBloodAltar4) world.getTileEntity(new BlockPos(x, y, z));
			return new GUIBloodAltar4(player.inventory, tile);
		}
		if (ID == ID_CONVERT_BACK) {
			return new GUIConvertBack();
		}
		if (ID == ID_SKILL) {
			return new GUISelectSkill();
		}
		if (ID == ID_MINION_CONTROL) {
			return new GUIMinionControl();
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == ID_ALTAR_4) {
			TileEntityBloodAltar4 tile = (TileEntityBloodAltar4) world.getTileEntity(new BlockPos(x, y, z));
			return tile.getNewInventoryContainer(player.inventory);
		}
		return null;
	}

}
