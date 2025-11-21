package de.teamlapen.vampirism.misc.mixin.client.accessor;

import de.teamlapen.vampirism.misc.extension.client.IImageWidgetSprite;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ImageWidget.Sprite.class)
public interface ImageWidgetSpriteAccessor extends IImageWidgetSprite {

    @Accessor("sprite")
    @Override
    ResourceLocation sprite();
}
