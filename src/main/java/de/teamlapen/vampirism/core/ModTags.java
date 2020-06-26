package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;

public class ModTags {
    public static class Blocks {
        public static final ITag<Block> CASTLE_BLOCK = tag("castle_block");
        public static final ITag<Block> CURSEDEARTH = tag("cursed_earth");
        public static final ITag<Block> CASTLE_STAIRS = tag("castle_stairs");
        public static final ITag<Block> CASTLE_SLAPS = tag("castle_slaps");

        private static ITag<Block> tag(ResourceLocation resourceLocation) {
            return new BlockTags.Wrapper(resourceLocation);
        }

        private static ITag<Block> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Items {
        public static final ITag<Item> CROSSBOW_ARROW = tag("crossbow_arrow");
        public static final ITag<Item> HUNTER_INTEL = tag("hunter_intel");
        public static final ITag<Item> PURE_BLOOD = tag("pure_blood");
        public static final ITag<Item> VAMPIRE_CLOAK = tag("vampire_cloak");
        public static final ITag<Item> CASTLE_BLOCK = tag("castle_block");
        public static final ITag<Item> GARLIC = tag(forge("crops/garlic"));
        public static final ITag<Item> HOLY_WATER = tag("holy_water");
        public static final ITag<Item> HOLY_WATER_SPLASH = tag("holy_water_splash");


        private static ITag<Item> tag(ResourceLocation resourceLocation) {
            return new ItemTags.Wrapper(resourceLocation);
        }

        private static ITag<Item> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Entities {
        public static final ITag<EntityType<?>> HUNTER = tag("hunter");
        public static final ITag<EntityType<?>> VAMPIRE = tag("vampire");

        private static ITag<EntityType<?>> tag(ResourceLocation resourceLocation) {
            return new EntityTypeTags.Wrapper(resourceLocation);
        }

        private static ITag<EntityType<?>> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Fluids {
        public static final ITag<Fluid> BLOOD = tag("vampirism_blood");
        public static final ITag<Fluid> IMPURE_BLOOD = tag("vampirism_impure_blood");

        private static ITag<Fluid> tag(ResourceLocation resourceLocation) {
            return new FluidTags.Wrapper(resourceLocation);
        }

        private static ITag<Fluid> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    private static ResourceLocation forge(String resourceName){
        return new ResourceLocation("forge", resourceName);
    }

    private static ResourceLocation vanilla(String resourceName){
        return new ResourceLocation(resourceName);
    }
}
