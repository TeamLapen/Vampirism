package de.teamlapen.vampirism.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class BaseOverlay implements LayeredDraw.Layer {

    protected final Minecraft mc = Minecraft.getInstance();

    public boolean canRenderOverlays() {
        return this.mc.player != null && this.mc.player.isAlive() && !this.mc.options.hideGui;
    }

    @NotNull
    public LocalPlayer player() {
        return this.mc.player;
    }
}
