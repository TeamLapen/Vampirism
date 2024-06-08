package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class ModItemTags {
    public static final TagKey<Item> CROSSBOW_ARROW = tag("crossbow_arrow");
    public static final TagKey<Item> HUNTER_INTEL = tag("hunter_intel");
    public static final TagKey<Item> PURE_BLOOD = tag("pure_blood");
    public static final TagKey<Item> GARLIC = common("crops/garlic");
    public static final TagKey<Item> HOLY_WATER = tag("holy_water");
    public static final TagKey<Item> HOLY_WATER_SPLASH = tag("holy_water_splash");
    public static final TagKey<Item> CURSEDEARTH = tag("cursed_earth");
    public static final TagKey<Item> DARK_SPRUCE_LOG = tag("dark_spruce_log");
    public static final TagKey<Item> CURSED_SPRUCE_LOG = tag("cursed_spruce_log");
    public static final TagKey<Item> HEART = tag("heart");
    public static final TagKey<Item> APPLICABLE_OIL_SWORD = tag("applicable_oil/sword");
    public static final TagKey<Item> APPLICABLE_OIL_PICKAXE = tag("applicable_oil/pickaxe");
    public static final TagKey<Item> APPLICABLE_OIL_ARMOR = tag("applicable_oil/armor");
    public static final TagKey<Item> DARK_STONE = tag("dark_stone");
    public static final TagKey<Item> DARK_STONE_BRICKS = tag("dark_stone_bricks");
    public static final TagKey<Item> POLISHED_DARK_STONE = tag("polished_dark_brick");
    public static final TagKey<Item> COBBLED_DARK_STONE = tag("cobbled_dark_brick");
    public static final TagKey<Item> DARK_STONE_TILES = tag("dark_brick_tiles");
    public static final TagKey<Item> NO_SPAWN = tag("no_spawn");
    public static final TagKey<Item> VAMPIRE_SPAWN = tag("vampire_spawn");
    public static final TagKey<Item> VAMPIRE_BEACON_PAYMENT_ITEM = tag("vampire_beacon_payment_item");
    public static final TagKey<Item> HEART_SEEKER = tag("swords/heart_seeker");
    public static final TagKey<Item> HEART_STRIKER = tag("swords/heart_striker");
    public static final TagKey<Item> VAMPIRE_SLAYER_ITEMS = tag("enchantable/vampire_slayer");
    public static final TagKey<Item> CROSSBOW_ENCHANTABLE = tag("enchantable/crossbow");

    public static final TagKey<Item> SINGLE_CROSSBOWS = tag("crossbows/single");
    public static final TagKey<Item> DOUBLE_CROSSBOWS = tag("crossbows/double");
    public static final TagKey<Item> TECH_CROSSBOWS = tag("crossbows/tech");
    public static final TagKey<Item> BASIC_CROSSBOWS = tag("crossbows/basic");
    public static final TagKey<Item> ENHANCED_CROSSBOWS = tag("crossbows/enhanced");
    public static final TagKey<Item> CROSSBOWS = tag("crossbows");

    public static final TagKey<Item> ARMOR_OF_SWIFTNESS = tag("armors/armor_of_swiftness");
    public static final TagKey<Item> ARMOR_OF_SWIFTNESS_NORMAL = tag("armors/armor_of_swiftness/normal");
    public static final TagKey<Item> ARMOR_OF_SWIFTNESS_ENHANCED = tag("armors/armor_of_swiftness/enhanced");
    public static final TagKey<Item> ARMOR_OF_SWIFTNESS_ULTIMATE = tag("armors/armor_of_swiftness/ultimate");

    public static final TagKey<Item> HUNTER_COAT = tag("armors/hunter_coat");
    public static final TagKey<Item> HUNTER_COAT_NORMAL = tag("armors/hunter_coat/normal");
    public static final TagKey<Item> HUNTER_COAT_ENHANCED = tag("armors/hunter_coat/enhanced");
    public static final TagKey<Item> HUNTER_COAT_ULTIMATE = tag("armors/hunter_coat/ultimate");

    public static final TagKey<Item> VAMPIRE_CLOTHING = tag("armors/vampire_clothing");
    public static final TagKey<Item> VAMPIRE_CLOAK = tag("armors/vampire_clothing/cloak");

    public static final TagKey<Item> HUNTER_ARMOR = tag("armors/hunter_armor");

    public static final TagKey<Item> ADVANCED_HUNTER_CROSSBOW_ARROWS = tag("advanced_hunter_crossbow_arrows");

    private static @NotNull TagKey<Item> tag(@NotNull String name) {
        return ItemTags.create(new ResourceLocation(REFERENCE.MODID, name));
    }

    private static @NotNull TagKey<Item> common(@NotNull String name) {
        return ItemTags.create(new ResourceLocation("c", name));
    }
}
