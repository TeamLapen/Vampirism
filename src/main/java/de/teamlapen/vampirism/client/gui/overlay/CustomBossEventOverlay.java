package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.mixin.client.BossOverlayGuiAccessor;
import de.teamlapen.vampirism.network.ClientboundUpdateMultiBossEventPacket;
import de.teamlapen.vampirism.world.MultiBossEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomBossEventOverlay implements IGuiOverlay {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private final @NotNull Minecraft client;
    private final Map<UUID, MultiBossEvent> bossInfoMap = new LinkedHashMap<>();

    public CustomBossEventOverlay() {
        this.client = Minecraft.getInstance();
    }

    public void clear() {
        this.bossInfoMap.clear();
    }

    public void read(@NotNull ClientboundUpdateMultiBossEventPacket packet) {
        if (packet.operation() == ClientboundUpdateMultiBossEventPacket.OperationType.ADD) {
            this.bossInfoMap.put(packet.uniqueId(), new MultiBossEvent(packet));
        } else if (packet.operation() == ClientboundUpdateMultiBossEventPacket.OperationType.REMOVE) {
            this.bossInfoMap.remove(packet.uniqueId());
        } else {
            this.bossInfoMap.get(packet.uniqueId()).updateFromPackage(packet);
        }
    }

    @Override
    public void render(ForgeGui gui, @NotNull GuiGraphics graphics, float partialTicks, int width, int height) {
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

            if (j >= this.client.getWindow().getGuiScaledHeight() / 3) {
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
            graphics.blit(GUI_BARS_TEXTURES, k + textureStart, j, textureStart, BossEvent.BossBarColor.WHITE.ordinal() * 5 * 2 + 5, width, 5);
            textureStart += width;
            graphics.setColor(1, 1, 1, 1);
        }
        if (value.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            graphics.setColor(1, 1, 1, 1);
            graphics.blit(GUI_BARS_TEXTURES, k, j, 0, 80 + (value.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
    }
}
