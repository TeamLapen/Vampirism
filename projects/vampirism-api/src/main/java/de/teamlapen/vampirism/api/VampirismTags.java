package de.teamlapen.vampirism.api;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class VampirismTags {

    public static class Factions {
        public static final TagKey<IFaction<?>> IS_HUNTER = tag("is_hunter");
        public static final TagKey<IFaction<?>> IS_VAMPIRE = tag("is_vampire");

        private static TagKey<IFaction<?>> tag(String name) {
            return TagKey.create(FactionRegistries.Keys.FACTION, VIdentifier.mod(name));
        }

    }

    @SuppressWarnings("SameParameterValue")
    public static class Biomes {
        public static final TagKey<Biome> HAS_NO_SUNDAMAGE = tag("has_no_sundamage");

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, VIdentifier.mod(name));
        }
    }

    @SuppressWarnings("SameParameterValue")
    public static class DimensionTypes {
        public static final TagKey<DimensionType> HAS_NO_SUNDAMAGE = tag("has_no_sundamage");

        private static TagKey<DimensionType> tag(String name) {
            return TagKey.create(Registries.DIMENSION_TYPE, VIdentifier.mod(name));
        }
    }

}
