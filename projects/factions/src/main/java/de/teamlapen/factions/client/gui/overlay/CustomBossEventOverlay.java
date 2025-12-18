package de.teamlapen.factions.client.gui.overlay;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.common.config.FactionConfig;
import de.teamlapen.factions.common.network.packets.client.ClientboundUpdateMultiBossEventPacket;
import de.teamlapen.factions.common.util.Color;
import de.teamlapen.factions.common.world.MultiBossEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomBossEventOverlay extends BaseOverlay {
    private static final ResourceLocation BAR_PROGRESS_SPRITE = FResourceLocation.mc("boss_bar/white_progress");
    private final Map<UUID, MultiBossEvent> bossInfoMap = new LinkedHashMap<>();

    public void clear() {
        this.bossInfoMap.clear();
    }

    public void read(ClientboundUpdateMultiBossEventPacket packet) {
        switch (packet.operation()) {
            case ClientboundUpdateMultiBossEventPacket.AddOperation operation:
                this.bossInfoMap.put(operation.uniqueId(), new MultiBossEvent(operation));
                break;
            case ClientboundUpdateMultiBossEventPacket.RemoveOperation operation:
                this.bossInfoMap.remove(operation.uniqueId());
                break;
            default:
                this.bossInfoMap.get(packet.operation().uniqueId()).updateFromPackage(packet.operation());
        }
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTicks) {
        if (!canRenderOverlays() || !FactionConfig.CLIENT.enableVillageRaidOverlayRendering.get()) {
            return;
        }
        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j = 12 + this.mc().gui.getBossOverlay().getEvents().size() * (10 + this.mc().font.lineHeight);
        for (MultiBossEvent value : bossInfoMap.values()) {
            int k = i / 2 - 91;
            this.render(graphics, k, j, value);
            Component itextcomponent = value.getName();
            int l = this.mc().font.width(itextcomponent);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            graphics.drawString(this.mc().font, itextcomponent, i1, j1, 16777215, true);

            if (j >= graphics.guiHeight() / 3) {
                break;
            }
        }
    }

    private void render(GuiGraphics graphics, int k, int j, MultiBossEvent value) {
        int textureStart = 0;
        List<Color> s = value.getColors();
        Map<Color, Float> perc = value.getEntries();
        for (int i = 0; i < s.size(); i++) {
            if (textureStart >= 182) break;
            Color color = s.get(i);
            int width = (int) (perc.getOrDefault(color, 0f) * 182);
            if (i == s.size() - 1) {
                if (textureStart + width < 182) {
                    width = 182 - textureStart;
                }
            }
            GuiRenderer.blitSprite(graphics, BAR_PROGRESS_SPRITE, 182, 5, textureStart, 0, k + textureStart, j, width, 5, color.getRGB());
            textureStart += width;
        }
        if (value.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BossHealthOverlay.OVERLAY_BACKGROUND_SPRITES[value.getOverlay().ordinal() - 1], k, j, 182, 5);
        }
    }
}
