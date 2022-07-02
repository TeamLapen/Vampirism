package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;

public class ModTags {
    private static ResourceLocation forge(String resourceName) {
        return new ResourceLocation("forge", resourceName);
    }

    private static ResourceLocation vanilla(String resourceName) {
        return new ResourceLocation(resourceName);
    }

    public static class Blocks {
        public static final ITag.INamedTag<Block> CASTLE_BLOCK = tag("castle_block");
        public static final ITag.INamedTag<Block> CURSEDEARTH = tag("cursed_earth");
        public static final ITag.INamedTag<Block> CASTLE_STAIRS = tag("castle_stairs");
        public static final ITag.INamedTag<Block> CASTLE_SLAPS = tag("castle_slaps");
        public static final ITag.INamedTag<Block> DARK_SPRUCE_LOG = tag("dark_spruce_log");
        public static final ITag.INamedTag<Block> CURSED_SPRUCE_LOG = tag("cursed_spruce_log");

        private static ITag.INamedTag<Block> tag(ResourceLocation resourceLocation) {
            return BlockTags.bind(resourceLocation.toString());
        }

        private static ITag.INamedTag<Block> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Items {
        public static final ITag.INamedTag<Item> CROSSBOW_ARROW = tag("crossbow_arrow");
        public static final ITag.INamedTag<Item> HUNTER_INTEL = tag("hunter_intel");
        public static final ITag.INamedTag<Item> PURE_BLOOD = tag("pure_blood");
        public static final ITag.INamedTag<Item> VAMPIRE_CLOAK = tag("vampire_cloak");
        public static final ITag.INamedTag<Item> CASTLE_BLOCK = tag("castle_block");
        public static final ITag.INamedTag<Item> GARLIC = tag(forge("crops/garlic"));
        public static final ITag.INamedTag<Item> HOLY_WATER = tag("holy_water");
        public static final ITag.INamedTag<Item> HOLY_WATER_SPLASH = tag("holy_water_splash");
        public static final ITag.INamedTag<Item> CASTLE_STAIRS = tag("castle_stairs");
        public static final ITag.INamedTag<Item> CASTLE_SLAPS = tag("castle_slaps");
        public static final ITag.INamedTag<Item> CURSEDEARTH = tag("cursed_earth");
        public static final ITag.INamedTag<Item> DARK_SPRUCE_LOG = tag("dark_spruce_log");
        public static final ITag.INamedTag<Item> CURSED_SPRUCE_LOG = tag("cursed_spruce_log");
        public static final ITag.INamedTag<Item> HEART = tag("heart");
        public static final ITag.INamedTag<Item> APPLICABLE_OIL_SWORD = tag("applicable_oil/sword");
        public static final ITag.INamedTag<Item> APPLICABLE_OIL_PICKAXE = tag("applicable_oil/pickaxe");
        public static final ITag.INamedTag<Item> APPLICABLE_OIL_ARMOR = tag("applicable_oil/armor");


        private static ITag.INamedTag<Item> tag(ResourceLocation resourceLocation) {
            return ItemTags.bind(resourceLocation.toString());
        }

        private static ITag.INamedTag<Item> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Entities {
        /**
         * All hunter mobs (including _imob)
         */
        public static final ITag.INamedTag<EntityType<?>> HUNTER = tag("hunter");
        /**
         * All vampire mobs (including _imob)
         */
        public static final ITag.INamedTag<EntityType<?>> VAMPIRE = tag("vampire");
        /**
         * Both advanced hunter mobs (normal + _imob)
         */
        public static final ITag.INamedTag<EntityType<?>> ADVANCED_HUNTER = tag("advanced_hunter");
        /**
         * Both advanced vampire mobs (normal + _imob)
         */
        public static final ITag.INamedTag<EntityType<?>> ADVANCED_VAMPIRE = tag("advanced_vampire");

        /**
         * Vanilla zombies
         */
        public static final ITag.INamedTag<EntityType<?>> ZOMBIES = tag("zombies");

        private static ITag.INamedTag<EntityType<?>> tag(ResourceLocation resourceLocation) {
            return EntityTypeTags.bind(resourceLocation.toString());
        }

        private static ITag.INamedTag<EntityType<?>> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Fluids {
        public static final ITag.INamedTag<Fluid> BLOOD = tag("vampirism_blood");
        public static final ITag.INamedTag<Fluid> IMPURE_BLOOD = tag("vampirism_impure_blood");

        private static ITag.INamedTag<Fluid> tag(ResourceLocation resourceLocation) {
            return FluidTags.bind(resourceLocation.toString());
        }

        private static ITag.INamedTag<Fluid> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }
}
