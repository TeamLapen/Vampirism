package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, REFERENCE.MODID, existingFileHelper);
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
            add(ModBlocks.bloody_spruce_log);
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
            add(ModItems.obsidian_armor_chest_normal);
            add(ModItems.obsidian_armor_chest_enhanced);
            add(ModItems.obsidian_armor_chest_ultimate);
            add(ModItems.obsidian_armor_feet_normal);
            add(ModItems.obsidian_armor_feet_enhanced);
            add(ModItems.obsidian_armor_feet_ultimate);
            add(ModItems.obsidian_armor_head_normal);
            add(ModItems.obsidian_armor_head_enhanced);
            add(ModItems.obsidian_armor_head_ultimate);
            add(ModItems.obsidian_armor_legs_normal);
            add(ModItems.obsidian_armor_legs_enhanced);
            add(ModItems.obsidian_armor_legs_ultimate);
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
            addAll(ModTags.Items.PURE_BLOOD.getAllElements());
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
            put(ModItems.item_med_chair, modLoc("item/med_chair"));
            put(ModItems.item_tent_spawner, modLoc("item/item_tent"));
            put(ModItems.pure_salt, modLoc("item/holy_salt"));
            put(ModItems.vampire_book, mcLoc("item/written_book"));
        }};

        blocks.forEach(this::block);
        items.forEach(this::item);
        itemsWithTexture.forEach(this::item);

        withExistingParent(ModBlocks.bloody_spruce_leaves, mcLoc("block/oak_leaves"));
        withExistingParent(ModBlocks.vampire_spruce_leaves, mcLoc("block/oak_leaves"));

        withExistingParent(ModBlocks.bloody_spruce_sapling, mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":block/" + ModBlocks.bloody_spruce_sapling.getRegistryName().getPath());

        withExistingParent(ModBlocks.alchemical_fire, modLoc("block/fire_side"));
        withExistingParent(ModBlocks.altar_inspiration, modLoc("block/altar_inspiration/altar_inspiration"));
        item("crossbow_arrow", modLoc("item/crossbow_arrow"), modLoc("item/crossbow_arrow_tip"));

        withExistingParent(ModItems.crossbow_arrow_normal, modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.crossbow_arrow_spitfire, modLoc("item/crossbow_arrow"));
        withExistingParent(ModItems.crossbow_arrow_vampire_killer, modLoc("item/crossbow_arrow"));

        item("armor_of_swiftness_chest", mcLoc("item/leather_chestplate"), modLoc("item/swiftness_chest_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_chest_normal, modLoc("item/armor_of_swiftness_chest"));
        withExistingParent(ModItems.armor_of_swiftness_chest_enhanced, modLoc("item/armor_of_swiftness_chest"));
        withExistingParent(ModItems.armor_of_swiftness_chest_ultimate, modLoc("item/armor_of_swiftness_chest"));

        item("armor_of_swiftness_feet", mcLoc("item/leather_boots"), modLoc("item/swiftness_feet_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_feet_normal, modLoc("item/armor_of_swiftness_feet"));
        withExistingParent(ModItems.armor_of_swiftness_feet_enhanced, modLoc("item/armor_of_swiftness_feet"));
        withExistingParent(ModItems.armor_of_swiftness_feet_ultimate, modLoc("item/armor_of_swiftness_feet"));

        item("armor_of_swiftness_head", mcLoc("item/leather_helmet"), modLoc("item/swiftness_head_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_head_normal, modLoc("item/armor_of_swiftness_head"));
        withExistingParent(ModItems.armor_of_swiftness_head_enhanced, modLoc("item/armor_of_swiftness_head"));
        withExistingParent(ModItems.armor_of_swiftness_head_ultimate, modLoc("item/armor_of_swiftness_head"));

        item("armor_of_swiftness_legs", mcLoc("item/leather_leggings"), modLoc("item/swiftness_legs_overlay"));
        withExistingParent(ModItems.armor_of_swiftness_legs_normal, modLoc("item/armor_of_swiftness_legs"));
        withExistingParent(ModItems.armor_of_swiftness_legs_enhanced, modLoc("item/armor_of_swiftness_legs"));
        withExistingParent(ModItems.armor_of_swiftness_legs_ultimate, modLoc("item/armor_of_swiftness_legs"));

        withExistingParent(ModItems.advanced_vampire_hunter_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.advanced_vampire_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.hunter_trainer_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_baron_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_spawn_egg, mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.vampire_hunter_spawn_egg, mcLoc("item/template_spawn_egg"));

        withExistingParent(ModItems.basic_crossbow, modLoc("item/crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_double_crossbow, modLoc("item/double_crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_tech_crossbow, modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra");

        withExistingParent(ModItems.basic_crossbow, modLoc("item/crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_double_crossbow, modLoc("item/double_crossbow")).texture("texture", "item/crossbow");
        withExistingParent(ModItems.basic_tech_crossbow, modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra");

        withExistingParent(ModItems.enhanced_crossbow, modLoc("item/crossbow")).texture("texture", "item/enhanced_crossbow");
        withExistingParent(ModItems.enhanced_double_crossbow, modLoc("item/double_crossbow")).texture("texture", "item/enhanced_crossbow");
        withExistingParent(ModItems.enhanced_tech_crossbow, modLoc("item/tech_crossbow")).texture("extra", "item/tech_crossbow_extra_enhanced");

        withExistingParent(ModItems.garlic_beacon_core_improved, ModItems.garlic_beacon_core).texture("texture", "block/garlic_beacon_inside_improved");

        withExistingParent(ModItems.heart_seeker_normal, modLoc("item/heart_seeker_model"));
        withExistingParent(ModItems.heart_seeker_enhanced, modLoc("item/heart_seeker_model")).texture("texture", "item/heart_seeker_enhanced");
        withExistingParent(ModItems.heart_seeker_ultimate, modLoc("item/heart_seeker_model")).texture("texture", "item/heart_seeker_ultimate");

        withExistingParent(ModItems.heart_striker_normal, modLoc("item/heart_striker_model"));
        withExistingParent(ModItems.heart_striker_enhanced, modLoc("item/heart_striker_model")).texture("texture", "item/heart_striker_enhanced");
        withExistingParent(ModItems.heart_striker_ultimate, modLoc("item/heart_striker_model")).texture("texture", "item/heart_striker_ultimate");

        withExistingParent(ModItems.hunter_axe_normal, modLoc("item/hunter_axe"));
        withExistingParent(ModItems.hunter_axe_enhanced, modLoc("item/hunter_axe"));
        withExistingParent(ModItems.hunter_axe_ultimate, modLoc("item/hunter_axe"));

        ModTags.Items.HUNTER_INTEL.getAllElements().forEach(item -> withExistingParent(item, modLoc("item/hunter_intel")));

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

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism Item Models";
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

    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder item(Item item, ResourceLocation... texture) {
        if (texture != null && texture.length == 0) {
            return withExistingParent(item, mcLoc("item/generated")).texture("layer0", REFERENCE.MODID + ":item/" + item.getRegistryName().getPath());
        }
        return item(item.getRegistryName().getPath(), texture);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public ItemModelBuilder withExistingParent(Item name, ResourceLocation parent) {
        return super.withExistingParent(name.getRegistryName().getPath(), parent);
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
    public ItemModelBuilder block(Block name) {
        try {
            return super.withExistingParent(name.getRegistryName().getPath(), REFERENCE.MODID + ":block/" + name.getRegistryName().getPath());
        } catch (IllegalStateException e) {
            return getBuilder(name.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + name.getRegistryName().getPath()));
        }
    }

}
