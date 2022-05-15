package de.teamlapen.vampirism.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.data.recipebuilder.AlchemicalCauldronRecipeBuilder;
import de.teamlapen.vampirism.data.recipebuilder.IItemWIthTierRecipeBuilder;
import de.teamlapen.vampirism.data.recipebuilder.ShapedWeaponTableRecipeBuilder;
import de.teamlapen.vampirism.data.recipebuilder.ShapelessWeaponTableRecipeBuilder;
import de.teamlapen.vampirism.inventory.recipes.ConfigCondition;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.NBTIngredient;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
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
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
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
        ItemLike leather = Items.LEATHER;
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


        ShapedRecipeBuilder.shaped(ModBlocks.BLOOD_GRINDER.get()).define('Z', hopper).define('Y', planks).define('D', diamond).define('X', iron_ingot).pattern(" Z ").pattern("YDY").pattern("YXY").unlockedBy("has_hopper", has(hopper)).save(consumer, general("blood_grinder"));
        ShapedRecipeBuilder.shaped(ModBlocks.BLOOD_SIEVE.get()).define('X', iron_ingot).define('Q', quartz_block).define('Y', planks).define('Z', cauldron).pattern("XQX").pattern("YZY").pattern("YXY").unlockedBy("has_cauldron", has(cauldron)).save(consumer, general("blood_sieve"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get(), 8).requires(castle_block_normal_brick, 8).requires(black_dye).unlockedBy("has_castle_brick", has(castle_block_normal_brick)).unlockedBy("has_black_dye", has(black_dye)).save(consumer, modId("general/castle_block_dark_brick_0"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get(), 7).requires(stone_bricks, 7).requires(black_dye).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_dark_brick_1"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.CASTLE_BLOCK_DARK_STONE.get(), 7).requires(stone, 7).requires(black_dye).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_dark_stone"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get(), 8).requires(stone_bricks, 8).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_normal_brick"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get(), 8).requires(castle_block_normal_brick, 8).requires(vampire_orchid).unlockedBy("has_orchid", has(vampire_orchid)).save(consumer, general("castle_block_purple_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), 6).pattern("###").define('#', castle_block_dark_brick).unlockedBy("has_castle_brick", has(castle_block_dark_brick)).save(consumer, modId("general/castle_slab_dark_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.CASTLE_SLAB_DARK_STONE.get(), 6).pattern("###").define('#', castle_block_dark_stone).unlockedBy("has_castle_brick", has(castle_block_dark_stone)).save(consumer, modId("general/castle_slab_dark_stone"));
        ShapedRecipeBuilder.shaped(ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get(), 6).pattern("###").define('#', castle_block_purple_brick).unlockedBy("has_castle_brick", has(castle_block_purple_brick)).save(consumer, modId("general/castle_slab_purple_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.CASTLE_STAIRS_DARK_BRICK.get(), 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_dark_brick).unlockedBy("has_castle_brick", has(castle_block_dark_brick)).save(consumer, modId("general/castle_stairs_dark_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.CASTLE_STAIRS_DARK_STONE.get(), 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_dark_stone).unlockedBy("has_castle_brick", has(castle_block_dark_stone)).save(consumer, general("castle_stairs_dark_stone"));
        ShapedRecipeBuilder.shaped(ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get(), 4).pattern("#  ").pattern("## ").pattern("###").define('#', castle_block_purple_brick).unlockedBy("has_castle_brick", has(castle_block_purple_brick)).save(consumer, general("castle_stairs_purple_brick"));
        ShapedRecipeBuilder.shaped(ModBlocks.ALTAR_CLEANSING.get()).pattern(" X ").pattern("YYY").pattern(" Y ").define('X', vampire_book).define('Y', planks).unlockedBy("has_vampire_book", has(planks)).save(consumer, general("altar_cleansing"));
        ShapedRecipeBuilder.shaped(ModBlocks.ALTAR_CLEANSING.get()).pattern("XZX").pattern("YYY").pattern(" Y ").define('X', vampire_fang).define('Y', planks).define('Z', book).unlockedBy("has_book", has(book)).save(consumer, general("altar_cleansing_new"));
        ShapedRecipeBuilder.shaped(ModBlocks.FIRE_PLACE.get()).pattern(" X ").pattern("XYX").define('X', logs).define('Y', coal_block).unlockedBy("has_logs", has(logs)).save(consumer, general("fire_place"));
        ShapelessRecipeBuilder.shapeless(ModItems.GARLIC_BREAD.get()).requires(garlic).requires(bread).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bread", has(bread)).save(consumer, general("garlic_bread"));
        ShapedRecipeBuilder.shaped(ModItems.INJECTION_EMPTY.get()).pattern(" X ").pattern(" X ").pattern(" Y ").define('X', glass).define('Y', glass_pane).unlockedBy("has_glass", has(glass)).unlockedBy("has_glass_pane", has(glass_pane)).save(consumer, general("injection_0"));
        ShapelessRecipeBuilder.shapeless(ModItems.INJECTION_GARLIC.get()).requires(injection_empty).requires(garlic).unlockedBy("has_injection", has(injection_empty)).save(consumer, general("injection_1"));
        ShapelessRecipeBuilder.shapeless(ModItems.INJECTION_SANGUINARE.get()).requires(injection_empty).requires(vampire_fang, 8).unlockedBy("has_injection", has(injection_empty)).save(consumer, general("injection_2"));
        ShapedRecipeBuilder.shaped(ModBlocks.TOTEM_BASE.get()).pattern("XYX").pattern("XYX").pattern("ZZZ").define('X', planks).define('Y', obsidian).define('Z', iron_ingot).unlockedBy("has_obsidian", has(obsidian)).save(consumer, general("totem_base"));
        ShapedRecipeBuilder.shaped(ModBlocks.TOTEM_TOP_CRAFTED.get()).pattern("X X").pattern(" Y ").pattern("XZX").define('X', obsidian).define('Y', diamond).define('Z', vampire_book).unlockedBy("has_diamond", has(diamondBlock)).unlockedBy("has_obsidian", has(obsidian)).save(consumer, general("totem_top"));
        ConditionalRecipe.builder().addCondition(new ConfigCondition("umbrella")).addRecipe((consumer1) -> ShapedRecipeBuilder.shaped(ModItems.UMBRELLA.get()).pattern("###").pattern("BAB").pattern(" A ").define('#', wool).define('A', stick).define('B', vampire_orchid).unlockedBy("has_wool", has(wool)).save(consumer1, general("umbrella"))).build(consumer, general("umbrella"));

        ShapedRecipeBuilder.shaped(ModBlocks.ALCHEMICAL_CAULDRON.get()).pattern("XZX").pattern("XXX").pattern("Y Y").define('X', iron_ingot).define('Y', stone_bricks).define('Z', garlic).unlockedBy("has_iron", has(iron_ingot)).save(consumer, hunter("alchemical_cauldron"));
        ShapedRecipeBuilder.shaped(ModBlocks.POTION_TABLE.get()).pattern("XXX").pattern("Y Y").pattern("ZZZ").define('X', glass_bottle).define('Y', planks).define('Z', iron_ingot).unlockedBy("has_glass_bottle", has(glass_bottle)).save(consumer, hunter("potion_table"));
        ShapedRecipeBuilder.shaped(ModBlocks.GARLIC_DIFFUSER_NORMAL.get()).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('O', obsidian).define('Z', garlic_diffuser_core).unlockedBy("has_diamond", has(diamond)).save(consumer, hunter("garlic_diffuser_normal"));
        ShapedRecipeBuilder.shaped(ModBlocks.HUNTER_TABLE.get()).pattern("XYW").pattern("ZZZ").pattern("Z Z").define('X', vampire_fang).define('Y', book).define('Z', planks).define('W', garlic).unlockedBy("has_fang", has(vampire_fang)).save(consumer, hunter("hunter_table"));
        ShapedRecipeBuilder.shaped(ModBlocks.MED_CHAIR.get()).pattern("XYX").pattern("XXX").pattern("XZX").define('X', iron_ingot).define('Y', wool).define('Z', glass_bottle).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("item_med_chair"));
        ShapedRecipeBuilder.shaped(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get()).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('Z', garlic_diffuser_core_improved).define('O', obsidian).unlockedBy("has_garlic_diffuser", has(garlic_diffuser_normal)).save(consumer, hunter("garlic_diffuser_improved"));
        ShapedRecipeBuilder.shaped(ModItems.STAKE.get()).pattern("X").pattern("Y").pattern("X").define('X', stick).define('Y', planks).unlockedBy("has_sticks", has(stick)).save(consumer, hunter("stake"));
        ShapedRecipeBuilder.shaped(ModBlocks.WEAPON_TABLE.get()).pattern("X  ").pattern("YYY").pattern(" Z ").define('X', bucket).define('Y', iron_ingot).define('Z', iron_block).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("weapon_table"));
        ShapedRecipeBuilder.shaped(ModItems.CROSSBOW_ARROW_NORMAL.get(), 6).pattern("X").pattern("Y").define('X', iron_ingot).define('Y', stick).unlockedBy("has_iron_ingot", has(iron_ingot)).save(consumer, hunter("crossbow_arrow_normal"));
        ShapelessRecipeBuilder.shapeless(ModItems.CROSSBOW_ARROW_NORMAL.get()).requires(Items.ARROW).unlockedBy("has_arrow", has(Items.ARROW)).save(consumer, hunter("crossbow_arrow_from_vanilla"));
        ShapelessRecipeBuilder.shapeless(ModItems.PURE_BLOOD_0.get()).requires(ModItems.PURE_BLOOD_1.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_1)).save(consumer, hunter("pure_blood0"));
        ShapelessRecipeBuilder.shapeless(ModItems.PURE_BLOOD_1.get()).requires(ModItems.PURE_BLOOD_2.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_2)).save(consumer, hunter("pure_blood1"));
        ShapelessRecipeBuilder.shapeless(ModItems.PURE_BLOOD_2.get()).requires(ModItems.PURE_BLOOD_3.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_3)).save(consumer, hunter("pure_blood2"));
        ShapelessRecipeBuilder.shapeless(ModItems.PURE_BLOOD_3.get()).requires(ModItems.PURE_BLOOD_4.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_4)).save(consumer, hunter("pure_blood3"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.CASTLE_BLOCK_DARK_BRICK.get()).unlockedBy("has_castle_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_block_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.CASTLE_STAIRS_DARK_STONE.get()).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.CASTLE_STAIRS_DARK_STONE.get()).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.CASTLE_STAIRS_DARK_BRICK.get()).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.CASTLE_STAIRS_DARK_BRICK.get()).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_purple_brick), ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get()).unlockedBy("has_stone", has(castle_block_purple_brick)).save(consumer, modId("stonecutting/castle_stairs_purple_brick_from_castle_block_purple_brick"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.CASTLE_SLAB_DARK_STONE.get(), 2).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_stone), ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), 2).unlockedBy("has_stone", has(castle_block_dark_stone)).save(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), 2).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_dark_brick), ModBlocks.CASTLE_SLAB_DARK_STONE.get(), 2).unlockedBy("has_stone", has(castle_block_dark_brick)).save(consumer, modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(castle_block_purple_brick), ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get(), 2).unlockedBy("has_stone", has(castle_block_purple_brick)).save(consumer, modId("stonecutting/castle_slaps_purple_brick_from_castle_block_purple_brick"));

        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.PURE_SALT.get(), 4).withIngredient(garlic).withFluid(new FluidStack(Fluids.WATER, 1)).withSkills(HunterSkills.BASIC_ALCHEMY.get()).cookTime(1200).build(consumer, modId("pure_salt"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 4).withIngredient(gun_powder).withFluid(holy_water_bottle_normal).build(consumer, modId("alchemical_fire_4"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 5).withIngredient(gun_powder).withFluid(holy_water_bottle_enhanced).build(consumer, modId("alchemical_fire_5"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 6).withIngredient(gun_powder).withFluid(holy_water_bottle_ultimate).build(consumer, modId("alchemical_fire_6"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE.get()).withIngredient(wool).withFluid(garlic).withSkills(HunterSkills.GARLIC_DIFFUSER.get()).build(consumer, modId("garlic_diffuser_core"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED.get()).withIngredient(garlic_diffuser_core).withFluid(holy_water_bottle_ultimate).withSkills(HunterSkills.GARLIC_DIFFUSER_IMPROVED.get()).experience(2.0f).build(consumer, modId("garlic_diffuser" +
                "_core_improved"));
        AlchemicalCauldronRecipeBuilder.cauldronRecipe(ModItems.PURIFIED_GARLIC.get(), 2).withIngredient(garlic).withFluid(holy_water).withSkills(HunterSkills.PURIFIED_GARLIC.get()).build(consumer, modId("purified_garlic"));

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get()).lava(1).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get()).lava(1).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get()).lava(1).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("    ").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XXXX").pattern("XYYX").pattern("XZZX").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get()).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', leather).define('Y', garlic).define('Z', potion(Potions.SWIFTNESS)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get()).lava(3).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', leather).define('Y', garlic).define('Z', gold_ingot).save(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_CHEST_NORMAL.get()).lava(2).pattern("XWWX").pattern("XZZX").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', leather).define('Z', garlic).define('W', vampire_fang).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_CHEST_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XWWX").pattern("XZZX").pattern("XYYX").pattern("XYYX").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).define('W', vampire_fang).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_LEGS_NORMAL.get()).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', iron_ingot).define('Z', garlic).define('Y', leather).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_LEGS_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', iron_ingot).define('Z', garlic).define('Y', diamond).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_HEAD_NORMAL.get()).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', iron_ingot).define('Y', leather).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_HEAD_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_FEET_NORMAL.get()).lava(2).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', leather).define('Z', garlic).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_COAT_FEET_ENHANCED.get()).lava(5).skills(HunterSkills.ENHANCED_ARMOR.get()).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', iron_ingot).define('Y', diamond).define('Z', garlic).save(consumer);

        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.BASIC_CROSSBOW.get()).lava(1).pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', iron_ingot).define('Y', string).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.BASIC_DOUBLE_CROSSBOW.get()).lava(1).skills(HunterSkills.DOUBLE_CROSSBOW.get()).pattern("YXXY").pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', iron_ingot).define('Y', string).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.BASIC_TECH_CROSSBOW.get()).lava(5).skills(HunterSkills.TECH_WEAPONS.get()).pattern("YXXY").pattern("XZZX").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).define('Z', diamond).save(consumer);
        ShapelessWeaponTableRecipeBuilder.shapelessWeaponTable(ModItems.CROSSBOW_ARROW_SPITFIRE.get(), 3).lava(1).requires(crossbow_arrow_normal, 3).requires(alchemical_fire).unlockedBy("has_crossbow_arrow_normal", has(crossbow_arrow_normal)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), 3).lava(1).pattern(" X ").pattern("XYX").pattern(" Z ").pattern(" W ").define('X', garlic).define('Y', gold_ingot).define('Z', stick).define('W', feather).unlockedBy("has_crossbow_arrow_normal", has(crossbow_arrow_normal)).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ENHANCED_CROSSBOW.get()).lava(2).skills(HunterSkills.ENHANCED_WEAPONS.get()).pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).lava(3).skills(HunterSkills.DOUBLE_CROSSBOW.get(), HunterSkills.ENHANCED_WEAPONS.get()).pattern("YXXY").pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', iron_ingot).define('Y', string).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.ENHANCED_TECH_CROSSBOW.get()).lava(5).skills(HunterSkills.TECH_WEAPONS.get()).pattern("YXXY").pattern("XZZX").pattern("XZZX").pattern(" XX ").define('X', iron_ingot).define('Y', string).define('Z', diamond).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_HAT_HEAD_0.get()).pattern(" YY ").pattern(" YY ").pattern("XXXX").define('X', iron_ingot).define('Y', black_wool).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_HAT_HEAD_1.get()).lava(1).pattern(" YY ").pattern("XXXX").define('X', iron_ingot).define('Y', black_wool).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.PITCHFORK.get()).pattern("X X").pattern("YYY").pattern(" Y ").pattern(" Y ").define('X', iron_ingot).define('Y', stick).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.TECH_CROSSBOW_AMMO_PACKAGE.get()).lava(1).pattern(" XZ ").pattern("YYYY").pattern("YYYY").pattern("YYYY").define('X', iron_ingot).define('Y', crossbow_arrow_normal).define('Z', planks).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_AXE_NORMAL.get(), 1, enchantment(2, Enchantments.KNOCKBACK)).lava(5).pattern("XXZY").pattern("XXZY").pattern("  ZY").pattern("  Z ").define('X', iron_ingot).define('Y', garlic).define('Z', stick).save(consumer);
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.HUNTER_AXE_ENHANCED.get(), 1, enchantment(3, Enchantments.KNOCKBACK)).lava(5).skills(HunterSkills.ENHANCED_WEAPONS.get()).pattern("XWZY").pattern("XWZY").pattern("  ZY").pattern("  Z ").define('X', iron_ingot).define('Y', garlic).define('W', diamond).define('Z', stick).save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.ALTAR_INFUSION.get()).pattern("YZY").pattern("ZZZ").define('Y', gold_ingot).define('Z', obsidian).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("altar_infusion"));
        ShapedRecipeBuilder.shaped(ModBlocks.ALTAR_INSPIRATION.get()).pattern(" X ").pattern("XYX").pattern("ZZZ").define('X', glass).define('Y', glass_bottle).define('Z', iron_ingot).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("altar_inspiration"));
        ShapedRecipeBuilder.shaped(ModBlocks.ALTAR_PILLAR.get()).pattern("X X").pattern("   ").pattern("XXX").define('X', stone_bricks).unlockedBy("has_stones", has(stone_bricks)).save(consumer, vampire("altar_pillar"));
        ShapedRecipeBuilder.shaped(ModBlocks.ALTAR_TIP.get()).pattern(" X ").pattern("XYX").define('X', iron_ingot).define('Y', iron_block).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("altar_tip"));
        ShapelessRecipeBuilder.shapeless(Items.GLASS_BOTTLE).requires(blood_bottle).unlockedBy("has_blood_bottle", has(blood_bottle)).save(consumer, vampire("blood_bottle_to_glass"));
        ShapedRecipeBuilder.shaped(ModBlocks.BLOOD_CONTAINER.get()).pattern("XYX").pattern("YZY").pattern("XYX").define('X', planks).define('Y', glass).define('Z', iron_ingot).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_container"));
        new Shapeless(ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get(), 3).addIngredient(iron_ingot, 3).requires(pure_blood_4).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_infused_enhanced_iron_ingot"));
        new Shapeless(ModItems.BLOOD_INFUSED_IRON_INGOT.get(), 3).addIngredient(iron_ingot, 3).requires(Ingredient.of(pure_blood_0, pure_blood_1, pure_blood_2, pure_blood_3)).unlockedBy("has_iron", has(iron_ingot)).save(consumer, vampire("blood_infused_iron_ingot"));
        ShapedRecipeBuilder.shaped(ModBlocks.BLOOD_PEDESTAL.get()).pattern("GYG").pattern("YZY").pattern("XXX").define('X', obsidian).define('Y', planks).define('Z', blood_bottle).define('G', gold_ingot).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("blood_pedestal"));
        ShapedRecipeBuilder.shaped(ModItems.HEART_SEEKER_ENHANCED.get()).pattern("X").pattern("X").pattern("Y").define('X', blood_infused_enhanced_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_enhanced_iron_ingot)).save(consumer, vampire("heart_seeker_enhanced"));
        ShapedRecipeBuilder.shaped(ModItems.HEART_STRIKER_ENHANCED.get()).pattern("XX").pattern("XX").pattern("YY").define('X', blood_infused_enhanced_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_enhanced_iron_ingot)).save(consumer, vampire("heart_striker_enhanced"));
        ShapedRecipeBuilder.shaped(ModItems.HEART_SEEKER_NORMAL.get()).pattern("X").pattern("X").pattern("Y").define('X', blood_infused_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_iron_ingot)).save(consumer, vampire("heart_seeker_normal"));
        ShapedRecipeBuilder.shaped(ModItems.HEART_STRIKER_NORMAL.get()).pattern("XX").pattern("XX").pattern("YY").define('X', blood_infused_iron_ingot).define('Y', stick).unlockedBy("has_ingot", has(blood_infused_iron_ingot)).save(consumer, vampire("heart_striker_normal"));

        ShapedRecipeBuilder.shaped(ModBlocks.coffin_white.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.WHITE_WOOL).unlockedBy("has_wool", has(Items.WHITE_WOOL)).save(consumer, vampire("coffin_white"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_orange.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.ORANGE_WOOL).unlockedBy("has_wool", has(Items.ORANGE_WOOL)).save(consumer, vampire("coffin_orange"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_magenta.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.MAGENTA_WOOL).unlockedBy("has_wool", has(Items.MAGENTA_WOOL)).save(consumer, vampire("coffin_magenta"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_light_blue.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.LIGHT_BLUE_WOOL).unlockedBy("has_wool", has(Items.LIGHT_BLUE_WOOL)).save(consumer, vampire("coffin_light_blue"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_yellow.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.YELLOW_WOOL).unlockedBy("has_wool", has(Items.YELLOW_WOOL)).save(consumer, vampire("coffin_yellow"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_lime.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.LIME_WOOL).unlockedBy("has_wool", has(Items.LIME_WOOL)).save(consumer, vampire("coffin_lime"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_pink.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.PINK_WOOL).unlockedBy("has_wool", has(Items.PINK_WOOL)).save(consumer, vampire("coffin_pink"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_gray.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.GRAY_WOOL).unlockedBy("has_wool", has(Items.GRAY_WOOL)).save(consumer, vampire("coffin_gray"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_light_gray.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.LIGHT_GRAY_WOOL).unlockedBy("has_wool", has(Items.LIGHT_GRAY_WOOL)).save(consumer, vampire("coffin_light_gray"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_cyan.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.CYAN_WOOL).unlockedBy("has_wool", has(Items.CYAN_WOOL)).save(consumer, vampire("coffin_cyan"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_purple.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.PURPLE_WOOL).unlockedBy("has_wool", has(Items.PURPLE_WOOL)).save(consumer, vampire("coffin_purple"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_blue.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.BLUE_WOOL).unlockedBy("has_wool", has(Items.BLUE_WOOL)).save(consumer, vampire("coffin_blue"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_brown.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.BROWN_WOOL).unlockedBy("has_wool", has(Items.BROWN_WOOL)).save(consumer, vampire("coffin_brown"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_green.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.GREEN_WOOL).unlockedBy("has_wool", has(Items.GREEN_WOOL)).save(consumer, vampire("coffin_green"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_red.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.RED_WOOL).unlockedBy("has_wool", has(Items.RED_WOOL)).save(consumer, vampire("coffin_red"));
        ShapedRecipeBuilder.shaped(ModBlocks.coffin_black.get()).pattern("XXX").pattern("YYY").pattern("XXX").define('X', planks).define('Y', Items.BLACK_WOOL).unlockedBy("has_wool", has(Items.BLACK_WOOL)).save(consumer, vampire("coffin_black"));


        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', blue_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_blue"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOAK_BLACK_RED.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', red_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_red"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', white_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_black_white"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', white_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_white_black"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOAK_RED_BLACK.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', red_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(consumer, vampire("vampire_cloak_red_black"));
        ItemStack blood_bottle_stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        blood_bottle_stack.setDamageValue(0);
        ConditionalRecipe.builder().addCondition(new NotCondition(new ConfigCondition("auto_convert"))).addRecipe((consumer1 -> new Shaped(blood_bottle_stack).pattern("XYX").pattern(" X ").define('X', glass).define('Y', rotten_flesh).unlockedBy("has_glass", has(glass)).save(consumer1, vampire("blood_bottle")))).build(consumer, vampire("blood_bottle"));

        new IItemWIthTierRecipeBuilder(ModItems.HEART_SEEKER_NORMAL.get(), 1).pattern(" X ").pattern("XYX").define('X', blood_infused_iron_ingot).define('Y', ModItems.HEART_SEEKER_NORMAL.get()).unlockedBy("has_heart_seeker", has(ModItems.HEART_SEEKER_NORMAL.get())).save(consumer, vampire("heart_seeker_normal_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.HEART_STRIKER_NORMAL.get(), 1).pattern("XXX").pattern("XYX").define('X', blood_infused_iron_ingot).define('Y', ModItems.HEART_STRIKER_NORMAL.get()).unlockedBy("has_heart_striker", has(ModItems.HEART_STRIKER_NORMAL.get())).save(consumer, vampire("heart_striker_normal_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.HEART_SEEKER_ENHANCED.get(), 1).pattern(" X ").pattern("XYX").define('X', blood_infused_enhanced_iron_ingot).define('Y', ModItems.HEART_SEEKER_ENHANCED.get()).unlockedBy("has_heart_seeker", has(ModItems.HEART_SEEKER_ENHANCED.get())).save(consumer, vampire("heart_seeker_enhanced_repair"));
        new IItemWIthTierRecipeBuilder(ModItems.HEART_STRIKER_ENHANCED.get(), 1).pattern("XXX").pattern("XYX").define('X', blood_infused_enhanced_iron_ingot).define('Y', ModItems.HEART_STRIKER_ENHANCED.get()).unlockedBy("has_heart_striker", has(ModItems.HEART_STRIKER_ENHANCED.get())).save(consumer, vampire("heart_striker_enhanced_repair"));

        //noinspection ConstantConditions
        ConditionalRecipe.builder().addCondition(new ModLoadedCondition("guideapi_vp")).addRecipe((consumer1 -> ShapelessRecipeBuilder.shapeless(ForgeRegistries.ITEMS.getValue(new ResourceLocation("guideapi_vp", "vampirism-guidebook"))).requires(vampire_fang).requires(book).unlockedBy("has_fang", has(vampire_fang)).save(consumer1, modId("general/guidebook")))).build(consumer, modId("general/guidebook"));

        ShapedRecipeBuilder.shaped(ModItems.CURE_APPLE.get()).pattern("YXY").pattern("YZY").pattern("YYY").define('Y', Items.GOLD_NUGGET).define('Z', Items.APPLE).define('X', ModItems.INJECTION_ZOMBIE_BLOOD.get()).unlockedBy("has_apple", has(Items.APPLE)).unlockedBy("has_zombie_blood", has(ModItems.INJECTION_ZOMBIE_BLOOD.get())).save(consumer, general("cure_item"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOTHING_LEGS.get()).pattern("XXX").pattern("X X").pattern("XYX").define('X', Items.GRAY_WOOL).define('Y', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.GRAY_WOOL)).save(consumer, vampire("vampire_clothing_legs"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOTHING_BOOTS.get()).pattern("XYX").pattern("X X").define('X', Items.BROWN_WOOL).define('Y', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.BROWN_WOOL)).save(consumer, vampire("vampire_clothing_boots"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOTHING_HAT.get()).pattern("ZXX").pattern(" Y ").pattern("XXX").define('X', Items.BLACK_WOOL).define('Y', Items.RED_WOOL).define('Z', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.BLACK_WOOL)).save(consumer, vampire("vampire_clothing_hat"));
        ShapedRecipeBuilder.shaped(ModItems.VAMPIRE_CLOTHING_CROWN.get()).pattern("XYX").pattern("XXX").define('X', Items.GOLD_INGOT).define('Y', Ingredient.of(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(consumer, vampire("vampire_clothing_crown"));

        ShapedRecipeBuilder.shaped(ModBlocks.CROSS.get()).pattern(" X ").pattern("XYX").pattern(" X ").define('X', planks).define('Y', holy_water).unlockedBy("has_planks", has(planks)).unlockedBy("has_holy", has(holy_water)).save(consumer, hunter("cross"));
        ShapedRecipeBuilder.shaped(ModItems.ITEM_CANDELABRA.get()).pattern("XXX").pattern("YYY").pattern("ZAZ").define('X', string).define('Y', Items.HONEYCOMB).define('Z', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_string", has(string)).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("candelabra"));
        ShapedRecipeBuilder.shaped(ModItems.ITEM_CANDELABRA.get()).pattern("YYY").pattern("ZAZ").define('Y', ItemTags.CANDLES).define('Z', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(ItemTags.CANDLES)).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_gold", has(gold_ingot)).save(consumer, vampire("candelabra_candles"));
        ShapedRecipeBuilder.shaped(ModBlocks.CHANDELIER.get()).pattern("XYX").pattern("ZYZ").pattern("BAB").define('X', string).define('Y', ModItems.ITEM_CANDELABRA.get()).define('Z', Items.HONEYCOMB).define('B', iron_ingot).define('A', gold_ingot).unlockedBy("has_string", has(string)).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_candelabra", has(ModItems.ITEM_CANDELABRA.get())).save(consumer, vampire("chandelier"));
        ShapedRecipeBuilder.shaped(ModBlocks.CHANDELIER.get()).pattern(" Y ").pattern("ZYZ").pattern("BAB").define('Y', ModItems.ITEM_CANDELABRA.get()).define('Z', ItemTags.CANDLES).define('B', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(ItemTags.CANDLES)).unlockedBy("has_candelabra", has(ModItems.ITEM_CANDELABRA.get())).save(consumer, vampire("chandelier_candle"));
        ShapedRecipeBuilder.shaped(ModItems.GARLIC_FINDER.get()).pattern("XXX").pattern("XYX").pattern("ZAZ").define('X', blood_infused_iron_ingot).define('Y', garlic).define('Z', planks).define('A', Tags.Items.DUSTS_REDSTONE).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bloodiron", has(blood_infused_iron_ingot)).unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE)).save(consumer, vampire("garlic_finder"));
        ShapedRecipeBuilder.shaped(ModBlocks.TOMBSTONE2.get()).pattern("XX ").pattern("XYX").pattern("XXX").define('X', Blocks.COBBLESTONE).define('Y', Tags.Items.STONE).unlockedBy("has_coble", has(Blocks.COBBLESTONE)).unlockedBy("has_stone", has(Tags.Items.STONE)).save(consumer, general("tombstone2"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.TOMBSTONE1.get()).requires(ModBlocks.TOMBSTONE2.get()).unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2.get())).save(consumer, general("tombstone1"));
        ShapelessRecipeBuilder.shapeless(ModBlocks.TOMBSTONE3.get()).requires(ModBlocks.TOMBSTONE2.get()).requires(Blocks.COBBLESTONE).unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2.get())).save(consumer, general("tombstone3"));
        ShapedRecipeBuilder.shaped(ModBlocks.GRAVE_CAGE.get()).pattern(" X ").pattern("XYX").pattern("XYX").define('X', iron_ingot).define('Y', Items.COAL).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_coal", has(Items.COAL)).save(consumer, general("grave_cage"));

        planksFromLog(consumer,ModBlocks.DARK_SPRUCE_PLANKS.get(), ModTags.Items.DARK_SPRUCE_LOG);
        planksFromLog(consumer,ModBlocks.CURSED_SPRUCE_PLANKS.get(), ModTags.Items.CURSED_SPRUCE_LOG);
        woodFromLogs(consumer,ModBlocks.DARK_SPRUCE_WOOD.get(), ModBlocks.DARK_SPRUCE_LOG.get());
        woodFromLogs(consumer,ModBlocks.CURSED_SPRUCE_WOOD.get(), ModBlocks.CURSED_SPRUCE_LOG.get());
        woodFromLogs(consumer,ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
        woodFromLogs(consumer,ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());

        ShapedRecipeBuilder.shaped(ModBlocks.THRONE.get()).pattern(" YZ").pattern("YYZ").pattern("XZX").define('Y', Blocks.RED_CARPET).define('Z', ItemTags.PLANKS).define('X', Items.STICK).unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wool", has(Blocks.RED_CARPET)).save(consumer, general("throne"));
        ShapedRecipeBuilder.shaped(ModBlocks.VAMPIRE_RACK.get()).pattern("XYX").pattern("ABC").pattern("XYX").define('X', ItemTags.PLANKS).define('Y', Items.BOOK).define('A', ModItems.VAMPIRE_FANG.get()).define('B', Items.GLASS_BOTTLE).define('C', Items.HONEYCOMB).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_book", has(Items.BOOK)).unlockedBy("has_fangs", has(ModItems.VAMPIRE_FANG.get())).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_potion", has(Items.GLASS_BOTTLE)).save(consumer, general("vampire_rack"));

        ShapedRecipeBuilder.shaped(ModItems.CRUCIFIX_NORMAL.get()).pattern("XY ").pattern("ZYZ").pattern(" Y ").define('X', ModItems.HOLY_SALT.get()).define('Y', planks).define('Z', stick).unlockedBy("salt", has(ModItems.HOLY_SALT.get())).unlockedBy("stick", has(stick)).unlockedBy("planks", has(planks)).save(consumer, hunter("crucifix"));
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.CRUCIFIX_ENHANCED.get()).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.HOLY_SALT.get()).define('Y', iron_ingot).define('Z', ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).define('A', ModItems.STAKE.get()).unlockedBy("iron", has(iron_ingot)).unlockedBy("blessed_salt", has(ModItems.HOLY_SALT.get())).unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_NORMAL.get())).unlockedBy("stake", has(ModItems.STAKE.get())).skills(HunterSkills.crucifix_wielder.get()).save(consumer, hunter("crucifix_enhanced"));
        ShapedWeaponTableRecipeBuilder.shapedWeaponTable(ModItems.CRUCIFIX_ULTIMATE.get()).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.ITEM_ALCHEMICAL_FIRE.get()).define('Y', Tags.Items.STORAGE_BLOCKS_GOLD).define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).define('A', ModItems.STAKE.get()).unlockedBy("fire", has(ModItems.ITEM_ALCHEMICAL_FIRE.get())).unlockedBy("gold", has(Tags.Items.STORAGE_BLOCKS_GOLD)).unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get())).unlockedBy("stake", has(ModItems.STAKE.get())).skills(HunterSkills.ultimate_crucifix.get()).save(consumer, hunter("crucifix_ultimate"));
    }

    private JsonObject enchantment(int level, Enchantment enchantment) {
        JsonObject nbt = new JsonObject();
        JsonArray enchantmentarray = new JsonArray();
        JsonObject enchantment1 = new JsonObject();
        enchantment1.addProperty("lvl", level);
        enchantment1.addProperty("id", RegUtil.id(enchantment).toString());
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

    private static class Shapeless extends ShapelessRecipeBuilder {
        public Shapeless(ItemLike itemProvider, int amount) {
            super(itemProvider, amount);
        }

        public ShapelessRecipeBuilder addIngredient(TagKey<Item> tag, int amount) {
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
        public void save(Consumer<FinishedRecipe> consumerIn, @Nonnull ResourceLocation id) {
            this.ensureValid(id);
            this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
            consumerIn.accept(new Result(id, this.count, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath()), this.stack));

        }

        private static class Result extends ShapedRecipeBuilder.Result {
            private final ItemStack stack;

            public Result(ResourceLocation idIn, int countIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, ItemStack stack) {
                super(idIn, stack.getItem(), countIn, groupIn, patternIn, keyIn, advancementBuilderIn, advancementIdIn);
                this.stack = stack;
            }

            @Override
            public void serializeRecipeData(@Nonnull JsonObject json) {
                super.serializeRecipeData(json);
                JsonObject result = json.get("result").getAsJsonObject();
                result.entrySet().clear();
                result.addProperty("item", RegUtil.id(this.stack.getItem()).toString());
                result.addProperty("count", this.stack.getCount());
                if (stack.hasTag())
                    result.addProperty("nbt", this.stack.getTag().toString());
            }
        }
    }
}
