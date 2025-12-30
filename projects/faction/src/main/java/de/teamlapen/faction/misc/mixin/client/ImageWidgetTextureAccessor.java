package de.teamlapen.faction.misc.mixin.client;

import de.teamlapen.faction.misc.extensions.client.IImageWidgetTexture;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ImageWidget.Texture.class)
public interface ImageWidgetTextureAccessor extends IImageWidgetTexture {
    
    @Accessor("texture")
    @Override
    Identifier texture();

    @Accessor("textureWidth")
    @Override
    int textureWidth();

    @Accessor("textureHeight")
    @Override
    int textureHeight();
    
}
