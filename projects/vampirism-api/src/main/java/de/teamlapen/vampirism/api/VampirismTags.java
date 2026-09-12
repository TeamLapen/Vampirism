package de.teamlapen.vampirism.api;

import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import static de.teamlapen.vampirism.api.APIUtil.*;


public class VampirismTags {

    public static class Factions {
        public static final TagKey<IFaction<?>> IS_HUNTER = faction("is_hunter");
        public static final TagKey<IFaction<?>> IS_VAMPIRE = faction("is_vampire");
    }

    public static class Biomes {
        public static final TagKey<Biome> HAS_NO_SUNDAMAGE = biome("has_no_sundamage");
    }

    public static class DimensionTypes {
        public static final TagKey<DimensionType> HAS_NO_SUNDAMAGE = dimensionType("has_no_sundamage");
    }

}
