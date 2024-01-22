package de.teamlapen.vampirism.client.gui.screens.diffuser;

import de.teamlapen.vampirism.inventory.diffuser.GarlicDiffuserMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GarlicDiffuserScreen extends DiffuserScreen<GarlicDiffuserMenu> {

    public GarlicDiffuserScreen(GarlicDiffuserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected int getProgressBarColor() {
        return 0xD0D0FF;
    }
}
