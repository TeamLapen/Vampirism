package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModStructureTags {
    public static final TagKey<Structure> ON_ANCIENT_REMAINS_MAPS = tag("on_ancient_remains_maps");
    public static final TagKey<Structure> ON_CRYPT_MAPS = tag("on_crypt_maps");
    public static final TagKey<Structure> HUNTER_OUTPOST = tag("hunter_outpost");
    public static final TagKey<Structure> VELMORRA_PORTAL = tag("velmorra_portal");

    private static TagKey<Structure> tag(String name) {
        return TagKey.create(Registries.STRUCTURE, VIdentifier.mod(name));
    }
}
