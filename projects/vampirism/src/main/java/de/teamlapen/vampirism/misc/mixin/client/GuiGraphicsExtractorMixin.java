package de.teamlapen.vampirism.misc.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.teamlapen.vampirism.misc.extension.client.IGuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin implements IGuiGraphicsExtractor {

    @Shadow
    @Final
    private TextureAtlas guiSprites;

    @Shadow
    private static GuiSpriteScaling getSpriteScaling(TextureAtlasSprite sprite) {
        return null;
    }

    @Shadow
    protected abstract void blitTiledSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int x, int y, int width, int height, int u, int v, int spriteWidth, int spriteHeight, int textureWidth, int textureHeight, int color);

    @Shadow
    public abstract void blitSprite(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color);

    @Shadow
    public abstract void text(Font font, FormattedCharSequence str, int x, int y, int color, boolean dropShadow);

    @Override
    public void vampirism$blitSpriteTiledOffset(Identifier texture, int x, int y, int width, int height, int xOffset, int yOffset, int color) {
        x += xOffset;
        y += yOffset;
        width -= xOffset;
        height -= yOffset;

        TextureAtlasSprite textureatlassprite = guiSprites.getSprite(texture);
        GuiSpriteScaling guispritescaling = getSpriteScaling(textureatlassprite);
        if (guispritescaling instanceof GuiSpriteScaling.Tile(int tileWidth, int tileHeight)) {
            blitTiledSprite(RenderPipelines.GUI_TEXTURED, textureatlassprite, x, y, width, height, xOffset, yOffset, tileWidth, tileHeight, tileWidth, tileHeight, color);
        } else {
            blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height, color);
        }
    }
}
