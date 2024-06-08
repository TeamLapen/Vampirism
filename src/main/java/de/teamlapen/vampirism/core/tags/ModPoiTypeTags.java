package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.jetbrains.annotations.NotNull;

public class ModPoiTypeTags {
    public static final TagKey<PoiType> HAS_FACTION = tag("has_faction");
    public static final TagKey<PoiType> IS_VAMPIRE = tag("has_faction/is_vampire");
    public static final TagKey<PoiType> IS_HUNTER = tag("has_faction/is_hunter");

    private static @NotNull TagKey<PoiType> tag(@NotNull String name) {
        return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(REFERENCE.MODID, name));
    }
}
