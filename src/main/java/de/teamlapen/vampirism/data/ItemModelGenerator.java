package de.teamlapen.vampirism.data;

import de.teamlapen.lib.lib.data.BaseItemModelGenerator;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemModelGenerator extends BaseItemModelGenerator {
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Set<Block> blocks = new HashSet<>() {{
            add(ModBlocks.ALTAR_TIP.get());
            add(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get());
            add(ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get());
            add(ModBlocks.CASTLE_BLOCK_DARK_STONE.get());
            add(ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get());
            add(ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get());
            add(ModBlocks.CASTLE_SLAB_DARK_BRICK.get());
            add(ModBlocks.CASTLE_SLAB_DARK_STONE.get());
            add(ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get());
            add(ModBlocks.CASTLE_STAIRS_DARK_BRICK.get());
            add(ModBlocks.CASTLE_STAIRS_DARK_STONE.get());
            add(ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get());
            add(ModBlocks.BLOOD_GRINDER.get());
            add(ModBlocks.BLOOD_PEDESTAL.get());
            add(ModBlocks.POTION_TABLE.get());
            add(ModBlocks.BLOOD_SIEVE.get());
            add(ModBlocks.ALTAR_CLEANSING.get());
            add(ModBlocks.CURSED_EARTH.get());
            add(ModBlocks.HUNTER_TABLE.get());
            add(ModBlocks.SUNSCREEN_BEACON.get());
            add(ModBlocks.TOTEM_TOP.get());
            add(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get());
            add(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get());
            add(ModBlocks.VAMPIRE_ORCHID.get());
            add(ModBlocks.BLOODY_SPRUCE_LOG.get());
            add(ModBlocks.CHANDELIER.get());
            add(ModBlocks.CROSS.get());
            add(ModBlocks.TOMBSTONE1.get());
            add(ModBlocks.TOMBSTONE2.get());
            add(ModBlocks.TOMBSTONE3.get());
            add(ModBlocks.GRAVE_CAGE.get());
            add(ModBlocks.CURSED_GRASS_BLOCK.get());
        }};
        Set<Item> items = new HashSet<>() {{
            add(ModItems.HUNTER_COAT_CHEST_NORMAL.get());
            add(ModItems.HUNTER_COAT_CHEST_ENHANCED.get());
            add(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get());
            add(ModItems.HUNTER_COAT_FEET_NORMAL.get());
            add(ModItems.HUNTER_COAT_FEET_ENHANCED.get());
            add(ModItems.HUNTER_COAT_FEET_ULTIMATE.get());
            add(ModItems.HUNTER_COAT_HEAD_NORMAL.get());
            add(ModItems.HUNTER_COAT_HEAD_ENHANCED.get());
            add(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get());
            add(ModItems.HUNTER_COAT_LEGS_NORMAL.get());
            add(ModItems.HUNTER_COAT_LEGS_ENHANCED.get());
            add(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get());
            add(ModItems.BLOOD_BUCKET.get());
            add(ModItems.IMPURE_BLOOD_BUCKET.get());
            add(ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get());
            add(ModItems.BLOOD_INFUSED_IRON_INGOT.get());
            add(ModItems.HOLY_SALT.get());
            add(ModItems.HOLY_SALT_WATER.get());
            add(ModItems.HUMAN_HEART.get());
            add(ModItems.INJECTION_EMPTY.get());
            add(ModItems.INJECTION_GARLIC.get());
            add(ModItems.INJECTION_SANGUINARE.get());
            add(ModItems.INJECTION_ZOMBIE_BLOOD.get());
            add(ModItems.OBSIDIAN_ARMOR_CHEST_NORMAL.get());
            add(ModItems.OBSIDIAN_ARMOR_CHEST_ENHANCED.get());
            add(ModItems.OBSIDIAN_ARMOR_CHEST_ULTIMATE.get());
            add(ModItems.OBSIDIAN_ARMOR_FEET_NORMAL.get());
            add(ModItems.OBSIDIAN_ARMOR_FEET_ENHANCED.get());
            add(ModItems.OBSIDIAN_ARMOR_FEET_ULTIMATE.get());
            add(ModItems.OBSIDIAN_ARMOR_HEAD_NORMAL.get());
            add(ModItems.OBSIDIAN_ARMOR_HEAD_ENHANCED.get());
            add(ModItems.OBSIDIAN_ARMOR_HEAD_ULTIMATE.get());
            add(ModItems.OBSIDIAN_ARMOR_LEGS_NORMAL.get());
            add(ModItems.OBSIDIAN_ARMOR_LEGS_ENHANCED.get());
            add(ModItems.OBSIDIAN_ARMOR_LEGS_ULTIMATE.get());
            add(ModItems.PURIFIED_GARLIC.get());
            add(ModItems.SOUL_ORB_VAMPIRE.get());
            add(ModItems.TECH_CROSSBOW_AMMO_PACKAGE.get());
            add(ModItems.VAMPIRE_BLOOD_BOTTLE.get());
            add(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get());
            add(ModItems.VAMPIRE_CLOAK_BLACK_RED.get());
            add(ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get());
            add(ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get());
            add(ModItems.VAMPIRE_CLOAK_RED_BLACK.get());
            add(ModItems.VAMPIRE_FANG.get());
            add(ModItems.WEAK_HUMAN_HEART.get());
            add(ModItems.ITEM_TENT.get());
            add(ModItems.PURE_BLOOD_0.get());
            add(ModItems.PURE_BLOOD_1.get());
            add(ModItems.PURE_BLOOD_2.get());
            add(ModItems.PURE_BLOOD_3.get());
            add(ModItems.PURE_BLOOD_4.get());
            add(ModItems.VAMPIRE_MINION_BINDING.get());
            add(ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get());
            add(ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get());
            add(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get());
            add(ModItems.HUNTER_MINION_EQUIPMENT.get());
            add(ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get());
            add(ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get());
            add(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get());
            add(ModItems.OBLIVION_POTION.get());
            add(ModItems.CURE_APPLE.get());
            add(ModItems.VAMPIRE_CLOTHING_HAT.get());
            add(ModItems.VAMPIRE_CLOTHING_BOOTS.get());
            add(ModItems.VAMPIRE_CLOTHING_LEGS.get());
            add(ModItems.VAMPIRE_CLOTHING_CROWN.get());
            add(ModItems.GARLIC_FINDER.get());
        }};
        Map<Item, ResourceLocation> itemsWithTexture = new HashMap<>() {{
            put(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), modLoc("item/holy_water_normal"));
            put(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), modLoc("item/holy_water_enhanced"));
            put(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), modLoc("item/holy_water_ultimate"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), modLoc("item/holy_water_splash_normal"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), modLoc("item/holy_water_splash_enhanced"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get(), modLoc("item/holy_water_splash_ultimate"));
            put(ModItems.GARLIC_BREAD.get(), modLoc("item/garlic_bread"));
            put(ModItems.HUNTER_HAT_HEAD_0.get(), modLoc("item/hunter_hat_0"));
            put(ModItems.HUNTER_HAT_HEAD_1.get(), modLoc("item/hunter_hat_1"));
            put(ModItems.ITEM_ALCHEMICAL_FIRE.get(), modLoc("item/alchemical_fire"));
            put(ModItems.ITEM_GARLIC.get(), modLoc("item/garlic"));
            put(ModItems.ITEM_MED_CHAIR.get(), modLoc("item/med_chair"));
            put(ModItems.ITEM_TENT_SPAWNER.get(), modLoc("item/item_tent"));
            put(ModItems.PURE_SALT.get(), modLoc("item/holy_salt"));
            put(ModItems.VAMPIRE_BOOK.get(), modLoc("item/vampire_book"));
        }};

        blocks.forEach(this::block);
        items.forEach(this::item);
        itemsWithTexture.forEach(this::item);

        block(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), "garlic_diffuser_weak");
        block(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), "garlic_diffuser_normal");
        block(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), "garlic_diffuser_improved");

        block(ModBlocks.HUNTER_TABLE.get(), "hunter_table/hunter_table");

        withExistingParent(ModBlocks.BLOODY_SPRUCE_LEAVES.get(), mcLoc("block/oak_leaves"));
        withExistingParent(ModBlocks.VAMPIRE_SPRUCE_LEAVES.get(), mcLoc("block/oak_leaves"));

        withExistingParent(ModBlocks.BLOODY_SPRUCE_SAPLING.get(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/" + ModBlocks.BLOODY_SPRUCE_SAPLING.getId().getPath());
        withExistingParent(ModBlocks.VAMPIRE_SPRUCE_SAPLING.get(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/" + ModBlocks.VAMPIRE_SPRUCE_SAPLING.getId().getPath());

        withExistingParent(ModBlocks.ALCHEMICAL_FIRE.get(), modLoc("block/fire_side"));
        withExistingParent(ModBlocks.ALTAR_INSPIRATION.get(), modLoc("block/altar_inspiration/altar_inspiration"));
        item("crossbow_arrow", modLoc("item/crossbow_arrow"), modLoc("item/crossbow_arrow_tip"));

        withExistingParent(ModItems.CROSSBOW_ARROW_NORMAL.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.CROSSBOW_ARROW_SPITFIRE.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), modLoc("item/crossbow_arrow"));

        item("armor_of_swiftness_chest", mcLoc("item/leather_chestplate"), modLoc("item/swiftness_chest_overlay"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), modLoc("item/armor_of_swiftness_chest"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), modLoc("item/armor_of_swiftness_chest"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), modLoc("item/armor_of_swiftness_chest"));

        item("armor_of_swiftness_feet", mcLoc("item/leather_boots"), modLoc("item/swiftness_feet_overlay"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), modLoc("item/armor_of_swiftness_feet"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), modLoc("item/armor_of_swiftness_feet"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), modLoc("item/armor_of_swiftness_feet"));

        item("armor_of_swiftness_head", mcLoc("item/leather_helmet"), modLoc("item/swiftness_head_overlay"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), modLoc("item/armor_of_swiftness_head"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), modLoc("item/armor_of_swiftness_head"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), modLoc("item/armor_of_swiftness_head"));

        item("armor_of_swiftness_legs", mcLoc("item/leather_leggings"), modLoc("item/swiftness_legs_overlay"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), modLoc("item/armor_of_swiftness_legs"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), modLoc("item/armor_of_swiftness_legs"));
        withExistingParent(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), modLoc("item/armor_of_swiftness_legs"));

        withExistingParent(ModItems.ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ADVANCED_VAMPIRE_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HUNTER_TRAINER_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VAMPIRE_BARON_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VAMPIRE_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VAMPIRE_HUNTER_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));

        withExistingParent(ModItems.BASIC_CROSSBOW.get(), modLoc("item/crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.BASIC_DOUBLE_CROSSBOW.get(), modLoc("item/double_crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.BASIC_TECH_CROSSBOW.get(), modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra");

        withExistingParent(ModItems.BASIC_CROSSBOW.get(), modLoc("item/crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.BASIC_DOUBLE_CROSSBOW.get(), modLoc("item/double_crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.BASIC_TECH_CROSSBOW.get(), modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra");

        withExistingParent(ModItems.ENHANCED_CROSSBOW.get(), modLoc("item/crossbow")).texture("texture", "item/enhanced_crossbow");
        withExistingParent(ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), modLoc("item/double_crossbow")).texture("texture", "item/enhanced_crossbow");
        withExistingParent(ModItems.ENHANCED_TECH_CROSSBOW.get(), modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra_enhanced");

        withExistingParent(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.get(), ModItems.GARLIC_DIFFUSER_CORE.get()).texture("texture", "block/garlic_diffuser_inside_improved");

        withExistingParent(ModItems.HEART_SEEKER_NORMAL.get(), modLoc("item/heart_seeker_model"));
        withExistingParent(ModItems.HEART_SEEKER_ENHANCED.get(), modLoc("item/heart_seeker_model")).texture("3", "item/heart_seeker_enhanced");
        withExistingParent(ModItems.HEART_SEEKER_ULTIMATE.get(), modLoc("item/heart_seeker_model")).texture("3", "item/heart_seeker_ultimate");

        withExistingParent(ModItems.HEART_STRIKER_NORMAL.get(), modLoc("item/heart_striker_model"));
        withExistingParent(ModItems.HEART_STRIKER_ENHANCED.get(), modLoc("item/heart_striker_model")).texture("2", "item/heart_striker_enhanced");
        withExistingParent(ModItems.HEART_STRIKER_ULTIMATE.get(), modLoc("item/heart_striker_model")).texture("2", "item/heart_striker_ultimate");

        withExistingParent(ModItems.HUNTER_AXE_NORMAL.get(), modLoc("item/hunter_axe"));
        withExistingParent(ModItems.HUNTER_AXE_ENHANCED.get(), modLoc("item/hunter_axe")).texture("texture", "item/hunter_axe_enhanced");
        withExistingParent(ModItems.HUNTER_AXE_ULTIMATE.get(), modLoc("item/hunter_axe")).texture("texture", "item/hunter_axe_ultimate");

        withExistingParent(ModItems.HUNTER_INTEL_0.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_1.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_2.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_3.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_4.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_5.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_6.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_7.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_8.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.HUNTER_INTEL_9.get(), modLoc("item/hunter_intel"));

        item(ModItems.RING.get(), modLoc("item/vampire_ring_layer0"), modLoc("item/vampire_ring_layer1"));
        item(ModItems.AMULET.get(), modLoc("item/vampire_amulet_layer0"), modLoc("item/vampire_amulet_layer1"));
        item(ModItems.OBI_BELT.get(), modLoc("item/vampire_obi_belt_layer0"), modLoc("item/vampire_obi_belt_layer1"));

        withExistingParent(ModItems.ITEM_CANDELABRA.get(), modLoc("block/candelabra"));


        singleTexture("blood_bottle", mcLoc("item/generated"), "layer0", modLoc("item/blood_bottle_0"))
                .override().predicate(mcLoc("damage"), 0f).model(withExistingParent("blood_bottle_0", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_0"))).end()
                .override().predicate(mcLoc("damage"), 0.11f).model(withExistingParent("blood_bottle_1", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_1"))).end()
                .override().predicate(mcLoc("damage"), 0.22f).model(withExistingParent("blood_bottle_2", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_2"))).end()
                .override().predicate(mcLoc("damage"), 0.33f).model(withExistingParent("blood_bottle_3", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_3"))).end()
                .override().predicate(mcLoc("damage"), 0.44f).model(withExistingParent("blood_bottle_4", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_4"))).end()
                .override().predicate(mcLoc("damage"), 0.55f).model(withExistingParent("blood_bottle_5", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_5"))).end()
                .override().predicate(mcLoc("damage"), 0.66f).model(withExistingParent("blood_bottle_6", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_6"))).end()
                .override().predicate(mcLoc("damage"), 0.77f).model(withExistingParent("blood_bottle_7", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_7"))).end()
                .override().predicate(mcLoc("damage"), 0.88f).model(withExistingParent("blood_bottle_8", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_8"))).end()
                .override().predicate(mcLoc("damage"), 0.99f).model(withExistingParent("blood_bottle_9", mcLoc("item/generated")).texture("layer0", modLoc("item/blood_bottle_9"))).end();
    }

}
