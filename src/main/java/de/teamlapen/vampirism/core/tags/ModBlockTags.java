package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModBlockTags {
    public static final TagKey<Block> CURSED_EARTH = tag("cursed_earth");
    public static final TagKey<Block> DARK_SPRUCE_LOG = tag("dark_spruce_log");
    public static final TagKey<Block> CURSED_SPRUCE_LOG = tag("cursed_spruce_log");
    public static final TagKey<Block> TOTEM_TOP_CRAFTED = tag("totem_top_crafted");
    public static final TagKey<Block> TOTEM_TOP_FRAGILE = tag("totem_top_fragile");
    public static final TagKey<Block> TOTEM_TOP = tag("totem_top");
    public static final TagKey<Block> COFFIN = tag("coffin");
    public static final TagKey<Block> DARK_STONE = tag("dark_stone");
    public static final TagKey<Block> DARK_STONE_BRICKS = tag("dark_stone_bricks");
    public static final TagKey<Block> POLISHED_DARK_STONE = tag("polished_dark_brick");
    public static final TagKey<Block> COBBLED_DARK_STONE = tag("cobbled_dark_brick");
    public static final TagKey<Block> DARK_STONE_TILES = tag("dark_brick_tiles");
    public static final TagKey<Block> PURPLE_STONE_BRICKS = tag("purple_brick_bricks");
    public static final TagKey<Block> PURPLE_STONE_TILES = tag("purple_brick_tiles");
    public static final TagKey<Block> NO_SPAWN = tag("no_spawn");
    public static final TagKey<Block> VAMPIRE_SPAWN = tag("vampire_spawn");
    public static final TagKey<Block> REMAINS = tag("remains");
    public static final TagKey<Block> ACTIVE_REMAINS = tag("active_remains");
    public static final TagKey<Block> VULNERABLE_REMAINS = tag("vulnerable_remains");
    public static final TagKey<Block> MOTHER_GROWS_ON = tag("mother_grows_on");
    public static final TagKey<Block> VAMPIRE_BEACON_BASE_BLOCKS = tag("vampire_beacon_base_blocks");
    public static final TagKey<Block> VAMPIRE_BEACON_BASE_ENHANCED_BLOCKS = tag("vampire_beacon_base_enhanced_blocks");
    public static final TagKey<Block> CREEPER_REPELLENT = tag("creeper_repellent");
    public static final TagKey<Block> GARLIC = common("crops/garlic");

    private static @NotNull TagKey<Block> tag(@NotNull String name) {
        return BlockTags.create(VResourceLocation.mod(name));
    }

    private static @NotNull TagKey<Block> common(@NotNull String name) {
        return BlockTags.create(VResourceLocation.common(name));
    }
}
