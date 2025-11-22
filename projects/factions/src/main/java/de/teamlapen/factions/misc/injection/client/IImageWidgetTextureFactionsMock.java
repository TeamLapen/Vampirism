package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IImageWidgetTexture;
import net.minecraft.resources.ResourceLocation;

public interface IImageWidgetTextureFactionsMock extends IImageWidgetTexture {

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
