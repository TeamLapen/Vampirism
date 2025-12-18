package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IImageWidgetSprite;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public interface IImageWidgetSpriteFactionsMock extends IImageWidgetSprite {

    @Override
    default ResourceLocation sprite() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
