package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.client.gui.GuiAltarInfusion;
import de.teamlapen.vampirism.client.gui.GuiSelectAction;
import de.teamlapen.vampirism.client.gui.GuiSkills;
import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Handle GUIs on server and client side
 */
public class ModGuiHandler implements IGuiHandler {
    public final static int ID_ACTION = 0;
    public final static int ID_SKILL = 1;
    public final static int ID_ALTAR_INFUSION = 2;

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        switch (ID) {
            case ID_ACTION:
                return new GuiSelectAction();
            case ID_SKILL:
                return new GuiSkills();
            case ID_ALTAR_INFUSION:
                TileAltarInfusion tile = (TileAltarInfusion) world.getTileEntity(new BlockPos(x, y, z));

                return new GuiAltarInfusion(player.inventory, tile);
        }
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == ID_ALTAR_INFUSION) {
            TileAltarInfusion tile = (TileAltarInfusion) world.getTileEntity(new BlockPos(x, y, z));
            return tile.getNewInventoryContainer(player.inventory);
        }
        return null;
    }
}
