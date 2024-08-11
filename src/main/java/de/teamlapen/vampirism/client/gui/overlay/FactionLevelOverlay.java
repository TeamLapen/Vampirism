package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.jetbrains.annotations.NotNull;

public class FactionLevelOverlay implements LayeredDraw.Layer {
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker partialTicks) {
        if (this.mc.player != null && this.mc.player.isAlive() && this.mc.player.jumpableVehicle() == null && !this.mc.options.hideGui && VampirismConfig.CLIENT.enableFactionLevelOverlayRendering.get()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(this.mc.player);
            IPlayableFaction<?> faction = handler.getCurrentFaction();
            if (this.mc.gameMode != null && this.mc.gameMode.hasExperience() && faction != null) {
                // boolean flag1 = false;
                int color = faction.getColor();
                int lord = handler.getLordLevel();
                String text;
                if (lord > 0) {
                    String title = handler.getLordTitle().getString();
                    text = title.substring(0, Math.min(3, title.length()));
                } else {
                    text = String.valueOf(handler.getCurrentLevel());
                }
                int x = (this.mc.getWindow().getGuiScaledWidth() - this.mc.font.width(text)) / 2 + VampirismConfig.CLIENT.guiLevelOffsetX.get();
                int y = this.mc.getWindow().getGuiScaledHeight() - VampirismConfig.CLIENT.guiLevelOffsetY.get();
                graphics.drawString(this.mc.font, text, x + 1, y, 0, false);
                graphics.drawString(this.mc.font, text, x - 1, y, 0, false);
                graphics.drawString(this.mc.font, text, x, y + 1, 0, false);
                graphics.drawString(this.mc.font, text, x, y - 1, 0, false);
                graphics.drawString(this.mc.font, text, x, y, color, false);
            }
        }
    }
}
