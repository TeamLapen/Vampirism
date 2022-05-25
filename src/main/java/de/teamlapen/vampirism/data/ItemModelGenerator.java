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

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void registerModels() {
        Set<Block> blocks = new HashSet<>() {{
            add(ModBlocks.altar_tip.get());
            add(ModBlocks.castle_block_dark_brick.get());
            add(ModBlocks.castle_block_dark_brick_bloody.get());
            add(ModBlocks.castle_block_dark_stone.get());
            add(ModBlocks.castle_block_normal_brick.get());
            add(ModBlocks.castle_block_purple_brick.get());
            add(ModBlocks.castle_slab_dark_brick.get());
            add(ModBlocks.castle_slab_dark_stone.get());
            add(ModBlocks.castle_slab_purple_brick.get());
            add(ModBlocks.castle_stairs_dark_brick.get());
            add(ModBlocks.castle_stairs_dark_stone.get());
            add(ModBlocks.castle_stairs_purple_brick.get());
            add(ModBlocks.blood_grinder.get());
            add(ModBlocks.blood_pedestal.get());
            add(ModBlocks.potion_table.get());
            add(ModBlocks.blood_sieve.get());
            add(ModBlocks.altar_cleansing.get());
            add(ModBlocks.cursed_earth.get());
            add(ModBlocks.hunter_table.get());
            add(ModBlocks.sunscreen_beacon.get());
            add(ModBlocks.totem_top.get());
            add(ModBlocks.totem_top_vampirism_vampire.get());
            add(ModBlocks.totem_top_vampirism_hunter.get());
            add(ModBlocks.vampire_orchid.get());
            add(ModBlocks.bloody_spruce_log.get());
            add(ModBlocks.chandelier.get());
            add(ModBlocks.cross.get());
            add(ModBlocks.tombstone1.get());
            add(ModBlocks.tombstone2.get());
            add(ModBlocks.tombstone3.get());
            add(ModBlocks.grave_cage.get());
            add(ModBlocks.cursed_grass_block.get());
        }};
        Set<Item> items = new HashSet<>() {{
            add(ModItems.hunter_coat_chest_normal.get());
            add(ModItems.hunter_coat_chest_enhanced.get());
            add(ModItems.hunter_coat_chest_ultimate.get());
            add(ModItems.hunter_coat_feet_normal.get());
            add(ModItems.hunter_coat_feet_enhanced.get());
            add(ModItems.hunter_coat_feet_ultimate.get());
            add(ModItems.hunter_coat_head_normal.get());
            add(ModItems.hunter_coat_head_enhanced.get());
            add(ModItems.hunter_coat_head_ultimate.get());
            add(ModItems.hunter_coat_legs_normal.get());
            add(ModItems.hunter_coat_legs_enhanced.get());
            add(ModItems.hunter_coat_legs_ultimate.get());
            add(ModItems.blood_bucket.get());
            add(ModItems.impure_blood_bucket.get());
            add(ModItems.blood_infused_enhanced_iron_ingot.get());
            add(ModItems.blood_infused_iron_ingot.get());
            add(ModItems.holy_salt.get());
            add(ModItems.holy_salt_water.get());
            add(ModItems.human_heart.get());
            add(ModItems.injection_empty.get());
            add(ModItems.injection_garlic.get());
            add(ModItems.injection_sanguinare.get());
            add(ModItems.injection_zombie_blood.get());
            add(ModItems.obsidian_armor_chest_normal.get());
            add(ModItems.obsidian_armor_chest_enhanced.get());
            add(ModItems.obsidian_armor_chest_ultimate.get());
            add(ModItems.obsidian_armor_feet_normal.get());
            add(ModItems.obsidian_armor_feet_enhanced.get());
            add(ModItems.obsidian_armor_feet_ultimate.get());
            add(ModItems.obsidian_armor_head_normal.get());
            add(ModItems.obsidian_armor_head_enhanced.get());
            add(ModItems.obsidian_armor_head_ultimate.get());
            add(ModItems.obsidian_armor_legs_normal.get());
            add(ModItems.obsidian_armor_legs_enhanced.get());
            add(ModItems.obsidian_armor_legs_ultimate.get());
            add(ModItems.purified_garlic.get());
            add(ModItems.soul_orb_vampire.get());
            add(ModItems.tech_crossbow_ammo_package.get());
            add(ModItems.vampire_blood_bottle.get());
            add(ModItems.vampire_cloak_black_blue.get());
            add(ModItems.vampire_cloak_black_red.get());
            add(ModItems.vampire_cloak_black_white.get());
            add(ModItems.vampire_cloak_white_black.get());
            add(ModItems.vampire_cloak_red_black.get());
            add(ModItems.vampire_fang.get());
            add(ModItems.weak_human_heart.get());
            add(ModItems.item_tent.get());
            add(ModItems.pure_blood_0.get());
            add(ModItems.pure_blood_1.get());
            add(ModItems.pure_blood_2.get());
            add(ModItems.pure_blood_3.get());
            add(ModItems.pure_blood_4.get());
            add(ModItems.vampire_minion_binding.get());
            add(ModItems.vampire_minion_upgrade_simple.get());
            add(ModItems.vampire_minion_upgrade_enhanced.get());
            add(ModItems.vampire_minion_upgrade_special.get());
            add(ModItems.hunter_minion_equipment.get());
            add(ModItems.hunter_minion_upgrade_simple.get());
            add(ModItems.hunter_minion_upgrade_enhanced.get());
            add(ModItems.hunter_minion_upgrade_special.get());
            add(ModItems.oblivion_potion.get());
            add(ModItems.cure_apple.get());
            add(ModItems.vampire_clothing_hat.get());
            add(ModItems.vampire_clothing_boots.get());
            add(ModItems.vampire_clothing_legs.get());
            add(ModItems.vampire_clothing_crown.get());
            add(ModItems.garlic_finder.get());
        }};
        Map<Item, ResourceLocation> itemsWithTexture = new HashMap<>() {{
            put(ModItems.holy_water_bottle_normal.get(), modLoc("item/holy_water_normal"));
            put(ModItems.holy_water_bottle_enhanced.get(), modLoc("item/holy_water_enhanced"));
            put(ModItems.holy_water_bottle_ultimate.get(), modLoc("item/holy_water_ultimate"));
            put(ModItems.holy_water_splash_bottle_normal.get(), modLoc("item/holy_water_splash_normal"));
            put(ModItems.holy_water_splash_bottle_enhanced.get(), modLoc("item/holy_water_splash_enhanced"));
            put(ModItems.holy_water_splash_bottle_ultimate.get(), modLoc("item/holy_water_splash_ultimate"));
            put(ModItems.garlic_bread.get(), modLoc("item/garlic_bread"));
            put(ModItems.hunter_hat_head_0.get(), modLoc("item/hunter_hat_0"));
            put(ModItems.hunter_hat_head_1.get(), modLoc("item/hunter_hat_1"));
            put(ModItems.item_alchemical_fire.get(), modLoc("item/alchemical_fire"));
            put(ModItems.item_garlic.get(), modLoc("item/garlic"));
            put(ModItems.item_med_chair.get(), modLoc("item/med_chair"));
            put(ModItems.item_tent_spawner.get(), modLoc("item/item_tent"));
            put(ModItems.pure_salt.get(), modLoc("item/holy_salt"));
            put(ModItems.vampire_book.get(), modLoc("item/vampire_book"));
        }};

        blocks.forEach(this::block);
        items.forEach(this::item);
        itemsWithTexture.forEach(this::item);

        block(ModBlocks.garlic_diffuser_weak.get(), "garlic_diffuser_weak");
        block(ModBlocks.garlic_diffuser_normal.get(), "garlic_diffuser_normal");
        block(ModBlocks.garlic_diffuser_improved.get(), "garlic_diffuser_improved");

        withExistingParent(ModBlocks.bloody_spruce_leaves.get(), mcLoc("block/oak_leaves"));
        withExistingParent(ModBlocks.vampire_spruce_leaves.get(), mcLoc("block/oak_leaves"));

        withExistingParent(ModBlocks.bloody_spruce_sapling.get(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/" + ModBlocks.bloody_spruce_sapling.get().getRegistryName().getPath());
        withExistingParent(ModBlocks.vampire_spruce_sapling.get(), mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/" + ModBlocks.vampire_spruce_sapling.get().getRegistryName().getPath());

        withExistingParent(ModBlocks.alchemical_fire.get(), modLoc("block/fire_side"));
        withExistingParent(ModBlocks.altar_inspiration.get(), modLoc("block/altar_inspiration/altar_inspiration"));
        item("crossbow_arrow", modLoc("item/crossbow_arrow"), modLoc("item/crossbow_arrow_tip"));

        withExistingParent(ModItems.crossbow_arrow_normal.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.crossbow_arrow_spitfire.get(), modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.crossbow_arrow_vampire_killer.get(), modLoc("item/crossbow_arrow"));

        item("armor_of_swiftness_chest", mcLoc("item/leather_chestplate"), modLoc("item/swiftness_chest_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_chest_normal.get(), modLoc("item/armor_of_swiftness_chest"));
        withExistingParent(ModItems.armor_of_swiftness_chest_enhanced.get(), modLoc("item/armor_of_swiftness_chest"));
        withExistingParent(ModItems.armor_of_swiftness_chest_ultimate.get(), modLoc("item/armor_of_swiftness_chest"));

        item("armor_of_swiftness_feet", mcLoc("item/leather_boots"), modLoc("item/swiftness_feet_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_feet_normal.get(), modLoc("item/armor_of_swiftness_feet"));
        withExistingParent(ModItems.armor_of_swiftness_feet_enhanced.get(), modLoc("item/armor_of_swiftness_feet"));
        withExistingParent(ModItems.armor_of_swiftness_feet_ultimate.get(), modLoc("item/armor_of_swiftness_feet"));

        item("armor_of_swiftness_head", mcLoc("item/leather_helmet"), modLoc("item/swiftness_head_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_head_normal.get(), modLoc("item/armor_of_swiftness_head"));
        withExistingParent(ModItems.armor_of_swiftness_head_enhanced.get(), modLoc("item/armor_of_swiftness_head"));
        withExistingParent(ModItems.armor_of_swiftness_head_ultimate.get(), modLoc("item/armor_of_swiftness_head"));

        item("armor_of_swiftness_legs", mcLoc("item/leather_leggings"), modLoc("item/swiftness_legs_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_legs_normal.get(), modLoc("item/armor_of_swiftness_legs"));
        withExistingParent(ModItems.armor_of_swiftness_legs_enhanced.get(), modLoc("item/armor_of_swiftness_legs"));
        withExistingParent(ModItems.armor_of_swiftness_legs_ultimate.get(), modLoc("item/armor_of_swiftness_legs"));

        withExistingParent(ModItems.advanced_vampire_hunter_spawn_egg.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.advanced_vampire_spawn_egg.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.hunter_trainer_spawn_egg.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_baron_spawn_egg.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_spawn_egg.get(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_hunter_spawn_egg.get(), mcLoc("item/template_spawn_egg"));

        withExistingParent(ModItems.basic_crossbow.get(), modLoc("item/crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_double_crossbow.get(), modLoc("item/double_crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_tech_crossbow.get(), modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra");

        withExistingParent(ModItems.basic_crossbow.get(), modLoc("item/crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_double_crossbow.get(), modLoc("item/double_crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_tech_crossbow.get(), modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra");

        withExistingParent(ModItems.enhanced_crossbow.get(), modLoc("item/crossbow")).texture("texture", "item/enhanced_crossbow");
        withExistingParent(ModItems.enhanced_double_crossbow.get(), modLoc("item/double_crossbow")).texture("texture", "item/enhanced_crossbow");
        withExistingParent(ModItems.enhanced_tech_crossbow.get(), modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra_enhanced");

        withExistingParent(ModItems.garlic_diffuser_core_improved.get(), ModItems.garlic_diffuser_core.get()).texture("texture", "block/garlic_diffuser_inside_improved");

        withExistingParent(ModItems.heart_seeker_normal.get(), modLoc("item/heart_seeker_model"));
        withExistingParent(ModItems.heart_seeker_enhanced.get(), modLoc("item/heart_seeker_model")).texture("3", "item/heart_seeker_enhanced");
        withExistingParent(ModItems.heart_seeker_ultimate.get(), modLoc("item/heart_seeker_model")).texture("3", "item/heart_seeker_ultimate");

        withExistingParent(ModItems.heart_striker_normal.get(), modLoc("item/heart_striker_model"));
        withExistingParent(ModItems.heart_striker_enhanced.get(), modLoc("item/heart_striker_model")).texture("2", "item/heart_striker_enhanced");
        withExistingParent(ModItems.heart_striker_ultimate.get(), modLoc("item/heart_striker_model")).texture("2", "item/heart_striker_ultimate");

        withExistingParent(ModItems.hunter_axe_normal.get(), modLoc("item/hunter_axe"));
        withExistingParent(ModItems.hunter_axe_enhanced.get(), modLoc("item/hunter_axe")).texture("texture", "item/hunter_axe_enhanced");
        withExistingParent(ModItems.hunter_axe_ultimate.get(), modLoc("item/hunter_axe")).texture("texture", "item/hunter_axe_ultimate");

        withExistingParent(ModItems.hunter_intel_0.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_1.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_2.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_3.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_4.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_5.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_6.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_7.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_8.get(), modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_9.get(), modLoc("item/hunter_intel"));

        item(ModItems.ring.get(), modLoc("item/vampire_ring_layer0"), modLoc("item/vampire_ring_layer1"));
        item(ModItems.amulet.get(), modLoc("item/vampire_amulet_layer0"), modLoc("item/vampire_amulet_layer1"));
        item(ModItems.obi_belt.get(), modLoc("item/vampire_obi_belt_layer0"), modLoc("item/vampire_obi_belt_layer1"));

        withExistingParent(ModItems.item_candelabra.get(), modLoc("block/candelabra"));


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
