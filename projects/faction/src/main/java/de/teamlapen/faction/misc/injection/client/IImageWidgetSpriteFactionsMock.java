package de.teamlapen.faction.misc.injection.client;

import de.teamlapen.faction.misc.extensions.client.IImageWidgetSprite;
import net.minecraft.resources.Identifier;

@Deprecated
public interface IImageWidgetSpriteFactionsMock extends IImageWidgetSprite {

    @Override
    default Identifier sprite() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
