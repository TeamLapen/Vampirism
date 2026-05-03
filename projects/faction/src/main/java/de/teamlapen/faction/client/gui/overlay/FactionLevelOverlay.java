package de.teamlapen.faction.client.gui.overlay;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class FactionLevelOverlay extends BaseOverlay {

    @Override
    protected boolean isEnabledInConfig() {
        return FactionConfig.client().showFactionLevelOverlay.get();
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker partialTicks) {
        if (canRenderOverlays() && this.player().jumpableVehicle() == null && !this.mc().options.hideGui) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(this.player());
            Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();
            if (this.mc().gameMode != null && this.mc().gameMode.hasExperience() && !IFaction.is(faction, de.teamlapen.faction.api.tags.FactionTags.IS_NEUTRAL)) {
                // boolean flag1 = false;
                int color = faction.value().getColor();
                int backGroundColor = ARGB.scaleRGB(color, 0.25f);
                Component text = handler.getLordPlayer().filter(x -> x.getLordLevel() > 0).map(ILordPlayer::getLordTitleShort).orElse(null);

                if (text == null) {
                    text = Component.literal(String.valueOf(handler.getCurrentLevel()));
                }

                int x = (this.mc().getWindow().getGuiScaledWidth() - this.mc().font.width(text)) / 2 + FactionConfig.client().factionLevelOverlayXPos.get();
                int y = this.mc().getWindow().getGuiScaledHeight() - FactionConfig.client().factionLevelOverlayYPos.get();
                graphics.text(font(), text, x + 1, y, backGroundColor, false);
                graphics.text(font(), text, x - 1, y, backGroundColor, false);
                graphics.text(font(), text, x, y + 1, backGroundColor, false);
                graphics.text(font(), text, x, y - 1, backGroundColor, false);
                graphics.text(font(), text, x, y, color, false);
            }
        }
    }
}
