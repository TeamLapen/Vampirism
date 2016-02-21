package de.teamlapen.lib.lib.gui.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Adds additional methods to vanilla Gui
 */
public class ExtendedGui extends Gui {

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors. Just changed the coloring from the {@link #drawGradientRect(int, int, int, int, int, int)}
     */
    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    protected void drawGradientRect2(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) right, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double) left, (double) top, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double) left, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double) right, (double) bottom, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
