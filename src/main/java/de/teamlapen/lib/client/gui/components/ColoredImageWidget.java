package de.teamlapen.lib.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

public abstract class ColoredImageWidget {

    ColoredImageWidget() {}

    public static ImageWidget texture(int width, int height, ResourceLocation texture, int textureWidth, int textureHeight, int color) {
        return new ColoredImageWidget.Texture(0, 0, width, height, texture, textureWidth, textureHeight, color);
    }

    public static ImageWidget sprite(int width, int height, ResourceLocation sprite, int color) {
        return new ColoredImageWidget.Sprite(0, 0, width, height, sprite, color);
    }

    public static class Sprite extends ImageWidget.Sprite {

        private final int color;

        public Sprite(int x, int y, int width, int height, ResourceLocation sprite, int color) {
            super(x, y, width, height, sprite);
            this.color = color;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.color);
        }
    }

    public static class Texture extends ImageWidget.Texture {

        private final int color;

        public Texture(int x, int y, int width, int height, ResourceLocation texture, int textureWidth, int textureHeight, int color) {
            super(x, y, width, height, texture, textureWidth, textureHeight);
            this.color = color;
        }

        @Override
        protected void renderWidget(GuiGraphics p_294145_, int p_294755_, int p_294985_, float p_294245_) {
            p_294145_.blit(
                    RenderPipelines.GUI_TEXTURED,
                    this.texture(),
                    this.getX(),
                    this.getY(),
                    0.0F,
                    0.0F,
                    this.getWidth(),
                    this.getHeight(),
                    this.textureWidth(),
                    this.textureHeight(),
                    color
            );
        }
    }
}
