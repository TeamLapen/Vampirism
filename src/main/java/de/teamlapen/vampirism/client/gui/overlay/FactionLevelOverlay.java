package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.entity.factions.FactionPlayerHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FactionLevelOverlay extends BaseOverlay {
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker partialTicks) {
        if (canRenderOverlays() && this.mc.player.jumpableVehicle() == null && !this.mc.options.hideGui && ModConfig.CLIENT.enableFactionLevelOverlayRendering.get()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(this.mc.player);
            Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();
            if (this.mc.gameMode != null && this.mc.gameMode.hasExperience() && !IFaction.is(faction, ModFactionTags.IS_NEUTRAL)) {
                // boolean flag1 = false;
                int color = faction.value().getColor();
                int lord = handler.getLordLevel();
                String text = null;
                if (lord > 0) {
                    text = Optional.ofNullable(handler.getLordTitleShort()).map(Component::getString).map(x -> x.substring(0, Math.min(3, x.length()))).orElse(null);
                }

                if (text == null) {
                    text = String.valueOf(handler.getCurrentLevel());
                }

                int x = (this.mc.getWindow().getGuiScaledWidth() - this.mc.font.width(text)) / 2 + ModConfig.CLIENT.guiLevelOffsetX.get();
                int y = this.mc.getWindow().getGuiScaledHeight() - ModConfig.CLIENT.guiLevelOffsetY.get();
                graphics.drawString(this.mc.font, text, x + 1, y, 0, false);
                graphics.drawString(this.mc.font, text, x - 1, y, 0, false);
                graphics.drawString(this.mc.font, text, x, y + 1, 0, false);
                graphics.drawString(this.mc.font, text, x, y - 1, 0, false);
                graphics.drawString(this.mc.font, text, x, y, color, false);
            }
        }
    }
}
