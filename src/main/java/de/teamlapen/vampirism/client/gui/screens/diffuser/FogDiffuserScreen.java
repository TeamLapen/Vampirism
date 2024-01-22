package de.teamlapen.vampirism.client.gui.screens.diffuser;

import de.teamlapen.vampirism.inventory.diffuser.FogDiffuserMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FogDiffuserScreen extends DiffuserScreen<FogDiffuserMenu> {

    public FogDiffuserScreen(FogDiffuserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected int getProgressBarColor() {
        return 0xFFD0D0;
    }
}
