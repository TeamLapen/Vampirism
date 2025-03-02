package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import de.teamlapen.vampirism.data.ModBlockFamilies;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.items.component.OilContent;
import de.teamlapen.vampirism.items.component.PureLevel;
import de.teamlapen.vampirism.recipes.ApplicableOilRecipe;
import de.teamlapen.vampirism.recipes.CleanOilRecipe;
import de.teamlapen.vampirism.recipes.ConfigCondition;
import de.teamlapen.vampirism.util.ItemDataUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VResourceLocation.modString;

public class RecipesProvider extends de.teamlapen.vampirism.data.provider.parent.RecipesProvider {

    protected RecipesProvider(HolderLookup.Provider p_360573_, RecipeOutput p_360872_) {
        super(p_360573_, p_360872_);
    }

    @Override
    @SuppressWarnings("UnreachableCode")
    protected void buildRecipes() {
        HolderLookup.RegistryLookup<Item> itemLookup = this.registries.lookupOrThrow(Registries.ITEM);
        ItemLike hopper = Blocks.HOPPER;
        ItemLike cauldron = Blocks.CAULDRON;
        ItemLike stone_bricks = Blocks.STONE_BRICKS;
        ItemLike vampire_orchid = ModBlocks.VAMPIRE_ORCHID.get();
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
        ItemLike holy_water_bottle_normal = ModItems.HOLY_WATER_BOTTLE_NORMAL.get();
        ItemLike holy_water_bottle_enhanced = ModItems.HOLY_WATER_BOTTLE_ENHANCED.get();
        ItemLike holy_water_bottle_ultimate = ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get();
        ItemLike string = Items.STRING;
        ItemLike black_wool = Items.BLACK_WOOL;
        ItemLike blue_wool = Items.BLUE_WOOL;
        ItemLike white_wool = Items.WHITE_WOOL;
        ItemLike red_wool = Items.RED_WOOL;
        ItemLike blood_bottle = ModItems.BLOOD_BOTTLE.get();
        ItemLike pure_blood_0 = ModItems.PURE_BLOOD_0.get();
        ItemLike pure_blood_1 = ModItems.PURE_BLOOD_1.get();
        ItemLike pure_blood_2 = ModItems.PURE_BLOOD_2.get();
        ItemLike pure_blood_3 = ModItems.PURE_BLOOD_3.get();
        ItemLike pure_blood_4 = ModItems.PURE_BLOOD_4.get();
        ItemLike blood_infused_iron_ingot = ModItems.BLOOD_INFUSED_IRON_INGOT.get();
        ItemLike rotten_flesh = Items.ROTTEN_FLESH;
        ItemLike amulet = ModItems.AMULET.get();
        ItemLike ring = ModItems.RING.get();
        ItemLike obi_belt = ModItems.OBI_BELT.get();
        ItemLike blood_container = ModBlocks.BLOOD_CONTAINER.get();
        ItemLike basalt = Blocks.BASALT;
        ItemLike mother_core = ModItems.MOTHER_CORE.get();
        ItemLike cursed_spruce_planks = ModBlocks.CURSED_SPRUCE_PLANKS.get();
        TagKey<Item> planks = ItemTags.PLANKS;
        TagKey<Item> glass = Tags.Items.GLASS_BLOCKS;
        TagKey<Item> glass_pane = Tags.Items.GLASS_PANES;
        TagKey<Item> logs = ItemTags.LOGS;
        TagKey<Item> diamond = Tags.Items.GEMS_DIAMOND;
        TagKey<Item> diamondBlock = Tags.Items.STORAGE_BLOCKS_DIAMOND;
        TagKey<Item> iron_ingot = Tags.Items.INGOTS_IRON;
        TagKey<Item> coal_block = Tags.Items.STORAGE_BLOCKS_COAL;
        TagKey<Item> garlic = ModItemTags.GARLIC;
        TagKey<Item> obsidian = Tags.Items.OBSIDIANS;
        TagKey<Item> wool = ItemTags.WOOL;
        TagKey<Item> stick = Tags.Items.RODS_WOODEN;
        TagKey<Item> iron_block = Tags.Items.STORAGE_BLOCKS_IRON;
        TagKey<Item> gold_ingot = Tags.Items.INGOTS_GOLD;
        TagKey<Item> pure_blood = ModItemTags.PURE_BLOOD;
        TagKey<Item> holy_water = ModItemTags.HOLY_WATER;
        TagKey<Item> heart = ModItemTags.HEART;
        TagKey<Item> beds = ItemTags.BEDS;


        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_GRINDER.get()).define('Z', hopper).define('Y', planks).define('D', diamond).define('X', iron_ingot).pattern(" Z ").pattern("YDY").pattern("YXY").unlockedBy("has_hopper", has(hopper)).save(output, general("blood_grinder"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_SIEVE.get()).define('X', iron_ingot).define('Q', Blocks.QUARTZ_BRICKS).define('Y', planks).define('Z', cauldron).pattern("XQX").pattern("YZY").pattern("YXY").unlockedBy("has_cauldron", has(cauldron)).save(output, general("blood_sieve"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING.get()).pattern(" X ").pattern("YYY").pattern(" Y ").define('X', vampire_book).define('Y', planks).unlockedBy("has_vampire_book", has(planks)).save(output, general("altar_cleansing"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING.get()).pattern("XZX").pattern("YYY").pattern(" Y ").define('X', vampire_fang).define('Y', planks).define('Z', book).unlockedBy("has_book", has(book)).save(output, general("altar_cleansing_new"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.FIRE_PLACE.get()).pattern(" X ").pattern("XYX").define('X', logs).define('Y', coal_block).unlockedBy("has_logs", has(logs)).save(output, general("fire_place"));
        shapeless(RecipeCategory.FOOD, ModItems.GARLIC_BREAD.get()).requires(garlic).requires(bread).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bread", has(bread)).save(output, general("garlic_bread"));
        shaped(RecipeCategory.MISC, ModItems.INJECTION_EMPTY.get()).pattern(" X ").pattern(" X ").pattern(" Y ").define('X', glass).define('Y', glass_pane).unlockedBy("has_glass", has(glass)).unlockedBy("has_glass_pane", has(glass_pane)).save(output, general("injection_0"));
        shapeless(RecipeCategory.MISC, ModItems.INJECTION_GARLIC.get()).requires(injection_empty).requires(garlic).unlockedBy("has_injection", has(injection_empty)).save(output, general("injection_1"));
        shapeless(RecipeCategory.MISC, ModItems.INJECTION_SANGUINARE.get()).requires(injection_empty).requires(vampire_fang, 8).unlockedBy("has_injection", has(injection_empty)).save(output, general("injection_2"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.TOTEM_BASE.get()).pattern("XYX").pattern("XYX").pattern("ZZZ").define('X', planks).define('Y', obsidian).define('Z', iron_ingot).unlockedBy("has_obsidian", has(obsidian)).save(output, general("totem_base"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.TOTEM_TOP_CRAFTED.get()).pattern("X X").pattern(" Y ").pattern("XZX").define('X', obsidian).define('Y', diamond).define('Z', vampire_book).unlockedBy("has_diamond", has(diamondBlock)).unlockedBy("has_obsidian", has(obsidian)).save(output, general("totem_top"));
        shaped(RecipeCategory.MISC, ModItems.UMBRELLA.get()).pattern("###").pattern("BAB").pattern(" A ").define('#', wool).define('A', stick).define('B', vampire_orchid).unlockedBy("has_wool", has(wool)).save(output.withConditions(new ConfigCondition("umbrella")), general("umbrella"));

        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALCHEMICAL_CAULDRON.get()).pattern("XZX").pattern("XXX").pattern("Y Y").define('X', iron_ingot).define('Y', stone_bricks).define('Z', garlic).unlockedBy("has_iron", has(iron_ingot)).save(output, hunter("alchemical_cauldron"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.POTION_TABLE.get()).pattern("XXX").pattern("Y Y").pattern("ZZZ").define('X', glass_bottle).define('Y', planks).define('Z', iron_ingot).unlockedBy("has_glass_bottle", has(glass_bottle)).save(output, hunter("potion_table"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_NORMAL.get()).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('O', obsidian).define('Z', garlic_diffuser_core).unlockedBy("has_diamond", has(diamond)).save(output, hunter("garlic_diffuser_normal"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.HUNTER_TABLE.get()).pattern("XYW").pattern("ZZZ").pattern("Z Z").define('X', vampire_fang).define('Y', book).define('Z', planks).define('W', garlic).unlockedBy("has_fang", has(vampire_fang)).save(output, hunter("hunter_table"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.MED_CHAIR.get()).pattern("XYX").pattern("XXX").pattern("XZX").define('X', iron_ingot).define('Y', wool).define('Z', glass_bottle).unlockedBy("has_iron_ingot", has(iron_ingot)).save(output, hunter("item_med_chair"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_IMPROVED.get()).pattern("XYX").pattern("YZY").pattern("OOO").define('X', planks).define('Y', diamond).define('Z', garlic_diffuser_core_improved).define('O', obsidian).unlockedBy("has_garlic_diffuser", has(garlic_diffuser_normal)).save(output, hunter("garlic_diffuser_improved"));
        shaped(RecipeCategory.COMBAT, ModItems.STAKE.get()).pattern("X").pattern("Y").pattern("X").define('X', stick).define('Y', planks).unlockedBy("has_sticks", has(stick)).save(output, hunter("stake"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.WEAPON_TABLE.get()).pattern("X  ").pattern("YYY").pattern(" Z ").define('X', bucket).define('Y', iron_ingot).define('Z', iron_block).unlockedBy("has_iron_ingot", has(iron_ingot)).save(output, hunter("weapon_table"));
        shaped(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL.get(), 6).pattern("X").pattern("Y").define('X', iron_ingot).define('Y', stick).unlockedBy("has_iron_ingot", has(iron_ingot)).save(output, hunter("crossbow_arrow_normal"));
        shapeless(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL.get()).requires(Items.ARROW).unlockedBy("has_arrow", has(Items.ARROW)).save(output, hunter("crossbow_arrow_from_vanilla"));
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_0.get()).requires(ModItems.PURE_BLOOD_1.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_1)).save(output, hunter("pure_blood0"));
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_1.get()).requires(ModItems.PURE_BLOOD_2.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_2)).save(output, hunter("pure_blood1"));
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_2.get()).requires(ModItems.PURE_BLOOD_3.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_3)).save(output, hunter("pure_blood2"));
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_3.get()).requires(ModItems.PURE_BLOOD_4.get()).requires(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).unlockedBy("has_pure_blood", has(pure_blood_4)).save(output, hunter("pure_blood3"));


        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_INFUSION.get()).pattern("YZY").pattern("ZZZ").define('Y', gold_ingot).define('Z', obsidian).unlockedBy("has_gold", has(gold_ingot)).save(output, vampire("altar_infusion"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_INSPIRATION.get()).pattern("X X").pattern("XYX").pattern("XXX").define('X', planks).define('Y', blood_container).unlockedBy("has_planks", has(planks)).unlockedBy("has_blood_container", has(blood_container)).save(output, vampire("altar_inspiration"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_PILLAR.get()).pattern("X X").pattern("   ").pattern("XXX").define('X', stone_bricks).unlockedBy("has_stones", has(stone_bricks)).save(output, vampire("altar_pillar"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_TIP.get()).pattern(" X ").pattern("XYX").define('X', iron_ingot).define('Y', iron_block).unlockedBy("has_iron", has(iron_ingot)).save(output, vampire("altar_tip"));
        shapeless(RecipeCategory.MISC, Items.GLASS_BOTTLE).requires(blood_bottle).unlockedBy("has_blood_bottle", has(blood_bottle)).save(output, vampire("blood_bottle_to_glass"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_CONTAINER.get()).pattern("XYX").pattern("YZY").pattern("XYX").define('X', planks).define('Y', glass).define('Z', iron_ingot).unlockedBy("has_iron", has(iron_ingot)).save(output, vampire("blood_container"));
        shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_IRON_INGOT, 3).requires(tag(iron_ingot), 3).requires(pure_blood_4).unlockedBy("has_iron", has(iron_ingot)).save(output, vampire("blood_infused_enhanced_iron_ingot"));
        shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_IRON_INGOT, 3).requires(tag(iron_ingot), 3).requires(Ingredient.of(pure_blood_0, pure_blood_1, pure_blood_2, pure_blood_3)).unlockedBy("has_iron", has(iron_ingot)).save(output, vampire("blood_infused_iron_ingot"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_PEDESTAL.get()).pattern("GYG").pattern("YZY").pattern("XXX").define('X', obsidian).define('Y', planks).define('Z', blood_bottle).define('G', gold_ingot).unlockedBy("has_gold", has(gold_ingot)).save(output, vampire("blood_pedestal"));

        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', blue_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(output, vampire("vampire_cloak_black_blue"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_BLACK_RED.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', red_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(output, vampire("vampire_cloak_black_red"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', white_wool).define('Y', black_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(output, vampire("vampire_cloak_black_white"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', white_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(output, vampire("vampire_cloak_white_black"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOAK_RED_BLACK.get()).pattern("YZY").pattern("XAX").pattern("Y Y").define('X', black_wool).define('Y', red_wool).define('Z', diamond).define('A', pure_blood).unlockedBy("has_pure_blood", has(pure_blood)).save(output, vampire("vampire_cloak_red_black"));
        ItemStack blood_bottle_stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        blood_bottle_stack.setDamageValue(0);
        ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.MISC, blood_bottle_stack).pattern("XYX").pattern(" X ").define('X', glass).define('Y', rotten_flesh).unlockedBy("has_glass", has(glass)).save(output.withConditions(new NotCondition(new ConfigCondition("auto_convert"))), vampire("blood_bottle"));

        BuiltInRegistries.ITEM.getOptional(VResourceLocation.loc("guideapi_vp", "vampirism-guidebook")).ifPresent(guideBook -> {
            shapeless(RecipeCategory.MISC, guideBook).requires(vampire_fang).requires(book).unlockedBy("has_fang", has(vampire_fang)).save(output.withConditions(new ModLoadedCondition("guideapi_vp")), modString("general/guidebook"));
        });

        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_LEGS.get()).pattern("XXX").pattern("X X").pattern("XYX").define('X', Items.GRAY_WOOL).define('Y', tag(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.GRAY_WOOL)).save(output, vampire("vampire_clothing_legs"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_BOOTS.get()).pattern("XYX").pattern("X X").define('X', Items.BROWN_WOOL).define('Y', tag(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.BROWN_WOOL)).save(output, vampire("vampire_clothing_boots"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_HAT.get()).pattern("ZXX").pattern(" Y ").pattern("XXX").define('X', Items.BLACK_WOOL).define('Y', Items.RED_WOOL).define('Z', tag(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_wool", has(Items.BLACK_WOOL)).save(output, vampire("vampire_clothing_hat"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_CROWN.get()).pattern("XYX").pattern("XXX").define('X', Items.GOLD_INGOT).define('Y', tag(heart)).unlockedBy("has_heart", has(heart)).unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(output, vampire("vampire_clothing_crown"));

        shaped(RecipeCategory.DECORATIONS, ModBlocks.CROSS.get()).pattern(" X ").pattern("XYX").pattern(" X ").define('X', planks).define('Y', holy_water).unlockedBy("has_planks", has(planks)).unlockedBy("has_holy", has(holy_water)).save(output, hunter("cross"));
        shaped(RecipeCategory.DECORATIONS, ModItems.ITEM_CANDELABRA.get()).pattern("XXX").pattern("YYY").pattern("ZAZ").define('X', string).define('Y', Items.HONEYCOMB).define('Z', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_string", has(string)).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_gold", has(gold_ingot)).save(output, vampire("candelabra"));
        shaped(RecipeCategory.DECORATIONS, ModItems.ITEM_CANDELABRA.get()).pattern("YYY").pattern("ZAZ").define('Y', ItemTags.CANDLES).define('Z', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(ItemTags.CANDLES)).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_gold", has(gold_ingot)).save(output, vampire("candelabra_candles"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.CHANDELIER.get()).pattern("XYX").pattern("ZYZ").pattern("BAB").define('X', string).define('Y', ModItems.ITEM_CANDELABRA.get()).define('Z', Items.HONEYCOMB).define('B', iron_ingot).define('A', gold_ingot).unlockedBy("has_string", has(string)).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_candelabra", has(ModItems.ITEM_CANDELABRA.get())).save(output, vampire("chandelier"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.CHANDELIER.get()).pattern(" Y ").pattern("ZYZ").pattern("BAB").define('Y', ModItems.ITEM_CANDELABRA.get()).define('Z', ItemTags.CANDLES).define('B', iron_ingot).define('A', gold_ingot).unlockedBy("has_honey", has(ItemTags.CANDLES)).unlockedBy("has_candelabra", has(ModItems.ITEM_CANDELABRA.get())).save(output, vampire("chandelier_candle"));
        shaped(RecipeCategory.MISC, ModItems.GARLIC_FINDER.get()).pattern("XXX").pattern("XYX").pattern("ZAZ").define('X', blood_infused_iron_ingot).define('Y', garlic).define('Z', planks).define('A', Tags.Items.DUSTS_REDSTONE).unlockedBy("has_garlic", has(garlic)).unlockedBy("has_bloodiron", has(blood_infused_iron_ingot)).unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE)).save(output, vampire("garlic_finder"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE2.get()).pattern("XX ").pattern("XYX").pattern("XXX").define('X', Blocks.COBBLESTONE).define('Y', Tags.Items.STONES).unlockedBy("has_coble", has(Blocks.COBBLESTONE)).unlockedBy("has_stone", has(Tags.Items.STONES)).save(output, general("tombstone2"));
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE1.get()).requires(ModBlocks.TOMBSTONE2.get()).unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2.get())).save(output, general("tombstone1"));
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE3.get()).requires(ModBlocks.TOMBSTONE2.get()).requires(Blocks.COBBLESTONE).unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2.get())).save(output, general("tombstone3"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GRAVE_CAGE.get()).pattern(" X ").pattern("XYX").pattern("XYX").define('X', iron_ingot).define('Y', Items.COAL).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_coal", has(Items.COAL)).save(output, general("grave_cage"));

        generateRecipes(ModBlockFamilies.DARK_SPRUCE_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.CURSED_SPRUCE_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.DARK_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.PURPLE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.DARK_STONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.POLISHED_DARK_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.COBBLED_DARK_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.DARK_STONE_TILES, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateRecipes(ModBlockFamilies.PURPLE_STONE_TILES, FeatureFlagSet.of(FeatureFlags.VANILLA));

        planksFromLog(ModBlocks.DARK_SPRUCE_PLANKS.get(), ModItemTags.DARK_SPRUCE_LOG, 4);
        planksFromLog(ModBlocks.CURSED_SPRUCE_PLANKS.get(), ModItemTags.CURSED_SPRUCE_LOG, 4);
        woodFromLogs(ModBlocks.DARK_SPRUCE_WOOD.get(), ModBlocks.DARK_SPRUCE_LOG.get());
        woodFromLogs(ModBlocks.CURSED_SPRUCE_WOOD.get(), ModBlocks.CURSED_SPRUCE_LOG.get());
        woodFromLogs(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
        woodFromLogs(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());
        woodenBoat(ModItems.DARK_SPRUCE_BOAT.get(), ModBlocks.DARK_SPRUCE_PLANKS.get());
        woodenBoat(ModItems.CURSED_SPRUCE_BOAT.get(), ModBlocks.CURSED_SPRUCE_PLANKS.get());
        chestBoat(ModItems.DARK_SPRUCE_CHEST_BOAT.get(), ModItems.DARK_SPRUCE_BOAT.get());
        chestBoat(ModItems.CURSED_SPRUCE_CHEST_BOAT.get(), ModItems.CURSED_SPRUCE_BOAT.get());

        shaped(RecipeCategory.DECORATIONS, ModBlocks.THRONE.get()).pattern(" YZ").pattern("YYZ").pattern("XZX").define('Y', Blocks.RED_CARPET).define('Z', ItemTags.PLANKS).define('X', Items.STICK).unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wool", has(Blocks.RED_CARPET)).save(output, general("throne"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.VAMPIRE_RACK.get()).pattern("XYX").pattern("ABC").pattern("XYX").define('X', ItemTags.PLANKS).define('Y', Items.BOOK).define('A', ModItems.VAMPIRE_FANG.get()).define('B', Items.GLASS_BOTTLE).define('C', Items.HONEYCOMB).unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_book", has(Items.BOOK)).unlockedBy("has_fangs", has(ModItems.VAMPIRE_FANG.get())).unlockedBy("has_honey", has(Items.HONEYCOMB)).unlockedBy("has_potion", has(Items.GLASS_BOTTLE)).save(output, general("vampire_rack"));

        shaped(RecipeCategory.COMBAT, ModItems.CRUCIFIX_NORMAL.get()).pattern("XY ").pattern("ZYZ").pattern(" Y ").define('X', ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).define('Y', planks).define('Z', stick).unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_NORMAL.get())).unlockedBy("stick", has(stick)).unlockedBy("planks", has(planks)).save(output, hunter("crucifix"));
        SpecialRecipeBuilder.special(ApplicableOilRecipe::new).save(output, REFERENCE.MODID + ":applicable_oil");


        SimpleCookingRecipeBuilder.smelting(Ingredient.of(amulet, ring), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.1f, 200).unlockedBy("has_amulet", has(amulet)).unlockedBy("has_ring", has(ring)).save(output, modString("gold_nugget_from_accessory_smelting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(amulet, ring), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.1f, 100).unlockedBy("has_amulet", has(amulet)).unlockedBy("has_ring", has(ring)).save(output, modString("gold_nugget_from_accessory_blasting"));
        shapeless(RecipeCategory.COMBAT, Items.LEATHER).requires(obi_belt).unlockedBy("has_obi_belt", has(obi_belt)).save(output, modString("leather_from_obi_belt"));
        shaped(RecipeCategory.COMBAT, ModBlocks.ALCHEMY_TABLE.get()).pattern("B  ").pattern("BBB").pattern("P P").define('B', basalt).define('P', planks).unlockedBy("has_basalt", has(basalt)).unlockedBy("has_planks", has(planks)).save(output);
        SpecialRecipeBuilder.special(CleanOilRecipe::new).save(output, REFERENCE.MODID + ":clean_oil");
        shaped(RecipeCategory.DECORATIONS, ModItems.ITEM_TENT.get()).pattern(" W ").pattern("WBW").define('W', wool).define('B', beds).unlockedBy("has_wool", has(wool)).unlockedBy("has_bed", has(beds)).save(output);
        shapeless(RecipeCategory.DECORATIONS, holy_water_bottle_normal, 2).requires(holy_water_bottle_enhanced).requires(ModItems.PURE_SALT_WATER.get()).unlockedBy("has_enhanced_holy_water", has(holy_water_bottle_enhanced)).save(output, "holy_water_bottle_normal_from_enhanced");
        shapeless(RecipeCategory.DECORATIONS, holy_water_bottle_enhanced, 2).requires(holy_water_bottle_ultimate).requires(ModItems.PURE_SALT_WATER.get()).unlockedBy("has_ultimate_holy_water", has(holy_water_bottle_enhanced)).save(output, "holy_water_bottle_enhanced_from_ultimate");
        hangingSign(ModItems.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
        hangingSign(ModItems.CURSED_SPRUCE_HANGING_SIGN.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.COBBLED_DARK_STONE.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_STONE.get(), 0.1f, 200).unlockedBy("has_cobbled_dark_stone", has(ModBlocks.COBBLED_DARK_STONE.get())).save(output, modString("dark_stone_from_cobbled_dark_stone_smelting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModBlocks.COBBLED_DARK_STONE.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_STONE.get(), 0.1f, 100).unlockedBy("has_cobbled_dark_stone", has(ModBlocks.COBBLED_DARK_STONE.get())).save(output, modString("dark_stone_from_cobbled_dark_stone_blasting"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BAT_CAGE.get()).pattern("GGG").pattern("GPG").pattern("PPP").define('G', gold_ingot).define('P', planks).unlockedBy("has_gold", has(gold_ingot)).unlockedBy("has_planks", has(planks)).save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.FOG_DIFFUSER.get()).pattern("XYX").pattern("YZY").pattern("OOO").define('X', cursed_spruce_planks).define('Y', diamond).define('O', obsidian).define('Z', mother_core).unlockedBy("has_diamond", has(diamond)).unlockedBy("has_cursed_plank", has(cursed_spruce_planks)).unlockedBy("has_mother_core", has(mother_core)).save(output, vampire("fog_diffuser"));
        shaped(RecipeCategory.MISC, ModBlocks.VAMPIRE_BEACON.get()).pattern("GGG").pattern("GCG").pattern("OOO").define('G', Items.GLASS).define('C', mother_core).define('O', obsidian).unlockedBy("has_mother_core", has(mother_core)).unlockedBy("has_obsidian", has(obsidian)).unlockedBy("has_glass", has(Items.GLASS)).save(output);


        shaped(RecipeCategory.DECORATIONS, ModItems.CANDLE_STICK.get()).pattern(" X ").pattern("YYY").define('X', iron_ingot).define('Y', Items.IRON_NUGGET).unlockedBy("has_iron", has(iron_ingot)).unlockedBy("has_nugget", has(Items.IRON_NUGGET)).save(output, vampire("candle_stick"));
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.VAMPIRE_SOUL_LANTERN.get()).requires(ModItems.SOUL_ORB_VAMPIRE).requires(Items.SOUL_LANTERN).unlockedBy("has_soul_orb", has(ModItems.SOUL_ORB_VAMPIRE)).unlockedBy("has_soul_lantern", has(Items.SOUL_LANTERN)).save(output);
        alchemyTable();
        stoneCutterRecipes();
        alchemyCauldron();
        weaponTable();
        coffins();
        infuser();
    }

    private void stoneCutterRecipes() {
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_BRICK_WALL.get(), ModBlocks.PURPLE_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_BRICK_SLAB.get(), ModBlocks.PURPLE_STONE_BRICKS.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_BRICK_STAIRS.get(), ModBlocks.PURPLE_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES.get(), ModBlocks.PURPLE_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_WALL.get(), ModBlocks.PURPLE_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_WALL.get(), ModBlocks.PURPLE_STONE_TILES.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_SLAB.get(), ModBlocks.PURPLE_STONE_BRICKS.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_SLAB.get(), ModBlocks.PURPLE_STONE_TILES.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_STAIRS.get(), ModBlocks.PURPLE_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_STAIRS.get(), ModBlocks.PURPLE_STONE_TILES.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_SLAB.get(), ModBlocks.DARK_STONE_BRICKS.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_SLAB.get(), ModBlocks.DARK_STONE_TILES.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_SLAB.get(), ModBlocks.COBBLED_DARK_STONE.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.COBBLED_DARK_STONE_SLAB.get(), ModBlocks.COBBLED_DARK_STONE.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.COBBLED_DARK_STONE_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.COBBLED_DARK_STONE_WALL.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_SLAB.get(), ModBlocks.POLISHED_DARK_STONE.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_STAIRS.get(), ModBlocks.POLISHED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_WALL.get(), ModBlocks.POLISHED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_SLAB.get(), ModBlocks.COBBLED_DARK_STONE.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_WALL.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICKS.get(), ModBlocks.POLISHED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICKS.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_STAIRS.get(), ModBlocks.DARK_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_STAIRS.get(), ModBlocks.POLISHED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_SLAB.get(), ModBlocks.COBBLED_DARK_STONE.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_SLAB.get(), ModBlocks.DARK_STONE_BRICKS.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_SLAB.get(), ModBlocks.POLISHED_DARK_STONE.get(), 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_WALL.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_WALL.get(), ModBlocks.DARK_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_WALL.get(), ModBlocks.POLISHED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES.get(), ModBlocks.POLISHED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES.get(), ModBlocks.DARK_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_STAIRS.get(), ModBlocks.DARK_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_STAIRS.get(), ModBlocks.DARK_STONE_TILES.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_WALL.get(), ModBlocks.DARK_STONE_BRICKS.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_WALL.get(), ModBlocks.DARK_STONE_TILES.get());
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_WALL.get(), ModBlocks.COBBLED_DARK_STONE.get());
    }

    private void alchemyTable() {
        alchemyTable(ModOils.PLANT)
                .ingredient(Ingredient.of(Items.GLASS_BOTTLE))
                .input(Ingredient.of(Items.WHEAT_SEEDS))
                .unlockedBy("has_bottles", has(Items.GLASS_BOTTLE)).unlockedBy("has_wheat_seeds", has(Items.WHEAT_SEEDS))
                .save(output, modString("plant_oil"));
        alchemyTable(ModOils.VAMPIRE_BLOOD)
                .plantOilIngredient()
                .input(Ingredient.of(ModItems.VAMPIRE_BLOOD_BOTTLE.get())).unlockedBy("has_wheat_seeds", has(ModItems.VAMPIRE_BLOOD_BOTTLE.get()))
                .save(output, modString("vampire_blood_oil"));
        alchemyTable(ModOils.HEALING)
                .bloodOilIngredient()
                .input(potion(Potions.HEALING, Potions.STRONG_HEALING))
                .save(output, modString("healing_oil"));
        alchemyTable(ModOils.POISON)
                .bloodOilIngredient()
                .input(potion(Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON))
                .save(output, modString("poison_oil"));
        alchemyTable(ModOils.WEAKNESS)
                .bloodOilIngredient()
                .input(potion(Potions.WEAKNESS, Potions.LONG_WEAKNESS))
                .save(output, modString("weakness_oil"));
        alchemyTable(ModOils.SLOWNESS)
                .bloodOilIngredient()
                .input(potion(Potions.SLOWNESS, Potions.STRONG_SLOWNESS, Potions.LONG_SLOWNESS))
                .save(output, modString("slowness_oil"));
        alchemyTable(ModOils.FIRE_RESISTANCE)
                .bloodOilIngredient()
                .input(potion(Potions.FIRE_RESISTANCE, Potions.LONG_FIRE_RESISTANCE))
                .save(output, modString("fire_resistance_oil"));
        alchemyTable(ModOils.SWIFTNESS)
                .bloodOilIngredient()
                .input(potion(Potions.SWIFTNESS, Potions.LONG_SWIFTNESS, Potions.STRONG_SWIFTNESS))
                .save(output, modString("swiftness_oil"));
        alchemyTable(ModOils.REGENERATION)
                .bloodOilIngredient()
                .input(potion(Potions.REGENERATION, Potions.LONG_REGENERATION, Potions.STRONG_REGENERATION))
                .save(output, modString("regeneration_oil"));
        alchemyTable(ModOils.NIGHT_VISION)
                .bloodOilIngredient()
                .input(potion(Potions.NIGHT_VISION, Potions.LONG_NIGHT_VISION))
                .save(output, modString("night_vision_oil"));
        alchemyTable(ModOils.STRENGTH)
                .bloodOilIngredient()
                .input(potion(Potions.STRENGTH, Potions.STRONG_STRENGTH, Potions.LONG_STRENGTH))
                .save(output, modString("strength_oil"));
        alchemyTable(ModOils.JUMP)
                .bloodOilIngredient()
                .input(potion(Potions.LEAPING, Potions.LONG_LEAPING, Potions.STRONG_LEAPING))
                .save(output, modString("jump_oil"));
        alchemyTable(ModOils.WATER_BREATHING)
                .bloodOilIngredient()
                .input(potion(Potions.WATER_BREATHING, Potions.LONG_WATER_BREATHING))
                .save(output, modString("water_breathing_oil"));
        alchemyTable(ModOils.INVISIBILITY)
                .bloodOilIngredient()
                .input(potion(Potions.INVISIBILITY, Potions.LONG_INVISIBILITY))
                .save(output, modString("invisibility_oil"));
        alchemyTable(ModOils.SLOW_FALLING)
                .bloodOilIngredient()
                .input(potion(Potions.SLOW_FALLING, Potions.LONG_SLOW_FALLING))
                .save(output, modString("slow_falling_oil"));
        alchemyTable(ModOils.LUCK)
                .bloodOilIngredient()
                .input(potion(Potions.LUCK))
                .save(output, modString("luck_oil"));
        alchemyTable(ModOils.SMELT)
                .bloodOilIngredient()
                .input(Ingredient.of(ModItems.ITEM_ALCHEMICAL_FIRE.get()))
                .save(output, modString("smelt_oil"));
        alchemyTable(ModOils.TELEPORT)
                .bloodOilIngredient()
                .input(Ingredient.of(Items.ENDER_PEARL))
                .save(output, modString("teleport_oil"));
        alchemyTable(ModOils.EVASION)
                .bloodOilIngredient()
                .input(Ingredient.of(Items.HONEY_BOTTLE))
                .save(output, modString("evasion_oil"));
        alchemyTable(ModOils.GARLIC).plantOilIngredient().input(tag(ModItemTags.GARLIC)).save(output, modString("garlic_oil"));
        alchemyTable(ModOils.SPITFIRE).plantOilIngredient().input(Ingredient.of(ModItems.ITEM_ALCHEMICAL_FIRE.get())).save(output, modString("spitfire_oil"));
        alchemyTable(ModOils.BLEEDING).plantOilIngredient().input(Ingredient.of(Items.AMETHYST_SHARD)).save(output, modString("bleeding_oil"));
        alchemyTable(ModOils.VAMPIRE_KILLER).oilIngredient(ModOils.GARLIC).input(tag(ModItemTags.HOLY_WATER)).save(output, modString("vampire_killer_oil"));
    }

    private void alchemyCauldron() {
        cauldronRecipe(ModItems.PURE_SALT, 4).withIngredient(ModItemTags.GARLIC).withFluid(new FluidStack(Fluids.WATER, 1)).withSkills(HunterSkills.BASIC_ALCHEMY).cookTime(1200).save(output);
        cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 4).withIngredient(Items.GUNPOWDER).withFluid(ModItems.HOLY_WATER_BOTTLE_NORMAL).save(output, modString("alchemical_fire_4"));
        cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 5).withIngredient(Items.GUNPOWDER).withFluid(ModItems.HOLY_WATER_BOTTLE_ENHANCED).save(output, modString("alchemical_fire_5"));
        cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE.get(), 6).withIngredient(Items.GUNPOWDER).withFluid(ModItems.HOLY_WATER_BOTTLE_ULTIMATE).save(output, modString("alchemical_fire_6"));
        cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE).withIngredient(ItemTags.WOOL).withFluid(ModItemTags.GARLIC).withSkills(HunterSkills.GARLIC_DIFFUSER).save(output);
        cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED).withIngredient(ModItems.GARLIC_DIFFUSER_CORE).withFluid(ModItems.HOLY_WATER_BOTTLE_ULTIMATE).withSkills(HunterSkills.GARLIC_DIFFUSER_IMPROVED).experience(2.0f).save(output);
        cauldronRecipe(ModItems.PURIFIED_GARLIC, 2).withIngredient(ModItemTags.GARLIC).withFluid(ModItemTags.HOLY_WATER).withSkills(HunterSkills.PURIFIED_GARLIC).save(output);
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK).withFluid(ModItems.PURE_BLOOD_0.get()).withIngredient(Items.IRON_BLOCK).cookTime(200).experience(0.1f).save(output, modString("blood_infused_iron_ingot_from_pure_blood_0"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK).withFluid(ModItems.PURE_BLOOD_1.get()).withIngredient(Items.IRON_BLOCK).cookTime(180).experience(0.15f).save(output, modString("blood_infused_iron_ingot_from_pure_blood_1"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK).withFluid(ModItems.PURE_BLOOD_2.get()).withIngredient(Items.IRON_BLOCK).cookTime(160).experience(0.2f).save(output, modString("blood_infused_iron_ingot_from_pure_blood_2"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK).withFluid(ModItems.PURE_BLOOD_3.get()).withIngredient(Items.IRON_BLOCK).cookTime(140).experience(0.25f).save(output, modString("blood_infused_iron_ingot_from_pure_blood_3"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK).withFluid(ModItems.PURE_BLOOD_4.get()).withIngredient(Items.IRON_BLOCK).cookTime(300).experience(0.3f).save(output, modString("blood_infused_enhanced_iron_ingot_from_pure_blood_4"));
    }

    private void weaponTable() {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get()).lava(1).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', potion(Potions.SWIFTNESS)).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get()).lava(3).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XZZX").pattern("XXXX").pattern("XYYX").pattern("XXXX").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', Tags.Items.INGOTS_GOLD).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get()).lava(1).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', potion(Potions.SWIFTNESS)).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get()).lava(3).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XZZX").pattern("XYYX").pattern("XXXX").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', Tags.Items.INGOTS_GOLD).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get()).lava(1).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("    ").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', potion(Potions.SWIFTNESS)).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get()).lava(3).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XXXX").pattern("XYYX").pattern("XZZX").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', Tags.Items.INGOTS_GOLD).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get()).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', potion(Potions.SWIFTNESS)).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get()).lava(3).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XXXX").pattern("XYYX").pattern("XZZX").pattern("X  X").define('X', Tags.Items.LEATHERS).define('Y', ModItemTags.GARLIC).define('Z', Tags.Items.INGOTS_GOLD).unlockedBy("has_leather", has(Tags.Items.LEATHERS)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD)).save(output);

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_NORMAL.get()).lava(2).pattern("XWWX").pattern("XZZX").pattern("XZZX").pattern("XYYX").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.LEATHERS).define('Z', ModItemTags.GARLIC).define('W', ModItems.VAMPIRE_FANG).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_ENHANCED.get()).lava(5).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XWWX").pattern("XZZX").pattern("XYYX").pattern("XYYX").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.GEMS_DIAMOND).define('Z', ModItemTags.GARLIC).define('W', ModItems.VAMPIRE_FANG).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_NORMAL.get()).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', Tags.Items.INGOTS_IRON).define('Z', ModItemTags.GARLIC).define('Y', Tags.Items.LEATHERS).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_ENHANCED.get()).lava(5).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("X  X").define('X', Tags.Items.INGOTS_IRON).define('Z', ModItemTags.GARLIC).define('Y', Tags.Items.GEMS_DIAMOND).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_NORMAL.get()).lava(2).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.LEATHERS).define('Z', ModItemTags.GARLIC).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_ENHANCED.get()).lava(5).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XYYX").pattern("XZZX").pattern("XZZX").pattern("    ").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.GEMS_DIAMOND).define('Z', ModItemTags.GARLIC).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_NORMAL.get()).lava(2).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.LEATHERS).define('Z', ModItemTags.GARLIC).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_ENHANCED.get()).lava(5).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("    ").pattern("X  X").pattern("XZZX").pattern("XYYX").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.GEMS_DIAMOND).define('Z', ModItemTags.GARLIC).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_garlic", has(ModItemTags.GARLIC)).save(output);

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_CROSSBOW.get()).lava(1).skills(HunterSkills.WEAPON_TABLE).pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.STRINGS).define('Z', ItemTags.PLANKS).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_DOUBLE_CROSSBOW.get()).lava(1).skills(HunterSkills.WEAPON_TABLE).pattern("YXXY").pattern("YXXY").pattern(" ZZ ").pattern(" ZZ ").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.STRINGS).define('Z', ItemTags.PLANKS).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_TECH_CROSSBOW.get()).lava(5).skills(HunterSkills.WEAPON_TABLE).pattern("YXXY").pattern("XZZX").pattern(" XX ").pattern(" XX ").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.STRINGS).define('Z', Tags.Items.GEMS_DIAMOND).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(output);

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_CROSSBOW.get()).lava(2).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', Tags.Items.INGOTS_IRON).define('Y', Tags.Items.STRINGS).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).lava(3).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("YXXY").pattern("YXXY").pattern(" XX ").pattern(" XX ").define('X', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('Y', Tags.Items.STRINGS).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_TECH_CROSSBOW.get()).lava(5).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("YXXY").pattern("XZZX").pattern("XZZX").pattern(" XX ").define('X', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('Y', Tags.Items.STRINGS).define('Z', Tags.Items.GEMS_DIAMOND).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_HEAD_0.get()).pattern(" YY ").pattern(" YY ").pattern("XXXX").define('X', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('Y', Items.BLACK_WOOL).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_HEAD_1.get()).lava(1).pattern(" YY ").pattern("XXXX").define('X', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('Y', Items.BLACK_WOOL).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.PITCHFORK.get()).pattern("X X").pattern("YYY").pattern(" Y ").pattern(" Y ").define('X', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('Y', Tags.Items.RODS_WOODEN).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.QUARREL_POUCH.get()).lava(1).pattern("ILLI").pattern("PLLP").pattern("ILLI").define('I', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('L', Tags.Items.LEATHERS).define('P', ItemTags.PLANKS).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_NORMAL.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 2)).lava(5).pattern("XXZY").pattern("XXZY").pattern("  ZY").pattern("  Z ").define('X', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('Y', ModItemTags.GARLIC).define('Z', Tags.Items.RODS_WOODEN).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_ENHANCED.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 3)).lava(5).skills(HunterSkills.MASTER_CRAFTSMANSHIP).pattern("XWZY").pattern("XWZY").pattern("  ZY").pattern("  Z ").define('X', Tags.Items.INGOTS_IRON).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).define('Y', ModItemTags.GARLIC).define('W', Tags.Items.GEMS_DIAMOND).define('Z', Tags.Items.RODS_WOODEN).save(output);

        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_SPITFIRE.get(), 1).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 1).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.SPITFIRE), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "spitfire_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_SPITFIRE.get(), 2).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 2).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.SPITFIRE), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "spitfire_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_SPITFIRE.get(), 3).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 3).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.SPITFIRE), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "spitfire_arrow_3");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_TELEPORT.get(), 1).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.TELEPORT), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output);
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_GARLIC.get(), 1).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 1).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.GARLIC), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "garlic_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_GARLIC.get(), 2).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 2).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.GARLIC), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "garlic_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_GARLIC.get(), 3).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 3).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.GARLIC), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "garlic_arrow_3");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_BLEEDING.get(), 1).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 1).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.BLEEDING), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "bleeding_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_BLEEDING.get(), 2).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 2).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.BLEEDING), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "bleeding_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_BLEEDING.get(), 3).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 3).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.BLEEDING), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "bleeding_arrow_3");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), 1).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 1).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_KILLER), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "vampire_killer_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), 2).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 2).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_KILLER), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "vampire_killer_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), 3).lava(1).requires(ModItems.CROSSBOW_ARROW_NORMAL, 3).requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_KILLER), ModItems.OIL_BOTTLE.get())).unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL)).save(output, "vampire_killer_arrow_3");
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CRUCIFIX_ENHANCED.get()).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).define('Y', Tags.Items.INGOTS_IRON).define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).define('A', ModItems.STAKE.get()).unlockedBy("iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_holy_water", has(ModItems.HOLY_WATER_BOTTLE_NORMAL.get())).unlockedBy("has_holy_water_enhanced", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get())).unlockedBy("stake", has(ModItems.STAKE.get())).skills(HunterSkills.CRUCIFIX_WIELDER).save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CRUCIFIX_ULTIMATE.get()).pattern("XYYX").pattern("YZAY").pattern("XYYX").pattern("XYYX").define('X', ModItems.ITEM_ALCHEMICAL_FIRE.get()).define('Y', Tags.Items.STORAGE_BLOCKS_GOLD).define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).define('A', ModItems.STAKE.get()).unlockedBy("fire", has(ModItems.ITEM_ALCHEMICAL_FIRE.get())).unlockedBy("gold", has(Tags.Items.STORAGE_BLOCKS_GOLD)).unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get())).unlockedBy("stake", has(ModItems.STAKE.get())).skills(HunterSkills.ULTIMATE_CRUCIFIX).save(output);


    }

    private void coffins() {
        coffinFromWool(output, ModBlocks.COFFIN_WHITE.get(), Items.WHITE_WOOL, vampire("coffin_white"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_ORANGE.get(), Items.ORANGE_WOOL, Items.ORANGE_DYE, vampire("coffin_orange"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_MAGENTA.get(), Items.MAGENTA_WOOL, Items.MAGENTA_DYE, vampire("coffin_magenta"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_LIGHT_BLUE.get(), Items.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE, vampire("coffin_light_blue"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_YELLOW.get(), Items.YELLOW_WOOL, Items.YELLOW_DYE, vampire("coffin_yellow"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_LIME.get(), Items.LIME_WOOL, Items.LIME_DYE, vampire("coffin_lime"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_PINK.get(), Items.PINK_WOOL, Items.PINK_DYE, vampire("coffin_pink"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_GRAY.get(), Items.GRAY_WOOL, Items.GRAY_DYE, vampire("coffin_gray"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_LIGHT_GRAY.get(), Items.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE, vampire("coffin_light_gray"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_CYAN.get(), Items.CYAN_WOOL, Items.CYAN_DYE, vampire("coffin_cyan"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_PURPLE.get(), Items.PURPLE_WOOL, Items.PURPLE_DYE, vampire("coffin_purple"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_BLUE.get(), Items.BLUE_WOOL, Items.BLUE_DYE, vampire("coffin_blue"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_BROWN.get(), Items.BROWN_WOOL, Items.BROWN_DYE, vampire("coffin_brown"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_GREEN.get(), Items.GREEN_WOOL, Items.GREEN_DYE, vampire("coffin_green"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_RED.get(), Items.RED_WOOL, Items.RED_DYE, vampire("coffin_red"));
        coffinFromWoolOrDye(output, ModBlocks.COFFIN_BLACK.get(), Items.BLACK_WOOL, Items.BLACK_DYE, vampire("coffin_black"));
    }

    private void infuser() {
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_IRON.toStack(), 0))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_0))
                .input(Ingredient.of(Items.RAW_IRON))
                .results(ModItems.VAMPIRE_BLOOD_BOTTLE.toStack())
                .burnTime(200)
                .unlockedBy("raw_iron", has(Items.RAW_IRON))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_0))
                .save(this.output, modString("raw_iron_pure_0"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_IRON.toStack(), 1))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_1))
                .input(Ingredient.of(Items.RAW_IRON))
                .results(ModItems.PURE_BLOOD_0.toStack())
                .burnTime(300)
                .unlockedBy("raw_iron", has(Items.RAW_IRON))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_1))
                .save(this.output, modString("raw_iron_pure_1"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_IRON.toStack(), 2))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_2))
                .input(Ingredient.of(Items.RAW_IRON))
                .results(ModItems.PURE_BLOOD_1.toStack())
                .burnTime(400)
                .unlockedBy("raw_iron", has(Items.RAW_IRON))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_2))
                .save(this.output, modString("raw_iron_pure_2"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_IRON.toStack(), 3))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_3))
                .input(Ingredient.of(Items.RAW_IRON))
                .results(ModItems.PURE_BLOOD_2.toStack())
                .burnTime(500)
                .unlockedBy("raw_iron", has(Items.RAW_IRON))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_3))
                .save(this.output, modString("raw_iron_pure_3"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_IRON.toStack(), 4))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_4))
                .input(Ingredient.of(Items.RAW_IRON))
                .results(ModItems.PURE_BLOOD_3.toStack())
                .burnTime(600)
                .unlockedBy("raw_iron", has(Items.RAW_IRON))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_4))
                .save(this.output, modString("raw_iron_pure_4"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_GOLD.toStack(), 0))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_0))
                .input(Ingredient.of(Items.RAW_GOLD))
                .results(ModItems.VAMPIRE_BLOOD_BOTTLE.toStack())
                .burnTime(200)
                .unlockedBy("raw_gold", has(Items.RAW_GOLD))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_0))
                .save(this.output, modString("raw_gold_pure_0"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_GOLD.toStack(), 1))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_1))
                .input(Ingredient.of(Items.RAW_GOLD))
                .results(ModItems.PURE_BLOOD_0.toStack())
                .burnTime(300)
                .unlockedBy("raw_gold", has(Items.RAW_GOLD))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_1))
                .save(this.output, modString("raw_gold_pure_1"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_GOLD.toStack(), 2))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_2))
                .input(Ingredient.of(Items.RAW_GOLD))
                .results(ModItems.PURE_BLOOD_1.toStack())
                .burnTime(400)
                .unlockedBy("raw_gold", has(Items.RAW_GOLD))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_2))
                .save(this.output, modString("raw_gold_pure_2"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_GOLD.toStack(), 3))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_3))
                .input(Ingredient.of(Items.RAW_GOLD))
                .results(ModItems.PURE_BLOOD_2.toStack())
                .burnTime(500)
                .unlockedBy("raw_gold", has(Items.RAW_GOLD))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_3))
                .save(this.output, modString("raw_gold_pure_3"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_RAW_GOLD.toStack(), 4))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_4))
                .input(Ingredient.of(Items.RAW_GOLD))
                .results(ModItems.PURE_BLOOD_3.toStack())
                .burnTime(600)
                .unlockedBy("raw_gold", has(Items.RAW_GOLD))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_4))
                .save(this.output, modString("raw_gold_pure_4"));

        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_DIAMOND.toStack(), 0))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_0))
                .input(tag(Tags.Items.GEMS_DIAMOND))
                .results(ModItems.VAMPIRE_BLOOD_BOTTLE.toStack())
                .burnTime(200)
                .unlockedBy("raw_diamonds", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_0))
                .save(this.output, modString("diamond_pure_0"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_DIAMOND.toStack(), 1))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_1))
                .input(tag(Tags.Items.GEMS_DIAMOND))
                .results(ModItems.PURE_BLOOD_0.toStack())
                .burnTime(400)
                .unlockedBy("raw_diamonds", has(Items.RAW_GOLD))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_1))
                .save(this.output, modString("diamond_pure_1"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_DIAMOND.toStack(), 2))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_2))
                .input(tag(Tags.Items.GEMS_DIAMOND))
                .results(ModItems.PURE_BLOOD_1.toStack())
                .burnTime(600)
                .unlockedBy("raw_diamonds", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_2))
                .save(this.output, modString("diamond_pure_2"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_DIAMOND.toStack(), 3))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_3))
                .input(tag(Tags.Items.GEMS_DIAMOND))
                .results(ModItems.PURE_BLOOD_2.toStack())
                .burnTime(800)
                .unlockedBy("raw_diamonds", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_3))
                .save(this.output, modString("diamond_pure_3"));
        infuser(PureLevel.pureBlood(ModItems.BLOOD_INFUSED_DIAMOND.toStack(), 4))
                .ingredients(Ingredient.of(ModItems.PURE_BLOOD_4))
                .input(tag(Tags.Items.GEMS_DIAMOND))
                .results(ModItems.PURE_BLOOD_3.toStack())
                .burnTime(1000)
                .unlockedBy("raw_diamonds", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_4))
                .save(this.output, modString("diamond_pure_4"));
        swordInfusing();


        infusedIron(0);
        infusedIron(1);
        infusedIron(2);
        infusedIron(3);
        infusedIron(4);

        infusedGold(0);
        infusedGold(1);
        infusedGold(2);
        infusedGold(3);
        infusedGold(4);

        shapeless(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_NETHERITE_INGOT, 0))
                .requires(Items.NETHERITE_SCRAP, 4)
                .requires(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_GOLD_INGOT), 4)
                .unlockedBy("has_blood_infused_gold_ingot", has(ModItems.BLOOD_INFUSED_GOLD_INGOT))
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .save(this.output, modString("netherite_scrap_pure_0"));
        shapeless(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_NETHERITE_INGOT, 1))
                .requires(Items.NETHERITE_SCRAP, 4)
                .requires(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_GOLD_INGOT), 4)
                .unlockedBy("has_blood_infused_gold_ingot", has(ModItems.BLOOD_INFUSED_GOLD_INGOT))
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .save(this.output, modString("netherite_scrap_pure_1"));
        shapeless(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_NETHERITE_INGOT, 2))
                .requires(Items.NETHERITE_SCRAP, 4)
                .requires(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_GOLD_INGOT), 4)
                .unlockedBy("has_blood_infused_gold_ingot", has(ModItems.BLOOD_INFUSED_GOLD_INGOT))
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .save(this.output, modString("netherite_scrap_pure_2"));
        shapeless(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_NETHERITE_INGOT, 3))
                .requires(Items.NETHERITE_SCRAP, 4)
                .requires(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_GOLD_INGOT), 4)
                .unlockedBy("has_blood_infused_gold_ingot", has(ModItems.BLOOD_INFUSED_GOLD_INGOT))
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .save(this.output, modString("netherite_scrap_pure_3"));
        shapeless(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_NETHERITE_INGOT, 4))
                .requires(Items.NETHERITE_SCRAP, 4)
                .requires(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_GOLD_INGOT), 4)
                .unlockedBy("has_blood_infused_gold_ingot", has(ModItems.BLOOD_INFUSED_GOLD_INGOT))
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .save(this.output, modString("netherite_scrap_pure_4"));

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,0)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_seeker_normal_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,1)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_seeker_normal_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,2)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_seeker_normal_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,3)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_seeker_normal_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,4)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_seeker_normal_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,0)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_seeker_enhanced_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,1)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_seeker_enhanced_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,2)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_seeker_enhanced_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,3)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_seeker_enhanced_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,4)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_seeker_enhanced_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,0)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_seeker_ultimate_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,1)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_seeker_ultimate_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,2)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_seeker_ultimate_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,3)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_seeker_ultimate_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,4)).pattern("X").pattern("X").pattern("Y").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_seeker_ultimate_pure_4");


        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,0)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_striker_normal_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,1)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_striker_normal_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,2)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_striker_normal_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,3)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_striker_normal_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,4)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_IRON_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT)).save(output, "heart_striker_normal_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,0)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_striker_enhanced_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,1)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_striker_enhanced_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,2)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_striker_enhanced_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,3)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_striker_enhanced_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,4)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_DIAMOND)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND)).save(output, "heart_striker_enhanced_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,0)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_striker_ultimate_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,1)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_striker_ultimate_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,2)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_striker_ultimate_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,3)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_striker_ultimate_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,4)).pattern("XX").pattern("XX").pattern("YY").define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).define('Y', Tags.Items.RODS_WOODEN).unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT)).save(output, "heart_striker_ultimate_pure_4");

        nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT,0) , RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModBlocks.BLOOD_INFUSED_IRON_BLOCK, 0), "_purity_0");
        nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT,1) , RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModBlocks.BLOOD_INFUSED_IRON_BLOCK, 1), "_purity_1");
        nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT,2) , RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModBlocks.BLOOD_INFUSED_IRON_BLOCK, 2), "_purity_2");
        nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT,3) , RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModBlocks.BLOOD_INFUSED_IRON_BLOCK, 3), "_purity_3");
        nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT,4) , RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK, 4), "_purity_4");

        smithingPure(ModItems.HEART_SEEKER_ENHANCED, 0, ModItems.HEART_SEEKER_ULTIMATE);
        smithingPure(ModItems.HEART_SEEKER_ENHANCED, 1, ModItems.HEART_SEEKER_ULTIMATE);
        smithingPure(ModItems.HEART_SEEKER_ENHANCED, 2, ModItems.HEART_SEEKER_ULTIMATE);
        smithingPure(ModItems.HEART_SEEKER_ENHANCED, 3, ModItems.HEART_SEEKER_ULTIMATE);
        smithingPure(ModItems.HEART_SEEKER_ENHANCED, 4, ModItems.HEART_SEEKER_ULTIMATE);

        smithingPure(ModItems.HEART_STRIKER_ENHANCED, 0, ModItems.HEART_STRIKER_ULTIMATE);
        smithingPure(ModItems.HEART_STRIKER_ENHANCED, 1, ModItems.HEART_STRIKER_ULTIMATE);
        smithingPure(ModItems.HEART_STRIKER_ENHANCED, 2, ModItems.HEART_STRIKER_ULTIMATE);
        smithingPure(ModItems.HEART_STRIKER_ENHANCED, 3, ModItems.HEART_STRIKER_ULTIMATE);
        smithingPure(ModItems.HEART_STRIKER_ENHANCED, 4, ModItems.HEART_STRIKER_ULTIMATE);

    }

    private void smithingPure(ItemLike item, int level, ItemLike result) {
        netheriteSmithing(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), item), RecipeCategory.COMBAT, DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), ModItems.BLOOD_INFUSED_NETHERITE_INGOT), PureLevel.pureBlood(result, level), "_purity_" + level);
    }

    private void infusedGold(int pureLevel) {
        SimpleCookingRecipeBuilder
                .smelting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(pureLevel), ModItems.BLOOD_INFUSED_RAW_GOLD), RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_GOLD_INGOT, pureLevel), (float)Math.pow(2f, pureLevel), 200 + pureLevel * 100)
                .unlockedBy("has_blood_infused_raw_gold", has(ModItems.BLOOD_INFUSED_RAW_GOLD))
                .save(this.output, modString("raw_gold_pure_" + pureLevel + "_smelting"));
        SimpleCookingRecipeBuilder
                .blasting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(pureLevel), ModItems.BLOOD_INFUSED_RAW_GOLD), RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_GOLD_INGOT, pureLevel),(float)Math.pow(2f, pureLevel), 100 + pureLevel * 50)
                .unlockedBy("has_blood_infused_raw_gold", has(ModItems.BLOOD_INFUSED_RAW_GOLD))
                .save(this.output, modString("raw_gold_pure_" + pureLevel + "_blasting"));
    }

    private void infusedIron(int pureLevel) {
        SimpleCookingRecipeBuilder
                .smelting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(pureLevel), ModItems.BLOOD_INFUSED_RAW_IRON), RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT, pureLevel), (float)Math.pow(2f, pureLevel), 200 + pureLevel * 100)
                .unlockedBy("has_blood_infused_raw_iron", has(ModItems.BLOOD_INFUSED_RAW_IRON))
                .save(this.output, modString("raw_iron_pure_" + pureLevel + "_smelting"));
        SimpleCookingRecipeBuilder
                .blasting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(pureLevel), ModItems.BLOOD_INFUSED_RAW_IRON), RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT, pureLevel),(float)Math.pow(2f, pureLevel), 100 + pureLevel * 50)
                .unlockedBy("has_blood_infused_raw_iron", has(ModItems.BLOOD_INFUSED_RAW_IRON))
                .save(this.output, modString("raw_iron_pure_" + pureLevel + "_blasting"));
    }

    private void swordInfusing() {
        Stream.of(ModItems.HEART_SEEKER_NORMAL, ModItems.HEART_SEEKER_ENHANCED, ModItems.HEART_SEEKER_ULTIMATE, ModItems.HEART_STRIKER_NORMAL, ModItems.HEART_STRIKER_ENHANCED, ModItems.HEART_STRIKER_ULTIMATE).forEach(item -> {
            for (int i = 1; i < 5; i++) {
                swordInfuse(item, i);
            }
        });
    }

    private void swordInfuse(ItemLike item, @Range(from = 1, to = 4) int level) {
        infuserUpgrade()
                .ingredients(Ingredient.of(PureBloodItem.getBloodItemForLevel(level)))
                .results(ItemStack.EMPTY)
                .burnTime(200)
                .unlockedBy("has_pure_blood", has(PureBloodItem.getBloodItemForLevel(level)))
                .input(CompoundIngredient.of(IntStream.range(0, level).mapToObj(x -> DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(x), item)).toArray(Ingredient[]::new)))
                .save(this.output, ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(item.asItem()).withSuffix("_infuse_" + level + "_upgrade")));
    }


    private @NotNull String general(String path) {
        return modString("general/" + path);
    }

    private @NotNull String hunter(String path) {
        return modString("hunter/" + path);
    }

    private @NotNull String vampire(String path) {
        return modString("vampire/" + path);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput output) {
            return new RecipesProvider(provider, output);
        }

        @Override
        public @NotNull String getName() {
            return "Vampirism Recipes";
        }
    }
}
