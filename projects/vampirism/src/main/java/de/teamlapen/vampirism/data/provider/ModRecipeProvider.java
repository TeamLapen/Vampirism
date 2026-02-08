package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModOils;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import de.teamlapen.vampirism.common.util.ItemDataUtils;
import de.teamlapen.vampirism.common.util.RegUtil;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.PureBloodItem;
import de.teamlapen.vampirism.common.world.items.VampireCloakItem;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import de.teamlapen.vampirism.common.world.items.recipes.*;
import de.teamlapen.vampirism.data.ModBlockFamilies;
import de.teamlapen.vampirism.data.provider.base.VampirismRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import org.jetbrains.annotations.Range;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VIdentifier.modString;
import static net.minecraft.data.recipes.RecipeBuilder.getDefaultRecipeId;

public class ModRecipeProvider extends VampirismRecipeProvider {

    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        recipesFunctionalBlocks();
        recipesDecorationalBlocks();
        recipesBuildingBlocks();
        recipesMisc();
        recipesToolsAndArmor();
        recipesStonecutter();
        recipesAlchemyTable();
        recipesAlchemyCauldron();
        recipesWeaponTable();
        recipesInfuser();

        BuiltInRegistries.ITEM.getOptional(REFERENCE.GUIDEBOOK_LOCATION).ifPresent(guideBook ->
                shapeless(RecipeCategory.MISC, guideBook)
                        .requires(ModItems.VAMPIRE_FANG)
                        .requires(Items.BOOK)
                        .unlockedBy("has_fang", has(ModItems.VAMPIRE_FANG))
                        .save(output.withConditions(new ModLoadedCondition(REFERENCE.GUIDEAPI_MODID)), modString("vampirism_guidebook"))
        );

