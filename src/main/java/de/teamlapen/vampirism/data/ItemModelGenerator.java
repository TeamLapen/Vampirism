package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, REFERENCE.MODID, existingFileHelper);
    }

    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder block(Block name) {
        try {
            return super.withExistingParent(name.getRegistryName().getPath(), REFERENCE.MODID + ":block/" + name.getRegistryName().getPath());
        } catch (IllegalStateException e) {
            return getBuilder(name.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + name.getRegistryName().getPath()));
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism Item Models";
    }

    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder item(Item item, ResourceLocation... texture) {
        if (texture != null && texture.length == 0) {
            return withExistingParent(item, mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":item/" + item.getRegistryName().getPath());
        }
        return item(item.getRegistryName().getPath(), texture);
    }

    public ItemModelBuilder item(String item, ResourceLocation... texture) {
        ItemModelBuilder model = withExistingParent(item, mcLoc("item/generated"));
        if (texture != null) {
            for (int i = 0; i < texture.length; i++) {
                model.texture("layer" + i, texture[i]);
            }
        }
        return model;
    }

    @Nonnull
    public ItemModelBuilder withExistingParent(Item name, Item parent) {
        return this.withExistingParent(name, parent.getRegistryName());
    }

    @SuppressWarnings({"UnusedReturnValue", "ConstantConditions"})
    @Nonnull
    public ItemModelBuilder withExistingParent(Block name, ResourceLocation parent) {
        return super.withExistingParent(name.getRegistryName().getPath(), parent);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public ItemModelBuilder withExistingParent(Item name, ResourceLocation parent) {
        return super.withExistingParent(name.getRegistryName().getPath(), parent);
    }

    @Override
    protected void registerModels() {
        Set<Block> blocks = new HashSet<Block>() {{
            add(ModBlocks.altar_tip);
            add(ModBlocks.castle_block_dark_brick);
            add(ModBlocks.castle_block_dark_brick_bloody);
            add(ModBlocks.castle_block_dark_stone);
            add(ModBlocks.castle_block_normal_brick);
            add(ModBlocks.castle_block_purple_brick);
            add(ModBlocks.castle_slab_dark_brick);
            add(ModBlocks.castle_slab_dark_stone);
            add(ModBlocks.castle_slab_purple_brick);
            add(ModBlocks.castle_stairs_dark_brick);
            add(ModBlocks.castle_stairs_dark_stone);
            add(ModBlocks.castle_stairs_purple_brick);
            add(ModBlocks.blood_grinder);
            add(ModBlocks.blood_pedestal);
            add(ModBlocks.potion_table);
            add(ModBlocks.blood_sieve);
            add(ModBlocks.church_altar);
            add(ModBlocks.cursed_earth);
            add(ModBlocks.garlic_beacon_weak);
            add(ModBlocks.garlic_beacon_normal);
            add(ModBlocks.garlic_beacon_improved);
            add(ModBlocks.hunter_table);
            add(ModBlocks.sunscreen_beacon);
            add(ModBlocks.totem_top);
            add(ModBlocks.totem_top_vampirism_vampire);
            add(ModBlocks.totem_top_vampirism_hunter);
            add(ModBlocks.vampire_orchid);
            add(ModBlocks.chandelier);
            add(ModBlocks.cross);
            add(ModBlocks.tombstone1);
            add(ModBlocks.tombstone2);
            add(ModBlocks.tombstone3);
            add(ModBlocks.grave_cage);
            add(ModBlocks.cursed_grass);
            add(ModBlocks.dark_spruce_log);
            add(ModBlocks.cursed_roots);
            add(ModBlocks.cursed_spruce_log);
            add(ModBlocks.stripped_dark_spruce_log);
            add(ModBlocks.stripped_cursed_spruce_log);
            add(ModBlocks.dark_spruce_planks);
            add(ModBlocks.cursed_spruce_planks);
            add(ModBlocks.dark_spruce_stairs);
            add(ModBlocks.cursed_spruce_stairs);
            add(ModBlocks.dark_spruce_wood);
            add(ModBlocks.cursed_spruce_wood);
            add(ModBlocks.stripped_dark_spruce_wood);
            add(ModBlocks.stripped_cursed_spruce_wood);
            add(ModBlocks.dark_spruce_pressure_place);
            add(ModBlocks.cursed_spruce_pressure_place);
            add(ModBlocks.dark_spruce_button);
            add(ModBlocks.cursed_spruce_button);
            add(ModBlocks.dark_spruce_slab);
            add(ModBlocks.cursed_spruce_slab);
            add(ModBlocks.dark_spruce_fence_gate);
            add(ModBlocks.cursed_spruce_fence_gate);
            add(ModBlocks.vampire_rack);
            add(ModBlocks.throne);
        }};
        Set<Item> items = new HashSet<Item>() {{
            add(ModItems.hunter_coat_chest_normal);
            add(ModItems.hunter_coat_chest_enhanced);
            add(ModItems.hunter_coat_chest_ultimate);
            add(ModItems.hunter_coat_feet_normal);
            add(ModItems.hunter_coat_feet_enhanced);
            add(ModItems.hunter_coat_feet_ultimate);
            add(ModItems.hunter_coat_head_normal);
            add(ModItems.hunter_coat_head_enhanced);
            add(ModItems.hunter_coat_head_ultimate);
            add(ModItems.hunter_coat_legs_normal);
            add(ModItems.hunter_coat_legs_enhanced);
            add(ModItems.hunter_coat_legs_ultimate);
            add(ModItems.blood_bucket);
            add(ModItems.impure_blood_bucket);
            add(ModItems.blood_infused_enhanced_iron_ingot);
            add(ModItems.blood_infused_iron_ingot);
            add(ModItems.holy_salt);
            add(ModItems.holy_salt_water);
            add(ModItems.human_heart);
            add(ModItems.injection_empty);
            add(ModItems.injection_garlic);
            add(ModItems.injection_sanguinare);
            add(ModItems.injection_zombie_blood);
            add(ModItems.purified_garlic);
            add(ModItems.soul_orb_vampire);
            add(ModItems.tech_crossbow_ammo_package);
            add(ModItems.vampire_blood_bottle);
            add(ModItems.vampire_cloak_black_blue);
            add(ModItems.vampire_cloak_black_red);
            add(ModItems.vampire_cloak_black_white);
            add(ModItems.vampire_cloak_white_black);
            add(ModItems.vampire_cloak_red_black);
            add(ModItems.vampire_fang);
            add(ModItems.weak_human_heart);
            add(ModItems.item_tent);
            add(ModItems.pure_blood_0);
            add(ModItems.pure_blood_1);
            add(ModItems.pure_blood_2);
            add(ModItems.pure_blood_3);
            add(ModItems.pure_blood_4);
            add(ModItems.vampire_minion_binding);
            add(ModItems.vampire_minion_upgrade_simple);
            add(ModItems.vampire_minion_upgrade_enhanced);
            add(ModItems.vampire_minion_upgrade_special);
            add(ModItems.hunter_minion_equipment);
            add(ModItems.hunter_minion_upgrade_simple);
            add(ModItems.hunter_minion_upgrade_enhanced);
            add(ModItems.hunter_minion_upgrade_special);
            add(ModItems.oblivion_potion);
            add(ModItems.cure_apple);
            add(ModItems.vampire_clothing_hat);
            add(ModItems.vampire_clothing_boots);
            add(ModItems.vampire_clothing_legs);
            add(ModItems.vampire_clothing_crown);
            add(ModItems.garlic_finder);
        }};
        Map<Item, ResourceLocation> itemsWithTexture = new HashMap<Item, ResourceLocation>() {{
            put(ModItems.holy_water_bottle_normal, modLoc("item/holy_water_normal"));
            put(ModItems.holy_water_bottle_enhanced, modLoc("item/holy_water_enhanced"));
            put(ModItems.holy_water_bottle_ultimate, modLoc("item/holy_water_ultimate"));
            put(ModItems.holy_water_splash_bottle_normal, modLoc("item/holy_water_splash_normal"));
            put(ModItems.holy_water_splash_bottle_enhanced, modLoc("item/holy_water_splash_enhanced"));
            put(ModItems.holy_water_splash_bottle_ultimate, modLoc("item/holy_water_splash_ultimate"));
            put(ModItems.garlic_bread, modLoc("item/garlic_bread"));
            put(ModItems.hunter_hat_head_0, modLoc("item/hunter_hat_0"));
            put(ModItems.hunter_hat_head_1, modLoc("item/hunter_hat_1"));
            put(ModItems.item_alchemical_fire, modLoc("item/alchemical_fire"));
            put(ModItems.item_garlic, modLoc("item/garlic"));
            put(ModItems.item_tent_spawner, modLoc("item/item_tent"));
            put(ModItems.pure_salt, modLoc("item/holy_salt"));
            put(ModItems.vampire_book, modLoc("item/vampire_book"));
            put(Item.byBlock(ModBlocks.med_chair), modLoc("item/med_chair"));
            put(ModBlocks.cursed_bark.asItem(), modLoc("block/" + ModBlocks.cursed_bark.getRegistryName().getPath()));
            put(ModItems.dark_spruce_sign, modLoc("item/" + ModItems.dark_spruce_sign.getRegistryName().getPath()));
            put(ModItems.cursed_spruce_sign, modLoc("item/" + ModItems.cursed_spruce_sign.getRegistryName().getPath()));
        }};

        blocks.forEach(this::block);
        items.forEach(this::item);
        itemsWithTexture.forEach(this::item);

        withExistingParent(ModBlocks.dark_spruce_leaves, mcLoc("block/oak_leaves"));

        withExistingParent(ModBlocks.dark_spruce_sapling, mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/" + ModBlocks.dark_spruce_sapling.getRegistryName().getPath());
        withExistingParent(ModBlocks.cursed_spruce_sapling, mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/" + ModBlocks.cursed_spruce_sapling.getRegistryName().getPath());

        withExistingParent(ModBlocks.alchemical_fire, modLoc("block/fire_side"));
        withExistingParent(ModBlocks.altar_inspiration, modLoc("block/altar_inspiration/altar_inspiration"));
        item("crossbow_arrow", modLoc("item/crossbow_arrow"), modLoc("item/crossbow_arrow_tip"));

        withExistingParent(ModItems.crossbow_arrow_normal, modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.crossbow_arrow_spitfire, modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.crossbow_arrow_vampire_killer, modLoc("item/crossbow_arrow"));

        item(ModItems.armor_of_swiftness_chest_normal, modLoc("item/armor_of_swiftness_chest_normal"), modLoc("item/armor_of_swiftness_chest_normal_overlay"));
        item(ModItems.armor_of_swiftness_chest_enhanced, modLoc("item/armor_of_swiftness_chest_enhanced"), modLoc("item/armor_of_swiftness_chest_enhanced_overlay"));
        item(ModItems.armor_of_swiftness_chest_ultimate, modLoc("item/armor_of_swiftness_chest_ultimate"), modLoc("item/armor_of_swiftness_chest_ultimate_overlay"));

        item(ModItems.armor_of_swiftness_feet_normal, modLoc("item/armor_of_swiftness_feet_normal"), modLoc("item/armor_of_swiftness_feet_normal_overlay"));
        item(ModItems.armor_of_swiftness_feet_enhanced, modLoc("item/armor_of_swiftness_feet_enhanced"), modLoc("item/armor_of_swiftness_feet_enhanced_overlay"));
        item(ModItems.armor_of_swiftness_feet_ultimate, modLoc("item/armor_of_swiftness_feet_ultimate"), modLoc("item/armor_of_swiftness_feet_ultimate_overlay"));

        item(ModItems.armor_of_swiftness_head_normal, modLoc("item/armor_of_swiftness_head_normal"), modLoc("item/armor_of_swiftness_head_normal_overlay"));
        item(ModItems.armor_of_swiftness_head_enhanced, modLoc("item/armor_of_swiftness_head_enhanced"), modLoc("item/armor_of_swiftness_head_enhanced_overlay"));
        item(ModItems.armor_of_swiftness_head_ultimate, modLoc("item/armor_of_swiftness_head_ultimate"), modLoc("item/armor_of_swiftness_head_ultimate_overlay"));

        item(ModItems.armor_of_swiftness_legs_normal, modLoc("item/armor_of_swiftness_legs_normal"), modLoc("item/armor_of_swiftness_legs_normal_overlay"));
        item(ModItems.armor_of_swiftness_legs_enhanced, modLoc("item/armor_of_swiftness_legs_enhanced"), modLoc("item/armor_of_swiftness_legs_enhanced_overlay"));
        item(ModItems.armor_of_swiftness_legs_ultimate, modLoc("item/armor_of_swiftness_legs_ultimate"), modLoc("item/armor_of_swiftness_legs_ultimate_overlay"));

        withExistingParent(ModItems.advanced_vampire_hunter_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.advanced_vampire_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.hunter_trainer_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_baron_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_hunter_spawn_egg, mcLoc("item/template_spawn_egg"));

        withExistingParent(ModItems.basic_crossbow, modLoc("item/crossbow")).texture("texture", "item/crossbow").texture("string", "item/crossbow_part_string").texture("arrow", "item/crossbow_part_arrow").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("basic_crossbow_unloaded", modLoc("item/crossbow_unloaded")).texture("texture", "item/crossbow").texture("string", "item/crossbow_part_string_unloaded"));
        withExistingParent(ModItems.basic_double_crossbow, modLoc("item/double_crossbow")).texture("texture", "item/crossbow_double").texture("string", "item/crossbow_part_double_string").texture("arrows", "item/crossbow_part_arrows").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("basic_double_crossbow_unloaded", modLoc("item/double_crossbow")).texture("texture", "item/crossbow_double").texture("string", "item/crossbow_part_double_string_unloaded"));
        withExistingParent(ModItems.basic_tech_crossbow, modLoc("item/tech_crossbow")).texture("texture", "item/tech_crossbow").texture("string", "item/crossbow_part_tech_string").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("basic_tech_crossbow_unloaded", modLoc("item/tech_crossbow_unloaded")).texture("texture", "item/tech_crossbow").texture("string", "item/crossbow_part_tech_string_unloaded"));

        withExistingParent(ModItems.enhanced_crossbow, modLoc("item/crossbow")).texture("texture", "item/crossbow_enhanced").texture("string", "item/crossbow_part_string").texture("arrow", "item/crossbow_part_arrow").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("enhanced_crossbow_unloaded", modLoc("item/crossbow_unloaded")).texture("texture", "item/crossbow_enhanced").texture("string", "item/crossbow_part_string_unloaded"));
        withExistingParent(ModItems.enhanced_double_crossbow, modLoc("item/double_crossbow")).texture("texture", "item/crossbow_double_enhanced").texture("string", "item/crossbow_part_double_string").texture("arrows", "item/crossbow_part_arrows").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("enhanced_double_crossbow_unloaded", modLoc("item/double_crossbow_unloaded")).texture("texture", "item/crossbow_double_enhanced").texture("string", "item/crossbow_part_double_string_unloaded"));
        withExistingParent(ModItems.enhanced_tech_crossbow, modLoc("item/tech_crossbow")).texture("texture", "item/tech_crossbow_enhanced").texture("string", "item/crossbow_part_tech_string").override().predicate(modLoc("charged"), 0.01f).model(withExistingParent("enhanced_tech_crossbow_unloaded", modLoc("item/tech_crossbow_unloaded")).texture("texture", "item/tech_crossbow_enhanced").texture("string", "item/crossbow_part_tech_string_unloaded"));

        withExistingParent(ModItems.garlic_beacon_core_improved, ModItems.garlic_beacon_core).texture("texture", "block/garlic_beacon_inside_improved");

        withExistingParent(ModItems.heart_seeker_normal, modLoc("item/heart_seeker_model"));
        withExistingParent(ModItems.heart_seeker_enhanced, modLoc("item/heart_seeker_model")).texture("3", "item/heart_seeker_enhanced");
        withExistingParent(ModItems.heart_seeker_ultimate, modLoc("item/heart_seeker_model")).texture("3", "item/heart_seeker_ultimate");

        withExistingParent(ModItems.heart_striker_normal, modLoc("item/heart_striker_model"));
        withExistingParent(ModItems.heart_striker_enhanced, modLoc("item/heart_striker_model")).texture("2", "item/heart_striker_enhanced");
        withExistingParent(ModItems.heart_striker_ultimate, modLoc("item/heart_striker_model")).texture("2", "item/heart_striker_ultimate");

        withExistingParent(ModItems.hunter_axe_normal, modLoc("item/hunter_axe"));
        withExistingParent(ModItems.hunter_axe_enhanced, modLoc("item/hunter_axe")).texture("texture", "item/hunter_axe_enhanced");
        withExistingParent(ModItems.hunter_axe_ultimate, modLoc("item/hunter_axe")).texture("texture", "item/hunter_axe_ultimate");

        withExistingParent(ModItems.hunter_intel_0, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_1, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_2, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_3, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_4, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_5, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_6, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_7, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_8, modLoc("item/hunter_intel"));
        withExistingParent(ModItems.hunter_intel_9, modLoc("item/hunter_intel"));

        item(ModItems.ring, modLoc("item/vampire_ring_layer0"), modLoc("item/vampire_ring_layer1"));
        item(ModItems.amulet, modLoc("item/vampire_amulet_layer0"), modLoc("item/vampire_amulet_layer1"));
        item(ModItems.obi_belt, modLoc("item/vampire_obi_belt_layer0"), modLoc("item/vampire_obi_belt_layer1"));

        withExistingParent(ModItems.item_candelabra, modLoc("block/candelabra"));

        withExistingParent(ModItems.crucifix_normal, modLoc("item/crucifix")).texture("texture", "item/crucifix_wooden");
        withExistingParent(ModItems.crucifix_enhanced, modLoc("item/crucifix")).texture("texture", "item/crucifix_iron");
        withExistingParent(ModItems.crucifix_ultimate, modLoc("item/crucifix")).texture("texture", "item/crucifix_gold");


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

        withExistingParent(ModBlocks.dark_spruce_trapdoor, modLoc("block/dark_spruce_trapdoor_bottom"));
        withExistingParent(ModBlocks.cursed_spruce_trapdoor, modLoc("block/cursed_spruce_trapdoor_bottom"));

        item(ModBlocks.dark_spruce_door.asItem(), modLoc("item/dark_spruce_door"));
        item(ModBlocks.cursed_spruce_door.asItem(), modLoc("item/cursed_spruce_door"));

        withExistingParent(ModBlocks.dark_spruce_button.asItem(), modLoc("block/dark_spruce_button_inventory"));
        withExistingParent(ModBlocks.cursed_spruce_button.asItem(), modLoc("block/cursed_spruce_button_inventory"));
        withExistingParent(ModBlocks.dark_spruce_fence.asItem(), modLoc("block/dark_spruce_fence_inventory"));
        withExistingParent(ModBlocks.cursed_spruce_fence.asItem(), modLoc("block/cursed_spruce_fence_inventory"));

    }

}
