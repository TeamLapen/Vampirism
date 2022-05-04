package de.teamlapen.vampirism.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.recipebuilder.*;
import de.teamlapen.vampirism.inventory.recipes.ConfigCondition;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RecipesGenerator extends RecipeProvider {
    public RecipesGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism Recipes";
    }

    @Override
    protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        IItemProvider hopper = Blocks.HOPPER;
        IItemProvider cauldron = Blocks.CAULDRON;
        IItemProvider black_dye = Items.BLACK_DYE;
        IItemProvider stone_bricks = Blocks.STONE_BRICKS;
        IItemProvider vampire_orchid = ModBlocks.vampire_orchid;
        IItemProvider stone = Blocks.STONE;
        IItemProvider castle_block_dark_brick = ModBlocks.castle_block_dark_brick;
        IItemProvider castle_block_dark_stone = ModBlocks.castle_block_dark_stone;
        IItemProvider castle_block_normal_brick = ModBlocks.castle_block_normal_brick;
        IItemProvider castle_block_purple_brick = ModBlocks.castle_block_purple_brick;
        IItemProvider vampire_book = ModItems.vampire_book;
        IItemProvider vampire_fang = ModItems.vampire_fang;
        IItemProvider book = Items.BOOK;
        IItemProvider bread = Items.BREAD;
        IItemProvider injection_empty = ModItems.injection_empty;
        IItemProvider glass_bottle = Items.GLASS_BOTTLE;
        IItemProvider garlic_beacon_core = ModItems.garlic_beacon_core;
        IItemProvider garlic_beacon_core_improved = ModItems.garlic_beacon_core_improved;
        IItemProvider garlic_beacon_normal = ModBlocks.garlic_beacon_normal;
        IItemProvider bucket = Items.BUCKET;
        IItemProvider gun_powder = Items.GUNPOWDER;
        IItemProvider holy_water_bottle_normal = ModItems.holy_water_bottle_normal;
        IItemProvider holy_water_bottle_enhanced = ModItems.holy_water_bottle_enhanced;
        IItemProvider holy_water_bottle_ultimate = ModItems.holy_water_bottle_ultimate;
        IItemProvider leather = Items.LEATHER;
        IItemProvider feather = Items.FEATHER;
        IItemProvider string = Items.STRING;
        IItemProvider black_wool = Items.BLACK_WOOL;
        IItemProvider blue_wool = Items.BLUE_WOOL;
        IItemProvider white_wool = Items.WHITE_WOOL;
        IItemProvider red_wool = Items.RED_WOOL;
        IItemProvider crossbow_arrow_normal = ModItems.crossbow_arrow_normal;
        IItemProvider blood_bottle = ModItems.blood_bottle;
        IItemProvider pure_blood_0 = ModItems.pure_blood_0;
        IItemProvider pure_blood_1 = ModItems.pure_blood_1;
        IItemProvider pure_blood_2 = ModItems.pure_blood_2;
        IItemProvider pure_blood_3 = ModItems.pure_blood_3;
        IItemProvider pure_blood_4 = ModItems.pure_blood_4;
        IItemProvider blood_infused_enhanced_iron_ingot = ModItems.blood_infused_enhanced_iron_ingot;
        IItemProvider blood_infused_iron_ingot = ModItems.blood_infused_iron_ingot;
        IItemProvider rotten_flesh = Items.ROTTEN_FLESH;
        IItemProvider alchemical_fire = ModItems.item_alchemical_fire;
        ITag<Item> planks = ItemTags.PLANKS;
        ITag<Item> glass = Tags.Items.GLASS;
        ITag<Item> glass_pane = Tags.Items.GLASS_PANES;
        ITag<Item> logs = ItemTags.LOGS;
        ITag<Item> diamond = Tags.Items.GEMS_DIAMOND;
        ITag<Item> diamondBlock = Tags.Items.STORAGE_BLOCKS_DIAMOND;
        ITag<Item> iron_ingot = Tags.Items.INGOTS_IRON;
        ITag<Item> quartz_block = Tags.Items.STORAGE_BLOCKS_QUARTZ;
        ITag<Item> coal_block = Tags.Items.STORAGE_BLOCKS_COAL;
        ITag<Item> garlic = ModTags.Items.GARLIC;
        ITag<Item> obsidian = Tags.Items.OBSIDIAN;
        ITag<Item> wool = ItemTags.WOOL;
        ITag<Item> stick = Tags.Items.RODS_WOODEN;
        ITag<Item> iron_block = Tags.Items.STORAGE_BLOCKS_IRON;
        ITag<Item> gold_ingot = Tags.Items.INGOTS_GOLD;
        ITag<Item> pure_blood = ModTags.Items.PURE_BLOOD;
        ITag<Item> holy_water = ModTags.Items.HOLY_WATER;

        ShapedRecipeBuilder.shaped(ModBlocks.blood_grinder).define('Z', hopper).define('Y', planks).define('D', diamond).define('X', iron_ingot).pattern(" Z ").pattern("YDY").pattern("YXY").unlockedBy("has_hopper", has(hopper)).save(consumer, general("blood_grinder"));
        ShapedRecipeBuilder.shaped(ModBlocks.blood_sieve).define('X', iron_ingot).define('Q', quartz_block).define('Y', planks).define('Z', cauldron).pattern("XQX").pattern("YZY").pattern("YXY").unlockedBy("has_cauldron", has(cauldron)).save(consumer, general("blood_sieve"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.castle_block_dark_brick, 8).requires(castle_block_normal_brick, 8).requires(black_dye).unlockedBy("has_castle_brick", has(castle_block_normal_brick)).unlockedBy("has_black_dye", has(black_dye)).save(consumer, modId("general/castle_block_dark_brick_0"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.castle_block_dark_brick, 7).requires(stone_bricks, 7).requires(black_dye).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_dark_brick_1"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.castle_block_dark_stone, 7).requires(stone, 7).requires(black_dye).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_dark_stone"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.castle_block_normal_brick, 8).requires(stone_bricks, 8).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_normal_brick"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.castle_block_purple_brick, 8).requires(castle_block_normal_brick, 8).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_purple_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.castle_slab_dark_brick, 6).pattern("###").define('#', castle_block_dark_brick).unlockedBy("has_castle_brick", has(castle_block_dark_brick)).save(consumer, modId("general/castle_slab_dark_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.castle_slab_dark_stone, 6).pattern("###").define('#', castle_block_dark_stone).unlockedBy("has_castle_brick", has(castle_block_dark_stone)).save(consumer, modId("general/castle_slab_dark_stone"));
        ShapedRecipeBuilder.shaped(ModBlocks.castle_slab_purple_brick, 6).pattern("###").define('#', castle_block_purple_brick).unlockedBy("has_castle_brick", has(castle_block_purple_brick)).save(consumer, modId("general/castle_slab_purple_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.castle_stairs_dark_brick, 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_dark_brick).unlockedBy("has_castle_brick", has(castle_block_dark_brick)).save(consumer, modId("general/castle_stairs_dark_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.castle_stairs_dark_stone, 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_dark_stone).unlockedBy("has_castle_brick", has(castle_block_dark_stone)).save(consumer, general("castle_stairs_dark_stone"));
        ShapedRecipeBuilder.shaped(ModBlocks.castle_stairs_purple_brick, 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_purple_brick).unlockedBy("has_castle_brick", has(castle_block_purple_brick)).save(consumer, general("castle_stairs_purple_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.church_altar).pattern(" X ").pattern("YYY").pattern(" Y ").define('X', vampire_book).define('Y', planks).unlockedBy("has_vampire_book", has(planks)).save(consumer, general("church_altar"));
        ShapedRecipeBuilder.shaped(ModBlocks.church_altar).pattern("XZX").pattern("YYY").pattern(" Y ").define('X', vampire_fang).define('Y', planks).define('Z', book).unlockedBy("has_book", has(book)).save(consumer, general("church_altar_new"));
        ShapedRecipeBuilder.shaped(ModBlocks.fire_place).pattern(" X ").pattern("XYX").define('X', logs).define('Y', coal_block).unlockedBy("has_logs", has(logs)).save(consumer, general("fire_place"));
        ShapelessRecipeBuilder.shapeless(ModItems.garlic_bread).requires(garlic).requires(bread).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bread", has(bread)).save(consumer, general("garlic_bread"));
        ShapedRecipeBuilder.shaped(ModItems.injection_empty).pattern(" X ").pattern(" X ").pattern(" Y ").define('X', glass).define('Y', glass_pane).unlockedBy("has_glass", has(glass)).unlockedBy("has_glass_pane", has(glass_pane)).save(consumer, general("injection_0"));
        ShapelessRecipeBuilder.shapeless(ModItems.injection_garlic).requires(injection_empty).requires(garlic).unlockedBy("has_injection", has(injection_empty)).save(consumer, general("injection_1"));
        ShapelessRecipeBuilder.shapeless(ModItems.injection_sanguinare).requires(injection_empty).requires(vampire_fang, 8).unlockedBy("has_injection", has(injection_empty)).save(consumer, general("injection_2"));
        ShapedRecipeBuilder.shaped(ModBlocks.totem_base).pattern("XYX").pattern("XYX").pattern("ZZZ").define('X', planks).define('Y', obsidian).define('Z', iron_ingot).unlockedBy("has_obsidian", has(obsidian)).save(consumer, general("totem_base"));
        ShapedRecipeBuilder.shaped(ModBlocks.totem_top_crafted).pattern("X X").pattern(" Y ").pattern("XZX").define('X', obsidian).define('Y', diamond).define('Z', vampire_book).unlockedBy("has_diamond", has(diamondBlock)).unlockedBy("has_obsidian", has(obsidian)).save(consumer, general("totem_top"));
        ConditionalRecipe.builder().addCondition(new ConfigCondition("umbrella")).addRecipe((consumer1) -> ShapedRecipeBuilder.shaped(ModItems.umbrella).pattern("###").pattern("BAB").pattern(" A ").define('#', wool).define('A', stick).define('B', vampire_orchid).unlockedBy("has_wool", has(wool)).save(consumer1, general("umbrella"))).build(consumer, general("umbrella"));

        ShapedRecipeBuilder.shaped(ModBlocks.alchemical_cauldron).pattern("XZX").pattern("XXX").pattern("Y Y").define('X', iron_ingot).define('Y', stone_bricks).define('Z', garlic).unlockedBy("has_iron", has(iron_ingot)).save(consumer, hunter("alchemical_cauldron"));
        ShapedRecipeBuilder.shaped(ModBlocks.potion_table).pattern("XXX").pattern("Y Y").pattern("ZZZ").define('X', glass_bottle).define('Y', planks).define('Z', iron_ingot).unlockedBy("has_glass_bottle", has(glass_bottle)).save(consumer, hunter("potion_table"));
        ShapedRecipeBuilder.shaped(ModBlocks.garlic_beacon_normal).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('O', obsidian).define('Z', garlic_beacon_core).unlockedBy("has_diamond", has(diamond)).save(consumer, hunter("garlic_beacon_normal"));
        ShapedRecipeBuilder.shaped(ModBlocks.hunter_table).pattern("XYW").pattern("ZZZ").pattern("Z Z").define('X', vampire_fang).define('Y', book).define('Z', planks).define('W', garlic).unlockedBy("has_fang", has(vampire_fang)).save(consumer, hunter("hunter_table"));
        ShapedRecipeBuilder.shaped(ModBlocks.med_chair).pattern("XYX").pattern("XXX").pattern("XZX").define('X', iron_ingot).define('Y', wool).define('Z', glass_bottle).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("item_med_chair"));
        ShapedRecipeBuilder.shaped(ModBlocks.garlic_beacon_improved).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('Z', garlic_beacon_core_improved).define('O', obsidian).unlockedBy("has_garlic_beacon", has(garlic_beacon_normal)).save(consumer, hunter("garlic_beacon_improved"));
        ShapedRecipeBuilder.shaped(ModItems.stake).pattern("X").pattern("Y").pattern("X").define('X', stick).define('Y', planks).unlockedBy("has_sticks", has(stick)).save(consumer, hunter("stake"));
        ShapedRecipeBuilder.shaped(ModBlocks.weapon_table).pattern("X  ").pattern("YYY").pattern(" Z ").define('X', bucket).define('Y', iron_ingot).define('Z', iron_block).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("weapon_table"));
        ShapedRecipeBuilder.shaped(ModItems.crossbow_arrow_normal, 6).pattern("X").pattern("Y").define('X', iron_ingot).define('Y', stick).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("crossbow_arrow_normal"));
        ShapelessRecipeBuilder.shapeless(ModItems.crossbow_arrow_normal).requires(Items.ARROW).unlockedBy("has_arrow", has(Items.ARROW)).save(consumer, hunter("crossbow_arrow_from_vanilla"));
        ShapelessRecipeBuilder.shapeless(ModItems.pure_blood_0).requires(ModItems.pure_blood_1).requires(ModItems.vampire_blood_bottle).unlockedBy("has_pure_blood", has(pure_blood_1)).save(consumer, hunter("pure_blood0"));
        ShapelessRecipeBuilder.shapeless(ModItems.pure_blood_1).requires(ModItems.pure_blood_2).requires(ModItems.vampire_blood_bottle).unlockedBy("has_pure_blood", has(pure_blood_2)).save(consumer, hunter("pure_blood1"));
        ShapelessRecipeBuilder.shapeless(ModItems.pure_blood_2).requires(ModItems.pure_blood_3).requires(ModItems.vampire_blood_bottle).unlockedBy("has_pure_blood", has(pure_blood_3)).save(consumer, hunter("pure_blood2"));
        ShapelessRecipeBuilder.shapeless(ModItems.pure_blood_3).requires(ModItems.pure_blood_4).requires(ModItems.vampire_blood_bottle).unlockedBy("has_pure_blood", has(pure_blood_4)).save(consumer, hunter("pure_blood3"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.castle_block_dark_brick).unlocks("has_castle_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_block_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.castle_stairs_dark_stone).unlocks("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.castle_stairs_dark_stone).unlocks("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.castle_stairs_dark_brick).unlocks("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.castle_stairs_dark_brick).unlocks("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_purple_brick), ModBlocks.castle_stairs_purple_brick).unlocks("has_stone", has(castle_block_purple_brick)).save(consumer, modId("stonecutting/castle_stairs_purple_brick_from_castle_block_purple_brick"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.castle_slab_dark_stone, 2).unlocks("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.castle_slab_dark_brick, 2).unlocks("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.castle_slab_dark_brick, 2).unlocks("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.castle_slab_dark_stone, 2).unlocks("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_purple_brick), ModBlocks.castle_slab_purple_brick, 2).unlocks("has_stone", has(castle_block_purple_brick)).save(consumer, modId("stonecutting/castle_slaps_purple_brick_from_castle_block_purple_brick"));

        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.pure_salt, 4).withIngredient(garlic).withFluid(new FluidStack(Fluids.WATER, 1)).withSkills(HunterSkills.basic_alchemy).cookTime(1200).build(consumer, modId("pure_salt"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.item_alchemical_fire, 4).withIngredient(gun_powder).withFluid(holy_water_bottle_normal).build(consumer, modId("alchemical_fire_4"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.item_alchemical_fire, 5).withIngredient(gun_powder).withFluid(holy_water_bottle_enhanced).build(consumer, modId("alchemical_fire_5"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.item_alchemical_fire, 6).withIngredient(gun_powder).withFluid(holy_water_bottle_ultimate).build(consumer, modId("alchemical_fire_6"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.garlic_beacon_core).withIngredient(wool).withFluid(garlic).withSkills(HunterSkills.garlic_beacon).build(consumer, modId("garlic_beacon_core"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.garlic_beacon_core_improved).withIngredient(garlic_beacon_core).withFluid(holy_water_bottle_ultimate).withSkills(HunterSkills.garlic_beacon_improved).experience(2.0f).build(consumer, modId("garlic_beacon_core_improved"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.purified_garlic, 2).withIngredient(garlic).withFluid(holy_water).withSkills(HunterSkills.purified_garlic).build(consumer, modId("purified_garlic"));

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_chest_normal).lava(1).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_chest_enhanced).lava(3).skills(HunterSkills.enhanced_armor).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_feet_normal).lava(1).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_feet_enhanced).lava(3).skills(HunterSkills.enhanced_armor).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_head_normal).lava(1).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("    ").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_head_enhanced).lava(3).skills(HunterSkills.enhanced_armor).pattern("XXXX").pattern("XYYX").pattern("XZZX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_legs_normal).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_legs_enhanced).lava(3).skills(HunterSkills.enhanced_armor).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_chest_normal).lava(2).pattern("XWWX").pattern("XZZX").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', leather).define('Z', garlic).define('W', vampire_fang).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_chest_enhanced).lava(5).skills(HunterSkills.enhanced_armor).pattern("XWWX").pattern("XZZX").pattern("XYYX").pattern("XYYX").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).define('W', vampire_fang).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_legs_normal).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', iron_ingot).define('Z', garlic).define('Y', leather).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_legs_enhanced).lava(5).skills(HunterSkills.enhanced_armor).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', iron_ingot).define('Z', garlic).define('Y', diamond).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_head_normal).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', iron_ingot).define('Y', leather).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_head_enhanced).lava(5).skills(HunterSkills.enhanced_armor).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_feet_normal).lava(2).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', leather).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_feet_enhanced).lava(5).skills(HunterSkills.enhanced_armor).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).save(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.basic_crossbow).lava(1).pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', iron_ingot).define('Y', string).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.basic_double_crossbow).lava(1).skills(HunterSkills.double_crossbow).pattern("YXXY").pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', iron_ingot).define('Y', string).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.basic_tech_crossbow).lava(5).skills(HunterSkills.tech_weapons).pattern("YXXY").pattern("XZZX").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).define('Z', diamond).save(consumer);
        ShapelessWeaponTableRecipeBuilder.shapelessWeaponTable(ModItems.crossbow_arrow_spitfire, 3).lava(1).requires(crossbow_arrow_normal, 3).requires(alchemical_fire).unlockedBy("has_crossbow_arrow_normal", has(crossbow_arrow_normal)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.crossbow_arrow_vampire_killer, 3).lava(1).pattern(" X ").pattern("XYX").pattern(" Z ").pattern(" W ").define('X', garlic).define('Y', gold_ingot).define('Z', stick).define('W', feather).unlockedBy("has_crossbow_arrow_normal", has(crossbow_arrow_normal)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.enhanced_crossbow).lava(2).skills(HunterSkills.enhanced_weapons).pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.enhanced_double_crossbow).lava(3).skills(HunterSkills.double_crossbow, HunterSkills.enhanced_weapons).pattern("YXXY").pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.enhanced_tech_crossbow).lava(5).skills(HunterSkills.tech_weapons).pattern("YXXY").pattern("XZZX").pattern("XZZX").pattern(" XX ").define('X', iron_ingot).define('Y', string).define('Z', diamond).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_hat_head_0).pattern(" YY ").pattern(" YY ").pattern("XXXX").define('X', iron_ingot).define('Y', black_wool).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_hat_head_1).lava(1).pattern(" YY ").pattern("XXXX").define('X', iron_ingot).define('Y', black_wool).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.pitchfork).pattern("X X").pattern("YYY").pattern(" Y ").pattern(" Y ").define('X', iron_ingot).define('Y', stick).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.tech_crossbow_ammo_package).lava(1).pattern(" XZ ").pattern("YYYY").pattern("YYYY").pattern("YYYY").define('X', iron_ingot).define('Y', crossbow_arrow_normal).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_axe_normal, 1, enchantment(2, Enchantments.KNOCKBACK)).lava(5).pattern("XXZY").pattern("XXZY").pattern("  ZY").pattern("  Z ").define('X', iron_ingot).define('Y', garlic).define('Z', stick).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_axe_enhanced, 1, enchantment(3, Enchantments.KNOCKBACK)).lava(5).skills(HunterSkills.enhanced_weapons).pattern("XWZY").pattern("XWZY").pattern("  ZY").pattern("  Z ").define('X', iron_ingot).define('Y', garlic).define('W', diamond).define('Z', stick).save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.altar_infusion).pattern("YZY").pattern("ZZZ").define('Y', gold_ingot).define('Z', obsidian).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("altar_infusion"));
        ShapedRecipeBuilder.shaped(ModBlocks.altar_inspiration).pattern(" X ").pattern("XYX").pattern("ZZZ").define('X', glass).define('Y', glass_bottle).define('Z', iron_ingot).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("altar_inspiration"));
        ShapedRecipeBuilder.shaped(ModBlocks.altar_pillar).pattern("X X").pattern("   ").pattern("XXX").define('X', stone_bricks).unlockedBy("has_stones", has(stone_bricks)).save(consumer, vampire("altar_pillar"));
        ShapedRecipeBuilder.shaped(ModBlocks.altar_tip).pattern(" X ").pattern("XYX").define('X', iron_ingot).define('Y', iron_block).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("altar_tip"));
        ShapelessRecipeBuilder.shapeless(Items.GLASS_BOTTLE).requires(blood_bottle).unlockedBy("has_blood_bottle", has(blood_bottle)).save(consumer, vampire("blood_bottle_to_glass"));
        ShapedRecipeBuilder.shaped(ModBlocks.blood_container).pattern("XYX").pattern("YZY").pattern("XYX").define('X', planks).define('Y', glass).define('Z', iron_ingot).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_container"));
        new Shapeless(ModItems.blood_infused_enhanced_iron_ingot, 3).addIngredient(iron_ingot, 3).requires(pure_blood_4).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_infused_enhanced_iron_ingot"));
        new Shapeless(ModItems.blood_infused_iron_ingot, 3).addIngredient(iron_ingot, 3).requires(Ingredient.of(pure_blood_0, pure_blood_1, pure_blood_2, pure_blood_3)).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_infused_iron_ingot"));
        ShapedRecipeBuilder.shaped(ModBlocks.blood_pedestal).pattern("GYG").pattern("YZY").pattern("XXX").define('X', obsidian).define('Y', planks).define('Z', blood_bottle).define('G', gold_ingot).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("blood_pedestal"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', wool).unlockedBy("has_wool", has(wool)).save(consumer, vampire("coffin"));
        ShapedRecipeBuilder.shaped(ModItems.heart_seeker_enhanced).pattern("X").pattern("X").pattern("Y").define('X', blood_infused_enhanced_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_enhanced_iron_ingot)).save(consumer, vampire("heart_seeker_enhanced"));
        ShapedRecipeBuilder.shaped(ModItems.heart_striker_enhanced).pattern("XX").pattern("XX").pattern("YY").define('X', blood_infused_enhanced_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_enhanced_iron_ingot)).save(consumer, vampire("heart_striker_enhanced"));
        ShapedRecipeBuilder.shaped(ModItems.heart_seeker_normal).pattern("X").pattern("X").pattern("Y").define('X', blood_infused_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_iron_ingot)).save(consumer, vampire("heart_seeker_normal"));
        ShapedRecipeBuilder.shaped(ModItems.heart_striker_normal).pattern("XX").pattern("XX").pattern("YY").define('X', blood_infused_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_iron_ingot)).save(consumer, vampire("heart_striker_normal"));

        ShapedRecipeBuilder.shaped(ModItems.vampire_cloak_black_blue).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', blue_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_blue"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_cloak_black_red).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', red_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_red"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_cloak_black_white).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', white_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_white"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_cloak_white_black).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', white_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_white_black"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_cloak_red_black).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', red_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_red_black"));
        ItemStack blood_bottle_stack = new ItemStack(ModItems.blood_bottle);
        blood_bottle_stack.setDamageValue(0);
        ConditionalRecipe.builder().addCondition(new NotCondition(new ConfigCondition("auto_convert"))).addRecipe((consumer1 -> new Shaped(blood_bottle_stack).pattern("XYX").pattern(" X ").define('X', glass).define('Y', rotten_flesh).unlockedBy("has_glass", has(glass)).save(consumer1, vampire("blood_bottle")))).build(consumer, vampire("blood_bottle"));

        new IItemWIthTierRecipeBuilder(ModItems.heart_seeker_normal, 1).pattern(" X ").pattern("XYX").define('X', blood_infused_iron_ingot).define('Y', ModItems.heart_seeker_normal).unlockedBy("has_heart_seeker", has(ModItems.heart_seeker_normal)).save(consumer, vampire("heart_seeker_normal_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.heart_striker_normal, 1).pattern("XXX").pattern("XYX").define('X', blood_infused_iron_ingot).define('Y', ModItems.heart_striker_normal).unlockedBy("has_heart_striker", has(ModItems.heart_striker_normal)).save(consumer, vampire("heart_striker_normal_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.heart_seeker_enhanced, 1).pattern(" X ").pattern("XYX").define('X', blood_infused_enhanced_iron_ingot).define('Y', ModItems.heart_seeker_enhanced).unlockedBy("has_heart_seeker", has(ModItems.heart_seeker_enhanced)).save(consumer, vampire("heart_seeker_enhanced_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.heart_striker_enhanced, 1).pattern("XXX").pattern("XYX").define('X', blood_infused_enhanced_iron_ingot).define('Y', ModItems.heart_striker_enhanced).unlockedBy("has_heart_striker", has(ModItems.heart_striker_enhanced)).save(consumer, vampire("heart_striker_enhanced_repair"));

        //noinspection ConstantConditions
        ConditionalRecipe.builder().addCondition(new ModLoadedCondition("guideapi-vp")).addRecipe((consumer1 -> ShapelessRecipeBuilder.shapeless(ForgeRegistries.ITEMS.getValue(new ResourceLocation("guideapi-vp", "vampirism-guidebook"))).requires(vampire_fang).requires(book).unlockedBy("has_fang", has(vampire_fang)).save(consumer1, modId("general/guidebook")))).build(consumer, modId("general/guidebook"));

        ShapedRecipeBuilder.shaped(ModItems.cure_apple).pattern("YXY").pattern("YZY").pattern("YYY").define('Y', Items.GOLD_NUGGET).define('Z', Items.APPLE).define('X', ModItems.injection_zombie_blood).unlockedBy("has_apple", has(Items.APPLE)).unlockedBy("has_zombie_blood", has(ModItems.injection_zombie_blood)).save(consumer, general("cure_item"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_clothing_legs).pattern("XXX").pattern("X X").pattern("XYX").define('X', Items.GRAY_WOOL).define('Y', Ingredient.of(ModItems.human_heart, ModItems.weak_human_heart)).unlockedBy("has_heart", has(ModItems.human_heart)).unlockedBy("has_wool", has(Items.GRAY_WOOL)).save(consumer, vampire("vampire_clothing_legs"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_clothing_boots).pattern("XYX").pattern("X X").define('X', Items.BROWN_WOOL).define('Y', Ingredient.of(ModItems.human_heart, ModItems.weak_human_heart)).unlockedBy("has_heart", has(ModItems.human_heart)).unlockedBy("has_wool", has(Items.BROWN_WOOL)).save(consumer, vampire("vampire_clothing_boots"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_clothing_hat).pattern("ZXX").pattern(" Y ").pattern("XXX").define('X', Items.BLACK_WOOL).define('Y', Items.RED_WOOL).define('Z', Ingredient.of(ModItems.human_heart, ModItems.weak_human_heart)).unlockedBy("has_heart", has(ModItems.human_heart)).unlockedBy("has_wool", has(Items.BLACK_WOOL)).save(consumer, vampire("vampire_clothing_hat"));
        ShapedRecipeBuilder.shaped(ModItems.vampire_clothing_crown).pattern("XYX").pattern("XXX").define('X', Items.GOLD_INGOT).define('Y', Ingredient.of(ModItems.human_heart, ModItems.weak_human_heart)).unlockedBy("has_heart", has(ModItems.human_heart)).unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(consumer, vampire("vampire_clothing_crown"));

        ShapedRecipeBuilder.shaped(ModBlocks.cross).pattern(" X ").pattern("XYX").pattern(" X ").define('X', planks).define('Y', holy_water).unlockedBy("has_planks", has(planks)).unlockedBy("has_holy", has(holy_water)).save(consumer, hunter("cross"));
        ShapedRecipeBuilder.shaped(ModItems.item_candelabra).pattern("XXX").pattern("YYY").pattern("ZAZ").define('X', string).define('Y', Items.HONEYCOMB).define('Z', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_string", has(string)).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("candelabra"));
        ShapedRecipeBuilder.shaped(ModBlocks.chandelier).pattern("XYX").pattern("ZYZ").pattern("BAB").define('X', string).define('Y', ModItems.item_candelabra).define('Z', Items.HONEYCOMB).define('B', iron_ingot).define('A', gold_ingot).unlockedBy("has_string", has(string)).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_candelabra", has(ModItems.item_candelabra)).save(consumer, vampire("chandelier"));
        ShapedRecipeBuilder.shaped(ModItems.garlic_finder).pattern("XXX").pattern("XYX").pattern("ZAZ").define('X', blood_infused_iron_ingot).define('Y', garlic).define('Z', planks).define('A', Tags.Items.DUSTS_REDSTONE).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bloodiron", has(blood_infused_iron_ingot)).unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE)).save(consumer, vampire("garlic_finder"));
        ShapedRecipeBuilder.shaped(ModBlocks.tombstone2).pattern("XX ").pattern("XYX").pattern("XXX").define('X', Blocks.COBBLESTONE).define('Y', Tags.Items.STONE).unlockedBy("has_coble", has(Blocks.COBBLESTONE)).unlockedBy("has_stone", has(Tags.Items.STONE)).save(consumer, general("tombstone2"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.tombstone1).requires(ModBlocks.tombstone2).unlockedBy("has_tomb", has(ModBlocks.tombstone2)).save(consumer, general("tombstone1"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.tombstone3).requires(ModBlocks.tombstone2).requires(Blocks.COBBLESTONE).unlockedBy("has_tomb", has(ModBlocks.tombstone2)).save(consumer, general("tombstone3"));
        ShapedRecipeBuilder.shaped(ModBlocks.grave_cage).pattern(" X ").pattern("XYX").pattern("XYX").define('X', iron_ingot).define('Y', Items.COAL).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_coal", has(Items.COAL)).save(consumer, general("grave_cage"));

        planksFromLog(consumer,ModBlocks.dark_spruce_planks, ModTags.Items.DARK_SPRUCE_LOG);
        planksFromLog(consumer,ModBlocks.cursed_spruce_planks, ModTags.Items.CURSED_SPRUCE_LOG);
        woodFromLogs(consumer,ModBlocks.dark_spruce_wood, ModBlocks.dark_spruce_log);
        woodFromLogs(consumer,ModBlocks.cursed_spruce_wood, ModBlocks.cursed_spruce_log);
        woodFromLogs(consumer,ModBlocks.stripped_dark_spruce_wood, ModBlocks.stripped_dark_spruce_log);
        woodFromLogs(consumer,ModBlocks.stripped_cursed_spruce_wood, ModBlocks.stripped_cursed_spruce_log);
        woodenButton(consumer, ModBlocks.dark_spruce_button, ModBlocks.dark_spruce_planks);
        woodenButton(consumer, ModBlocks.cursed_spruce_button, ModBlocks.cursed_spruce_planks);
        woodenDoor(consumer, ModBlocks.dark_spruce_door, ModBlocks.dark_spruce_planks);
        woodenDoor(consumer, ModBlocks.cursed_spruce_door, ModBlocks.cursed_spruce_planks);
        woodenFence(consumer, ModBlocks.dark_spruce_fence, ModBlocks.dark_spruce_planks);
        woodenFence(consumer, ModBlocks.cursed_spruce_fence, ModBlocks.cursed_spruce_planks);
        woodenFenceGate(consumer, ModBlocks.dark_spruce_fence_gate, ModBlocks.dark_spruce_planks);
        woodenFenceGate(consumer, ModBlocks.cursed_spruce_fence_gate, ModBlocks.cursed_spruce_planks);
        woodenPressurePlate(consumer, ModBlocks.dark_spruce_pressure_place, ModBlocks.dark_spruce_planks);
        woodenPressurePlate(consumer, ModBlocks.cursed_spruce_pressure_place, ModBlocks.cursed_spruce_planks);
        woodenSlab(consumer, ModBlocks.dark_spruce_slab, ModBlocks.dark_spruce_planks);
        woodenSlab(consumer, ModBlocks.cursed_spruce_slab, ModBlocks.cursed_spruce_planks);
        woodenStairs(consumer, ModBlocks.dark_spruce_stairs, ModBlocks.dark_spruce_planks);
        woodenStairs(consumer, ModBlocks.cursed_spruce_stairs, ModBlocks.cursed_spruce_planks);
        woodenTrapdoor(consumer, ModBlocks.dark_spruce_trapdoor, ModBlocks.dark_spruce_planks);
        woodenTrapdoor(consumer, ModBlocks.cursed_spruce_trapdoor, ModBlocks.cursed_spruce_planks);
        woodenSign(consumer, ModItems.dark_spruce_sign, ModBlocks.dark_spruce_planks);
        woodenSign(consumer, ModItems.cursed_spruce_sign, ModBlocks.cursed_spruce_planks);
        ShapedRecipeBuilder.shaped(ModBlocks.throne).pattern(" YZ").pattern("YYZ").pattern("XZX").define('Y', Blocks.RED_CARPET).define('Z', ItemTags.PLANKS).define('X', Items.STICK).unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wool", has(Blocks.RED_CARPET)).save(consumer, general("throne"));
        ShapedRecipeBuilder.shaped(ModBlocks.vampire_rack).pattern("XYX").pattern("ABC").pattern("XYX").define('X', ItemTags.PLANKS).define('Y', Items.BOOK).define('A', ModItems.vampire_fang).define('B', Items.GLASS_BOTTLE).define('C', Items.HONEYCOMB).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_book", has(Items.BOOK)).unlockedBy("has_fangs", has(ModItems.vampire_fang)).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_potion", has(Items.GLASS_BOTTLE)).save(consumer, general("vampire_rack"));

        ShapedRecipeBuilder.shaped(ModItems.crucifix_normal).pattern("XY ").pattern("ZYZ").pattern(" Y ").define('X', ModItems.holy_salt).define('Y', planks).define('Z', stick).unlockedBy("salt", has(ModItems.holy_salt)).unlockedBy("stick", has(stick)).unlockedBy("planks", has(planks)).save(consumer, hunter("crucifix"));
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.crucifix_enhanced).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.holy_salt).define('Y', iron_ingot).define('Z', ModItems.holy_water_bottle_normal).define('A', ModItems.stake).unlockedBy("iron", has(iron_ingot)).unlockedBy("blessed_salt", has(ModItems.holy_salt)).unlockedBy("holy_water", has(ModItems.holy_water_bottle_normal)).unlockedBy("stake", has(ModItems.stake)).skills(HunterSkills.crucifix_wielder).save(consumer, hunter("crucifix_enhanced"));
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.crucifix_ultimate).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.item_alchemical_fire).define('Y', Tags.Items.STORAGE_BLOCKS_GOLD).define('Z', ModItems.holy_water_bottle_enhanced).define('A', ModItems.stake).unlockedBy("fire", has(ModItems.item_alchemical_fire)).unlockedBy("gold", has(Tags.Items.STORAGE_BLOCKS_GOLD)).unlockedBy("holy_water", has(ModItems.holy_water_bottle_enhanced)).unlockedBy("stake", has(ModItems.stake)).skills(HunterSkills.ultimate_crucifix).save(consumer, hunter("crucifix_ultimate"));

        CustomRecipeBuilder.special(ModRecipes.applicable_oil).save(consumer, REFERENCE.MODID+":applicable_oil");
        AlchemyTableRecipeBuilder.builder(OilUtils.createOilItem(ModOils.plant_oil)).ingredient(Ingredient.of(new ItemStack(Items.GLASS_BOTTLE))).input(Ingredient.of(new ItemStack(Items.WHEAT_SEEDS))).withCriterion("has_bottles", has(Items.GLASS_BOTTLE)).withCriterion("has_wheat_seeds", has(Items.WHEAT_SEEDS)).build(consumer, new ResourceLocation(REFERENCE.MODID, "plant_oil"));
    }

    private JsonObject enchantment(int level, Enchantment enchantment) {
        JsonObject nbt = new JsonObject();
        JsonArray enchantmentarray = new JsonArray();
        JsonObject enchantment1 = new JsonObject();
        enchantment1.addProperty("lvl", level);
        enchantment1.addProperty("id", Helper.getIDSafe(enchantment).toString());
        enchantmentarray.add(enchantment1);
        nbt.add("Enchantments", enchantmentarray);
        return nbt;
    }

    private ResourceLocation general(String path) {
        return modId("general/" + path);
    }

    private ResourceLocation hunter(String path) {
        return modId("hunter/" + path);
    }

    private ResourceLocation modId(String path) {
        return new ResourceLocation(REFERENCE.MODID, path);
    }

    private Ingredient potion(Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION, 1);
        PotionUtils.setPotion(stack, potion);
        return new NBTIngredient(stack);
    }

    private ResourceLocation vampire(String path) {
        return modId("vampire/" + path);
    }

    private static class NBTIngredient extends net.minecraftforge.common.crafting.NBTIngredient {
        public NBTIngredient(ItemStack stack) {
            super(stack);
        }
    }

    private static class Shapeless extends ShapelessRecipeBuilder {
        public Shapeless(IItemProvider itemProvider, int amount) {
            super(itemProvider, amount);
        }

        public ShapelessRecipeBuilder addIngredient(ITag<Item> tag, int amount) {
            return this.requires(Ingredient.of(tag), amount);
        }
    }

    private static class Shaped extends ShapedRecipeBuilder {
        private final ItemStack stack;

        public Shaped(ItemStack resultIn) {
            super(resultIn.getItem(), resultIn.getCount());
            this.stack = resultIn;
        }

        @Override
        public void save(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
            this.ensureValid(id);
            this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
            consumerIn.accept(new Result(id, this.count, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath()), this.stack));

        }

        private class Result extends ShapedRecipeBuilder.Result {
            private final ItemStack stack;

            public Result(ResourceLocation idIn, int countIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, ItemStack stack) {
                super(idIn, stack.getItem(), countIn, groupIn, patternIn, keyIn, advancementBuilderIn, advancementIdIn);
                this.stack = stack;
            }

            @Override
            public void serializeRecipeData(JsonObject json) {
                super.serializeRecipeData(json);
                JsonObject result = json.get("result").getAsJsonObject();
                result.entrySet().clear();
                result.addProperty("item", Helper.getIDSafe(this.stack.getItem()).toString());
                result.addProperty("count", this.stack.getCount());
                if (stack.hasTag())
                    result.addProperty("nbt", this.stack.getTag().toString());
            }
        }
    }
}
