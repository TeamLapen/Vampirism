package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IImageWidgetSprite;
import net.minecraft.resources.Identifier;

@Deprecated
public interface IImageWidgetSpriteFactionsMock extends IImageWidgetSprite {

    @Override
    default Identifier sprite() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
