package de.teamlapen.factions.client.gui.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public abstract class TextureOverlay extends BaseOverlay {

    protected void renderTextureOverlay(GuiGraphics pGuiGraphics, ResourceLocation pShaderLocation, float pAlpha) {
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, pShaderLocation, 0, 0, 0.0F, 0.0F, pGuiGraphics.guiWidth(), pGuiGraphics.guiHeight(), pGuiGraphics.guiWidth(), pGuiGraphics.guiHeight(), ARGB.color(pAlpha, -90));
    }

    protected void scaleBy(float progress, float type, float start, float end, GuiGraphics graphics) {
        int i = graphics.guiWidth();
        int j = graphics.guiHeight();
        float f = lerp(progress, type, start, end);
        graphics.pose().translate((float) i / 2F, (float) j / 2F);
        graphics.pose().scale(f);
        graphics.pose().translate((float) (-i) / 2F, (float) (-j) / 2F);
    }

    protected static float lerp(float progress, double type, float start, float end) {
        return Mth.lerp((float) Math.pow(progress, type) / (float) Math.pow(1f, type), start, end);
    }

}
