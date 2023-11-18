package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import org.jetbrains.annotations.NotNull;

public class MigrationData {

    @SubscribeEvent
    public static void onMissingMappings(@NotNull MissingMappingsEvent event) {
        event.getAllMappings(VampirismRegistries.SKILLS_ID).forEach(MigrationData::fixSkill);
        event.getAllMappings(ForgeRegistries.Keys.POTIONS).forEach(MigrationData::fixPotions);
        event.getAllMappings(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).forEach(MigrationData::fixTiles);
        event.getAllMappings(ForgeRegistries.Keys.ITEMS).forEach(MigrationData::fixItems);
        event.getAllMappings(ForgeRegistries.Keys.BLOCKS).forEach(MigrationData::fixBlocks);
        event.getAllMappings(ForgeRegistries.Keys.ENCHANTMENTS).forEach(MigrationData::fixEnchantments);
        event.getAllMappings(ForgeRegistries.Keys.ENTITY_TYPES).forEach(MigrationData::fixEntityTypes);
        event.getAllMappings(ForgeRegistries.Keys.MOB_EFFECTS).forEach(MigrationData::fixEffects);
    }

    public static void fixSkill(@NotNull MissingMappingsEvent.Mapping<ISkill<?>> mapping) {
        switch (mapping.getKey().toString()) {
            case "vampirism:creeper_avoided", "vampirism:enhanced_crossbow", "vampirism:vampire_forest_fog" -> mapping.ignore();
            case "vampirism:bat" -> mapping.remap(VampireSkills.FLEDGLING.get());
            case "vampirism:garlic_beacon_improved" -> mapping.remap(HunterSkills.GARLIC_DIFFUSER_IMPROVED.get());
            case "vampirism:garlic_beacon" -> mapping.remap(HunterSkills.GARLIC_DIFFUSER.get());
            case "vampirism:holy_water_enhanced" -> mapping.remap(HunterSkills.ENHANCED_BLESSING.get());
            default -> {
                if (mapping.getKey().toString().startsWith("vampirism:blood_potion_")) {
                    mapping.ignore();
                }
            }
        }
    }

    public static void fixPotions(@NotNull MissingMappingsEvent.Mapping<Potion> mapping) {
        switch (mapping.getKey().toString()) {
            case "vampirism:long_strong_resistance", "vampirism:very_long_resistance" -> mapping.remap(ModPotions.LONG_RESISTANCE.get());
            case "vampirism:very_strong_resistance" -> mapping.remap(ModPotions.STRONG_RESISTANCE.get());
            case "vampirism:thirst", "vampirism:long_thirst", "vampirism:strong_thirst", "vampirism:very_long_thirst", "vampirism:very_strong_thirst", "vampirism:long_strong_thirst" -> mapping.ignore();
            case "vampirism:very_strong_harming" -> mapping.remap(Potions.STRONG_HARMING);
        }
    }

