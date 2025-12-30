package de.teamlapen.faction.misc.mixin.client;

import de.teamlapen.faction.misc.extensions.client.IImageWidgetSprite;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ImageWidget.Sprite.class)
public interface ImageWidgetSpriteAccessor extends IImageWidgetSprite {

    @Accessor("sprite")
    @Override
    Identifier sprite();
}
