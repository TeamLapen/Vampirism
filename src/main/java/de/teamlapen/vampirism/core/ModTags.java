package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class ModTags {
    private static @NotNull ResourceLocation forge(@NotNull String resourceName) {
        return new ResourceLocation("forge", resourceName);
    }

    private static @NotNull ResourceLocation vanilla(@NotNull String resourceName) {
        return new ResourceLocation(resourceName);
    }

    public static class Blocks {
        public static final TagKey<Block> CASTLE_BLOCK = tag("castle_block");
        public static final TagKey<Block> CURSED_EARTH = tag("cursed_earth");
        public static final TagKey<Block> CASTLE_STAIRS = tag("castle_stairs");
        public static final TagKey<Block> CASTLE_SLAPS = tag("castle_slaps");
        public static final TagKey<Block> DARK_SPRUCE_LOG = tag("dark_spruce_log");
        public static final TagKey<Block> CURSED_SPRUCE_LOG = tag("cursed_spruce_log");
        public static final TagKey<Block> TOTEM_TOP_CRAFTED = tag("totem_top_crafted");
        public static final TagKey<Block> TOTEM_TOP_FRAGILE = tag("totem_top_fragile");
        public static final TagKey<Block> TOTEM_TOP = tag("totem_top");
        public static final TagKey<Block> COFFIN = tag("coffin");

        private static @NotNull TagKey<Block> tag(@NotNull ResourceLocation resourceLocation) {
            return BlockTags.create(resourceLocation);
        }

        private static @NotNull TagKey<Block> tag(@NotNull String name) {
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
        public static final TagKey<Item> APPLICABLE_OIL_SWORD = tag("applicable_oil/sword");
        public static final TagKey<Item> APPLICABLE_OIL_PICKAXE = tag("applicable_oil/pickaxe");
        public static final TagKey<Item> APPLICABLE_OIL_ARMOR = tag("applicable_oil/armor");
        public static final TagKey<Item> HUNTER_COAT = tag("armo/hunter_coat");

        private static @NotNull TagKey<Item> tag(@NotNull ResourceLocation resourceLocation) {
            return ItemTags.create(resourceLocation);
        }

        private static @NotNull TagKey<Item> tag(@NotNull String name) {
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

        private static @NotNull TagKey<EntityType<?>> tag(@NotNull ResourceLocation resourceLocation) {
            return TagKey.create(Registries.ENTITY_TYPE, resourceLocation);
        }

        private static @NotNull TagKey<EntityType<?>> tag(@NotNull String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> BLOOD = tag("vampirism_blood");
        public static final TagKey<Fluid> IMPURE_BLOOD = tag("vampirism_impure_blood");

        private static @NotNull TagKey<Fluid> tag(@NotNull ResourceLocation resourceLocation) {
            return FluidTags.create(resourceLocation);
        }

        private static @NotNull TagKey<Fluid> tag(@NotNull String name) {
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
            public static final TagKey<Biome> VAMPIRE_HUT = tag("has_structure/vampire_hut");
            public static final TagKey<Biome> HUNTER_OUTPOST = tag("has_structure/outpost");
            public static final TagKey<Biome> VAMPIRE_ALTAR = tag("has_structure/vampire_altar");
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
            return TagKey.create(Registries.BIOME, new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class PoiTypes {
        public static final TagKey<PoiType> HAS_FACTION = tag("has_faction");
        public static final TagKey<PoiType> IS_VAMPIRE = tag("is_vampire");
        public static final TagKey<PoiType> IS_HUNTER = tag("is_hunter");

        private static @NotNull TagKey<PoiType> tag(@NotNull String name) {
            return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Professions {
        public static final TagKey<VillagerProfession> HAS_FACTION = tag("has_faction");
        public static final TagKey<VillagerProfession> IS_VAMPIRE = tag("is_vampire");
        public static final TagKey<VillagerProfession> IS_HUNTER = tag("is_hunter");

        private static @NotNull TagKey<VillagerProfession> tag(@NotNull String name) {
            return TagKey.create(Registries.VILLAGER_PROFESSION, new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class DamageTypes {

        public static final TagKey<DamageType> ENTITY_PHYSICAL = tag("entity_physical");

        private static @NotNull TagKey<DamageType> tag(@NotNull String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Tasks {

        public static final TagKey<Task> HAS_FACTION = tag("has_faction");
        public static final TagKey<Task> IS_VAMPIRE = tag("has_faction/vampire");
        public static final TagKey<Task> IS_HUNTER = tag("has_faction/hunter");
        public static final TagKey<Task> IS_UNIQUE = tag("is_unique");
        public static final TagKey<Task> AWARDS_LORD_LEVEL = tag("awards_lord_level");

        private static @NotNull TagKey<Task> tag(@NotNull String name) {
            return TagKey.create(VampirismRegistries.TASK_ID, new ResourceLocation(REFERENCE.MODID, name));
        }
    }
}
