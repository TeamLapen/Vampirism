package de.teamlapen.faction.client.gui.overlay;

import de.teamlapen.faction.client.IMinecraftAccessor;
import net.neoforged.neoforge.client.gui.GuiLayer;

public abstract class BaseOverlay implements GuiLayer, IMinecraftAccessor {


    public boolean canRenderOverlays() {
        return this.player() != null && this.player().isAlive() && !this.mc().options.hideGui;
    }
}
