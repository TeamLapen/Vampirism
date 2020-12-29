package de.teamlapen.vampirism.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.data.recipebuilder.AlchemicalCauldronRecipeBuilder;
import de.teamlapen.vampirism.data.recipebuilder.IItemWIthTierRecipeBuilder;
import de.teamlapen.vampirism.data.recipebuilder.ShapedWeaponTableRecipeBuilder;
import de.teamlapen.vampirism.data.recipebuilder.ShapelessWeaponTableRecipeBuilder;
import de.teamlapen.vampirism.inventory.recipes.ConfigCondition;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
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

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
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

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_grinder).key('Z', hopper).key('Y', planks).key('D', diamond).key('X', iron_ingot).patternLine(" Z ").patternLine("YDY").patternLine("YXY").addCriterion("has_hopper", hasItem(hopper)).build(consumer, general("blood_grinder"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_sieve).key('X', iron_ingot).key('Q', quartz_block).key('Y', planks).key('Z', cauldron).patternLine("XQX").patternLine("YZY").patternLine("YXY").addCriterion("has_cauldron", hasItem(cauldron)).build(consumer, general("blood_sieve"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_brick, 8).addIngredient(castle_block_normal_brick, 8).addIngredient(black_dye).addCriterion("has_castle_brick", hasItem(castle_block_normal_brick)).addCriterion("has_black_dye", hasItem(black_dye)).build(consumer, modId("general/castle_block_dark_brick_0"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_brick, 7).addIngredient(stone_bricks, 7).addIngredient(black_dye).addIngredient(vampire_orchid).addCriterion("has_orchid", hasItem(vampire_orchid)).build(consumer, general("castle_block_dark_brick_1"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_stone, 7).addIngredient(stone, 7).addIngredient(black_dye).addIngredient(vampire_orchid).addCriterion("has_orchid", hasItem(vampire_orchid)).build(consumer, general("castle_block_dark_stone"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_normal_brick, 8).addIngredient(stone_bricks, 8).addIngredient(vampire_orchid).addCriterion("has_orchid", hasItem(vampire_orchid)).build(consumer, general("castle_block_normal_brick"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_purple_brick, 8).addIngredient(castle_block_normal_brick, 8).addIngredient(vampire_orchid).addCriterion("has_orchid", hasItem(vampire_orchid)).build(consumer, general("castle_block_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_dark_brick, 6).patternLine("###").key('#', castle_block_dark_brick).addCriterion("has_castle_brick", hasItem(castle_block_dark_brick)).build(consumer, modId("general/castle_slab_dark_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_dark_stone, 6).patternLine("###").key('#', castle_block_dark_stone).addCriterion("has_castle_brick", hasItem(castle_block_dark_stone)).build(consumer, modId("general/castle_slab_dark_stone"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_purple_brick, 6).patternLine("###").key('#', castle_block_purple_brick).addCriterion("has_castle_brick", hasItem(castle_block_purple_brick)).build(consumer, modId("general/castle_slab_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_stairs_dark_brick, 4).patternLine("#  ").patternLine("## ").patternLine("###").key('#', castle_block_dark_brick).addCriterion("has_castle_brick", hasItem(castle_block_dark_brick)).build(consumer, modId("general/castle_stairs_dark_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_stairs_dark_stone, 4).patternLine("#  ").patternLine("## ").patternLine("###").key('#', castle_block_dark_stone).addCriterion("has_castle_brick", hasItem(castle_block_dark_stone)).build(consumer, general("castle_stairs_dark_stone"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_stairs_purple_brick, 4).patternLine("#  ").patternLine("## ").patternLine("###").key('#', castle_block_purple_brick).addCriterion("has_castle_brick", hasItem(castle_block_purple_brick)).build(consumer, general("castle_stairs_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.church_altar).patternLine(" X ").patternLine("YYY").patternLine(" Y ").key('X', vampire_book).key('Y', planks).addCriterion("has_vampire_book", hasItem(planks)).build(consumer, general("church_altar"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.church_altar).patternLine("XZX").patternLine("YYY").patternLine(" Y ").key('X', vampire_fang).key('Y', planks).key('Z', book).addCriterion("has_book", hasItem(book)).build(consumer, general("church_altar_new"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.fire_place).patternLine(" X ").patternLine("XYX").key('X', logs).key('Y', coal_block).addCriterion("has_logs", hasItem(logs)).build(consumer, general("fire_place"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.garlic_bread).addIngredient(garlic).addIngredient(bread).addCriterion("has_garlic", hasItem(garlic)).addCriterion("has_bread", hasItem(bread)).build(consumer, general("garlic_bread"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.injection_empty).patternLine(" X ").patternLine(" X ").patternLine(" Y ").key('X', glass).key('Y', glass_pane).addCriterion("has_glass", hasItem(glass)).addCriterion("has_glass_pane", hasItem(glass_pane)).build(consumer, general("injection_0"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.injection_garlic).addIngredient(injection_empty).addIngredient(garlic).addCriterion("has_injection", hasItem(injection_empty)).build(consumer, general("injection_1"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.injection_sanguinare).addIngredient(injection_empty).addIngredient(vampire_fang, 8).addCriterion("has_injection", hasItem(injection_empty)).build(consumer, general("injection_2"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.totem_base).patternLine("XYX").patternLine("XYX").patternLine("ZZZ").key('X', planks).key('Y', obsidian).key('Z', iron_ingot).addCriterion("has_obsidian", hasItem(obsidian)).build(consumer, general("totem_base"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.totem_top_crafted).patternLine("X X").patternLine(" Y ").patternLine("XZX").key('X', obsidian).key('Y', diamond).key('Z', vampire_book).addCriterion("has_diamond", hasItem(diamondBlock)).addCriterion("has_obsidian", hasItem(obsidian)).build(consumer, general("totem_top"));
        ConditionalRecipe.builder().addCondition(new ConfigCondition("umbrella")).addRecipe((consumer1) -> ShapedRecipeBuilder.shapedRecipe(ModItems.umbrella).patternLine("###").patternLine("BAB").patternLine(" A ").key('#', wool).key('A', stick).key('B', vampire_orchid).addCriterion("has_wool", hasItem(wool)).build(consumer1, general("umbrella"))).build(consumer, general("umbrella"));

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.alchemical_cauldron).patternLine("XZX").patternLine("XXX").patternLine("Y Y").key('X', iron_ingot).key('Y', stone_bricks).key('Z', garlic).addCriterion("has_iron", hasItem(iron_ingot)).build(consumer, hunter("alchemical_cauldron"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.potion_table).patternLine("XXX").patternLine("Y Y").patternLine("ZZZ").key('X', glass_bottle).key('Y', planks).key('Z', iron_ingot).addCriterion("has_glass_bottle", hasItem(glass_bottle)).build(consumer, hunter("potion_table"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.garlic_beacon_normal).patternLine("XYX").patternLine("YZY").patternLine("OOO").key('X', planks).key('Y', diamond).key('O', obsidian).key('Z', garlic_beacon_core).addCriterion("has_diamond", hasItem(diamond)).build(consumer, hunter("garlic_beacon_normal"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.hunter_table).patternLine("XYW").patternLine("ZZZ").patternLine("Z Z").key('X', vampire_fang).key('Y', book).key('Z', planks).key('W', garlic).addCriterion("has_fang", hasItem(vampire_fang)).build(consumer, hunter("hunter_table"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.item_med_chair).patternLine("XYX").patternLine("XXX").patternLine("XZX").key('X', iron_ingot).key('Y', wool).key('Z', glass_bottle).addCriterion("has_iron_ingot", hasItem(iron_ingot)).build(consumer, hunter("item_med_chair"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.garlic_beacon_improved).patternLine("XYX").patternLine("YZY").patternLine("OOO").key('X', planks).key('Y', diamond).key('Z', garlic_beacon_core_improved).key('O', obsidian).addCriterion("has_garlic_beacon", hasItem(garlic_beacon_normal)).build(consumer, hunter("garlic_beacon_improved"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.stake).patternLine("X").patternLine("Y").patternLine("X").key('X', stick).key('Y', planks).addCriterion("has_sticks", hasItem(stick)).build(consumer, hunter("stake"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.weapon_table).patternLine("X  ").patternLine("YYY").patternLine(" Z ").key('X', bucket).key('Y', iron_ingot).key('Z', iron_block).addCriterion("has_iron_ingot", hasItem(iron_ingot)).build(consumer, hunter("weapon_table"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.crossbow_arrow_normal, 6).patternLine("X").patternLine("Y").key('X', iron_ingot).key('Y', stick).addCriterion("has_iron_ingot", hasItem(iron_ingot)).build(consumer, hunter("crossbow_arrow_normal"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.crossbow_arrow_normal).addIngredient(Items.ARROW).addCriterion("has_arrow", hasItem(Items.ARROW)).build(consumer, hunter("crossbow_arrow_from_vanilla"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.pure_blood_0).addIngredient(ModItems.pure_blood_1).addIngredient(ModItems.vampire_blood_bottle).addCriterion("has_pure_blood", hasItem(pure_blood_1)).build(consumer, hunter("pure_blood0"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.pure_blood_1).addIngredient(ModItems.pure_blood_2).addIngredient(ModItems.vampire_blood_bottle).addCriterion("has_pure_blood", hasItem(pure_blood_2)).build(consumer, hunter("pure_blood1"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.pure_blood_2).addIngredient(ModItems.pure_blood_3).addIngredient(ModItems.vampire_blood_bottle).addCriterion("has_pure_blood", hasItem(pure_blood_3)).build(consumer, hunter("pure_blood2"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.pure_blood_3).addIngredient(ModItems.pure_blood_4).addIngredient(ModItems.vampire_blood_bottle).addCriterion("has_pure_blood", hasItem(pure_blood_4)).build(consumer, hunter("pure_blood3"));

        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone), ModBlocks.castle_block_dark_brick).addCriterion("has_castle_stone", hasItem(castle_block_dark_stone)).build(consumer, modId("stonecutting/castle_block_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone), ModBlocks.castle_stairs_dark_stone).addCriterion("has_stone", hasItem(castle_block_dark_stone)).build(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick), ModBlocks.castle_stairs_dark_stone).addCriterion("has_stone", hasItem(castle_block_dark_brick)).build(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick), ModBlocks.castle_stairs_dark_brick).addCriterion("has_stone", hasItem(castle_block_dark_brick)).build(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone), ModBlocks.castle_stairs_dark_brick).addCriterion("has_stone", hasItem(castle_block_dark_stone)).build(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_purple_brick), ModBlocks.castle_stairs_purple_brick).addCriterion("has_stone", hasItem(castle_block_purple_brick)).build(consumer, modId("stonecutting/castle_stairs_purple_brick_from_castle_block_purple_brick"));

        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone), ModBlocks.castle_slab_dark_stone, 2).addCriterion("has_stone", hasItem(castle_block_dark_stone)).build(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone), ModBlocks.castle_slab_dark_brick, 2).addCriterion("has_stone", hasItem(castle_block_dark_stone)).build(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick), ModBlocks.castle_slab_dark_brick, 2).addCriterion("has_stone", hasItem(castle_block_dark_brick)).build(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick), ModBlocks.castle_slab_dark_stone, 2).addCriterion("has_stone", hasItem(castle_block_dark_brick)).build(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_purple_brick), ModBlocks.castle_slab_purple_brick, 2).addCriterion("has_stone", hasItem(castle_block_purple_brick)).build(consumer, modId("stonecutting/castle_slaps_purple_brick_from_castle_block_purple_brick"));

        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.pure_salt, 4).withIngredient(garlic).withFluid(new FluidStack(Fluids.WATER, 1)).withSkills(HunterSkills.basic_alchemy).cookTime(1200).build(consumer, modId("pure_salt"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.item_alchemical_fire, 4).withIngredient(gun_powder).withFluid(holy_water_bottle_normal).build(consumer, modId("alchemical_fire_4"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.item_alchemical_fire, 5).withIngredient(gun_powder).withFluid(holy_water_bottle_enhanced).build(consumer, modId("alchemical_fire_5"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.item_alchemical_fire, 6).withIngredient(gun_powder).withFluid(holy_water_bottle_ultimate).build(consumer, modId("alchemical_fire_6"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.garlic_beacon_core).withIngredient(wool).withFluid(garlic).withSkills(HunterSkills.garlic_beacon).build(consumer, modId("garlic_beacon_core"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.garlic_beacon_core_improved).withIngredient(garlic_beacon_core).withFluid(holy_water_bottle_ultimate).withSkills(HunterSkills.garlic_beacon_improved).experience(2.0f).build(consumer, modId("garlic_beacon_core_improved"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.purified_garlic, 2).withIngredient(garlic).withFluid(holy_water).withSkills(HunterSkills.purified_garlic).build(consumer, modId("purified_garlic"));

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_chest_normal).lava(1).patternLine("XZZX").patternLine("XXXX").patternLine("XYYX").patternLine("XXXX").key('X', leather).key('Y', garlic).key('Z', potion(Potions.SWIFTNESS)).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_chest_enhanced).lava(3).skills(HunterSkills.enhanced_armor).patternLine("XZZX").patternLine("XXXX").patternLine("XYYX").patternLine("XXXX").key('X', leather).key('Y', garlic).key('Z', gold_ingot).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_feet_normal).lava(1).patternLine("XZZX").patternLine("XYYX").patternLine("XXXX").key('X', leather).key('Y', garlic).key('Z', potion(Potions.SWIFTNESS)).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_feet_enhanced).lava(3).skills(HunterSkills.enhanced_armor).patternLine("XZZX").patternLine("XYYX").patternLine("XXXX").key('X', leather).key('Y', garlic).key('Z', gold_ingot).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_head_normal).lava(1).patternLine("XXXX").patternLine("XYYX").patternLine("XZZX").patternLine("    ").key('X', leather).key('Y', garlic).key('Z', potion(Potions.SWIFTNESS)).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_head_enhanced).lava(3).skills(HunterSkills.enhanced_armor).patternLine("XXXX").patternLine("XYYX").patternLine("XZZX").key('X', leather).key('Y', garlic).key('Z', gold_ingot).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_legs_normal).patternLine("XXXX").patternLine("XYYX").patternLine("XZZX").patternLine("X  X").key('X', leather).key('Y', garlic).key('Z', potion(Potions.SWIFTNESS)).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.armor_of_swiftness_legs_enhanced).lava(3).skills(HunterSkills.enhanced_armor).patternLine("XXXX").patternLine("XYYX").patternLine("XZZX").patternLine("X  X").key('X', leather).key('Y', garlic).key('Z', gold_ingot).build(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_chest_normal).lava(2).patternLine("XWWX").patternLine("XZZX").patternLine("XZZX").patternLine("XYYX").key('X', iron_ingot).key('Y', leather).key('Z', garlic).key('W', vampire_fang).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_chest_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("XWWX").patternLine("XZZX").patternLine("XYYX").patternLine("XYYX").key('X', iron_ingot).key('Y', diamond).key('Z', garlic).key('W', vampire_fang).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_legs_normal).lava(2).patternLine("XYYX").patternLine("XZZX").patternLine("XZZX").patternLine("X  X").key('X', iron_ingot).key('Z', garlic).key('Y', leather).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_legs_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("XYYX").patternLine("XZZX").patternLine("XZZX").patternLine("X  X").key('X', iron_ingot).key('Z', garlic).key('Y', diamond).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_head_normal).lava(2).patternLine("XYYX").patternLine("XZZX").patternLine("XZZX").patternLine("    ").key('X', iron_ingot).key('Y', leather).key('Z', garlic).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_head_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("XYYX").patternLine("XZZX").patternLine("XZZX").patternLine("    ").key('X', iron_ingot).key('Y', diamond).key('Z', garlic).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_feet_normal).lava(2).patternLine("    ").patternLine("X  X").patternLine("XZZX").patternLine("XYYX").key('X', iron_ingot).key('Y', leather).key('Z', garlic).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_coat_feet_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("    ").patternLine("X  X").patternLine("XZZX").patternLine("XYYX").key('X', iron_ingot).key('Y', diamond).key('Z', garlic).build(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_chest_normal).lava(5).patternLine("ZXXZ").patternLine("XYYX").patternLine("XYYX").patternLine("XYYX").key('X', iron_ingot).key('Y', obsidian).key('Z', leather).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_chest_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("ZXXZ").patternLine("DYYD").patternLine("XYYX").patternLine("DYYD").key('X', iron_ingot).key('Y', obsidian).key('Z', leather).key('D', diamond).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_feet_normal).lava(5).patternLine("    ").patternLine("X  X").patternLine("XYYX").patternLine("XYYX").key('X', iron_ingot).key('Y', obsidian).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_feet_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("    ").patternLine("XYYX").patternLine("XYYX").patternLine("XDDX").key('X', iron_ingot).key('Y', obsidian).key('D', diamond).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_head_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("XDDX").patternLine("XYYX").patternLine("XYYX").patternLine("    ").key('X', iron_ingot).key('Y', obsidian).key('D', diamond).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_head_normal).lava(5).patternLine("XXXX").patternLine("XYYX").patternLine("XYYX").patternLine("    ").key('X', iron_ingot).key('Y', obsidian).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_legs_enhanced).lava(5).skills(HunterSkills.enhanced_armor).patternLine("XDDX").patternLine("XYYX").patternLine("XYYX").patternLine("XYYX").key('X', iron_ingot).key('Y', obsidian).key('D', diamond).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.obsidian_armor_legs_normal).lava(5).patternLine("XXXX").patternLine("XYYX").patternLine("XYYX").patternLine("XYYX").key('X', iron_ingot).key('Y', obsidian).build(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.basic_crossbow).lava(1).patternLine("YXXY").patternLine(" ZZ ").patternLine(" ZZ ").key('X', iron_ingot).key('Y', string).key('Z', planks).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.basic_double_crossbow).lava(1).skills(HunterSkills.double_crossbow).patternLine("YXXY").patternLine("YXXY").patternLine(" ZZ ").patternLine(" ZZ ").key('X', iron_ingot).key('Y', string).key('Z', planks).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.basic_tech_crossbow).lava(5).skills(HunterSkills.tech_weapons).patternLine("YXXY").patternLine("XZZX").patternLine(" XX ").patternLine(" XX ").key('X', iron_ingot).key('Y', string).key('Z', diamond).build(consumer);
        ShapelessWeaponTableRecipeBuilder.shapelessWeaponTable(ModItems.crossbow_arrow_spitfire, 3).lava(1).addIngredient(crossbow_arrow_normal, 3).addIngredient(alchemical_fire).addCriterion("has_crossbow_arrow_normal", hasItem(crossbow_arrow_normal)).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.crossbow_arrow_vampire_killer, 3).lava(1).patternLine(" X ").patternLine("XYX").patternLine(" Z ").patternLine(" W ").key('X', garlic).key('Y', gold_ingot).key('Z', stick).key('W', feather).addCriterion("has_crossbow_arrow_normal", hasItem(crossbow_arrow_normal)).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.enhanced_crossbow).lava(2).skills(HunterSkills.enhanced_crossbow).patternLine("YXXY").patternLine(" XX ").patternLine(" XX ").key('X', iron_ingot).key('Y', string).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.enhanced_double_crossbow).lava(3).skills(HunterSkills.double_crossbow, HunterSkills.enhanced_crossbow).patternLine("YXXY").patternLine("YXXY").patternLine(" XX ").patternLine(" XX ").key('X', iron_ingot).key('Y', string).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.enhanced_tech_crossbow).lava(5).skills(HunterSkills.tech_weapons).patternLine("YXXY").patternLine("XZZX").patternLine("XZZX").patternLine(" XX ").key('X', iron_ingot).key('Y', string).key('Z', diamond).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_hat_head_0).patternLine(" YY ").patternLine(" YY ").patternLine("XXXX").key('X', iron_ingot).key('Y', black_wool).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_hat_head_1).lava(1).patternLine(" YY ").patternLine("XXXX").key('X', iron_ingot).key('Y', black_wool).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.pitchfork).patternLine("X X").patternLine("YYY").patternLine(" Y ").patternLine(" Y ").key('X', iron_ingot).key('Y', stick).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.tech_crossbow_ammo_package).lava(1).patternLine(" XZ ").patternLine("YYYY").patternLine("YYYY").patternLine("YYYY").key('X', iron_ingot).key('Y', crossbow_arrow_normal).key('Z', planks).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_axe_normal, 1, enchantment(2, Enchantments.KNOCKBACK)).lava(5).patternLine("XXZY").patternLine("XXZY").patternLine("  ZY").patternLine("  Z ").key('X', iron_ingot).key('Y', garlic).key('Z', stick).build(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.hunter_axe_enhanced, 1, enchantment(3, Enchantments.KNOCKBACK)).lava(5).skills(HunterSkills.enhanced_weapons).patternLine("XWZY").patternLine("XWZY").patternLine("  ZY").patternLine("  Z ").key('X', iron_ingot).key('Y', garlic).key('W', diamond).key('Z', stick).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.altar_infusion).patternLine("YZY").patternLine("ZZZ").key('Y', gold_ingot).key('Z', obsidian).addCriterion("has_gold", hasItem(gold_ingot)).build(consumer, vampire("altar_infusion"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.altar_inspiration).patternLine(" X ").patternLine("XYX").patternLine("ZZZ").key('X', glass).key('Y', glass_bottle).key('Z', iron_ingot).addCriterion("has_iron", hasItem(iron_ingot)).build(consumer, vampire("altar_inspiration"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.altar_pillar).patternLine("X X").patternLine("   ").patternLine("XXX").key('X', stone_bricks).addCriterion("has_stones", hasItem(stone_bricks)).build(consumer, vampire("altar_pillar"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.altar_tip).patternLine(" X ").patternLine("XYX").key('X', iron_ingot).key('Y', iron_block).addCriterion("has_iron", hasItem(iron_ingot)).build(consumer, vampire("altar_tip"));
        ShapelessRecipeBuilder.shapelessRecipe(Items.GLASS_BOTTLE).addIngredient(blood_bottle).addCriterion("has_blood_bottle", hasItem(blood_bottle)).build(consumer, vampire("blood_bottle_to_glass"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_container).patternLine("XYX").patternLine("YZY").patternLine("XYX").key('X', planks).key('Y', glass).key('Z', iron_ingot).addCriterion("has_iron", hasItem(iron_ingot)).build(consumer, vampire("blood_container"));
        new Shapeless(ModItems.blood_infused_enhanced_iron_ingot, 3).addIngredient(iron_ingot, 3).addIngredient(pure_blood_4).addCriterion("has_iron", hasItem(iron_ingot)).build(consumer, vampire("blood_infused_enhanced_iron_ingot"));
        new Shapeless(ModItems.blood_infused_iron_ingot, 3).addIngredient(iron_ingot, 3).addIngredient(Ingredient.fromItems(pure_blood_0, pure_blood_1, pure_blood_2, pure_blood_3)).addCriterion("has_iron", hasItem(iron_ingot)).build(consumer, vampire("blood_infused_iron_ingot"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_pedestal).patternLine("GYG").patternLine("YZY").patternLine("XXX").key('X', obsidian).key('Y', planks).key('Z', blood_bottle).key('G', gold_ingot).addCriterion("has_gold", hasItem(gold_ingot)).build(consumer, vampire("blood_pedestal"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.coffin).patternLine("XXX").patternLine("YYY").patternLine("XXX").key('X', planks).key('Y', wool).addCriterion("has_wool", hasItem(wool)).build(consumer, vampire("coffin"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.heart_seeker_enhanced).patternLine("X").patternLine("X").patternLine("Y").key('X', blood_infused_enhanced_iron_ingot).key('Y', stick).addCriterion("has_ingot", hasItem(blood_infused_enhanced_iron_ingot)).build(consumer, vampire("heart_seeker_enhanced"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.heart_striker_enhanced).patternLine("XX").patternLine("XX").patternLine("YY").key('X', blood_infused_enhanced_iron_ingot).key('Y', stick).addCriterion("has_ingot", hasItem(blood_infused_enhanced_iron_ingot)).build(consumer, vampire("heart_striker_enhanced"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.heart_seeker_normal).patternLine("X").patternLine("X").patternLine("Y").key('X', blood_infused_iron_ingot).key('Y', stick).addCriterion("has_ingot", hasItem(blood_infused_iron_ingot)).build(consumer, vampire("heart_seeker_normal"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.heart_striker_normal).patternLine("XX").patternLine("XX").patternLine("YY").key('X', blood_infused_iron_ingot).key('Y', stick).addCriterion("has_ingot", hasItem(blood_infused_iron_ingot)).build(consumer, vampire("heart_striker_normal"));

        ShapedRecipeBuilder.shapedRecipe(ModItems.vampire_cloak_black_blue).patternLine("YZY").patternLine("XAX").patternLine("Y Y").key('X', blue_wool).key('Y', black_wool).key('Z', diamond).key('A', pure_blood).addCriterion("has_pure_blood", hasItem(pure_blood)).build(consumer, vampire("vampire_cloak_black_blue"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.vampire_cloak_black_red).patternLine("YZY").patternLine("XAX").patternLine("Y Y").key('X', red_wool).key('Y', black_wool).key('Z', diamond).key('A', pure_blood).addCriterion("has_pure_blood", hasItem(pure_blood)).build(consumer, vampire("vampire_cloak_black_red"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.vampire_cloak_black_white).patternLine("YZY").patternLine("XAX").patternLine("Y Y").key('X', white_wool).key('Y', black_wool).key('Z', diamond).key('A', pure_blood).addCriterion("has_pure_blood", hasItem(pure_blood)).build(consumer, vampire("vampire_cloak_black_white"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.vampire_cloak_white_black).patternLine("YZY").patternLine("XAX").patternLine("Y Y").key('X', black_wool).key('Y', white_wool).key('Z', diamond).key('A', pure_blood).addCriterion("has_pure_blood", hasItem(pure_blood)).build(consumer, vampire("vampire_cloak_white_black"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.vampire_cloak_red_black).patternLine("YZY").patternLine("XAX").patternLine("Y Y").key('X', black_wool).key('Y', red_wool).key('Z', diamond).key('A', pure_blood).addCriterion("has_pure_blood", hasItem(pure_blood)).build(consumer, vampire("vampire_cloak_red_black"));
        ItemStack blood_bottle_stack = new ItemStack(ModItems.blood_bottle);
        blood_bottle_stack.setDamage(0);
        ConditionalRecipe.builder().addCondition(new NotCondition(new ConfigCondition("auto_convert"))).addRecipe((consumer1 -> new Shaped(blood_bottle_stack).patternLine("XYX").patternLine(" X ").key('X', glass).key('Y', rotten_flesh).addCriterion("has_glass", hasItem(glass)).build(consumer1, vampire("blood_bottle")))).build(consumer, vampire("blood_bottle"));

        new IItemWIthTierRecipeBuilder(ModItems.heart_seeker_normal, 1).patternLine(" X ").patternLine("XYX").key('X', blood_infused_iron_ingot).key('Y', ModItems.heart_seeker_normal).addCriterion("has_heart_seeker", hasItem(ModItems.heart_seeker_normal)).build(consumer, vampire("heart_seeker_normal_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.heart_striker_normal, 1).patternLine("XXX").patternLine("XYX").key('X', blood_infused_iron_ingot).key('Y', ModItems.heart_striker_normal).addCriterion("has_heart_striker", hasItem(ModItems.heart_striker_normal)).build(consumer, vampire("heart_striker_normal_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.heart_seeker_enhanced, 1).patternLine(" X ").patternLine("XYX").key('X', blood_infused_enhanced_iron_ingot).key('Y', ModItems.heart_seeker_enhanced).addCriterion("has_heart_seeker", hasItem(ModItems.heart_seeker_enhanced)).build(consumer, vampire("heart_seeker_enhanced_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.heart_striker_enhanced, 1).patternLine("XXX").patternLine("XYX").key('X', blood_infused_enhanced_iron_ingot).key('Y', ModItems.heart_striker_enhanced).addCriterion("has_heart_striker", hasItem(ModItems.heart_striker_enhanced)).build(consumer, vampire("heart_striker_enhanced_repair"));

        //noinspection ConstantConditions
        ConditionalRecipe.builder().addCondition(new ModLoadedCondition("guideapi-vp")).addRecipe((consumer1 -> ShapelessRecipeBuilder.shapelessRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation("guideapi-vp", "vampirism-guidebook"))).addIngredient(vampire_fang).addIngredient(book).addCriterion("has_fang", hasItem(vampire_fang)).build(consumer1, modId("general/guidebook")))).build(consumer, modId("general/guidebook"));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism Recipes";
    }

    private ResourceLocation modId(String path) {
        return new ResourceLocation(REFERENCE.MODID, path);
    }

    private ResourceLocation hunter(String path) {
        return modId("hunter/" + path);
    }

    private ResourceLocation vampire(String path) {
        return modId("vampire/" + path);
    }

    private ResourceLocation general(String path) {
        return modId("general/" + path);
    }

    private Ingredient potion(Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION, 1);
        PotionUtils.addPotionToItemStack(stack, potion);
        return new NBTIngredient(stack);
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
            return this.addIngredient(Ingredient.fromTag(tag), amount);
        }
    }

    private static class Shaped extends ShapedRecipeBuilder {
        private final ItemStack stack;

        public Shaped(ItemStack resultIn) {
            super(resultIn.getItem(), resultIn.getCount());
            this.stack = resultIn;
        }

        @Override
        public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
            this.validate(id);
            this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
            consumerIn.accept(new Result(id, this.count, this.group == null ? "" : this.group, this.pattern, this.key, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath()), this.stack));

        }

        private class Result extends ShapedRecipeBuilder.Result {
            private final ItemStack stack;

            public Result(ResourceLocation idIn, int countIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, ItemStack stack) {
                super(idIn, stack.getItem(), countIn, groupIn, patternIn, keyIn, advancementBuilderIn, advancementIdIn);
                this.stack = stack;
            }

            @Override
            public void serialize(JsonObject json) {
                super.serialize(json);
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
