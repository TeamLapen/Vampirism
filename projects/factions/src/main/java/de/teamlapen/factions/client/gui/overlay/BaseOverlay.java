package de.teamlapen.factions.client.gui.overlay;

import de.teamlapen.factions.client.IMinecraftAccessor;
import net.neoforged.neoforge.client.gui.GuiLayer;

public abstract class BaseOverlay implements GuiLayer, IMinecraftAccessor {


    public boolean canRenderOverlays() {
        return this.player() != null && this.player().isAlive() && !this.mc().options.hideGui;
    }
}
