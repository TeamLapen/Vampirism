package de.teamlapen.lib.lib.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Adds additional methods to vanilla Gui
 */
@OnlyIn(Dist.CLIENT)
public class ExtendedGui extends AbstractGui {

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     * Similar to func_238468_a_, but with gradient on the horizontal axis
     */
    protected void fillGradient2(MatrixStack stack, int left, int top, int right, int bottom, int startColor, int endColor) {
        Matrix4f matrix = stack.getLast().getMatrix();
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(matrix, right, top, this.func_230927_p_()).color(f1, f2, f3, f).endVertex(); //Get blit offset
        worldrenderer.pos(matrix, left, top, this.func_230927_p_()).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(matrix, left, bottom, this.func_230927_p_()).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(matrix, right, bottom, this.func_230927_p_()).color(f1, f2, f3, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }



}
