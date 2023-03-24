package de.teamlapen.vampirism.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.recipebuilder.*;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.ShapedRecipeBuilderAccessor;
import de.teamlapen.vampirism.recipes.ConfigCondition;
import de.teamlapen.vampirism.util.NBTIngredient;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RecipesGenerator extends VanillaRecipeProvider {//TODO 1.20 move to de.teamlapen.vampirism.data.provider

    public RecipesGenerator(@NotNull PackOutput packOutput) {
        super(packOutput);
    }


    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ItemLike hopper = Blocks.HOPPER;
        ItemLike cauldron = Blocks.CAULDRON;
        ItemLike black_dye = Items.BLACK_DYE;
        ItemLike stone_bricks = Blocks.STONE_BRICKS;
        ItemLike vampire_orchid = ModBlocks.VAMPIRE_ORCHID.get();
        ItemLike stone = Blocks.STONE;
        ItemLike castle_block_dark_brick = ModBlocks.CASTLE_BLOCK_DARK_BRICK.get();
        ItemLike castle_block_dark_stone = ModBlocks.CASTLE_BLOCK_DARK_STONE.get();
        ItemLike castle_block_normal_brick = ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get();
        ItemLike castle_block_purple_brick = ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get();
        ItemLike vampire_book = ModItems.VAMPIRE_BOOK.get();
        ItemLike vampire_fang = ModItems.VAMPIRE_FANG.get();
        ItemLike book = Items.BOOK;
        ItemLike bread = Items.BREAD;
        ItemLike injection_empty = ModItems.INJECTION_EMPTY.get();
        ItemLike glass_bottle = Items.GLASS_BOTTLE;
        ItemLike garlic_diffuser_core = ModItems.GARLIC_DIFFUSER_CORE.get();
        ItemLike garlic_diffuser_core_improved = ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.get();
        ItemLike garlic_diffuser_normal = ModBlocks.GARLIC_DIFFUSER_NORMAL.get();
        ItemLike bucket = Items.BUCKET;
        ItemLike gun_powder = Items.GUNPOWDER;
        ItemLike holy_water_bottle_normal = ModItems.HOLY_WATER_BOTTLE_NORMAL.get();
        ItemLike holy_water_bottle_enhanced = ModItems.HOLY_WATER_BOTTLE_ENHANCED.get();
        ItemLike holy_water_bottle_ultimate = ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get();
        ItemLike feather = Items.FEATHER;
        ItemLike string = Items.STRING;
        ItemLike black_wool = Items.BLACK_WOOL;
        ItemLike blue_wool = Items.BLUE_WOOL;
        ItemLike white_wool = Items.WHITE_WOOL;
        ItemLike red_wool = Items.RED_WOOL;
        ItemLike crossbow_arrow_normal = ModItems.CROSSBOW_ARROW_NORMAL.get();
        ItemLike blood_bottle = ModItems.BLOOD_BOTTLE.get();
        ItemLike pure_blood_0 = ModItems.PURE_BLOOD_0.get();
        ItemLike pure_blood_1 = ModItems.PURE_BLOOD_1.get();
        ItemLike pure_blood_2 = ModItems.PURE_BLOOD_2.get();
        ItemLike pure_blood_3 = ModItems.PURE_BLOOD_3.get();
        ItemLike pure_blood_4 = ModItems.PURE_BLOOD_4.get();
        ItemLike blood_infused_enhanced_iron_ingot = ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get();
        ItemLike blood_infused_iron_ingot = ModItems.BLOOD_INFUSED_IRON_INGOT.get();
        ItemLike rotten_flesh = Items.ROTTEN_FLESH;
        ItemLike alchemical_fire = ModItems.ITEM_ALCHEMICAL_FIRE.get();
        ItemLike amulet = ModItems.AMULET.get();
        ItemLike ring = ModItems.RING.get();
        ItemLike obi_belt = ModItems.OBI_BELT.get();
        ItemLike blood_container = ModBlocks.BLOOD_CONTAINER.get();
        ItemLike basalt = Blocks.BASALT;
        TagKey<Item> planks = ItemTags.PLANKS;
        TagKey<Item> glass = Tags.Items.GLASS;
        TagKey<Item> glass_pane = Tags.Items.GLASS_PANES;
        TagKey<Item> logs = ItemTags.LOGS;
        TagKey<Item> diamond = Tags.Items.GEMS_DIAMOND;
        TagKey<Item> diamondBlock = Tags.Items.STORAGE_BLOCKS_DIAMOND;
        TagKey<Item> iron_ingot = Tags.Items.INGOTS_IRON;
        TagKey<Item> quartz_block = Tags.Items.STORAGE_BLOCKS_QUARTZ;
        TagKey<Item> coal_block = Tags.Items.STORAGE_BLOCKS_COAL;
        TagKey<Item> garlic = ModTags.Items.GARLIC;
        TagKey<Item> obsidian = Tags.Items.OBSIDIAN;
        TagKey<Item> wool = ItemTags.WOOL;
        TagKey<Item> stick = Tags.Items.RODS_WOODEN;
        TagKey<Item> iron_block = Tags.Items.STORAGE_BLOCKS_IRON;
        TagKey<Item> gold_ingot = Tags.Items.INGOTS_GOLD;
        TagKey<Item> pure_blood = ModTags.Items.PURE_BLOOD;
        TagKey<Item> holy_water = ModTags.Items.HOLY_WATER;
        TagKey<Item> heart = ModTags.Items.HEART;
        TagKey<Item> leather = Tags.Items.LEATHER;
        TagKey<Item> beds = ItemTags.BEDS;


        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_GRINDER.get()).define('Z', hopper).define('Y', planks).define('D', diamond).define('X', iron_ingot).pattern(" Z ").pattern("YDY").pattern("YXY").unlockedBy("has_hopper", has(hopper)).save(consumer, general("blood_grinder"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_SIEVE.get()).define('X', iron_ingot).define('Q', quartz_block).define('Y', planks).define('Z', cauldron).pattern("XQX").pattern("YZY").pattern("YXY").unlockedBy("has_cauldron", has(cauldron)).save(consumer, general("blood_sieve"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CASTLE_BLOCK_DARK_BRICK.get(), 8).requires(castle_block_normal_brick, 8).requires(black_dye).unlockedBy("has_castle_brick", has(castle_block_normal_brick)).unlockedBy("has_black_dye", has(black_dye)).save(consumer, modId("general/castle_block_dark_brick_0"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CASTLE_BLOCK_DARK_BRICK.get(), 7).requires(stone_bricks, 7).requires(black_dye).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_dark_brick_1"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CASTLE_BLOCK_DARK_STONE.get(), 7).requires(stone, 7).requires(black_dye).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_dark_stone"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get(), 8).requires(stone_bricks, 8).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_normal_brick"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get(), 8).requires(castle_block_normal_brick, 8).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_purple_brick"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), 6).pattern("###").define('#', castle_block_dark_brick).unlockedBy("has_castle_brick", has(castle_block_dark_brick)).save(consumer, modId("general/castle_slab_dark_brick"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_DARK_STONE.get(), 6).pattern("###").define('#', castle_block_dark_stone).unlockedBy("has_castle_brick", has(castle_block_dark_stone)).save(consumer, modId("general/castle_slab_dark_stone"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get(), 6).pattern("###").define('#', castle_block_purple_brick).unlockedBy("has_castle_brick", has(castle_block_purple_brick)).save(consumer, modId("general/castle_slab_purple_brick"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_DARK_BRICK.get(), 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_dark_brick).unlockedBy("has_castle_brick", has(castle_block_dark_brick)).save(consumer, modId("general/castle_stairs_dark_brick"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_DARK_STONE.get(), 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_dark_stone).unlockedBy("has_castle_brick", has(castle_block_dark_stone)).save(consumer, general("castle_stairs_dark_stone"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get(), 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_purple_brick).unlockedBy("has_castle_brick", has(castle_block_purple_brick)).save(consumer, general("castle_stairs_purple_brick"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING.get()).pattern(" X ").pattern("YYY").pattern(" Y ").define('X', vampire_book).define('Y', planks).unlockedBy("has_vampire_book", has(planks)).save(consumer, general("altar_cleansing"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING.get()).pattern("XZX").pattern("YYY").pattern(" Y ").define('X', vampire_fang).define('Y', planks).define('Z', book).unlockedBy("has_book", has(book)).save(consumer, general("altar_cleansing_new"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.FIRE_PLACE.get()).pattern(" X ").pattern("XYX").define('X', logs).define('Y', coal_block).unlockedBy("has_logs", has(logs)).save(consumer, general("fire_place"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GARLIC_BREAD.get()).requires(garlic).requires(bread).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bread", has(bread)).save(consumer, general("garlic_bread"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INJECTION_EMPTY.get()).pattern(" X ").pattern(" X ").pattern(" Y ").define('X', glass).define('Y', glass_pane).unlockedBy("has_glass", has(glass)).unlockedBy("has_glass_pane", has(glass_pane)).save(consumer, general("injection_0"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INJECTION_GARLIC.get()).requires(injection_empty).requires(garlic).unlockedBy("has_injection", has(injection_empty)).save(consumer, general("injection_1"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INJECTION_SANGUINARE.get()).requires(injection_empty).requires(vampire_fang, 8).unlockedBy("has_injection", has(injection_empty)).save(consumer, general("injection_2"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TOTEM_BASE.get()).pattern("XYX").pattern("XYX").pattern("ZZZ").define('X', planks).define('Y', obsidian).define('Z', iron_ingot).unlockedBy("has_obsidian", has(obsidian)).save(consumer, general("totem_base"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TOTEM_TOP_CRAFTED.get()).pattern("X X").pattern(" Y ").pattern("XZX").define('X', obsidian).define('Y', diamond).define('Z', vampire_book).unlockedBy("has_diamond", has(diamondBlock)).unlockedBy("has_obsidian", has(obsidian)).save(consumer, general("totem_top"));
        ConditionalRecipe.builder().addCondition(new ConfigCondition("umbrella")).addRecipe((consumer1) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UMBRELLA.get()).pattern("###").pattern("BAB").pattern(" A ").define('#', wool).define('A', stick).define('B', vampire_orchid).unlockedBy("has_wool", has(wool)).save(consumer1, general("umbrella"))).build(consumer, general("umbrella"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALCHEMICAL_CAULDRON.get()).pattern("XZX").pattern("XXX").pattern("Y Y").define('X', iron_ingot).define('Y', stone_bricks).define('Z', garlic).unlockedBy("has_iron", has(iron_ingot)).save(consumer, hunter("alchemical_cauldron"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.POTION_TABLE.get()).pattern("XXX").pattern("Y Y").pattern("ZZZ").define('X', glass_bottle).define('Y', planks).define('Z', iron_ingot).unlockedBy("has_glass_bottle", has(glass_bottle)).save(consumer, hunter("potion_table"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_NORMAL.get()).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('O', obsidian).define('Z', garlic_diffuser_core).unlockedBy("has_diamond", has(diamond)).save(consumer, hunter("garlic_diffuser_normal"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.HUNTER_TABLE.get()).pattern("XYW").pattern("ZZZ").pattern("Z Z").define('X', vampire_fang).define('Y', book).define('Z', planks).define('W', garlic).unlockedBy("has_fang", has(vampire_fang)).save(consumer, hunter("hunter_table"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.MED_CHAIR.get()).pattern("XYX").pattern("XXX").pattern("XZX").define('X', iron_ingot).define('Y', wool).define('Z', glass_bottle).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("item_med_chair"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_IMPROVED.get()).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('Z', garlic_diffuser_core_improved).define('O', obsidian).unlockedBy("has_garlic_diffuser", has(garlic_diffuser_normal)).save(consumer, hunter("garlic_diffuser_improved"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.STAKE.get()).pattern("X").pattern("Y").pattern("X").define('X', stick).define('Y', planks).unlockedBy("has_sticks", has(stick)).save(consumer, hunter("stake"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WEAPON_TABLE.get()).pattern("X  ").pattern("YYY").pattern(" Z ").define('X', bucket).define('Y', iron_ingot).define('Z', iron_block).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("weapon_table"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL.get(), 6).pattern("X").pattern("Y").define('X', iron_ingot).define('Y', stick).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("crossbow_arrow_normal"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL.get()).requires(Items.ARROW).unlockedBy("has_arrow", has(Items.ARROW)).save(consumer, hunter("crossbow_arrow_from_vanilla"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_0.get()).requires(ModItems.PURE_BLOOD_1.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_1)).save(consumer, hunter("pure_blood0"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_1.get()).requires(ModItems.PURE_BLOOD_2.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_2)).save(consumer, hunter("pure_blood1"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_2.get()).requires(ModItems.PURE_BLOOD_3.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_3)).save(consumer, hunter("pure_blood2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_3.get()).requires(ModItems.PURE_BLOOD_4.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_4)).save(consumer, hunter("pure_blood3"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_BLOCK_DARK_BRICK.get()).unlockedBy("has_castle_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_block_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_DARK_STONE.get()).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_DARK_STONE.get()).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_DARK_BRICK.get()).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_DARK_BRICK.get()).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_purple_brick), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get()).unlockedBy("has_stone", has(castle_block_purple_brick)).save(consumer, modId("stonecutting/castle_stairs_purple_brick_from_castle_block_purple_brick"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_DARK_STONE.get(), 2).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), 2).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), 2).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_DARK_STONE.get(), 2).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_purple_brick), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get(), 2).unlockedBy("has_stone", has(castle_block_purple_brick)).save(consumer, modId("stonecutting/castle_slaps_purple_brick_from_castle_block_purple_brick"));

        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.PURE_SALT.get(), 4).withIngredient(garlic).withFluid(new FluidStack(Fluids.WATER, 1)).withSkills(HunterSkills.BASIC_ALCHEMY.get()).cookTime(1200).build(consumer, modId("pure_salt"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 4).withIngredient(gun_powder).withFluid(holy_water_bottle_normal).build(consumer, modId("alchemical_fire_4"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 5).withIngredient(gun_powder).withFluid(holy_water_bottle_enhanced).build(consumer, modId("alchemical_fire_5"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 6).withIngredient(gun_powder).withFluid(holy_water_bottle_ultimate).build(consumer, modId("alchemical_fire_6"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE.get()).withIngredient(wool).withFluid(garlic).withSkills(HunterSkills.GARLIC_DIFFUSER.get()).build(consumer, modId("garlic_diffuser_core"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.get()).withIngredient(garlic_diffuser_core).withFluid(holy_water_bottle_ultimate).withSkills(HunterSkills.GARLIC_DIFFUSER_IMPROVED.get()).experience(2.0f).build(consumer, modId("garlic_diffuser" +
                "_core_improved"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.PURIFIED_GARLIC.get(), 2).withIngredient(garlic).withFluid(holy_water).withSkills(HunterSkills.PURIFIED_GARLIC.get()).build(consumer, modId("purified_garlic"));

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get()).lava(1).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get()).lava(1).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get()).lava(1).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("    ").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XXXX").pattern("XYYX").pattern("XZZX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get()).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_NORMAL.get()).lava(2).pattern("XWWX").pattern("XZZX").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', leather).define('Z', garlic).define('W', vampire_fang).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XWWX").pattern("XZZX").pattern("XYYX").pattern("XYYX").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).define('W', vampire_fang).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_NORMAL.get()).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', iron_ingot).define('Z', garlic).define('Y', leather).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', iron_ingot).define('Z', garlic).define('Y', diamond).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_NORMAL.get()).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', iron_ingot).define('Y', leather).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_NORMAL.get()).lava(2).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', leather).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).save(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_CROSSBOW.get()).lava(1).pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', iron_ingot).define('Y', string).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_DOUBLE_CROSSBOW.get()).lava(1).skills(HunterSkills.DOUBLE_CROSSBOW.get()).pattern("YXXY").pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', iron_ingot).define('Y', string).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_TECH_CROSSBOW.get()).lava(5).skills(HunterSkills.TECH_WEAPONS.get()).pattern("YXXY").pattern("XZZX").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).define('Z', diamond).save(consumer);
        ShapelessWeaponTableRecipeBuilder.shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_SPITFIRE.get(), 3).lava(1).requires(crossbow_arrow_normal, 3).requires(alchemical_fire).unlockedBy("has_crossbow_arrow_normal", has(crossbow_arrow_normal)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), 3).lava(1).pattern(" X ").pattern("XYX").pattern(" Z ").pattern(" W ").define('X', garlic).define('Y', gold_ingot).define('Z', stick).define('W', feather).unlockedBy("has_crossbow_arrow_normal", has(crossbow_arrow_normal)).save(consumer);
        ShapelessWeaponTableRecipeBuilder.shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_TELEPORT.get(), 1).lava(1).requires(crossbow_arrow_normal).requires(new NBTIngredient(OilUtils.createOilItem(ModOils.TELEPORT.get()))).unlockedBy("has_crossbow_arrow_normal", has(crossbow_arrow_normal)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_CROSSBOW.get()).lava(2).skills(HunterSkills.ENHANCED_WEAPONS.get()).pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).lava(3).skills(HunterSkills.DOUBLE_CROSSBOW.get(), HunterSkills.ENHANCED_WEAPONS.get()).pattern("YXXY").pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_TECH_CROSSBOW.get()).lava(5).skills(HunterSkills.TECH_WEAPONS.get()).pattern("YXXY").pattern("XZZX").pattern("XZZX").pattern(" XX ").define('X', iron_ingot).define('Y', string).define('Z', diamond).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_HEAD_0.get()).pattern(" YY ").pattern(" YY ").pattern("XXXX").define('X', iron_ingot).define('Y', black_wool).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_HEAD_1.get()).lava(1).pattern(" YY ").pattern("XXXX").define('X', iron_ingot).define('Y', black_wool).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.PITCHFORK.get()).pattern("X X").pattern("YYY").pattern(" Y ").pattern(" Y ").define('X', iron_ingot).define('Y', stick).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARROW_CLIP.get()).lava(1).pattern("ILLI").pattern("PLLP").pattern("ILLI").define('I', iron_ingot).define('L', leather).define('P', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_AXE_NORMAL.get(), 1, enchantment(2, Enchantments.KNOCKBACK)).lava(5).pattern("XXZY").pattern("XXZY").pattern("  ZY").pattern("  Z ").define('X', iron_ingot).define('Y', garlic).define('Z', stick).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_AXE_ENHANCED.get(), 1, enchantment(3, Enchantments.KNOCKBACK)).lava(5).skills(HunterSkills.ENHANCED_WEAPONS.get()).pattern("XWZY").pattern("XWZY").pattern("  ZY").pattern("  Z ").define('X', iron_ingot).define('Y', garlic).define('W', diamond).define('Z', stick).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_INFUSION.get()).pattern("YZY").pattern("ZZZ").define('Y', gold_ingot).define('Z', obsidian).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("altar_infusion"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_INSPIRATION.get()).pattern("X X").pattern("XYX").pattern("XXX").define('X', planks).define('Y', blood_container).unlockedBy("has_planks", has(planks)).unlockedBy("has_blood_container", has(blood_container)).save(consumer, vampire("altar_inspiration"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_PILLAR.get()).pattern("X X").pattern("   ").pattern("XXX").define('X', stone_bricks).unlockedBy("has_stones", has(stone_bricks)).save(consumer, vampire("altar_pillar"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_TIP.get()).pattern(" X ").pattern("XYX").define('X', iron_ingot).define('Y', iron_block).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("altar_tip"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GLASS_BOTTLE).requires(blood_bottle).unlockedBy("has_blood_bottle", has(blood_bottle)).save(consumer, vampire("blood_bottle_to_glass"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_CONTAINER.get()).pattern("XYX").pattern("YZY").pattern("XYX").define('X', planks).define('Y', glass).define('Z', iron_ingot).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_container"));
        new Shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get(), 3).addIngredient(iron_ingot, 3).requires(pure_blood_4).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_infused_enhanced_iron_ingot"));
        new Shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_IRON_INGOT.get(), 3).addIngredient(iron_ingot, 3).requires(Ingredient.of(pure_blood_0, pure_blood_1, pure_blood_2, pure_blood_3)).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_infused_iron_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_PEDESTAL.get()).pattern("GYG").pattern("YZY").pattern("XXX").define('X', obsidian).define('Y', planks).define('Z', blood_bottle).define('G', gold_ingot).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("blood_pedestal"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HEART_SEEKER_ENHANCED.get()).pattern("X").pattern("X").pattern("Y").define('X', blood_infused_enhanced_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_enhanced_iron_ingot)).save(consumer, vampire("heart_seeker_enhanced"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HEART_STRIKER_ENHANCED.get()).pattern("XX").pattern("XX").pattern("YY").define('X', blood_infused_enhanced_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_enhanced_iron_ingot)).save(consumer, vampire("heart_striker_enhanced"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HEART_SEEKER_NORMAL.get()).pattern("X").pattern("X").pattern("Y").define('X', blood_infused_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_iron_ingot)).save(consumer, vampire("heart_seeker_normal"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HEART_STRIKER_NORMAL.get()).pattern("XX").pattern("XX").pattern("YY").define('X', blood_infused_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_iron_ingot)).save(consumer, vampire("heart_striker_normal"));

        coffinFromWool(consumer, ModBlocks.COFFIN_WHITE.get(), Items.WHITE_WOOL, vampire("coffin_white"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_ORANGE.get(), Items.ORANGE_WOOL, Items.ORANGE_DYE, vampire("coffin_orange"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_MAGENTA.get(), Items.MAGENTA_WOOL, Items.MAGENTA_DYE, vampire("coffin_magenta"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_LIGHT_BLUE.get(), Items.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE, vampire("coffin_light_blue"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_YELLOW.get(), Items.YELLOW_WOOL, Items.YELLOW_DYE, vampire("coffin_yellow"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_LIME.get(), Items.LIME_WOOL, Items.LIME_DYE, vampire("coffin_lime"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_PINK.get(), Items.PINK_WOOL, Items.PINK_DYE, vampire("coffin_pink"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_GRAY.get(), Items.GRAY_WOOL, Items.GRAY_DYE, vampire("coffin_gray"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_LIGHT_GRAY.get(), Items.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE, vampire("coffin_light_gray"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_CYAN.get(), Items.CYAN_WOOL, Items.CYAN_DYE, vampire("coffin_cyan"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_PURPLE.get(), Items.PURPLE_WOOL, Items.PURPLE_DYE, vampire("coffin_purple"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_BLUE.get(), Items.BLUE_WOOL, Items.BLUE_DYE, vampire("coffin_blue"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_BROWN.get(), Items.BROWN_WOOL, Items.BROWN_DYE, vampire("coffin_brown"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_GREEN.get(), Items.GREEN_WOOL, Items.GREEN_DYE, vampire("coffin_green"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_RED.get(), Items.RED_WOOL, Items.RED_DYE, vampire("coffin_red"));
        coffinFromWoolOrDye(consumer, ModBlocks.COFFIN_BLACK.get(), Items.BLACK_WOOL, Items.BLACK_DYE, vampire("coffin_black"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', blue_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_blue"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_BLACK_RED.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', red_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_red"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', white_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_white"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', white_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_white_black"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_RED_BLACK.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', red_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_red_black"));
        ItemStack blood_bottle_stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        blood_bottle_stack.setDamageValue(0);
        ConditionalRecipe.builder().addCondition(new NotCondition(new ConfigCondition("auto_convert"))).addRecipe((consumer1 -> new Shaped(RecipeCategory.MISC, blood_bottle_stack).pattern("XYX").pattern(" X ").define('X', glass).define('Y', rotten_flesh).unlockedBy("has_glass", has(glass)).save(consumer1, vampire("blood_bottle")))).build(consumer, vampire("blood_bottle"));

        new IItemWIthTierRecipeBuilder(RecipeCategory.COMBAT, ModItems.HEART_SEEKER_NORMAL.get(), 1).pattern(" X ").pattern("XYX").define('X', blood_infused_iron_ingot).define('Y', ModItems.HEART_SEEKER_NORMAL.get()).unlockedBy("has_heart_seeker", has(ModItems.HEART_SEEKER_NORMAL.get())).save(consumer, vampire("heart_seeker_normal_repair"));
        new IItemWIthTierRecipeBuilder(RecipeCategory.COMBAT,ModItems.HEART_STRIKER_NORMAL.get(), 1).pattern("XXX").pattern("XYX").define('X', blood_infused_iron_ingot).define('Y', ModItems.HEART_STRIKER_NORMAL.get()).unlockedBy("has_heart_striker", has(ModItems.HEART_STRIKER_NORMAL.get())).save(consumer, vampire("heart_striker_normal_repair"));
        new IItemWIthTierRecipeBuilder(RecipeCategory.COMBAT, ModItems.HEART_SEEKER_ENHANCED.get(), 1).pattern(" X ").pattern("XYX").define('X', blood_infused_enhanced_iron_ingot).define('Y', ModItems.HEART_SEEKER_ENHANCED.get()).unlockedBy("has_heart_seeker", has(ModItems.HEART_SEEKER_ENHANCED.get())).save(consumer, vampire("heart_seeker_enhanced_repair"));
        new IItemWIthTierRecipeBuilder(RecipeCategory.COMBAT, ModItems.HEART_STRIKER_ENHANCED.get(), 1).pattern("XXX").pattern("XYX").define('X', blood_infused_enhanced_iron_ingot).define('Y', ModItems.HEART_STRIKER_ENHANCED.get()).unlockedBy("has_heart_striker", has(ModItems.HEART_STRIKER_ENHANCED.get())).save(consumer, vampire("heart_striker_enhanced_repair"));

        //noinspection ConstantConditions
        ConditionalRecipe.builder().addCondition(new ModLoadedCondition("guideapi_vp")).addRecipe((consumer1 -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ForgeRegistries.ITEMS.getValue(new ResourceLocation("guideapi_vp", "vampirism-guidebook"))).requires(vampire_fang).requires(book).unlockedBy("has_fang", has(vampire_fang)).save(consumer1, modId("general/guidebook")))).build(consumer, modId("general/guidebook"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_LEGS.get()).pattern("XXX").pattern("X X").pattern("XYX").define('X', Items.GRAY_WOOL).define('Y', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.GRAY_WOOL)).save(consumer, vampire("vampire_clothing_legs"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_BOOTS.get()).pattern("XYX").pattern("X X").define('X', Items.BROWN_WOOL).define('Y', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.BROWN_WOOL)).save(consumer, vampire("vampire_clothing_boots"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_HAT.get()).pattern("ZXX").pattern(" Y ").pattern("XXX").define('X', Items.BLACK_WOOL).define('Y', Items.RED_WOOL).define('Z', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.BLACK_WOOL)).save(consumer, vampire("vampire_clothing_hat"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_CROWN.get()).pattern("XYX").pattern("XXX").define('X', Items.GOLD_INGOT).define('Y', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(consumer, vampire("vampire_clothing_crown"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CROSS.get()).pattern(" X ").pattern("XYX").pattern(" X ").define('X', planks).define('Y', holy_water).unlockedBy("has_planks", has(planks)).unlockedBy("has_holy", has(holy_water)).save(consumer, hunter("cross"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.ITEM_CANDELABRA.get()).pattern("XXX").pattern("YYY").pattern("ZAZ").define('X', string).define('Y', Items.HONEYCOMB).define('Z', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_string", has(string)).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("candelabra"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.ITEM_CANDELABRA.get()).pattern("YYY").pattern("ZAZ").define('Y', ItemTags.CANDLES).define('Z', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(ItemTags.CANDLES)).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("candelabra_candles"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHANDELIER.get()).pattern("XYX").pattern("ZYZ").pattern("BAB").define('X', string).define('Y', ModItems.ITEM_CANDELABRA.get()).define('Z', Items.HONEYCOMB).define('B', iron_ingot).define('A', gold_ingot).unlockedBy("has_string", has(string)).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_candelabra", has(ModItems.ITEM_CANDELABRA.get())).save(consumer, vampire("chandelier"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHANDELIER.get()).pattern(" Y ").pattern("ZYZ").pattern("BAB").define('Y', ModItems.ITEM_CANDELABRA.get()).define('Z', ItemTags.CANDLES).define('B', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(ItemTags.CANDLES)).unlockedBy("has_candelabra", has(ModItems.ITEM_CANDELABRA.get())).save(consumer, vampire("chandelier_candle"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GARLIC_FINDER.get()).pattern("XXX").pattern("XYX").pattern("ZAZ").define('X', blood_infused_iron_ingot).define('Y', garlic).define('Z', planks).define('A', Tags.Items.DUSTS_REDSTONE).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bloodiron", has(blood_infused_iron_ingot)).unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE)).save(consumer, vampire("garlic_finder"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE2.get()).pattern("XX ").pattern("XYX").pattern("XXX").define('X', Blocks.COBBLESTONE).define('Y', Tags.Items.STONE).unlockedBy("has_coble", has(Blocks.COBBLESTONE)).unlockedBy("has_stone", has(Tags.Items.STONE)).save(consumer, general("tombstone2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE1.get()).requires(ModBlocks.TOMBSTONE2.get()).unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2.get())).save(consumer, general("tombstone1"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE3.get()).requires(ModBlocks.TOMBSTONE2.get()).requires(Blocks.COBBLESTONE).unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2.get())).save(consumer, general("tombstone3"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GRAVE_CAGE.get()).pattern(" X ").pattern("XYX").pattern("XYX").define('X', iron_ingot).define('Y', Items.COAL).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_coal", has(Items.COAL)).save(consumer, general("grave_cage"));

        generateRecipes(consumer, ModBlockFamilies.DARK_SPRUCE_PLANKS);
        generateRecipes(consumer, ModBlockFamilies.CURSED_SPRUCE_PLANKS);

        planksFromLog(consumer, ModBlocks.DARK_SPRUCE_PLANKS.get(), ModTags.Items.DARK_SPRUCE_LOG, 4);
        planksFromLog(consumer, ModBlocks.CURSED_SPRUCE_PLANKS.get(), ModTags.Items.CURSED_SPRUCE_LOG, 4);
        woodFromLogs(consumer, ModBlocks.DARK_SPRUCE_WOOD.get(), ModBlocks.DARK_SPRUCE_LOG.get());
        woodFromLogs(consumer, ModBlocks.CURSED_SPRUCE_WOOD.get(), ModBlocks.CURSED_SPRUCE_LOG.get());
        woodFromLogs(consumer, ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
        woodFromLogs(consumer, ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());
        woodenBoat(consumer, ModItems.DARK_SPRUCE_BOAT.get(), ModBlocks.DARK_SPRUCE_PLANKS.get());
        woodenBoat(consumer, ModItems.CURSED_SPRUCE_BOAT.get(), ModBlocks.CURSED_SPRUCE_PLANKS.get());
        chestBoat(consumer, ModItems.DARK_SPRUCE_CHEST_BOAT.get(), ModBlocks.DARK_SPRUCE_PLANKS.get());
        chestBoat(consumer, ModItems.CURSED_SPRUCE_CHEST_BOAT.get(), ModBlocks.CURSED_SPRUCE_PLANKS.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.THRONE.get()).pattern(" YZ").pattern("YYZ").pattern("XZX").define('Y', Blocks.RED_CARPET).define('Z', ItemTags.PLANKS).define('X', Items.STICK).unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wool", has(Blocks.RED_CARPET)).save(consumer, general("throne"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.VAMPIRE_RACK.get()).pattern("XYX").pattern("ABC").pattern("XYX").define('X', ItemTags.PLANKS).define('Y', Items.BOOK).define('A', ModItems.VAMPIRE_FANG.get()).define('B', Items.GLASS_BOTTLE).define('C', Items.HONEYCOMB).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_book", has(Items.BOOK)).unlockedBy("has_fangs", has(ModItems.VAMPIRE_FANG.get())).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_potion", has(Items.GLASS_BOTTLE)).save(consumer, general("vampire_rack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CRUCIFIX_NORMAL.get()).pattern("XY ").pattern("ZYZ").pattern(" Y ").define('X', ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).define('Y', planks).define('Z', stick).unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_NORMAL.get())).unlockedBy("stick", has(stick)).unlockedBy("planks", has(planks)).save(consumer, hunter("crucifix"));
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CRUCIFIX_ENHANCED.get()).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).define('Y', iron_ingot).define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).define('A', ModItems.STAKE.get()).unlockedBy("iron", has(iron_ingot)).unlockedBy("has_holy_water", has(ModItems.HOLY_WATER_BOTTLE_NORMAL.get())).unlockedBy("has_holy_water_enhanced", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get())).unlockedBy("stake", has(ModItems.STAKE.get())).skills(HunterSkills.CRUCIFIX_WIELDER.get()).save(consumer, modId("crucifix_enhanced"));
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CRUCIFIX_ULTIMATE.get()).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.ITEM_ALCHEMICAL_FIRE.get()).define('Y', Tags.Items.STORAGE_BLOCKS_GOLD).define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).define('A', ModItems.STAKE.get()).unlockedBy("fire", has(ModItems.ITEM_ALCHEMICAL_FIRE.get())).unlockedBy("gold", has(Tags.Items.STORAGE_BLOCKS_GOLD)).unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get())).unlockedBy("stake", has(ModItems.STAKE.get())).skills(HunterSkills.ULTIMATE_CRUCIFIX.get()).save(consumer, modId("crucifix_ultimate"));

        SpecialRecipeBuilder.special(ModRecipes.APPLICABLE_OIL.get()).save(consumer, REFERENCE.MODID + ":applicable_oil");
        AlchemyTableRecipeBuilder
                .builder(ModOils.PLANT)
                .ingredient(Ingredient.of(new ItemStack(Items.GLASS_BOTTLE)))
                .input(Ingredient.of(new ItemStack(Items.WHEAT_SEEDS)))
                .withCriterion("has_bottles", has(Items.GLASS_BOTTLE)).withCriterion("has_wheat_seeds", has(Items.WHEAT_SEEDS))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "plant_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.VAMPIRE_BLOOD)
                .plantOilIngredient()
                .input(Ingredient.of(ModItems.VAMPIRE_BLOOD_BOTTLE.get())).withCriterion("has_wheat_seeds", has(ModItems.VAMPIRE_BLOOD_BOTTLE.get()))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "vampire_blood_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.HEALING)
                .bloodOilIngredient()
                .input(potion(Potions.HEALING, Potions.STRONG_HEALING))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "healing_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.POISON)
                .bloodOilIngredient()
                .input(potion(Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "poison_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.WEAKNESS)
                .bloodOilIngredient()
                .input(potion(Potions.WEAKNESS, Potions.LONG_WEAKNESS))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "weakness_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.SLOWNESS)
                .bloodOilIngredient()
                .input(potion(Potions.SLOWNESS, Potions.STRONG_SLOWNESS, Potions.LONG_SLOWNESS))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "slowness_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.FIRE_RESISTANCE)
                .bloodOilIngredient()
                .input(potion(Potions.FIRE_RESISTANCE, Potions.LONG_FIRE_RESISTANCE))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "fire_resistance_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.SWIFTNESS)
                .bloodOilIngredient()
                .input(potion(Potions.SWIFTNESS, Potions.LONG_SWIFTNESS, Potions.STRONG_SWIFTNESS))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "swiftness_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.REGENERATION)
                .bloodOilIngredient()
                .input(potion(Potions.REGENERATION, Potions.LONG_REGENERATION, Potions.STRONG_REGENERATION))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "regeneration_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.NIGHT_VISION)
                .bloodOilIngredient()
                .input(potion(Potions.NIGHT_VISION, Potions.LONG_NIGHT_VISION))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "night_vision_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.STRENGTH)
                .bloodOilIngredient()
                .input(potion(Potions.STRENGTH, Potions.STRONG_STRENGTH, Potions.LONG_STRENGTH))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "strength_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.JUMP)
                .bloodOilIngredient()
                .input(potion(Potions.LEAPING, Potions.LONG_LEAPING, Potions.STRONG_LEAPING))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "jump_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.WATER_BREATHING)
                .bloodOilIngredient()
                .input(potion(Potions.WATER_BREATHING, Potions.LONG_WATER_BREATHING))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "water_breathing_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.INVISIBILITY)
                .bloodOilIngredient()
                .input(potion(Potions.INVISIBILITY, Potions.LONG_INVISIBILITY))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "invisibility_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.SLOW_FALLING)
                .bloodOilIngredient()
                .input(potion(Potions.SLOW_FALLING, Potions.LONG_SLOW_FALLING))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "slow_falling_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.LUCK)
                .bloodOilIngredient()
                .input(potion(Potions.LUCK))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "luck_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.SMELT)
                .bloodOilIngredient()
                .input(new NBTIngredient(new ItemStack(ModItems.ITEM_ALCHEMICAL_FIRE.get())))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "smelt_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.TELEPORT)
                .bloodOilIngredient()
                .input(new NBTIngredient(new ItemStack(Items.ENDER_PEARL)))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "teleport_oil"));
        AlchemyTableRecipeBuilder
                .builder(ModOils.EVASION)
                .bloodOilIngredient()
                .input(Ingredient.of(new ItemStack(Items.HONEY_BOTTLE)))
                .build(consumer, new ResourceLocation(REFERENCE.MODID, "evasion_oil"));

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(amulet, ring), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.1f, 200).unlockedBy("has_amulet", has(amulet)).unlockedBy("has_ring", has(ring)).save(consumer, new ResourceLocation(REFERENCE.MODID, "gold_nugget_from_accessory_smelting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(amulet, ring), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.1f, 100).unlockedBy("has_amulet", has(amulet)).unlockedBy("has_ring", has(ring)).save(consumer, new ResourceLocation(REFERENCE.MODID, "gold_nugget_from_accessory_blasting"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, Items.LEATHER).requires(obi_belt).unlockedBy("has_obi_belt", has(obi_belt)).save(consumer, new ResourceLocation(REFERENCE.MODID, "leather_from_obi_belt"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.ALCHEMY_TABLE.get()).pattern("B  ").pattern("BBB").pattern("P P").define('B', basalt).define('P', planks).unlockedBy("has_basalt", has(basalt)).unlockedBy("has_planks", has(planks)).save(consumer);
        SpecialRecipeBuilder.special(ModRecipes.CLEAN_OIL.get()).save(consumer, REFERENCE.MODID+":clean_oil");
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.ITEM_TENT.get()).pattern(" W ").pattern("WBW").define('W', wool).define('B', beds).unlockedBy("has_wool", has(wool)).unlockedBy("has_bed", has(beds)).save(consumer);
    }

    private @NotNull JsonObject enchantment(int level, Enchantment enchantment) {
        JsonObject nbt = new JsonObject();
        JsonArray enchantmentarray = new JsonArray();
        JsonObject enchantment1 = new JsonObject();
        enchantment1.addProperty("lvl", level);
        enchantment1.addProperty("id", RegUtil.id(enchantment).toString());
        enchantmentarray.add(enchantment1);
        nbt.add("Enchantments", enchantmentarray);
        return nbt;
    }

    private @NotNull ResourceLocation general(String path) {
        return modId("general/" + path);
    }

    private @NotNull ResourceLocation hunter(String path) {
        return modId("hunter/" + path);
    }

    private @NotNull ResourceLocation modId(@NotNull String path) {
        return new ResourceLocation(REFERENCE.MODID, path);
    }

    private @NotNull Ingredient potion(Potion @NotNull ... potion) {
        return new NBTIngredient(Arrays.stream(potion).map(p -> PotionUtils.setPotion(new ItemStack(Items.POTION, 1), p)).toArray(ItemStack[]::new));
    }

    private @NotNull Ingredient potion(@NotNull Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION, 1);
        PotionUtils.setPotion(stack, potion);
        return new NBTIngredient(stack);
    }

    protected void coffinFromWoolOrDye(Consumer<FinishedRecipe> consumer, ItemLike coffin, ItemLike wool, ItemLike dye, ResourceLocation path) {
        coffinFromWool(consumer, coffin, wool, path);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, coffin).requires(ModBlocks.COFFIN_WHITE.get()).requires(dye).unlockedBy("has_coffin", has(ModBlocks.COFFIN_WHITE.get())).unlockedBy("has_dye", has(dye)).save(consumer, path.withPath(p -> p + "_from_white"));
    }

    protected void coffinFromWool(Consumer<FinishedRecipe> consumer, ItemLike coffin, ItemLike wool, ResourceLocation path) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, coffin).pattern("XXX").pattern("YYY").pattern("XXX").define('X', ItemTags.PLANKS).define('Y', wool).unlockedBy("has_wool", has(wool)).save(consumer, path);
    }

    private @NotNull ResourceLocation vampire(String path) {
        return modId("vampire/" + path);
    }

    private static class Shapeless extends ShapelessRecipeBuilder {
        public Shapeless(@NotNull RecipeCategory category, @NotNull ItemLike itemProvider, int amount) {
            super(category, itemProvider, amount);
        }

        public @NotNull ShapelessRecipeBuilder addIngredient(@NotNull TagKey<Item> tag, int amount) {
            return this.requires(Ingredient.of(tag), amount);
        }
    }

    private static class Shaped extends ShapedRecipeBuilder {
        private final @NotNull ItemStack stack;

        public Shaped(@NotNull RecipeCategory category, @NotNull ItemStack resultIn) {
            super(category, resultIn.getItem(), resultIn.getCount());
            this.stack = resultIn;
        }

        @Override
        public void save(@NotNull Consumer<FinishedRecipe> consumerIn, @NotNull ResourceLocation id) {
            this.ensureValid(id);
            this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
            consumerIn.accept(new Result(id, this.count, this.group == null ? "" : this.group, this.rows, determineBookCategory(((ShapedRecipeBuilderAccessor) this).getRecipeCategory()), this.key, this.advancement, id.withPath("recipes/" + ((ShapedRecipeBuilderAccessor) this).getRecipeCategory().getFolderName() + "/" + id.getPath()), this.stack, ((ShapedRecipeBuilderAccessor) this).getShowNotification()));
        }

        private static class Result extends ShapedRecipeBuilder.Result {
            private final @NotNull ItemStack stack;

            public Result(@NotNull ResourceLocation idIn, int countIn, @NotNull String groupIn, @NotNull List<String> patternIn, CraftingBookCategory category, @NotNull Map<Character, Ingredient> keyIn, Advancement.@NotNull Builder advancementBuilderIn, @NotNull ResourceLocation advancementIdIn, @NotNull ItemStack stack, boolean showNotification) {
                super(idIn, stack.getItem(), countIn, groupIn, category, patternIn, keyIn, advancementBuilderIn, advancementIdIn, showNotification);
                this.stack = stack;
            }

            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
                super.serializeRecipeData(json);
                JsonObject result = json.get("result").getAsJsonObject();
                result.entrySet().clear();
                result.addProperty("item", RegUtil.id(this.stack.getItem()).toString());
                result.addProperty("count", this.stack.getCount());
                if (stack.hasTag()) {
                    result.addProperty("nbt", this.stack.getTag().toString());
                }
            }
        }
    }
}
