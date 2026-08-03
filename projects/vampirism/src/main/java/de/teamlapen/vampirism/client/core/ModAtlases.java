package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;

public class ModAtlases {

    public static final Identifier COFFINS = VIdentifier.mod("coffins");

    public static final SpriteId SUN_FIRE_0 = Sheets.BLOCKS_MAPPER.apply(VIdentifier.mod("sun_fire_0"));
    public static final SpriteId SUN_FIRE_1 = Sheets.BLOCKS_MAPPER.apply(VIdentifier.mod("sun_fire_1"));
}
