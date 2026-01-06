package de.teamlapen.faction.misc.injection.client;

import de.teamlapen.faction.misc.extensions.client.IImageWidgetTexture;
import net.minecraft.resources.Identifier;

@Deprecated
public interface IImageWidgetTextureFactionsMock extends IImageWidgetTexture {

    @Override
    default Identifier texture() {
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
