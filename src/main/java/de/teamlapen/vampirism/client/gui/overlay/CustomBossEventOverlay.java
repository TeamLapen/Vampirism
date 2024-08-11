package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.mixin.client.accessor.BossHealthOverlayAccessor;
import de.teamlapen.vampirism.mixin.client.accessor.BossOverlayGuiAccessor;
import de.teamlapen.vampirism.network.ClientboundUpdateMultiBossEventPacket;
import de.teamlapen.vampirism.world.MultiBossEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomBossEventOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation BAR_PROGRESS_SPRITE = VResourceLocation.mc("boss_bar/white_progress");
    private final @NotNull Minecraft client;
    private final Map<UUID, MultiBossEvent> bossInfoMap = new LinkedHashMap<>();

    public CustomBossEventOverlay() {
        this.client = Minecraft.getInstance();
    }

    public void clear() {
        this.bossInfoMap.clear();
    }

    public void read(@NotNull ClientboundUpdateMultiBossEventPacket packet) {
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
        if (!VampirismConfig.CLIENT.enableVillageRaidOverlayRendering.get()) {
            return;
        }
        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j = 12 + ((BossOverlayGuiAccessor) this.client.gui.getBossOverlay()).getMapBossInfos().size() * (10 + this.client.font.lineHeight);
        for (MultiBossEvent value : bossInfoMap.values()) {
            int k = i / 2 - 91;
            this.render(graphics, k, j, value);
            Component itextcomponent = value.getName();
            int l = this.client.font.width(itextcomponent);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            graphics.drawString(this.client.font, itextcomponent, i1, j1, 16777215, true);

            if (j >= graphics.guiHeight()  / 3) {
                break;
            }
        }
    }

    private void render(@NotNull GuiGraphics graphics, int k, int j, @NotNull MultiBossEvent value) {
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
            graphics.setColor(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF());
            graphics.blitSprite(BAR_PROGRESS_SPRITE, 182, 5, textureStart, 0, k + textureStart, j, width, 5);
            graphics.setColor(1, 1, 1, 1);
            textureStart += width;
        }
        if (value.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            graphics.setColor(1, 1, 1, 1);
            RenderSystem.enableBlend();
            graphics.blitSprite(BossHealthOverlayAccessor.getOVERLAY_BACKGROUND_SPRITES()[value.getOverlay().ordinal()-1], k, j, 182, 5);
            RenderSystem.disableBlend();
        }
    }
}
