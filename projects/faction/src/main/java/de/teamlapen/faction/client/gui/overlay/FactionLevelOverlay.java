package de.teamlapen.faction.client.gui.overlay;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.tags.FactionTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class FactionLevelOverlay extends BaseOverlay {

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays() && this.player().jumpableVehicle() == null && !this.mc().options.hideGui && this.mc().gameMode != null && this.mc().gameMode.hasExperience() && FactionConfig.client().enableFactionLevelOverlayRendering.get()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(this.player());
            Component component = handler.getCurrentFactionPlayer().map(IFactionPlayer::getShortLevelDisplay).orElse(null);

            if (component != null) {
                int color = handler.getFaction().value().getColor();
                int backGroundColor = ARGB.scaleRGB(color, 0.25f);

                int x = (this.mc().getWindow().getGuiScaledWidth() - this.mc().font.width(component)) / 2 + FactionConfig.client().guiLevelOffsetX.get();
                int y = this.mc().getWindow().getGuiScaledHeight() - FactionConfig.client().guiLevelOffsetY.get();
                graphics.drawString(font(), component, x + 1, y, backGroundColor, false);
                graphics.drawString(font(), component, x - 1, y, backGroundColor, false);
                graphics.drawString(font(), component, x, y + 1, backGroundColor, false);
                graphics.drawString(font(), component, x, y - 1, backGroundColor, false);
                graphics.drawString(font(), component, x, y, color, false);
            }
        }
    }
}
