package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.tags.*;
import net.minecraft.resources.ResourceLocation;

public class ModTags {
    private static ResourceLocation forge(String resourceName) {
        return new ResourceLocation("forge", resourceName);
    }

    private static ResourceLocation vanilla(String resourceName) {
        return new ResourceLocation(resourceName);
    }

    public static class Blocks {
        public static final Tag.Named<Block> CASTLE_BLOCK = tag("castle_block");
        public static final Tag.Named<Block> CURSEDEARTH = tag("cursed_earth");
        public static final Tag.Named<Block> CASTLE_STAIRS = tag("castle_stairs");
        public static final Tag.Named<Block> CASTLE_SLAPS = tag("castle_slaps");

        private static Tag.Named<Block> tag(ResourceLocation resourceLocation) {
            return BlockTags.bind(resourceLocation.toString());
        }

        private static Tag.Named<Block> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Items {
        public static final Tag.Named<Item> CROSSBOW_ARROW = tag("crossbow_arrow");
        public static final Tag.Named<Item> HUNTER_INTEL = tag("hunter_intel");
        public static final Tag.Named<Item> PURE_BLOOD = tag("pure_blood");
        public static final Tag.Named<Item> VAMPIRE_CLOAK = tag("vampire_cloak");
        public static final Tag.Named<Item> CASTLE_BLOCK = tag("castle_block");
        public static final Tag.Named<Item> GARLIC = tag(forge("crops/garlic"));
        public static final Tag.Named<Item> HOLY_WATER = tag("holy_water");
        public static final Tag.Named<Item> HOLY_WATER_SPLASH = tag("holy_water_splash");
        public static final Tag.Named<Item> CASTLE_STAIRS = tag("castle_stairs");
        public static final Tag.Named<Item> CASTLE_SLAPS = tag("castle_slaps");
        public static final Tag.Named<Item> CURSEDEARTH = tag("cursed_earth");


        private static Tag.Named<Item> tag(ResourceLocation resourceLocation) {
            return ItemTags.bind(resourceLocation.toString());
        }

        private static Tag.Named<Item> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Entities {
        /**
         * All hunter mobs (including _imob)
         */
        public static final Tag.Named<EntityType<?>> HUNTER = tag("hunter");
        /**
         * All vampire mobs (including _imob)
         */
        public static final Tag.Named<EntityType<?>> VAMPIRE = tag("vampire");
        /**
         * Both advanced hunter mobs (normal + _imob)
         */
        public static final Tag.Named<EntityType<?>> ADVANCED_HUNTER = tag("advanced_hunter");
        /**
         * Both advanced vampire mobs (normal + _imob)
         */
        public static final Tag.Named<EntityType<?>> ADVANCED_VAMPIRE = tag("advanced_vampire");

        private static Tag.Named<EntityType<?>> tag(ResourceLocation resourceLocation) {
            return EntityTypeTags.bind(resourceLocation.toString());
        }

        private static Tag.Named<EntityType<?>> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Fluids {
        public static final Tag.Named<Fluid> BLOOD = tag("vampirism_blood");
        public static final Tag.Named<Fluid> IMPURE_BLOOD = tag("vampirism_impure_blood");

        private static Tag.Named<Fluid> tag(ResourceLocation resourceLocation) {
            return FluidTags.bind(resourceLocation.toString());
        }

        private static Tag.Named<Fluid> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }
}
