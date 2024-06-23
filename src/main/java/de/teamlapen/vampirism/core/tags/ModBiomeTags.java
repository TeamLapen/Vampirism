package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class ModBiomeTags {

    public static class HasFaction {
        public static final TagKey<Biome> IS_FACTION_BIOME = tag("has_faction");
        public static final TagKey<Biome> IS_VAMPIRE_BIOME = tag("has_faction/vampire");
        public static final TagKey<Biome> IS_HUNTER_BIOME = tag("has_faction/hunter");
    }

    public static class HasStructure {
        public static final TagKey<Biome> HUNTER_TENT = tag("has_structure/hunter_tent");
        public static final TagKey<Biome> VAMPIRE_DUNGEON = tag("has_structure/vampire_dungeon");
        public static final TagKey<Biome> VAMPIRE_HUT = tag("has_structure/vampire_hut");
        public static final TagKey<Biome> HUNTER_OUTPOST_PLAINS = tag("has_structure/outpost/plains");
        public static final TagKey<Biome> HUNTER_OUTPOST_DESERT = tag("has_structure/outpost/desert");
        public static final TagKey<Biome> HUNTER_OUTPOST_VAMPIRE_FOREST = tag("has_structure/outpost/vampire_forest");
        public static final TagKey<Biome> HUNTER_OUTPOST_BADLANDS = tag("has_structure/outpost/badlands");
        public static final TagKey<Biome> VAMPIRE_ALTAR = tag("has_structure/vampire_altar");
        public static final TagKey<Biome> MOTHER = tag("has_structure/mother");
        public static final TagKey<Biome> CRYPT = tag("has_structure/crypt");
    }

    public static class HasSpawn {
        public static final TagKey<Biome> VAMPIRE = tag("has_spawn/vampire");
        public static final TagKey<Biome> ADVANCED_VAMPIRE = tag("has_spawn/advanced_vampire");
        public static final TagKey<Biome> HUNTER = tag("has_spawn/hunter");
        public static final TagKey<Biome> ADVANCED_HUNTER = tag("has_spawn/advanced_hunter");
    }

    public static class NoSpawn {
        public static final TagKey<Biome> VAMPIRE = tag("no_spawn/vampire");
        public static final TagKey<Biome> ADVANCED_VAMPIRE = tag("no_spawn/advanced_vampire");
        public static final TagKey<Biome> HUNTER = tag("no_spawn/hunter");
        public static final TagKey<Biome> ADVANCED_HUNTER = tag("no_spawn/advanced_hunter");
    }

    private static @NotNull TagKey<Biome> tag(@NotNull String name) {
        return TagKey.create(Registries.BIOME, VResourceLocation.mod(name));
    }
}
