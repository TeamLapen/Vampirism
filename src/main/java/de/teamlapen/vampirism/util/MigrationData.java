package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.NotNull;

public class MigrationData {


    @SubscribeEvent
    public static void fix(NewRegistryEvent event) {
        fixSkillsVampire(new Mapping(VampireSkills.SKILLS));
        fixSkillsHunter(new Mapping(HunterSkills.SKILLS));
        fixPotions(new Mapping(ModPotions.POTIONS));
        fixTiles(new Mapping(ModTiles.BLOCK_ENTITY_TYPES));
        fixItems(new Mapping(ModItems.ITEMS));
        fixBlocks(new Mapping(ModBlocks.BLOCKS));
        fixEnchantments(new Mapping(ModEnchantments.ENCHANTMENTS));
        fixEntityTypes(new Mapping(ModEntities.ENTITY_TYPES));
        fixEffects(new Mapping(ModEffects.EFFECTS));
    }

    public record Mapping(DeferredRegister<?> register) {
        public void remap(String id, String newId) {
                remap(new ResourceLocation(id), new ResourceLocation(newId));
            }

        public void remap(ResourceLocation id, ResourceLocation object) {
            register.addAlias(id, object);
        }
    }

    private static void fixSkillsVampire(@NotNull Mapping consumer) {
        consumer.remap("vampirism:bat", "vampirism:fledgling");
    }
    private static void fixSkillsHunter(@NotNull Mapping consumer) {
        consumer.remap("vampirism:garlic_beacon_improved", "vampirism:garlic_diffuser_improved");
        consumer.remap("vampirism:garlic_beacon", "vampirism:garlic_diffuser");
        consumer.remap("vampirism:holy_water_enhanced", "vampirism:enhanced_blessing");
    }

    private static void fixPotions(@NotNull Mapping mapping) {
        mapping.remap("vampirism:long_strong_resistance", "vampirism:long_resistance");
        mapping.remap("vampirism:very_long_resistance", "vampirism:long_resistance");
        mapping.remap("vampirism:very_strong_resistance", "vampirism:strong_resistance");
        mapping.remap("vampirism:very_strong_harming", "strong_harming");
    }

    private static void fixTiles(@NotNull Mapping mapping) {
        mapping.remap("vampirism:garlic_beacon", "vampirism:garlic_diffuser");
    }

    private static void fixItems(@NotNull Mapping mapping) {
        mapping.remap("vampirism:vampire_clothing_head", "vampirism:vampire_clothing_crown");
        mapping.remap("vampirism:vampire_clothing_feet", "vampirism:vampire_clothing_boots");
        mapping.remap("vampirism:garlic_beacon_core", "vampirism:garlic_diffuser_core");
        mapping.remap("vampirism:garlic_beacon_core_improved", "vampirism:garlic_diffuser_core_improved");
        mapping.remap("vampirism:garlic_beacon_normal", "vampirism:garlic_diffuser_normal");
        mapping.remap("vampirism:garlic_beacon_weak", "vampirism:garlic_diffuser_weak");
        mapping.remap("vampirism:garlic_beacon_improved", "vampirism:garlic_diffuser_improved");
        mapping.remap("vampirism:church_altar", "vampirism:altar_cleansing");
        mapping.remap("vampirism:item_med_chair", "vampirism:med_chair");
        mapping.remap("vampirism:bloody_spruce_log", "vampirism:cursed_spruce_log");
        mapping.remap("vampirism:bloody_spruce_leaves", "vampirism:dark_spruce_leaves");
        mapping.remap("vampirism:coffin", "vampirism:coffin_red");
        mapping.remap("vampirism:holy_salt_water", "vampirism:pure_salt_water");
        mapping.remap("vampirism:holy_salt", "vampirism:pure_salt");
        mapping.remap("vampirism:injection_zombie_blood", "apple");
        mapping.remap("vampirism:cure_apple", "golden_apple");
        mapping.remap("vampirism:obsidian_armor_head_normal", "vampirism:hunter_coat_head_normal");
        mapping.remap("vampirism:obsidian_armor_chest_normal", "vampirism:hunter_coat_chest_normal");
        mapping.remap("vampirism:obsidian_armor_legs_normal", "vampirism:hunter_coat_legs_normal");
        mapping.remap("vampirism:obsidian_armor_feet_normal", "vampirism:hunter_coat_feet_normal");
        mapping.remap("vampirism:obsidian_armor_head_enhanced","vampirism:hunter_coat_head_enhanced" );
        mapping.remap("vampirism:obsidian_armor_chest_enhanced", "vampirism:hunter_coat_chest_enhanced");
        mapping.remap("vampirism:obsidian_armor_legs_enhanced","vampirism:hunter_coat_legs_enhanced" );
        mapping.remap("vampirism:obsidian_armor_feet_enhanced","vampirism:hunter_coat_feet_enhanced" );
        mapping.remap("vampirism:obsidian_armor_head_ultimate", "vampirism:hunter_coat_head_ultimate");
        mapping.remap("vampirism:obsidian_armor_chest_ultimate","vampirism:hunter_coat_chest_ultimate" );
        mapping.remap("vampirism:obsidian_armor_legs_ultimate","vampirism:hunter_coat_legs_ultimate" );
        mapping.remap("vampirism:obsidian_armor_feet_ultimate", "vampirism:hunter_coat_feet_ultimate");


    }

