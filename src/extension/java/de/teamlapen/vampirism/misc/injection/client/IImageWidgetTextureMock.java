package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IImageWidgetTexture;
import net.minecraft.resources.ResourceLocation;

public interface IImageWidgetTextureMock extends IImageWidgetTexture {

    @Override
    default ResourceLocation texture() {
        return null;
    }

    @Override
    default int textureWidth() {
        return 0;
    }

    @Override
    default int textureHeight() {
        return 0;
    }
}