    public static void fixTiles(@NotNull MissingMappingsEvent.Mapping<BlockEntityType<?>> mapping) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (mapping.getKey().toString()) {
            case "vampirism:garlic_beacon" -> mapping.remap(ModTiles.GARLIC_DIFFUSER.get());
        }
    }

    public static void fixItems(@NotNull MissingMappingsEvent.Mapping<Item> mapping) {
        switch (mapping.getKey().toString()) {
            case "vampirism:blood_potion", "vampirism:blood_potion_table" -> mapping.ignore();
            case "vampirism:vampire_clothing_head" -> mapping.remap(ModItems.VAMPIRE_CLOTHING_CROWN.get());
            case "vampirism:vampire_clothing_feet" -> mapping.remap(ModItems.VAMPIRE_CLOTHING_BOOTS.get());
            case "vampirism:garlic_beacon_core" -> mapping.remap(ModItems.GARLIC_DIFFUSER_CORE.get());
            case "vampirism:garlic_beacon_core_improved" -> mapping.remap(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.get());
            case "vampirism:garlic_beacon_normal" -> mapping.remap(ModBlocks.GARLIC_DIFFUSER_NORMAL.get().asItem());
            case "vampirism:garlic_beacon_weak" -> mapping.remap(ModBlocks.GARLIC_DIFFUSER_WEAK.get().asItem());
            case "vampirism:garlic_beacon_improved" -> mapping.remap(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get().asItem());
            case "vampirism:church_altar" -> mapping.remap(ModBlocks.ALTAR_CLEANSING.get().asItem());
            case "vampirism:item_med_chair" -> mapping.remap(ModBlocks.MED_CHAIR.get().asItem());
            case "vampirism:bloody_spruce_log" -> mapping.remap(ModBlocks.CURSED_SPRUCE_LOG.get().asItem());
            case "vampirism:bloody_spruce_leaves" -> mapping.remap(ModBlocks.DARK_SPRUCE_LEAVES.get().asItem());
            case "vampirism:coffin" -> mapping.remap(ModBlocks.COFFIN_RED.get().asItem());
            case "vampirism:holy_salt_water" -> mapping.remap(ModItems.PURE_SALT_WATER.get());
            case "vampirism:holy_salt" -> mapping.remap(ModItems.PURE_SALT.get());
            case "vampirism:injection_zombie_blood" -> mapping.remap(Items.APPLE);
            case "vampirism:cure_apple" -> mapping.remap(Items.GOLDEN_APPLE);
            case "vampirism:obsidian_armor_head_normal" -> mapping.remap(ModItems.HUNTER_COAT_HEAD_NORMAL.get());
            case "vampirism:obsidian_armor_chest_normal" -> mapping.remap(ModItems.HUNTER_COAT_CHEST_NORMAL.get());
            case "vampirism:obsidian_armor_legs_normal" -> mapping.remap(ModItems.HUNTER_COAT_LEGS_NORMAL.get());
            case "vampirism:obsidian_armor_feet_normal" -> mapping.remap(ModItems.HUNTER_COAT_FEET_NORMAL.get());
            case "vampirism:obsidian_armor_head_enhanced" -> mapping.remap(ModItems.HUNTER_COAT_HEAD_ENHANCED.get());
            case "vampirism:obsidian_armor_chest_enhanced" -> mapping.remap(ModItems.HUNTER_COAT_CHEST_ENHANCED.get());
            case "vampirism:obsidian_armor_legs_enhanced" -> mapping.remap(ModItems.HUNTER_COAT_LEGS_ENHANCED.get());
            case "vampirism:obsidian_armor_feet_enhanced" -> mapping.remap(ModItems.HUNTER_COAT_FEET_ENHANCED.get());
            case "vampirism:obsidian_armor_head_ultimate" -> mapping.remap(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get());
            case "vampirism:obsidian_armor_chest_ultimate" -> mapping.remap(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get());
            case "vampirism:obsidian_armor_legs_ultimate" -> mapping.remap(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get());
            case "vampirism:obsidian_armor_feet_ultimate" -> mapping.remap(ModItems.HUNTER_COAT_FEET_ULTIMATE.get());
        }
    }

    public static void fixBlocks(@NotNull MissingMappingsEvent.Mapping<Block> mapping) {
        switch (mapping.getKey().toString()) {
            case "vampirism:blood_potion_table" -> mapping.remap(ModBlocks.POTION_TABLE.get());
            case "vampirism:garlic_beacon_normal" -> mapping.remap(ModBlocks.GARLIC_DIFFUSER_NORMAL.get());
            case "vampirism:garlic_beacon_weak" -> mapping.remap(ModBlocks.GARLIC_DIFFUSER_WEAK.get());
            case "vampirism:garlic_beacon_improved" -> mapping.remap(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get());
            case "vampirism:church_altar" -> mapping.remap(ModBlocks.ALTAR_CLEANSING.get());
            case "vampirism:vampire_spruce_leaves", "vampirism:bloody_spruce_leaves" -> mapping.remap(ModBlocks.DARK_SPRUCE_LEAVES.get());
            case "vampirism:bloody_spruce_log" -> mapping.remap(ModBlocks.CURSED_SPRUCE_LOG.get());
            case "vampirism:cursed_grass_block" -> mapping.remap(ModBlocks.CURSED_GRASS.get());
            case "cursed_bark" -> mapping.ignore();
            case "castle_block_dark_brick" -> mapping.remap(ModBlocks.DARK_STONE_BRICKS.get());
            case "castle_block_dark_brick_bloody" -> mapping.remap(ModBlocks.BLOODY_DARK_STONE_BRICKS.get());
            case "castle_block_dark_stone" -> mapping.remap(ModBlocks.DARK_STONE.get());
            case "castle_block_normal_brick" -> mapping.remap(Blocks.STONE_BRICKS);
            case "castle_slab_dark_brick" -> mapping.remap(ModBlocks.DARK_STONE_BRICK_SLAB.get());
            case "castle_slab_dark_stone" -> mapping.remap(ModBlocks.DARK_STONE_SLAB.get());
            case "castle_stairs_dark_brick" -> mapping.remap(ModBlocks.DARK_STONE_BRICK_STAIRS.get());
            case "castle_stairs_dark_stone" -> mapping.remap(ModBlocks.DARK_STONE_STAIRS.get());
            case "castle_block_dark_brick_cracked" -> mapping.remap(ModBlocks.CRACKED_DARK_STONE_BRICKS.get());
            case "castle_block_dark_brick_wall" -> mapping.remap(ModBlocks.DARK_STONE_BRICK_WALL.get());
            case "castle_block_purple_brick" -> mapping.remap(ModBlocks.PURPLE_STONE_BRICKS.get());
            case "castle_slab_purple_brick" -> mapping.remap(ModBlocks.PURPLE_STONE_BRICK_SLAB.get());
            case "castle_stairs_purple_brick" -> mapping.remap(ModBlocks.PURPLE_STONE_BRICK_STAIRS.get());
            case "castle_block_purple_brick_wall" -> mapping.remap(ModBlocks.PURPLE_STONE_BRICK_WALL.get());
        }
    }


    static void fixEnchantments(@NotNull MissingMappingsEvent.Mapping<Enchantment> mapping) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (mapping.getKey().toString()) {
            case "vampirism:crossbowinfinite" -> mapping.remap(Enchantments.INFINITY_ARROWS);
        }
    }

    static void fixEntityTypes(@NotNull MissingMappingsEvent.Mapping<EntityType<?>> mapping) {
        switch (mapping.getKey().toString()) {
            case "vampirism:vampire_hunter" -> mapping.remap(ModEntities.HUNTER.get());
            case "vampirism:vampire_hunter_imob" -> mapping.remap(ModEntities.HUNTER_IMOB.get());
        }
    }

    public static void fixEffects(@NotNull MissingMappingsEvent.Mapping<MobEffect> mapping) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (mapping.getKey().toString()) {
            case "vampirism:thirst" -> mapping.remap(MobEffects.HUNGER);
        }
    }
}
