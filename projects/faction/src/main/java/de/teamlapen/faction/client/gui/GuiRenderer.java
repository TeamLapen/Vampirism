package de.teamlapen.faction.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class GuiRenderer {

    private static final int DEFAULT_IMAGE_WIDTH = 256;
    private static final int DEFAULT_IMAGE_HEIGHT = 256;

    public static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    public static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height, int imageWidth, int imageHeight) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, imageWidth, imageHeight);
    }

    public static void blitColored(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height, int imageWidth, int imageHeight, int color) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, imageWidth, imageHeight, color);
    }

    public static void blitWithOffset(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int xOffset, int yOffset, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, xOffset, yOffset, width, height, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    public static void blitWithOffset(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int xOffset, int yOffset, int width, int height, int imageWidth, int imageHeight) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, xOffset, yOffset, width, height, imageWidth, imageHeight);
    }

    public static void blitSpriteTiled(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height, int color) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height, color);
    }

    public static void blitSprite(GuiGraphicsExtractor graphics, Identifier texture, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight, int color) {
        graphics.enableScissor(x, y, x+ uWidth, y+vHeight);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x - uPosition, y - vPosition, textureWidth, textureHeight, color);
        graphics.disableScissor();
    }

}
