package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IImageWidgetTexture;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public interface IImageWidgetTextureFactionsMock extends IImageWidgetTexture {

    @Override
    default ResourceLocation texture() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default int textureWidth() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default int textureHeight() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
