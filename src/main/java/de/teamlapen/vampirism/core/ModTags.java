package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {
    public static class Blocks {
        public static final Tag<Block> OBSIDIAN = tag("forge", "obsidian");
        public static final Tag<Block> CASTLE_BLOCK = tag("castle_block");
        public static final Tag<Block> CURSEDEARTH = tag("cursed_earth");

        private static Tag<Block> tag(String domain, String name) {
            return new BlockTags.Wrapper(new ResourceLocation(domain, name));
        }

        private static Tag<Block> tag(String name) {
            return new BlockTags.Wrapper(new ResourceLocation(REFERENCE.MODID, name));
        }
    }

    public static class Items {
        public static final Tag<Item> CROSSBOW_ARROW = tag("crossbow_arrow");
        public static final Tag<Item> HUNTER_INTEL = tag("hunter_intel");
        public static final Tag<Item> PURE_BLOOD = tag("pure_blood");
        public static final Tag<Item> VAMPIRE_CLOAK = tag("vampire_cloak");
        public static final Tag<Item> OBSIDIAN = tag("forge", "obsidian");
        public static final Tag<Item> CASTLE_BLOCK = tag("castle_block");


        private static Tag<Item> tag(String domain, String name) {
            return new ItemTags.Wrapper(new ResourceLocation(domain, name));
        }

        private static Tag<Item> tag(String name) {
            return new ItemTags.Wrapper(new ResourceLocation(REFERENCE.MODID, name));
        }
    }
}
