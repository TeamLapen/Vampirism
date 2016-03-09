package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.client.gui.GuiSelectAction;
import de.teamlapen.vampirism.client.gui.GuiSkills;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Handle GUIs on server and client side
 */
public class ModGuiHandler implements IGuiHandler {
    public final static int ID_ACTION = 0;
    public final static int ID_SKILL = 1;

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        switch (ID) {
            case ID_ACTION:
                return new GuiSelectAction();
            case ID_SKILL:
                return new GuiSkills();
        }
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
