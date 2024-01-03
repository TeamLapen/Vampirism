package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.RevertBackMenu;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class InjectionChairRevertBackScreen extends RevertBackScreen implements MenuAccess<RevertBackMenu> {
    private final RevertBackMenu menu;

    public InjectionChairRevertBackScreen(RevertBackMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        this.menu = pMenu;
    }

    @Override
    public @NotNull RevertBackMenu getMenu() {
        return this.menu;
    }
}
