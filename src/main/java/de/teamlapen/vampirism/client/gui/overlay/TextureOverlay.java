package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class TextureOverlay implements LayeredDraw.Layer {

    protected void renderTextureOverlay(GuiGraphics pGuiGraphics, ResourceLocation pShaderLocation, float pAlpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, pAlpha);
        pGuiGraphics.blit(pShaderLocation, 0, 0, -90, 0.0F, 0.0F, pGuiGraphics.guiWidth(), pGuiGraphics.guiHeight(), pGuiGraphics.guiWidth(), pGuiGraphics.guiHeight());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void scaleBy(float progress, float type, float start, float end, GuiGraphics graphics) {
        int i = graphics.guiWidth();
        int j = graphics.guiHeight();
        float f = lerp(progress, type, start, end);
        graphics.pose().translate((float)i / 2F, (float)j / 2F, 0.0F);
        graphics.pose().scale(f, f, f);
        graphics.pose().translate((float)(-i) / 2F, (float)(-j) / 2F, 0.0F);
    }

    protected static float lerp(float progress, double type, float start, float end) {
        return Mth.lerp((float) Math.pow(progress, type) / (float) Math.pow(1f, type), start, end);
    }

}