        SpecialRecipeBuilder.special(ApplicableOilRecipe::new).save(output, modString("applicable_oil"));
        SpecialRecipeBuilder.special(CleanOilRecipe::new).save(output, modString("clean_oil"));
        SpecialRecipeBuilder.special(RerollVampireBookRecipe::new).save(output, modString("reroll_vampire_book"));
        SpecialRecipeBuilder.special(FillBottleFromSyringeRecipe::new).save(output, modString("fill_bottle_from_syringe"));
    }

    private void recipesFunctionalBlocks() {
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_INSPIRATION)
                .pattern("X X")
                .pattern("XYX")
                .pattern("XXX")
                .define('X', PLANKS)
                .define('Y', ModBlocks.BLOOD_CONTAINER)
                .unlockedBy("has_planks", has(PLANKS))
                .unlockedBy("has_blood_container", has(ModBlocks.BLOOD_CONTAINER))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_INFUSION)
                .pattern("YZY")
                .pattern("ZZZ")
                .define('Y', GOLD_INGOT)
                .define('Z', OBSIDIAN)
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_PILLAR)
                .pattern("X X")
                .pattern("   ")
                .pattern("XXX")
                .define('X', Blocks.STONE_BRICKS)
                .unlockedBy("has_stones", has(Blocks.STONE_BRICKS))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_TIP)
                .pattern(" X ")
                .pattern("XYX")
                .define('X', IRON_INGOT)
                .define('Y', IRON_BLOCK)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_PEDESTAL)
                .pattern("GYG")
                .pattern("YZY")
                .pattern("XXX")
                .define('X', OBSIDIAN)
                .define('Y', PLANKS)
                .define('Z', ModItems.BLOOD_BOTTLE)
                .define('G', GOLD_INGOT)
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_CONTAINER)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("XYX")
                .define('X', PLANKS)
                .define('Y', GLASS)
                .define('Z', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_GRINDER)
                .pattern("PIP")
                .pattern("PSP")
                .pattern("PIP")
                .define('I', IRON_INGOT)
                .define('P', PLANKS)
                .define('S', Blocks.STONE_SLAB)
                .unlockedBy("has_iron_ingot", has(IRON_INGOT))
                .unlockedBy("has_planks", has(PLANKS))
                .unlockedBy("has_stone_slab", has(Blocks.STONE_SLAB))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_SIEVE)
                .pattern(" I ")
                .pattern("PCP")
                .pattern(" I ")
                .define('I', IRON_INGOT)
                .define('P', PLANKS)
                .define('C', ModBlocks.BLOOD_CONTAINER)
                .unlockedBy("has_iron_ingot", has(IRON_INGOT))
                .unlockedBy("has_planks", has(PLANKS))
                .unlockedBy("has_blood_container", has(ModBlocks.BLOOD_CONTAINER))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.FOG_DIFFUSER)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("OOO")
                .define('X', ModBlocks.CURSED_SPRUCE_PLANKS)
                .define('Y', DIAMOND)
                .define('O', OBSIDIAN)
                .define('Z', ModItems.MOTHER_CORE)
                .unlockedBy("has_diamond", has(DIAMOND))
                .unlockedBy("has_cursed_plank", has(ModBlocks.CURSED_SPRUCE_PLANKS))
                .unlockedBy("has_mother_core", has(ModItems.MOTHER_CORE))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.HUNTER_TABLE)
                .pattern("XYW")
                .pattern("ZZZ")
                .pattern("Z Z")
                .define('X', ModItems.VAMPIRE_FANG)
                .define('Y', Items.BOOK)
                .define('Z', PLANKS)
                .define('W', GARLIC)
                .unlockedBy("has_fang", has(ModItems.VAMPIRE_FANG))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.WEAPON_TABLE)
                .pattern("X  ")
                .pattern("YYY")
                .pattern(" Z ")
                .define('X', BUCKET)
                .define('Y', IRON_INGOT)
                .define('Z', IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(IRON_INGOT))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALCHEMICAL_CAULDRON)
                .pattern("XZX")
                .pattern("XXX")
                .pattern("Y Y")
                .define('X', IRON_INGOT)
                .define('Y', Blocks.STONE_BRICKS)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.POTION_TABLE)
                .pattern("XXX")
                .pattern("Y Y")
                .pattern("ZZZ")
                .define('X', Items.GLASS_BOTTLE)
                .define('Y', PLANKS)
                .define('Z', IRON_INGOT)
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModBlocks.ALCHEMY_TABLE)
                .pattern("B  ")
                .pattern("BBB")
                .pattern("P P")
                .define('B', Blocks.BASALT)
                .define('P', PLANKS)
                .unlockedBy("has_basalt", has(Blocks.BASALT))
                .unlockedBy("has_planks", has(PLANKS))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING)
                .pattern(" X ")
                .pattern("YYY")
                .pattern(" Y ")
                .define('X', ModItems.VAMPIRE_BOOK)
                .define('Y', PLANKS)
                .unlockedBy("has_vampire_book", has(PLANKS))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING)
                .pattern("XZX")
                .pattern("YYY")
                .pattern(" Y ")
                .define('X', ModItems.VAMPIRE_FANG)
                .define('Y', PLANKS)
                .define('Z', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output, modString("altar_cleansing_fang"));

        shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_NORMAL)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("OOO")
                .define('X', PLANKS)
                .define('Y', DIAMOND)
                .define('O', OBSIDIAN)
                .define('Z', ModItems.GARLIC_DIFFUSER_CORE)
                .unlockedBy("has_diamond", has(DIAMOND))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_IMPROVED)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("OOO")
                .define('X', PLANKS)
                .define('Y', DIAMOND)
                .define('Z', ModItems.GARLIC_DIFFUSER_CORE_IMPROVED)
                .define('O', OBSIDIAN)
                .unlockedBy("has_garlic_diffuser", has(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED))
                .save(output);
        shaped(RecipeCategory.MISC, ModBlocks.VAMPIRE_BEACON)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("OOO")
                .define('G', GLASS)
                .define('C', ModItems.MOTHER_CORE)
                .define('O', OBSIDIAN)
                .unlockedBy("has_mother_core", has(ModItems.MOTHER_CORE))
                .unlockedBy("has_obsidian", has(OBSIDIAN))
                .unlockedBy("has_glass", has(GLASS))
                .save(output);
    }

    private void recipesDecorationalBlocks() {
        shaped(RecipeCategory.DECORATIONS, ModBlocks.FIRE_PLACE)
                .pattern(" X ")
                .pattern("XYX")
                .define('X', LOG)
                .define('Y', COAL_BLOCK)
                .unlockedBy("has_logs", has(LOG))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.CANDLE_STICK)
                .pattern(" I ")
                .pattern("NNN")
                .define('I', IRON_INGOT)
                .define('N', IRON_NUGGET)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_nugget", has(IRON_NUGGET))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.CANDELABRA)
                .pattern("III")
                .pattern("NIN")
                .define('I', IRON_INGOT)
                .define('N', IRON_NUGGET)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_nugget", has(IRON_NUGGET))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.CHANDELIER)
                .pattern(" A ")
                .pattern("ICI")
                .define('C', ModItems.CANDELABRA)
                .define('I', IRON_INGOT)
                .define('A', Blocks.IRON_CHAIN)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_candelabra", has(ModItems.CANDELABRA))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.VAMPIRE_SOUL_LANTERN)
                .pattern("GGG")
                .pattern("GSG")
                .pattern("GGG")
                .define('S', ModItems.SOUL_ORB_VAMPIRE)
                .define('G', GOLD_NUGGET)
                .unlockedBy("has_soul_orb", has(ModItems.SOUL_ORB_VAMPIRE))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.CROSS)
                .pattern(" X ")
                .pattern("XYX")
                .pattern(" X ")
                .define('X', PLANKS)
                .define('Y', HOLY_WATER)
                .unlockedBy("has_planks", has(PLANKS))
                .unlockedBy("has_holy", has(HOLY_WATER))
                .save(output);
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE1)
                .requires(ModBlocks.TOMBSTONE2)
                .unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE2)
                .pattern("XX ")
                .pattern("XYX")
                .pattern("XXX")
                .define('X', COBBLESTONE)
                .define('Y', STONE)
                .unlockedBy("has_coble", has(COBBLESTONE))
                .unlockedBy("has_stone", has(STONE))
                .save(output);
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE3)
                .requires(ModBlocks.TOMBSTONE2)
                .requires(Blocks.COBBLESTONE)
                .unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GRAVE_CAGE)
                .pattern(" X ")
                .pattern("XYX")
                .pattern("XYX")
                .define('X', IRON_INGOT)
                .define('Y', COAL)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_coal", has(COAL))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.VAMPIRE_RACK)
                .pattern("XYX")
                .pattern("ABC")
                .pattern("XYX")
                .define('X', PLANKS)
                .define('Y', Items.BOOK)
                .define('A', ModItems.VAMPIRE_FANG)
                .define('B', Items.GLASS_BOTTLE)
                .define('C', Items.HONEYCOMB)
                .unlockedBy("has_planks", has(PLANKS))
                .unlockedBy("has_book", has(Items.BOOK))
                .unlockedBy("has_fangs", has(ModItems.VAMPIRE_FANG))
                .unlockedBy("has_honey", has(Items.HONEYCOMB))
                .unlockedBy("has_potion", has(Items.GLASS_BOTTLE))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.THRONE)
                .pattern(" YZ")
                .pattern("YYZ")
                .pattern("XZX")
                .define('Y', Blocks.RED_CARPET)
                .define('Z', PLANKS)
                .define('X', STICK)
                .unlockedBy("has_stick", has(STICK))
                .unlockedBy("has_planks", has(PLANKS))
                .unlockedBy("has_wool", has(Blocks.RED_CARPET))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BAT_CAGE)
                .pattern("GGG")
                .pattern("GPG")
                .pattern("PPP")
                .define('G', GOLD_INGOT)
                .define('P', PLANKS)
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .unlockedBy("has_planks", has(PLANKS))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, ModItems.ITEM_TENT)
                .pattern(" W ")
                .pattern("WBW")
                .define('W', WOOL)
                .define('B', BED)
                .unlockedBy("has_wool", has(WOOL))
                .unlockedBy("has_bed", has(BED))
                .save(output);

        coffinFromWool(output, ModBlocks.COFFIN_WHITE, Items.WHITE_WOOL);
        ColorListsUtil.COFFINS.forEach(coffin -> {
            DyeColor color = coffin.getColor();
            if (color != DyeColor.WHITE) {
                coffinFromWoolOrDye(output, coffin, ColorListsUtil.DYED_WOOL.get(color), ColorListsUtil.DYE_ITEMS.get(color));
            }
        });
    }

    private void coffinFromWool(RecipeOutput consumer, ItemLike coffin, ItemLike wool) {
        shaped(RecipeCategory.DECORATIONS, coffin)
                .pattern("XXX")
                .pattern("YYY")
                .pattern("XXX")
                .define('X', PLANKS)
                .define('Y', wool)
                .unlockedBy("has_wool", has(wool))
                .save(consumer);
    }

    private void coffinFromWoolOrDye(RecipeOutput consumer, ItemLike coffin, ItemLike wool, ItemLike dye) {
        coffinFromWool(consumer, coffin, wool);
        shapeless(RecipeCategory.DECORATIONS, coffin)
                .requires(ModBlocks.COFFIN_WHITE)
                .requires(dye)
                .unlockedBy("has_coffin", has(ModBlocks.COFFIN_WHITE))
                .unlockedBy("has_dye", has(dye))
                .save(consumer, modString(RegUtil.id(coffin).getPath()) + "_from_white");
    }

    private void recipesBuildingBlocks() {
        Stream.of(
                ModBlockFamilies.DARK_SPRUCE_PLANKS,
                ModBlockFamilies.CURSED_SPRUCE_PLANKS,
                ModBlockFamilies.DARK_STONE,
                ModBlockFamilies.PURPLE_BRICKS,
                ModBlockFamilies.DARK_STONE_BRICKS,
                ModBlockFamilies.POLISHED_DARK_STONE,
                ModBlockFamilies.COBBLED_DARK_STONE,
                ModBlockFamilies.DARK_STONE_TILES,
                ModBlockFamilies.PURPLE_STONE_TILES
        ).forEach(blockFamily -> generateRecipes(blockFamily, FeatureFlagSet.of(FeatureFlags.VANILLA)));

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_STONE_BRICKS, 4)
                .define('#', ModBlocks.DARK_STONE)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_dark_stone", has(ModBlocks.DARK_STONE))
                .save(output);
        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PURPLE_STONE_BRICKS, 8)
                .requires(ModBlocks.DARK_STONE_BRICKS, 8)
                .requires(ModBlocks.VAMPIRE_ORCHID)
                .unlockedBy("has_dark_stone_bricks", has(ModBlocks.DARK_STONE_BRICKS))
                .save(output);

        planksFromLog(ModBlocks.DARK_SPRUCE_PLANKS, ModItemTags.DARK_SPRUCE_LOG, 4);
        planksFromLog(ModBlocks.CURSED_SPRUCE_PLANKS, ModItemTags.CURSED_SPRUCE_LOG, 4);
        woodFromLogs(ModBlocks.DARK_SPRUCE_WOOD, ModBlocks.DARK_SPRUCE_LOG);
        woodFromLogs(ModBlocks.CURSED_SPRUCE_WOOD, ModBlocks.CURSED_SPRUCE_LOG);
        woodFromLogs(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD, ModBlocks.STRIPPED_DARK_SPRUCE_LOG);
        woodFromLogs(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD, ModBlocks.STRIPPED_CURSED_SPRUCE_LOG);
        woodenBoat(ModItems.DARK_SPRUCE_BOAT, ModBlocks.DARK_SPRUCE_PLANKS);
        woodenBoat(ModItems.CURSED_SPRUCE_BOAT, ModBlocks.CURSED_SPRUCE_PLANKS);
        chestBoat(ModItems.DARK_SPRUCE_CHEST_BOAT, ModItems.DARK_SPRUCE_BOAT);
        chestBoat(ModItems.CURSED_SPRUCE_CHEST_BOAT, ModItems.CURSED_SPRUCE_BOAT);
        hangingSign(ModItems.DARK_SPRUCE_HANGING_SIGN, ModBlocks.STRIPPED_DARK_SPRUCE_LOG);
        hangingSign(ModItems.CURSED_SPRUCE_HANGING_SIGN, ModBlocks.STRIPPED_CURSED_SPRUCE_LOG);

        smeltingAndBlasting(RecipeCategory.BUILDING_BLOCKS, "dark_stone_from_cobbled_dark_stone", ModBlocks.COBBLED_DARK_STONE, ModBlocks.DARK_STONE, 0.1f);
    }

    private void recipesMisc() {
        shapeless(RecipeCategory.FOOD, ModItems.GARLIC_BREAD)
                .requires(GARLIC)
                .requires(BREAD)
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_bread", has(BREAD))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SYRINGE_EMPTY)
                .pattern("I")
                .pattern("G")
                .pattern("N")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .unlockedBy("has_iron_nugget", has(Tags.Items.NUGGETS_IRON))
                .save(output);
        shapeless(RecipeCategory.MISC, ModItems.INJECTION_GARLIC)
                .requires(ModItems.SYRINGE_EMPTY)
                .requires(GARLIC)
                .unlockedBy("has_injection", has(ModItems.SYRINGE_EMPTY))
                .save(output);
        shapeless(RecipeCategory.MISC, ModItems.INJECTION_SANGUINARE)
                .requires(ModItems.SYRINGE_EMPTY).requires(ModItems.VAMPIRE_FANG, 8)
                .unlockedBy("has_injection", has(ModItems.SYRINGE_EMPTY))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.FABRIC_FILTER)
                .pattern("SWS")
                .define('S', STRING)
                .define('W', WOOL)
                .unlockedBy("has_string", has(STRING))
                .unlockedBy("has_wool", has(WOOL))
                .save(output);

        shapeless(RecipeCategory.MISC, Items.GLASS_BOTTLE)
                .requires(ModItems.BLOOD_BOTTLE)
                .unlockedBy("has_blood_bottle", has(ModItems.BLOOD_BOTTLE))
                .save(output, modString("blood_bottle_to_glass"));

        ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.MISC, ItemDataUtils.createBloodBottle(0))
                .pattern("XYX")
                .pattern(" X ")
                .define('X', GLASS)
                .define('Y', Items.ROTTEN_FLESH)
                .unlockedBy("has_glass", has(GLASS))
                .save(output.withConditions(new NotCondition(new ConfigCondition("auto_convert"))));

        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_0)
                .requires(ModItems.PURE_BLOOD_1)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_1))
                .save(output);
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_1)
                .requires(ModItems.PURE_BLOOD_2)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_2))
                .save(output);
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_2)
                .requires(ModItems.PURE_BLOOD_3)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_3))
                .save(output);
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_3)
                .requires(ModItems.PURE_BLOOD_4)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_4))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_IRON_INGOT, 3)
                .requires(tag(IRON_INGOT), 3)
                .requires(ModItems.PURE_BLOOD_4)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output, modString("blood_infused_iron_ingot_pure_4"));
        shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_IRON_INGOT, 3)
                .requires(tag(IRON_INGOT), 3)
                .requires(Ingredient.of(ModItems.PURE_BLOOD_0, ModItems.PURE_BLOOD_1, ModItems.PURE_BLOOD_2, ModItems.PURE_BLOOD_3))
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output, modString("blood_infused_iron_ingot_pure_0_to_3"));

        shapeless(RecipeCategory.DECORATIONS, ModItems.HOLY_WATER_BOTTLE_NORMAL, 2)
                .requires(ModItems.HOLY_WATER_BOTTLE_ENHANCED)
                .requires(ModItems.PURE_SALT_WATER)
                .unlockedBy("has_enhanced_holy_water", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED))
                .save(output, modString("holy_water_bottle_normal_from_enhanced"));
        shapeless(RecipeCategory.DECORATIONS, ModItems.HOLY_WATER_BOTTLE_ENHANCED, 2)
                .requires(ModItems.HOLY_WATER_BOTTLE_ULTIMATE)
                .requires(ModItems.PURE_SALT_WATER)
                .unlockedBy("has_ultimate_holy_water", has(ModItems.HOLY_WATER_BOTTLE_ULTIMATE))
                .save(output, modString("holy_water_bottle_enhanced_from_ultimate"));

        smeltingAndBlasting(RecipeCategory.MISC, "gold_nugget_from_accessory", new ItemLike[] { ModItems.AMULET, ModItems.RING }, Items.GOLD_NUGGET, 0.1f);
        shapeless(RecipeCategory.COMBAT, Items.LEATHER)
                .requires(ModItems.OBI_BELT)
                .unlockedBy("has_obi_belt", has(ModItems.OBI_BELT))
                .save(output, modString("leather_from_obi_belt"));
    }

    private void recipesToolsAndArmor() {
        shaped(RecipeCategory.COMBAT, ModItems.STAKE)
                .pattern("X")
                .pattern("Y")
                .pattern("X")
                .define('X', STICK)
                .define('Y', PLANKS)
                .unlockedBy("has_sticks", has(STICK))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.CRUCIFIX_NORMAL)
                .pattern("XY ")
                .pattern("ZYZ")
                .pattern(" Y ")
                .define('X', ModItems.HOLY_WATER_BOTTLE_NORMAL)
                .define('Y', PLANKS)
                .define('Z', STICK)
                .unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_NORMAL))
                .unlockedBy("stick", has(STICK))
                .unlockedBy("planks", has(PLANKS))
                .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL, 6)
                .pattern("X")
                .pattern("Y")
                .define('X', IRON_INGOT)
                .define('Y', STICK)
                .unlockedBy("has_iron_ingot", has(IRON_INGOT))
                .save(output);
        shapeless(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL)
                .requires(Items.ARROW)
                .unlockedBy("has_arrow", has(Items.ARROW))
                .save(output, modString("crossbow_arrow_from_vanilla"));

        shaped(RecipeCategory.MISC, ModItems.UMBRELLA)
                .pattern("###")
                .pattern("BAB")
                .pattern(" A ")
                .define('#', WOOL)
                .define('A', STICK)
                .define('B', ModBlocks.VAMPIRE_ORCHID)
                .unlockedBy("has_wool", has(WOOL))
                .save(output.withConditions(new ConfigCondition("umbrella")));
        shaped(RecipeCategory.MISC, ModItems.GARLIC_FINDER)
                .pattern("XXX")
                .pattern("XYX")
                .pattern("ZAZ")
                .define('X', ModItems.BLOOD_INFUSED_IRON_INGOT)
                .define('Y', GARLIC)
                .define('Z', PLANKS)
                .define('A', REDSTONE_DUST)
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_blood_infused_iron", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .unlockedBy("has_redstone", has(REDSTONE_DUST))
                .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_LEGS)
                .pattern("XXX")
                .pattern("X X")
                .pattern("XYX")
                .define('X', Items.GRAY_WOOL)
                .define('Y', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_wool", has(Items.GRAY_WOOL))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_BOOTS)
                .pattern("XYX")
                .pattern("X X")
                .define('X', Items.BROWN_WOOL)
                .define('Y', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_wool", has(Items.BROWN_WOOL))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_HAT)
                .pattern("ZXX")
                .pattern(" Y ")
                .pattern("XXX")
                .define('X', Items.BLACK_WOOL)
                .define('Y', Items.RED_WOOL)
                .define('Z', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_wool", has(Items.BLACK_WOOL))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_CROWN)
                .pattern("XYX")
                .pattern("XXX")
                .define('X', GOLD_INGOT)
                .define('Y', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output);

        colorWithDye(ColorListsUtil.VAMPIRE_CLOAKS, RecipeCategory.COMBAT, VIdentifier::modString);
        ColorListsUtil.VAMPIRE_CLOAKS.keySet().forEach(dye -> {
            VampireCloakItem cloakItem = ColorListsUtil.VAMPIRE_CLOAKS.get(dye);
            Item woolItem = ColorListsUtil.DYED_WOOL.get(dye);
            if (cloakItem != null && woolItem != null) {
                vampireCloak(cloakItem, woolItem);
            }
        });
    }

    protected void vampireCloak(Item item, Item wool) {
        shaped(RecipeCategory.COMBAT, item)
                .define('W', wool)
                .define('D', DIAMOND)
                .define('P', PURE_BLOOD)
                .pattern("WDW")
                .pattern("WPW")
                .pattern("W W")
                .unlockedBy("has_pure_blood", has(PURE_BLOOD))
                .save(output);
    }

    private void recipesStonecutter() {
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_BRICK_WALL, ModBlocks.PURPLE_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_BRICK_SLAB, ModBlocks.PURPLE_STONE_BRICKS, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_BRICK_STAIRS, ModBlocks.PURPLE_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES, ModBlocks.PURPLE_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_WALL, ModBlocks.PURPLE_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_WALL, ModBlocks.PURPLE_STONE_TILES);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_SLAB, ModBlocks.PURPLE_STONE_BRICKS, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_SLAB, ModBlocks.PURPLE_STONE_TILES, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_STAIRS, ModBlocks.PURPLE_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.PURPLE_STONE_TILES_STAIRS, ModBlocks.PURPLE_STONE_TILES);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_SLAB, ModBlocks.DARK_STONE_BRICKS, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_SLAB, ModBlocks.DARK_STONE_TILES, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_SLAB, ModBlocks.COBBLED_DARK_STONE, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.COBBLED_DARK_STONE_SLAB, ModBlocks.COBBLED_DARK_STONE, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.COBBLED_DARK_STONE_STAIRS, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.COBBLED_DARK_STONE_WALL, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_SLAB, ModBlocks.POLISHED_DARK_STONE, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_STAIRS, ModBlocks.POLISHED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_WALL, ModBlocks.POLISHED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_SLAB, ModBlocks.COBBLED_DARK_STONE, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_STAIRS, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.POLISHED_DARK_STONE_WALL, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICKS, ModBlocks.POLISHED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICKS, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_STAIRS, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_STAIRS, ModBlocks.DARK_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_STAIRS, ModBlocks.POLISHED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_SLAB, ModBlocks.COBBLED_DARK_STONE, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_SLAB, ModBlocks.DARK_STONE_BRICKS, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_SLAB, ModBlocks.POLISHED_DARK_STONE, 2);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_WALL, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_WALL, ModBlocks.DARK_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_BRICK_WALL, ModBlocks.POLISHED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES, ModBlocks.POLISHED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES, ModBlocks.DARK_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_STAIRS, ModBlocks.DARK_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_STAIRS, ModBlocks.DARK_STONE_TILES);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_STAIRS, ModBlocks.COBBLED_DARK_STONE);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_WALL, ModBlocks.DARK_STONE_BRICKS);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_WALL, ModBlocks.DARK_STONE_TILES);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, ModBlocks.DARK_STONE_TILES_WALL, ModBlocks.COBBLED_DARK_STONE);
    }

    private void recipesAlchemyTable() {
        alchemyTable(ModOils.PLANT)
                .ingredient(Ingredient.of(Items.GLASS_BOTTLE))
                .input(Ingredient.of(Items.WHEAT_SEEDS))
                .unlockedBy("has_bottles", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_wheat_seeds", has(Items.WHEAT_SEEDS))
                .save(output, modString("plant_oil"));
        alchemyTable(ModOils.VAMPIRE_BLOOD)
                .plantOilIngredient()
                .input(Ingredient.of(ModItems.VAMPIRE_BLOOD_BOTTLE))
                .unlockedBy("has_wheat_seeds", has(ModItems.VAMPIRE_BLOOD_BOTTLE))
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
                .input(Ingredient.of(ModItems.ITEM_ALCHEMICAL_FIRE))
                .save(output, modString("smelt_oil"));
        alchemyTable(ModOils.TELEPORT)
                .bloodOilIngredient()
                .input(Ingredient.of(Items.ENDER_PEARL))
                .save(output, modString("teleport_oil"));
        alchemyTable(ModOils.EVASION)
                .bloodOilIngredient()
                .input(Ingredient.of(Items.HONEY_BOTTLE))
                .save(output, modString("evasion_oil"));
        alchemyTable(ModOils.GARLIC)
                .plantOilIngredient()
                .input(tag(GARLIC))
                .save(output, modString("garlic_oil"));
        alchemyTable(ModOils.SPITFIRE)
                .plantOilIngredient()
                .input(Ingredient.of(ModItems.ITEM_ALCHEMICAL_FIRE))
                .save(output, modString("spitfire_oil"));
        alchemyTable(ModOils.BLEEDING)
                .plantOilIngredient()
                .input(Ingredient.of(Items.AMETHYST_SHARD))
                .save(output, modString("bleeding_oil"));
        alchemyTable(ModOils.VAMPIRE_KILLER)
                .oilIngredient(ModOils.GARLIC)
                .input(tag(HOLY_WATER))
                .save(output, modString("vampire_killer_oil"));
    }

    private void recipesAlchemyCauldron() {
        cauldronRecipe(ModItems.PURE_SALT, 4)
                .withIngredient(GARLIC)
                .withFluid(new FluidStack(Fluids.WATER, 1))
                .withSkills(HunterSkills.BASIC_ALCHEMY)
                .cookTime(1200)
                .save(output);
        cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE, 4)
                .withIngredient(Items.GUNPOWDER)
                .withFluid(ModItems.HOLY_WATER_BOTTLE_NORMAL)
                .save(output, modString("alchemical_fire_4"));
        cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE, 5)
                .withIngredient(Items.GUNPOWDER)
                .withFluid(ModItems.HOLY_WATER_BOTTLE_ENHANCED)
                .save(output, modString("alchemical_fire_5"));
        cauldronRecipe(ModItems.ITEM_ALCHEMICAL_FIRE, 6)
                .withIngredient(Items.GUNPOWDER)
                .withFluid(ModItems.HOLY_WATER_BOTTLE_ULTIMATE)
                .save(output, modString("alchemical_fire_6"));
        cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE)
                .withIngredient(WOOL)
                .withFluid(GARLIC)
                .withSkills(HunterSkills.GARLIC_DIFFUSER)
                .save(output);
        cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED)
                .withIngredient(ModItems.GARLIC_DIFFUSER_CORE)
                .withFluid(ModItems.HOLY_WATER_BOTTLE_ULTIMATE)
                .withSkills(HunterSkills.GARLIC_DIFFUSER_IMPROVED)
                .experience(2.0f)
                .save(output);
        cauldronRecipe(ModItems.PURIFIED_GARLIC, 2)
                .withIngredient(GARLIC)
                .withFluid(HOLY_WATER)
                .withSkills(HunterSkills.PURIFIED_GARLIC)
                .save(output);
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK)
                .withFluid(ModItems.PURE_BLOOD_0)
                .withIngredient(Items.IRON_BLOCK)
                .cookTime(200)
                .experience(0.1f)
                .save(output, modString("blood_infused_iron_ingot_from_pure_blood_0"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK)
                .withFluid(ModItems.PURE_BLOOD_1)
                .withIngredient(Items.IRON_BLOCK)
                .cookTime(180)
                .experience(0.15f)
                .save(output, modString("blood_infused_iron_ingot_from_pure_blood_1"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK)
                .withFluid(ModItems.PURE_BLOOD_2)
                .withIngredient(Items.IRON_BLOCK)
                .cookTime(160)
                .experience(0.2f)
                .save(output, modString("blood_infused_iron_ingot_from_pure_blood_2"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_IRON_BLOCK)
                .withFluid(ModItems.PURE_BLOOD_3)
                .withIngredient(Items.IRON_BLOCK)
                .cookTime(140)
                .experience(0.25f)
                .save(output, modString("blood_infused_iron_ingot_from_pure_blood_3"));
        cauldronRecipe(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK)
                .withFluid(ModItems.PURE_BLOOD_4)
                .withIngredient(Items.IRON_BLOCK)
                .cookTime(300)
                .experience(0.3f)
                .save(output, modString("blood_infused_enhanced_iron_ingot_from_pure_blood_4"));
    }

    private void recipesWeaponTable() {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL)
                .lava(1)
                .pattern("XZZX")
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', GOLD_INGOT)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', DIAMOND)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_diamond", has(DIAMOND))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL)
                .lava(1)
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', GOLD_INGOT)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', DIAMOND)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_diamond", has(DIAMOND))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL)
                .lava(1)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', GOLD_INGOT)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', DIAMOND)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_diamond", has(DIAMOND))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', GOLD_INGOT)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', LEATHER)
                .define('Y', GARLIC)
                .define('Z', DIAMOND)
                .unlockedBy("has_leather", has(LEATHER))
                .unlockedBy("has_garlic", has(GARLIC))
                .unlockedBy("has_diamond", has(DIAMOND))
                .save(output);
        netheriteSmithing(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get());
        netheriteSmithing(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get());
        netheriteSmithing(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get());
        netheriteSmithing(ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get());

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_NORMAL)
                .lava(2)
                .pattern("XWWX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("XYYX")
                .define('X', IRON_INGOT)
                .define('Y', LEATHER)
                .define('Z', GARLIC)
                .define('W', ModItems.VAMPIRE_FANG)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XWWX")
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XYYX")
                .define('X', IRON_INGOT)
                .define('Y', DIAMOND)
                .define('Z', GARLIC)
                .define('W', ModItems.VAMPIRE_FANG)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XWWX")
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XYYX")
                .define('X', IRON_INGOT)
                .define('Y', NETHERITE_INGOT)
                .define('Z', GARLIC)
                .define('W', ModItems.VAMPIRE_FANG)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_netherite", has(NETHERITE_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_NORMAL)
                .lava(2)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', IRON_INGOT)
                .define('Z', GARLIC)
                .define('Y', LEATHER)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', IRON_INGOT)
                .define('Z', GARLIC)
                .define('Y', DIAMOND)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', IRON_INGOT)
                .define('Z', GARLIC)
                .define('Y', NETHERITE_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_netherite", has(NETHERITE_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_NORMAL)
                .lava(2)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', IRON_INGOT)
                .define('Y', LEATHER)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', IRON_INGOT)
                .define('Y', DIAMOND)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', IRON_INGOT)
                .define('Y', NETHERITE_INGOT)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_netherite", has(NETHERITE_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_NORMAL)
                .lava(2)
                .pattern("    ")
                .pattern("X  X")
                .pattern("XZZX")
                .pattern("XYYX")
                .define('X', IRON_INGOT)
                .define('Y', LEATHER)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("    ")
                .pattern("X  X")
                .pattern("XZZX")
                .pattern("XYYX")
                .define('X', IRON_INGOT)
                .define('Y', DIAMOND)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("    ")
                .pattern("X  X")
                .pattern("XZZX")
                .pattern("XYYX")
                .define('X', IRON_INGOT)
                .define('Y', NETHERITE_INGOT)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_netherite", has(NETHERITE_INGOT))
                .unlockedBy("has_garlic", has(GARLIC))
                .save(output);
        netheriteSmithing(ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_ULTIMATE.get());
        netheriteSmithing(ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_ULTIMATE.get());
        netheriteSmithing(ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_ULTIMATE.get());
        netheriteSmithing(ModItems.HUNTER_COAT_FEET_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_ULTIMATE.get());


        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_CROSSBOW)
                .lava(1)
                .skills(HunterSkills.WEAPON_TABLE)
                .pattern("YXXY")
                .pattern(" ZZ ")
                .pattern(" ZZ ")
                .define('X', IRON_INGOT)
                .define('Y', Tags.Items.STRINGS)
                .define('Z', PLANKS)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_DOUBLE_CROSSBOW)
                .lava(1)
                .skills(HunterSkills.WEAPON_TABLE)
                .pattern("YXXY")
                .pattern("YXXY")
                .pattern(" ZZ ")
                .pattern(" ZZ ")
                .define('X', IRON_INGOT)
                .define('Y', Tags.Items.STRINGS)
                .define('Z', PLANKS)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_TECH_CROSSBOW)
                .lava(5)
                .skills(HunterSkills.WEAPON_TABLE)
                .pattern("YXXY")
                .pattern("XZZX")
                .pattern(" XX ")
                .pattern(" XX ")
                .define('X', IRON_INGOT)
                .define('Y', Tags.Items.STRINGS)
                .define('Z', DIAMOND)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output);

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_CROSSBOW)
                .lava(2)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("YXXY")
                .pattern(" XX ")
                .pattern(" XX ")
                .define('X', IRON_INGOT)
                .define('Y', Tags.Items.STRINGS)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_DOUBLE_CROSSBOW)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("YXXY")
                .pattern("YXXY")
                .pattern(" XX ")
                .pattern(" XX ")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', Tags.Items.STRINGS)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_TECH_CROSSBOW)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("YXXY")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern(" XX ")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', Tags.Items.STRINGS)
                .define('Z', DIAMOND)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_TALL)
                .pattern(" YY ")
                .pattern(" YY ")
                .pattern("XXXX")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', Items.BLACK_WOOL)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_BROAD)
                .lava(1)
                .pattern(" YY ")
                .pattern("XXXX")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', Items.BLACK_WOOL)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.PITCHFORK)
                .pattern("X X")
                .pattern("YYY")
                .pattern(" Y ")
                .pattern(" Y ")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', STICK)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.QUARREL_POUCH)
                .lava(1)
                .pattern("ILLI")
                .pattern("PLLP")
                .pattern("ILLI")
                .define('I', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('L', LEATHER)
                .define('P', PLANKS)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_NORMAL.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 1))
                .lava(5)
                .pattern("XXZY")
                .pattern("XXZY")
                .pattern("  ZY")
                .pattern("  Z ")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', GARLIC)
                .define('Z', STICK)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_ENHANCED.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 2))
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XWZY")
                .pattern("XWZY")
                .pattern("  ZY")
                .pattern("  Z ")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', GARLIC)
                .define('W', DIAMOND)
                .define('Z', STICK)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_ULTIMATE.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 3))
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XWZY")
                .pattern("XWZY")
                .pattern("  ZY")
                .pattern("  Z ")
                .define('X', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .define('Y', GARLIC)
                .define('W', NETHERITE_INGOT)
                .define('Z', STICK)
                .save(output);
        netheriteSmithing(ModItems.HUNTER_AXE_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.HUNTER_AXE_ULTIMATE.get());

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CRUCIFIX_ENHANCED)
                .pattern("XYYX")
                .pattern("YZAY")
                .pattern("XYYX")
                .pattern("XYYX")
                .define('X', ModItems.HOLY_WATER_BOTTLE_NORMAL)
                .define('Y', IRON_INGOT)
                .define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED)
                .define('A', ModItems.STAKE)
                .unlockedBy("iron", has(IRON_INGOT))
                .unlockedBy("has_holy_water", has(ModItems.HOLY_WATER_BOTTLE_NORMAL))
                .unlockedBy("has_holy_water_enhanced", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED))
                .unlockedBy("stake", has(ModItems.STAKE))
                .skills(HunterSkills.CRUCIFIX_WIELDER)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CRUCIFIX_ULTIMATE)
                .pattern("XYYX")
                .pattern("YZAY")
                .pattern("XYYX")
                .pattern("XYYX")
                .define('X', ModItems.ITEM_ALCHEMICAL_FIRE)
                .define('Y', GOLD_BLOCK)
                .define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED)
                .define('A', ModItems.STAKE)
                .unlockedBy("fire", has(ModItems.ITEM_ALCHEMICAL_FIRE))
                .unlockedBy("gold", has(GOLD_BLOCK))
                .unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED))
                .unlockedBy("stake", has(ModItems.STAKE))
                .skills(HunterSkills.ULTIMATE_CRUCIFIX)
                .save(output);

        crossbowArrowRecipe(ModItems.CROSSBOW_ARROW_TELEPORT, ModOils.TELEPORT, 1);
        upToThreeCrossbowArrowRecipe(ModItems.CROSSBOW_ARROW_SPITFIRE, ModOils.SPITFIRE);
        upToThreeCrossbowArrowRecipe(ModItems.CROSSBOW_ARROW_GARLIC, ModOils.GARLIC);
        upToThreeCrossbowArrowRecipe(ModItems.CROSSBOW_ARROW_BLEEDING, ModOils.BLEEDING);
        upToThreeCrossbowArrowRecipe(ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER, ModOils.VAMPIRE_KILLER);
    }

    private void recipesInfuser() {
        fiveTieredMetalInfusionRecipe(Items.RAW_IRON, ModItems.BLOOD_INFUSED_RAW_IRON);
        fiveTieredMetalInfusionRecipe(Items.RAW_GOLD, ModItems.BLOOD_INFUSED_RAW_GOLD);
        fiveTieredMetalInfusionRecipe(DIAMOND, ModItems.BLOOD_INFUSED_DIAMOND);

        fiveTieredInfusedMetalSmeltingRecipe(ModItems.BLOOD_INFUSED_RAW_IRON, ModItems.BLOOD_INFUSED_IRON_INGOT);
        fiveTieredInfusedMetalSmeltingRecipe(ModItems.BLOOD_INFUSED_RAW_GOLD, ModItems.BLOOD_INFUSED_GOLD_INGOT);

        for (int i = 0; i < 5; i++) {
            shapeless(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_NETHERITE_INGOT, i))
                    .requires(Items.NETHERITE_SCRAP, 4)
                    .requires(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(i), ModItems.BLOOD_INFUSED_GOLD_INGOT), 4)
                    .unlockedBy("has_blood_infused_gold_ingot", has(ModItems.BLOOD_INFUSED_GOLD_INGOT))
                    .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                    .save(this.output, modString("netherite_scrap_pure_" + i));
        }

        Stream.of(ModItems.HEART_SEEKER_NORMAL, ModItems.HEART_SEEKER_ENHANCED, ModItems.HEART_SEEKER_ULTIMATE, ModItems.HEART_STRIKER_NORMAL, ModItems.HEART_STRIKER_ENHANCED, ModItems.HEART_STRIKER_ULTIMATE).forEach(item -> {
            for (int i = 1; i < 5; i++) {
                swordInfuse(item, i);
            }
        });

        String heartSeekerPattern = "X\nX\nY";
        fiveTieredInfusedSwordCrafting(ModItems.HEART_SEEKER_NORMAL, ModItems.BLOOD_INFUSED_IRON_INGOT, heartSeekerPattern);
        fiveTieredInfusedSwordCrafting(ModItems.HEART_SEEKER_ENHANCED, ModItems.BLOOD_INFUSED_DIAMOND, heartSeekerPattern);
        fiveTieredInfusedSwordCrafting(ModItems.HEART_SEEKER_ULTIMATE, ModItems.BLOOD_INFUSED_NETHERITE_INGOT, heartSeekerPattern);

        String heartStrikerPattern = "XX\nXX\nYY";
        fiveTieredInfusedSwordCrafting(ModItems.HEART_STRIKER_NORMAL, ModItems.BLOOD_INFUSED_IRON_INGOT, heartStrikerPattern);
        fiveTieredInfusedSwordCrafting(ModItems.HEART_STRIKER_ENHANCED, ModItems.BLOOD_INFUSED_DIAMOND, heartStrikerPattern);
        fiveTieredInfusedSwordCrafting(ModItems.HEART_STRIKER_ULTIMATE, ModItems.BLOOD_INFUSED_NETHERITE_INGOT, heartStrikerPattern);

        for (int i = 0; i < 5; i++) {
            nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(ModItems.BLOOD_INFUSED_IRON_INGOT, i), RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(i == 4 ? ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK : ModBlocks.BLOOD_INFUSED_IRON_BLOCK, i), "_purity_" + i);
        }

        for (int i = 0; i < 5; i++) {
            smithingPure(ModItems.HEART_SEEKER_ENHANCED, i, ModItems.HEART_SEEKER_ULTIMATE);
            smithingPure(ModItems.HEART_STRIKER_ENHANCED, i, ModItems.HEART_STRIKER_ULTIMATE);
        }

        for (int i = 0; i < 4; i++) {
            infuser(ModItems.RITUAL_KNIFE_HEART.toStack())
                    .ingredient(i, Ingredient.of(ModItems.MOTHER_CORE))
                    .input(Ingredient.of(ModItems.RITUAL_KNIFE))
                    .results(ItemStack.EMPTY)
                    .burnTime(600)
                    .unlockedBy("has_mother_core", has(ModItems.MOTHER_CORE))
                    .unlockedBy("has_ritual_knife", has(ModItems.RITUAL_KNIFE))
                    .save(this.output, ResourceKey.create(Registries.RECIPE, getDefaultRecipeId(ModItems.RITUAL_KNIFE_HEART.get()).withSuffix("_" + i)));
        }
    }

    private void swordInfuse(ItemLike item, @Range(from = 1, to = 4) int level) {
        infuserUpgrade()
                .ingredients(Ingredient.of(PureBloodItem.getBloodItemForLevel(level)))
                .results(ItemStack.EMPTY)
                .burnTime(200)
                .unlockedBy("has_pure_blood", has(PureBloodItem.getBloodItemForLevel(level)))
                .input(CompoundIngredient.of(IntStream.range(0, level).mapToObj(x -> DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(x), item)).toArray(Ingredient[]::new)))
                .save(this.output, ResourceKey.create(Registries.RECIPE, RegUtil.id(item.asItem()).withSuffix("_infuse_" + level + "_upgrade")));
    }

    private void smithingPure(ItemLike item, int level, ItemLike result) {
        netheriteSmithing(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), item), RecipeCategory.COMBAT, DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), ModItems.BLOOD_INFUSED_NETHERITE_INGOT), result.asItem(), DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), new PureLevel(level)).build(), "_purity_" + level);
    }


    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "Vampirism Recipes";
        }
    }
}
