package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ModTags {
    private static ResourceLocation forge(String resourceName) {
        return new ResourceLocation("forge", resourceName);
    }

    private static ResourceLocation vanilla(String resourceName) {
        return new ResourceLocation(resourceName);
    }

    public static class Blocks {
        public static final TagKey<Block> CASTLE_BLOCK = tag("castle_block");
        public static final TagKey<Block> CURSED_EARTH = tag("cursed_earth");
        public static final TagKey<Block> CASTLE_STAIRS = tag("castle_stairs");
        public static final TagKey<Block> CASTLE_SLAPS = tag("castle_slaps");
        public static final TagKey<Block> DARK_SPRUCE_LOG = tag("dark_spruce_log");
        public static final TagKey<Block> CURSED_SPRUCE_LOG = tag("cursed_spruce_log");

        private static TagKey<Block> tag(ResourceLocation resourceLocation) {
            return BlockTags.create(resourceLocation);
        }

        private static TagKey<Block> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> CROSSBOW_ARROW = tag("crossbow_arrow");
        public static final TagKey<Item> HUNTER_INTEL = tag("hunter_intel");
        public static final TagKey<Item> PURE_BLOOD = tag("pure_blood");
        public static final TagKey<Item> VAMPIRE_CLOAK = tag("vampire_cloak");
        public static final TagKey<Item> CASTLE_BLOCK = tag("castle_block");
        public static final TagKey<Item> GARLIC = tag(forge("crops/garlic"));
        public static final TagKey<Item> HOLY_WATER = tag("holy_water");
        public static final TagKey<Item> HOLY_WATER_SPLASH = tag("holy_water_splash");
        public static final TagKey<Item> CASTLE_STAIRS = tag("castle_stairs");
        public static final TagKey<Item> CASTLE_SLAPS = tag("castle_slaps");
        public static final TagKey<Item> CURSEDEARTH = tag("cursed_earth");
        public static final TagKey<Item> DARK_SPRUCE_LOG = tag("dark_spruce_log");
        public static final TagKey<Item> CURSED_SPRUCE_LOG = tag("cursed_spruce_log");
        public static final TagKey<Item> HEART = tag("heart");

        private static TagKey<Item> tag(ResourceLocation resourceLocation) {
            return ItemTags.create(resourceLocation);
        }

        private static TagKey<Item> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Entities {
        /**
         * All hunter mobs (including _imob)
         */
        public static final TagKey<EntityType<?>> HUNTER = tag("hunter");
        /**
         * All vampire mobs (including _imob)
         */
        public static final TagKey<EntityType<?>> VAMPIRE = tag("vampire");
        /**
         * Both advanced hunter mobs (normal + _imob)
         */
        public static final TagKey<EntityType<?>> ADVANCED_HUNTER = tag("advanced_hunter");
        /**
         * Both advanced vampire mobs (normal + _imob)
         */
        public static final TagKey<EntityType<?>> ADVANCED_VAMPIRE = tag("advanced_vampire");

        /**
         * Vanilla zombies
         */
        public static final TagKey<EntityType<?>> ZOMBIES = tag("zombies");

        private static TagKey<EntityType<?>> tag(ResourceLocation resourceLocation) {
            return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, resourceLocation);
        }

        private static TagKey<EntityType<?>> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> BLOOD = tag("vampirism_blood");
        public static final TagKey<Fluid> IMPURE_BLOOD = tag("vampirism_impure_blood");

        private static TagKey<Fluid> tag(ResourceLocation resourceLocation) {
            return FluidTags.create(resourceLocation);
        }

        private static TagKey<Fluid> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Biomes {

        public static final TagKey<Biome> IS_FACTION_BIOME = tag("has_faction");
        public static final TagKey<Biome> IS_VAMPIRE_BIOME = tag("has_faction/vampire");
        public static final TagKey<Biome> IS_HUNTER_BIOME = tag("has_faction/hunter");

        public static class HasStructure {
            public static final TagKey<Biome> HUNTER_TENT = tag("has_structure/hunter_tent");
            public static final TagKey<Biome> VAMPIRE_DUNGEON = tag("has_structure/vampire_dungeon");
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

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class PoiTypes {
        public static final TagKey<PoiType> HAS_FACTION = tag("has_faction");
        public static final TagKey<PoiType> IS_VAMPIRE = tag("is_vampire");
        public static final TagKey<PoiType> IS_HUNTER = tag("is_hunter");

        private static TagKey<PoiType> tag(String name) {
            return TagKey.create(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Professions {
        public static final TagKey<VillagerProfession> HAS_FACTION = tag("has_faction");
        public static final TagKey<VillagerProfession> IS_VAMPIRE = tag("is_vampire");
        public static final TagKey<VillagerProfession> IS_HUNTER = tag("is_hunter");

        private static TagKey<VillagerProfession> tag(String name) {
            return TagKey.create(Registry.VILLAGER_PROFESSION_REGISTRY, new ResourceLocation(REFERENCE.MODID, name));
        }
    }
}
