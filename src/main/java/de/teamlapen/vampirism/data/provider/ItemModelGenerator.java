package de.teamlapen.vampirism.data.provider;

import de.teamlapen.lib.lib.data.BaseItemModelGenerator;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemModelGenerator extends BaseItemModelGenerator {

    public ItemModelGenerator(@NotNull PackOutput packOutput, @NotNull ExistingFileHelper existingFileHelper) {
        super(packOutput, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Set<Block> blocks = new HashSet<>() {{
            add(ModBlocks.ALTAR_TIP.get());
            add(ModBlocks.BLOODY_DARK_STONE_BRICKS.get());
            add(ModBlocks.BLOOD_GRINDER.get());
            add(ModBlocks.BLOOD_PEDESTAL.get());
            add(ModBlocks.POTION_TABLE.get());
            add(ModBlocks.BLOOD_SIEVE.get());
            add(ModBlocks.ALTAR_CLEANSING.get());
            add(ModBlocks.CURSED_EARTH.get());
            add(ModBlocks.SUNSCREEN_BEACON.get());
            add(ModBlocks.TOTEM_TOP.get());
            add(ModBlocks.CHANDELIER.get());
            add(ModBlocks.CROSS.get());
            add(ModBlocks.TOMBSTONE1.get());
            add(ModBlocks.TOMBSTONE2.get());
            add(ModBlocks.TOMBSTONE3.get());
            add(ModBlocks.GRAVE_CAGE.get());
            add(ModBlocks.CURSED_GRASS.get());
            add(ModBlocks.DARK_SPRUCE_LOG.get());
            add(ModBlocks.CURSED_SPRUCE_LOG.get());
            add(ModBlocks.CURSED_SPRUCE_LOG_CURED.get());
            add(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
            add(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());
            add(ModBlocks.DARK_SPRUCE_PLANKS.get());
            add(ModBlocks.CURSED_SPRUCE_PLANKS.get());
            add(ModBlocks.DARK_SPRUCE_STAIRS.get());
            add(ModBlocks.CURSED_SPRUCE_STAIRS.get());
            add(ModBlocks.DARK_SPRUCE_WOOD.get());
            add(ModBlocks.CURSED_SPRUCE_WOOD.get());
            add(ModBlocks.CURSED_SPRUCE_WOOD_CURED.get());
            add(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get());
            add(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get());
            add(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE.get());
            add(ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE.get());
            add(ModBlocks.DARK_SPRUCE_BUTTON.get());
            add(ModBlocks.CURSED_SPRUCE_BUTTON.get());
            add(ModBlocks.DARK_SPRUCE_SLAB.get());
            add(ModBlocks.CURSED_SPRUCE_SLAB.get());
            add(ModBlocks.DARK_SPRUCE_FENCE_GATE.get());
            add(ModBlocks.CURSED_SPRUCE_FENCE_GATE.get());
            add(ModBlocks.VAMPIRE_RACK.get());
            add(ModBlocks.THRONE.get());
            add(ModBlocks.CURSED_EARTH_PATH.get());
            add(ModBlocks.CRACKED_DARK_STONE_BRICKS.get());
            add(ModBlocks.DARK_STONE_BRICKS.get());
            add(ModBlocks.DARK_STONE_BRICK_WALL.get());
            add(ModBlocks.DARK_STONE_BRICK_SLAB.get());
            add(ModBlocks.DARK_STONE_BRICK_STAIRS.get());
            add(ModBlocks.CHISELED_DARK_STONE_BRICKS.get());
            add(ModBlocks.DARK_STONE.get());
            add(ModBlocks.DARK_STONE_STAIRS.get());
            add(ModBlocks.DARK_STONE_SLAB.get());
            add(ModBlocks.DARK_STONE_WALL.get());
            add(ModBlocks.DARK_STONE_TILES.get());
            add(ModBlocks.DARK_STONE_TILES_STAIRS.get());
            add(ModBlocks.DARK_STONE_TILES_SLAB.get());
            add(ModBlocks.DARK_STONE_TILES_WALL.get());
            add(ModBlocks.POLISHED_DARK_STONE.get());
            add(ModBlocks.POLISHED_DARK_STONE_STAIRS.get());
            add(ModBlocks.POLISHED_DARK_STONE_SLAB.get());
            add(ModBlocks.POLISHED_DARK_STONE_WALL.get());
            add(ModBlocks.COBBLED_DARK_STONE.get());
            add(ModBlocks.COBBLED_DARK_STONE_SLAB.get());
            add(ModBlocks.COBBLED_DARK_STONE_STAIRS.get());
            add(ModBlocks.COBBLED_DARK_STONE_WALL.get());
            add(ModBlocks.CRACKED_DARK_STONE_TILES.get());
            add(ModBlocks.REMAINS.get());
            add(ModBlocks.VULNERABLE_REMAINS.get());
            add(ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get());
            add(ModBlocks.CURSED_HANGING_ROOTS.get());
            add(ModBlocks.MOTHER.get());
            add(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
            add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get());
            add(ModBlocks.VAMPIRE_BEACON.get());
            add(ModBlocks.PURPLE_STONE_BRICKS.get());
            add(ModBlocks.PURPLE_STONE_BRICK_WALL.get());
            add(ModBlocks.PURPLE_STONE_BRICK_SLAB.get());
            add(ModBlocks.PURPLE_STONE_BRICK_STAIRS.get());
            add(ModBlocks.PURPLE_STONE_TILES.get());
            add(ModBlocks.PURPLE_STONE_TILES_WALL.get());
            add(ModBlocks.PURPLE_STONE_TILES_SLAB.get());
            add(ModBlocks.PURPLE_STONE_TILES_STAIRS.get());
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
            add(ModItems.PURE_SALT.get());
            add(ModItems.PURE_SALT_WATER.get());
            add(ModItems.HUMAN_HEART.get());
            add(ModItems.INJECTION_EMPTY.get());
            add(ModItems.INJECTION_GARLIC.get());
            add(ModItems.INJECTION_SANGUINARE.get());
            add(ModItems.PURIFIED_GARLIC.get());
            add(ModItems.SOUL_ORB_VAMPIRE.get());
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
            add(ModItems.VAMPIRE_CLOTHING_HAT.get());
            add(ModItems.VAMPIRE_CLOTHING_BOOTS.get());
            add(ModItems.VAMPIRE_CLOTHING_LEGS.get());
            add(ModItems.VAMPIRE_CLOTHING_CROWN.get());
            add(ModItems.GARLIC_FINDER.get());
            add(ModItems.DARK_SPRUCE_BOAT.get());
            add(ModItems.CURSED_SPRUCE_BOAT.get());
            add(ModItems.DARK_SPRUCE_CHEST_BOAT.get());
            add(ModItems.CURSED_SPRUCE_CHEST_BOAT.get());
        }};
        Map<Item, ResourceLocation> itemsWithTexture = new HashMap<>() {{
            put(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), modLoc("item/holy_water_normal"));
            put(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), modLoc("item/holy_water_enhanced"));
            put(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), modLoc("item/holy_water_ultimate"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), modLoc("item/holy_water_splash_normal"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), modLoc("item/holy_water_splash_enhanced"));
            put(ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get(), modLoc("item/holy_water_splash_ultimate"));
            put(ModItems.GARLIC_BREAD.get(), modLoc("item/garlic_bread"));
            put(ModItems.ITEM_ALCHEMICAL_FIRE.get(), modLoc("item/alchemical_fire"));
            put(ModItems.ITEM_GARLIC.get(), modLoc("item/garlic"));
            put(ModBlocks.MED_CHAIR.get().asItem(), modLoc("item/med_chair"));
            put(ModItems.ITEM_TENT_SPAWNER.get(), modLoc("item/item_tent"));
            put(ModItems.VAMPIRE_BOOK.get(), modLoc("item/vampire_book"));
            put(ModBlocks.DIRECT_CURSED_BARK.get().asItem(), modLoc("block/cursed_bark"));
            put(ModItems.DARK_SPRUCE_SIGN.get(), modLoc("item/dark_spruce_sign"));
            put(ModItems.CURSED_SPRUCE_SIGN.get(), modLoc("item/cursed_spruce_sign"));
            put(ModItems.DARK_SPRUCE_SIGN.get(), modLoc("item/dark_spruce_sign"));
            put(ModItems.CURSED_SPRUCE_SIGN.get(), modLoc("item/cursed_spruce_sign"));
            put(ModItems.DARK_SPRUCE_HANGING_SIGN.get(), modLoc("item/dark_spruce_hanging_sign"));
            put(ModItems.CURSED_SPRUCE_HANGING_SIGN.get(), modLoc("item/cursed_spruce_hanging_sign"));
            put(ModItems.MOTHER_CORE.get(), modLoc("item/mother_core"));
        }};

        blocks.forEach(this::block);
        items.forEach(this::item);
        itemsWithTexture.forEach(this::item);

        block(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), "garlic_diffuser_weak");
        block(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), "garlic_diffuser_normal");
        block(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), "garlic_diffuser_improved");

        withExistingParent(ModBlocks.DARK_SPRUCE_LEAVES.get(), mcLoc("block/oak_leaves"));

        withExistingParent(ModBlocks.DARK_SPRUCE_SAPLING.get(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/dark_spruce_sapling");
        withExistingParent(ModBlocks.CURSED_SPRUCE_SAPLING.get(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/cursed_spruce_sapling");

        withExistingParent(ModBlocks.CURSED_ROOTS.get().asItem(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/cursed_roots");
        withExistingParent(ModBlocks.VAMPIRE_ORCHID.get().asItem(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/vampire_orchid");

        withExistingParent(ModBlocks.ALCHEMICAL_FIRE.get(), modLoc("block/fire_side"));
        withExistingParent(ModBlocks.ALTAR_INSPIRATION.get(), modLoc("block/altar_inspiration/altar_inspiration"));
        item("crossbow_arrow", modLoc("item/crossbow_arrow"), modLoc("item/crossbow_arrow_tip"));

        withExistingParent(ModItems.CROSSBOW_ARROW_NORMAL.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.CROSSBOW_ARROW_SPITFIRE.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.CROSSBOW_ARROW_TELEPORT.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.CROSSBOW_ARROW_BLEEDING.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.CROSSBOW_ARROW_GARLIC.get(), modLoc("item/crossbow_arrow"));

        item(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), modLoc("item/armor_of_swiftness_chest_normal"), modLoc("item/armor_of_swiftness_chest_normal_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), modLoc("item/armor_of_swiftness_chest_enhanced"), modLoc("item/armor_of_swiftness_chest_enhanced_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), modLoc("item/armor_of_swiftness_chest_ultimate"), modLoc("item/armor_of_swiftness_chest_ultimate_overlay"));

        item(ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), modLoc("item/armor_of_swiftness_feet_normal"), modLoc("item/armor_of_swiftness_feet_normal_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), modLoc("item/armor_of_swiftness_feet_enhanced"), modLoc("item/armor_of_swiftness_feet_enhanced_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), modLoc("item/armor_of_swiftness_feet_ultimate"), modLoc("item/armor_of_swiftness_feet_ultimate_overlay"));

        item(ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), modLoc("item/armor_of_swiftness_head_normal"), modLoc("item/armor_of_swiftness_head_normal_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), modLoc("item/armor_of_swiftness_head_enhanced"), modLoc("item/armor_of_swiftness_head_enhanced_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), modLoc("item/armor_of_swiftness_head_ultimate"), modLoc("item/armor_of_swiftness_head_ultimate_overlay"));

        item(ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), modLoc("item/armor_of_swiftness_legs_normal"), modLoc("item/armor_of_swiftness_legs_normal_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), modLoc("item/armor_of_swiftness_legs_enhanced"), modLoc("item/armor_of_swiftness_legs_enhanced_overlay"));
        item(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), modLoc("item/armor_of_swiftness_legs_ultimate"), modLoc("item/armor_of_swiftness_legs_ultimate_overlay"));

        withExistingParent(ModItems.ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ADVANCED_VAMPIRE_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HUNTER_TRAINER_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VAMPIRE_BARON_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VAMPIRE_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VAMPIRE_HUNTER_SPAWN_EGG.get(), mcLoc("item/template_spawn_egg"));

        withExistingParent(ModItems.BASIC_CROSSBOW.get(), modLoc("item/crossbow")).texture("texture", "item/crossbow").texture("string", "item/crossbow_part_string").texture("arrow", "item/crossbow_part_arrow").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("basic_crossbow_unloaded", modLoc("item/crossbow_unloaded")).texture("texture", "item/crossbow").texture("string", "item/crossbow_part_string_unloaded"));
        withExistingParent(ModItems.BASIC_DOUBLE_CROSSBOW.get(), modLoc("item/double_crossbow")).texture("texture", "item/crossbow_double").texture("string", "item/crossbow_part_double_string").texture("arrows", "item/crossbow_part_arrows").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("basic_double_crossbow_unloaded", modLoc("item/double_crossbow_unloaded")).texture("texture", "item/crossbow_double").texture("string", "item/crossbow_part_double_string_unloaded"));
        withExistingParent(ModItems.BASIC_TECH_CROSSBOW.get(), modLoc("item/tech_crossbow")).texture("texture", "item/tech_crossbow").texture("string", "item/crossbow_part_tech_string").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("basic_tech_crossbow_unloaded", modLoc("item/tech_crossbow_unloaded")).texture("texture", "item/tech_crossbow").texture("string", "item/crossbow_part_tech_string_unloaded"));

        withExistingParent(ModItems.ENHANCED_CROSSBOW.get(), modLoc("item/crossbow")).texture("texture", "item/crossbow_enhanced").texture("string", "item/crossbow_part_string").texture("arrow", "item/crossbow_part_arrow").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("enhanced_crossbow_unloaded", modLoc("item/crossbow_unloaded")).texture("texture", "item/crossbow_enhanced").texture("string", "item/crossbow_part_string_unloaded"));
        withExistingParent(ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), modLoc("item/double_crossbow")).texture("texture", "item/crossbow_double_enhanced").texture("string", "item/crossbow_part_double_string").texture("arrows", "item/crossbow_part_arrows").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("enhanced_double_crossbow_unloaded", modLoc("item/double_crossbow_unloaded")).texture("texture", "item/crossbow_double_enhanced").texture("string", "item/crossbow_part_double_string_unloaded"));
        withExistingParent(ModItems.ENHANCED_TECH_CROSSBOW.get(), modLoc("item/tech_crossbow")).texture("texture", "item/tech_crossbow_enhanced").texture("string", "item/crossbow_part_tech_string").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("enhanced_tech_crossbow_unloaded", modLoc("item/tech_crossbow_unloaded")).texture("texture", "item/tech_crossbow_enhanced").texture("string", "item/crossbow_part_tech_string_unloaded"));

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

        withExistingParent(ModItems.CRUCIFIX_NORMAL.get(), modLoc("item/crucifix")).texture("texture", "item/crucifix_wooden");
        withExistingParent(ModItems.CRUCIFIX_ENHANCED.get(), modLoc("item/crucifix")).texture("texture", "item/crucifix_iron");
        withExistingParent(ModItems.CRUCIFIX_ULTIMATE.get(), modLoc("item/crucifix")).texture("texture", "item/crucifix_gold");


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

        singleTexture("tech_crossbow_ammo_package", mcLoc("item/generated"), "layer0", modLoc("item/arrow_clip0"))
                .override().predicate(new ResourceLocation(REFERENCE.MODID, "filled"), 0.0f).model(withExistingParent("arrow_clip/arrow_clip0",  mcLoc("item/generated")).texture("layer0", modLoc("item/arrow_clip0"))).end()
                .override().predicate(new ResourceLocation(REFERENCE.MODID, "filled"), 0.01f).model(withExistingParent("arrow_clip/arrow_clip1",  mcLoc("item/generated")).texture("layer0", modLoc("item/arrow_clip1"))).end()
                .override().predicate(new ResourceLocation(REFERENCE.MODID, "filled"), 0.55f).model(withExistingParent("arrow_clip/arrow_clip2",  mcLoc("item/generated")).texture("layer0", modLoc("item/arrow_clip2"))).end()
                .override().predicate(new ResourceLocation(REFERENCE.MODID, "filled"), 0.99f).model(withExistingParent("arrow_clip/arrow_clip3",  mcLoc("item/generated")).texture("layer0", modLoc("item/arrow_clip3"))).end();

        withExistingParent(ModBlocks.DARK_SPRUCE_TRAPDOOR.get(), modLoc("block/dark_spruce_trapdoor_bottom"));
        withExistingParent(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get(), modLoc("block/cursed_spruce_trapdoor_bottom"));

        item(ModBlocks.DARK_SPRUCE_DOOR.get().asItem(), modLoc("item/dark_spruce_door"));
        item(ModBlocks.CURSED_SPRUCE_DOOR.get().asItem(), modLoc("item/cursed_spruce_door"));

        withExistingParent(ModBlocks.DARK_SPRUCE_BUTTON.get().asItem(), modLoc("block/dark_spruce_button_inventory"));
        withExistingParent(ModBlocks.CURSED_SPRUCE_BUTTON.get().asItem(), modLoc("block/cursed_spruce_button_inventory"));
        withExistingParent(ModBlocks.DARK_SPRUCE_FENCE.get().asItem(), modLoc("block/dark_spruce_fence_inventory"));
        withExistingParent(ModBlocks.CURSED_SPRUCE_FENCE.get().asItem(), modLoc("block/cursed_spruce_fence_inventory"));

        for (DyeColor dye : DyeColor.values()) {
            getBuilder("coffin_" + dye.getName()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/coffin/coffin_bottom_" + dye.getName()))
                    .transforms()
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(30, 160, 0).translation(-1, 0, 1).scale(0.23f, 0.23f, 0.23f).end()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(30, 160, 0).translation(0, 0, 3).scale(0.375f, 0.375f, 0.375f).end()
                    .transform(ItemDisplayContext.GUI).rotation(30, 160, 0).translation(-1, -1, -4).scale(0.5325f, 0.5325f, 0.5325f).end()
                    .transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 2, -2).scale(0.25f, 0.25f, 0.25f).end()
                    .transform(ItemDisplayContext.HEAD).rotation(0, 180, 0).translation(0, 2, -8).scale(1, 1, 1).end()
                    .transform(ItemDisplayContext.FIXED).rotation(270, 0, 0).translation(0, -4, -4).scale(0.5f, 0.5f, 0.5f).end()
                    .end();
        }
        this.item(ModItems.OIL_BOTTLE.get(), modLoc("item/oil_bottle"), modLoc("item/oil_bottle_overlay"));
        withExistingParent(ModBlocks.ALCHEMY_TABLE.get(), modLoc("block/alchemy_table/alchemy_table"));
        withExistingParent(ModBlocks.DARK_STONE_BRICK_WALL.get(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/dark_stone_bricks"));
        withExistingParent(ModBlocks.PURPLE_STONE_BRICK_WALL.get(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/purple_stone_bricks"));
        withExistingParent(ModBlocks.DARK_STONE_WALL.get(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/dark_stone"));
        withExistingParent(ModBlocks.DARK_STONE_TILES_WALL.get(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/dark_stone_tiles"));
        withExistingParent(ModBlocks.PURPLE_STONE_TILES_WALL.get(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/purple_stone_tiles"));
        withExistingParent(ModBlocks.COBBLED_DARK_STONE_WALL.get(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/cobbled_dark_stone"));
        withExistingParent(ModBlocks.POLISHED_DARK_STONE_WALL.get(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/polished_dark_stone"));
        withExistingParent(ModBlocks.INFESTED_DARK_STONE.get(), modLoc("block/dark_stone"));
        block(ModBlocks.FOG_DIFFUSER.get(), "fog_diffuser");
        withExistingParent(ModBlocks.CURSED_HANGING_ROOTS.get().asItem(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/cursed_hanging_roots");
        withExistingParent(ModItems.CANDLE_STICK.get(), modLoc("block/candle_stick"));
    }

}
