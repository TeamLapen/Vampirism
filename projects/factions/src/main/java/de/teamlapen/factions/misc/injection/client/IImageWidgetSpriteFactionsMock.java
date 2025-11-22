package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IImageWidgetSprite;
import net.minecraft.resources.ResourceLocation;

public interface IImageWidgetSpriteFactionsMock extends IImageWidgetSprite {

    @Override
    default ResourceLocation sprite() {
        return null;
    }
}