    private static void fixBlocks(@NotNull Mapping mapping) {
        mapping.remap("vampirism:blood_potion_table","vampirism:potion_table" );
        mapping.remap("vampirism:garlic_beacon_normal", "vampirism:totem_top_vampirism_hunter_crafted");
        mapping.remap("vampirism:garlic_beacon_weak", "vampirism:garlic_diffuser_weak");
        mapping.remap("vampirism:garlic_beacon_improved", "vampirism:garlic_diffuser_improved");
        mapping.remap("vampirism:church_altar", "vampirism:altar_cleansing");
        mapping.remap("vampirism:vampire_spruce_leaves", "vampirism:dark_spruce_leaves");
        mapping.remap("vampirism:bloody_spruce_leaves","vampirism:dark_spruce_leaves" );
        mapping.remap("vampirism:bloody_spruce_log", "vampirism:cursed_spruce_log");
        mapping.remap("vampirism:cursed_grass_block", "vampirism:cursed_grass");
        mapping.remap("castle_block_dark_brick", "vampirism:dark_stone_bricks");
        mapping.remap("castle_block_dark_brick_bloody", "vampirism:bloody_dark_stone_bricks");
        mapping.remap("castle_block_dark_stone", "vampirism:dark_stone");
        mapping.remap("castle_block_normal_brick", "stone_bricks");
        mapping.remap("castle_slab_dark_brick", "vampirism:dark_stone_brick_slab");
        mapping.remap("castle_slab_dark_stone", "vampirism:dark_stone_slab");
        mapping.remap("castle_stairs_dark_brick", "vampirism:dark_stone_brick_stairs");
        mapping.remap("castle_stairs_dark_stone", "vampirism:dark_stone_stairs");
        mapping.remap("castle_block_dark_brick_cracked","vampirism:cracked_dark_stone_bricks");
        mapping.remap("castle_block_dark_brick_wall", "vampirism:dark_stone_brick_wall");
        mapping.remap("castle_block_purple_brick", "vampirism:purple_stone_bricks");
        mapping.remap("castle_slab_purple_brick", "vampirism:purple_stone_brick_slab");
        mapping.remap("castle_stairs_purple_brick", "vampirism:purple_stone_brick_stairs");
        mapping.remap("castle_block_purple_brick_wall", "vampirism:purple_stone_brick_wall");
        mapping.remap("dark_spruce_pressure_place", "vampirism:dark_spruce_pressure_plate");
        mapping.remap("cursed_spruce_pressure_place", "vampirism:cursed_spruce_pressure_plate");
    }


    private static void fixEnchantments(@NotNull Mapping mapping) {
        mapping.remap("vampirism:crossbowinfinite", "infinity");
    }

    private static void fixEntityTypes(@NotNull Mapping mapping) {
        mapping.remap("vampirism:vampire_hunter", "vampirism:hunter");
        mapping.remap("vampirism:vampire_hunter_imob", "vampirism:hunter_imob");
    }

    private static void fixEffects(@NotNull Mapping mapping) {
        mapping.remap("vampirism:thirst", "hunger");
    }
}
