package de.teamlapen.lib.lib.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Adds additional methods to vanilla Gui
 */
public class ExtendedGui {

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     * Similar to fillGradient, but with gradient on the horizontal axis
     */
    protected void fillGradient2(@NotNull PoseStack stack, int left, int top, int right, int bottom, int startColor, int endColor) {
        Matrix4f matrix = stack.last().pose();
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.addVertex(matrix, right, top, 0).setColor(f1, f2, f3, f);
        bufferBuilder.addVertex(matrix, left, top, 0).setColor(f5, f6, f7, f4);
        bufferBuilder.addVertex(matrix, left, bottom, 0).setColor(f5, f6, f7, f4);
        bufferBuilder.addVertex(matrix, right, bottom, 0).setColor(f1, f2, f3, f);
        RenderSystem.disableBlend();

    }


}
