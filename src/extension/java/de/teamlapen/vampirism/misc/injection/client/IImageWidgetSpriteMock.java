package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IImageWidgetSprite;
import net.minecraft.resources.ResourceLocation;

public interface IImageWidgetSpriteMock extends IImageWidgetSprite {

    @Override
    default ResourceLocation sprite() {
        return null;
    }
}
