package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {
    public static class Blocks {
        public static final Tag<Block> CASTLE_BLOCK = tag("castle_block");
        public static final Tag<Block> CURSEDEARTH = tag("cursed_earth");
        public static final Tag<Block> CASTLE_STAIRS = tag("castle_stairs");
        public static final Tag<Block> CASTLE_SLAPS = tag("castle_slaps");

        private static Tag<Block> tag(ResourceLocation resourceLocation) {
            return new BlockTags.Wrapper(resourceLocation);
        }

        private static Tag<Block> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Items {
        public static final Tag<Item> CROSSBOW_ARROW = tag("crossbow_arrow");
        public static final Tag<Item> HUNTER_INTEL = tag("hunter_intel");
        public static final Tag<Item> PURE_BLOOD = tag("pure_blood");
        public static final Tag<Item> VAMPIRE_CLOAK = tag("vampire_cloak");
        public static final Tag<Item> CASTLE_BLOCK = tag("castle_block");


        private static Tag<Item> tag(ResourceLocation resourceLocation) {
            return new ItemTags.Wrapper(resourceLocation);
        }

        private static Tag<Item> tag(String name) {
            return tag(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Entities {
        public static final Tag<EntityType<?>> HUNTER = tag("hunter");
        public static final Tag<EntityType<?>> VAMPIRE = tag("vampire");

        private static Tag<EntityType<?>> tag(ResourceLocation resourceLocation) {
            return new EntityTypeTags.Wrapper(resourceLocation);
        }

        private static Tag<EntityType<?>> tag(String name) {
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
