package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

public class FactionLevelOverlay extends BaseOverlay {

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker partialTicks) {
        if (canRenderOverlays() && this.player().jumpableVehicle() == null && !this.mc().options.hideGui && ModConfig.CLIENT.enableFactionLevelOverlayRendering.get()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(this.player());
            Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();
            if (this.mc().gameMode != null && this.mc().gameMode.hasExperience() && !IFaction.is(faction, ModFactionTags.IS_NEUTRAL)) {
                // boolean flag1 = false;
                int color = faction.value().getColor();
                int backGroundColor = ARGB.scaleRGB(color, 0.25f);
                int lord = handler.getLordLevel();
                Component text = null;
                if (lord > 0) {
                    text = handler.getLordTitleShort();
                }

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
