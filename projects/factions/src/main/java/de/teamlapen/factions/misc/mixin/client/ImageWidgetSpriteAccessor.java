package de.teamlapen.factions.misc.mixin.client;

import de.teamlapen.factions.misc.extensions.client.IImageWidgetSprite;
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
