package de.teamlapen.factions.client.gui.overlay;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.ILordPlayer;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.config.ModConfig;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.tags.FactionTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class FactionLevelOverlay extends BaseOverlay {

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays() && this.player().jumpableVehicle() == null && !this.mc().options.hideGui && ModConfig.CLIENT.enableFactionLevelOverlayRendering.get()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(this.player());
            Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();
            if (this.mc().gameMode != null && this.mc().gameMode.hasExperience() && !IFaction.is(faction, FactionTags.IS_NEUTRAL)) {
                // boolean flag1 = false;
                int color = faction.value().getColor();
                int backGroundColor = ARGB.scaleRGB(color, 0.25f);
                Component text = handler.getLordPlayer().filter(x -> x.getLordLevel() > 0).map(ILordPlayer::getLordTitleShort).orElse(null);

                if (text == null) {
                    text = Component.literal(String.valueOf(handler.getCurrentLevel()));
                }

                int x = (this.mc().getWindow().getGuiScaledWidth() - this.mc().font.width(text)) / 2 + ModConfig.CLIENT.guiLevelOffsetX.get();
                int y = this.mc().getWindow().getGuiScaledHeight() - ModConfig.CLIENT.guiLevelOffsetY.get();
                graphics.drawString(font(), text, x + 1, y, backGroundColor, false);
                graphics.drawString(font(), text, x - 1, y, backGroundColor, false);
                graphics.drawString(font(), text, x, y + 1, backGroundColor, false);
                graphics.drawString(font(), text, x, y - 1, backGroundColor, false);
                graphics.drawString(font(), text, x, y, color, false);
            }
        }
    }
}
