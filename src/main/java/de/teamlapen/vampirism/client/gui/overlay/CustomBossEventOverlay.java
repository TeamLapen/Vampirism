package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.mixin.client.BossOverlayGuiAccessor;
import de.teamlapen.vampirism.network.SUpdateMultiBossEventPacket;
import de.teamlapen.vampirism.world.MultiBossEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomBossEventOverlay extends GuiComponent implements IGuiOverlay {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private final Minecraft client;
    private final Map<UUID, MultiBossEvent> bossInfoMap = new LinkedHashMap<>();

    public CustomBossEventOverlay() {
        this.client = Minecraft.getInstance();
    }

    public void clear() {
        this.bossInfoMap.clear();
    }

    public void read(SUpdateMultiBossEventPacket packet) {
        if (packet.getOperation() == SUpdateMultiBossEventPacket.OperationType.ADD) {
            this.bossInfoMap.put(packet.getUniqueId(), new MultiBossEvent(packet));
        } else if (packet.getOperation() == SUpdateMultiBossEventPacket.OperationType.REMOVE) {
            this.bossInfoMap.remove(packet.getUniqueId());
        } else {
            this.bossInfoMap.get(packet.getUniqueId()).updateFromPackage(packet);
        }
    }

    @Override
    public void render(ForgeGui gui, PoseStack stack, float partialTicks, int width, int height) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();

        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j = 12 + ((BossOverlayGuiAccessor) this.client.gui.getBossOverlay()).getMapBossInfos().size() * (10 + this.client.font.lineHeight);
        for (MultiBossEvent value : bossInfoMap.values()) {
            int k = i / 2 - 91;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI_BARS_TEXTURES);
            this.render(stack, k, j, value);
            Component itextcomponent = value.getName();
            int l = this.client.font.width(itextcomponent);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            this.client.font.drawShadow(stack, itextcomponent, (float) i1, (float) j1, 16777215);

            if (j >= this.client.getWindow().getGuiScaledHeight() / 3) {
                break;
            }
        }
        RenderSystem.disableBlend();
    }

    private void render(PoseStack stack, int k, int j, MultiBossEvent value) {
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
            RenderSystem.setShaderColor(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF());
            this.blit(stack, k + textureStart, j, textureStart, BossEvent.BossBarColor.WHITE.ordinal() * 5 * 2 + 5, width, 5);
            textureStart += width;
        }
        if (value.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            this.blit(stack, k, j, 0, 80 + (value.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
    }
}
