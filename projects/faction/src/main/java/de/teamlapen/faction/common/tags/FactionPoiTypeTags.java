package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class FactionPoiTypeTags {

    public static final TagKey<PoiType> HAS_FACTION = tag("has_faction");

    private static TagKey<PoiType> tag(String name) {
        return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, FIdentifier.mod(name));
    }
}
