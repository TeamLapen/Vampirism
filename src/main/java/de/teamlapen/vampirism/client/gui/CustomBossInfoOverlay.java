package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.mixin.client.BossOverlayGuiAccessor;
import de.teamlapen.vampirism.network.SUpdateMultiBossInfoPacket;
import de.teamlapen.vampirism.world.DummyBossInfo;
import de.teamlapen.vampirism.world.MultiBossInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomBossInfoOverlay extends AbstractGui {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private final Minecraft client;
    private final Map<UUID, MultiBossInfo> bossInfoMap = new LinkedHashMap<>();

    public CustomBossInfoOverlay() {
        this.client = Minecraft.getInstance();
    }

    public void clear() {
        this.bossInfoMap.clear();
    }

    @SubscribeEvent
    public void onRenderOverlayBoss(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            render(event.getMatrixStack());
            RenderSystem.disableBlend();
        }
    }

    public void read(SUpdateMultiBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            this.bossInfoMap.put(packet.getUniqueId(), new MultiBossInfo(packet));
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
            this.bossInfoMap.remove(packet.getUniqueId());
        } else {
            this.bossInfoMap.get(packet.getUniqueId()).updateFromPackage(packet);
        }
    }

    public void render(MatrixStack stack) {
        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j = 12 + ((BossOverlayGuiAccessor) this.client.gui.getBossOverlay()).getMapBossInfos().size() * (10 + this.client.font.lineHeight);
        for (MultiBossInfo value : bossInfoMap.values()) {
            int k = i / 2 - 91;
            net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event =
                    net.minecraftforge.client.ForgeHooksClient.bossBarRenderPre(stack, this.client.getWindow(), new DummyBossInfo(value.getUniqueId(), value.getName()), k, j, 10 + this.client.font.lineHeight);
            if (!event.isCanceled()) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                Minecraft.getInstance().getTextureManager().bind(GUI_BARS_TEXTURES);
                this.render(stack, k, j, value);
                ITextComponent itextcomponent = value.getName();
                int l = this.client.font.width(itextcomponent);
                int i1 = i / 2 - l / 2;
                int j1 = j - 9;
                this.client.font.drawShadow(stack, itextcomponent, (float) i1, (float) j1, 16777215);
            }
            j += event.getIncrement();
            net.minecraftforge.client.ForgeHooksClient.bossBarRenderPost(stack, this.client.getWindow());

            if (j >= this.client.getWindow().getGuiScaledHeight() / 3) {
                break;
            }
        }
    }

    private void render(MatrixStack stack, int k, int j, MultiBossInfo value) {
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
            RenderSystem.color4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            this.blit(stack, k + textureStart, j, textureStart, BossInfo.Color.WHITE.ordinal() * 5 * 2 + 5, width, 5);
            textureStart += width;
        }
        if (value.getOverlay() != BossInfo.Overlay.PROGRESS) {
            RenderSystem.color4f(1, 1, 1, 1);
            this.blit(stack, k, j, 0, 80 + (value.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
    }
}
