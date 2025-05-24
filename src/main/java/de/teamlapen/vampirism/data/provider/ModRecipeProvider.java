package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import de.teamlapen.vampirism.data.ModBlockFamilies;
import de.teamlapen.vampirism.data.provider.parent.ExtendedRecipeProvider;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.items.VampireCloakItem;
import de.teamlapen.vampirism.items.component.OilContent;
import de.teamlapen.vampirism.items.component.PureLevel;
import de.teamlapen.vampirism.recipes.ApplicableOilRecipe;
import de.teamlapen.vampirism.recipes.CleanOilRecipe;
import de.teamlapen.vampirism.recipes.ConfigCondition;
import de.teamlapen.vampirism.recipes.RerollVampireBookRecipe;
import de.teamlapen.vampirism.util.ColorListsUtil;
import de.teamlapen.vampirism.util.ItemDataUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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

import static de.teamlapen.vampirism.api.util.VResourceLocation.modString;

public class ModRecipeProvider extends ExtendedRecipeProvider {

    public static final TagKey<Item> IRON_INGOT = Tags.Items.INGOTS_IRON;
    public static final TagKey<Item> GOLD_INGOT = Tags.Items.INGOTS_GOLD;
    public static final TagKey<Item> COAL = ItemTags.COALS;
    public static final TagKey<Item> DIAMOND = Tags.Items.GEMS_DIAMOND;
    public static final TagKey<Item> REDSTONE_DUST = Tags.Items.DUSTS_REDSTONE;
    public static final TagKey<Item> IRON_BLOCK = Tags.Items.STORAGE_BLOCKS_IRON;
    public static final TagKey<Item> GOLD_BLOCK = Tags.Items.STORAGE_BLOCKS_GOLD;
    public static final TagKey<Item> COAL_BLOCK = Tags.Items.STORAGE_BLOCKS_COAL;
    public static final TagKey<Item> DIAMOND_BLOCK = Tags.Items.STORAGE_BLOCKS_DIAMOND;
    public static final TagKey<Item> IRON_NUGGET = Tags.Items.NUGGETS_IRON;
    public static final TagKey<Item> GOLD_NUGGET = Tags.Items.NUGGETS_GOLD;
    public static final TagKey<Item> GARLIC = ModItemTags.GARLIC;
    public static final TagKey<Item> BREAD = Tags.Items.FOODS_BREAD;
    public static final TagKey<Item> HEART = ModItemTags.HEART;
    public static final TagKey<Item> HOLY_WATER = ModItemTags.HOLY_WATER;
    public static final TagKey<Item> BUCKET = Tags.Items.BUCKETS_EMPTY;
    public static final TagKey<Item> PLANKS = ItemTags.PLANKS;
    public static final TagKey<Item> LOG = ItemTags.LOGS;
    public static final TagKey<Item> STICK = Tags.Items.RODS_WOODEN;
    public static final TagKey<Item> STONE = Tags.Items.STONES;
    public static final TagKey<Item> COBBLESTONE = Tags.Items.COBBLESTONES;
    public static final TagKey<Item> GLASS = Tags.Items.GLASS_BLOCKS;
    public static final TagKey<Item> GLASS_PANE = Tags.Items.GLASS_PANES;
    public static final TagKey<Item> OBSIDIAN = Tags.Items.OBSIDIANS;
    public static final TagKey<Item> WOOL = ItemTags.WOOL;
    public static final TagKey<Item> BED = ItemTags.BEDS;

    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        recipesFunctionalBlocks();
        recipesDecorationalBlocks();
        recipesBuildingBlocks();
        recipesMisc();
        recipesTools();
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
                        .save(output.withConditions(new ModLoadedCondition(REFERENCE.GUIDEAPI_MODID)), modString("general/guidebook"))
        );

        SpecialRecipeBuilder.special(ApplicableOilRecipe::new).save(output, modString("applicable_oil"));
        SpecialRecipeBuilder.special(CleanOilRecipe::new).save(output, modString("clean_oil"));
        SpecialRecipeBuilder.special(RerollVampireBookRecipe::new).save(output, modString("reroll_vampire_book"));
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
                .save(output, vampire("altar_inspiration"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_INFUSION)
                .pattern("YZY")
                .pattern("ZZZ")
                .define('Y', GOLD_INGOT)
                .define('Z', OBSIDIAN)
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output, vampire("altar_infusion"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_PILLAR)
                .pattern("X X")
                .pattern("   ")
                .pattern("XXX")
                .define('X', Blocks.STONE_BRICKS)
                .unlockedBy("has_stones", has(Blocks.STONE_BRICKS))
                .save(output, vampire("altar_pillar"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_TIP)
                .pattern(" X ")
                .pattern("XYX")
                .define('X', IRON_INGOT)
                .define('Y', IRON_BLOCK)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output, vampire("altar_tip"));

        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_PEDESTAL)
                .pattern("GYG")
                .pattern("YZY")
                .pattern("XXX")
                .define('X', OBSIDIAN)
                .define('Y', PLANKS)
                .define('Z', ModItems.BLOOD_BOTTLE)
                .define('G', GOLD_INGOT)
                .unlockedBy("has_gold", has(GOLD_INGOT))
                .save(output, vampire("blood_pedestal"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_CONTAINER)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("XYX")
                .define('X', PLANKS)
                .define('Y', GLASS)
                .define('Z', IRON_INGOT)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output, vampire("blood_container"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_GRINDER)
                .pattern(" Z ")
                .pattern("YDY")
                .pattern("YXY")
                .define('Z', Blocks.HOPPER)
                .define('Y', PLANKS)
                .define('D', DIAMOND)
                .define('X', IRON_INGOT)
                .unlockedBy("has_hopper", has(Blocks.HOPPER))
                .save(output, general("blood_grinder"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOOD_SIEVE)
                .pattern("XQX")
                .pattern("YZY")
                .pattern("YXY")
                .define('X', IRON_INGOT)
                .define('Q', Blocks.QUARTZ_BRICKS)
                .define('Y', PLANKS)
                .define('Z', Blocks.CAULDRON)
                .unlockedBy("has_cauldron", has(Blocks.CAULDRON))
                .save(output, general("blood_sieve"));
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
                .save(output, vampire("fog_diffuser"));

        shaped(RecipeCategory.DECORATIONS, ModBlocks.HUNTER_TABLE)
                .pattern("XYW")
                .pattern("ZZZ")
                .pattern("Z Z")
                .define('X', ModItems.VAMPIRE_FANG)
                .define('Y', Items.BOOK)
                .define('Z', PLANKS)
                .define('W', GARLIC)
                .unlockedBy("has_fang", has(ModItems.VAMPIRE_FANG))
                .save(output, hunter("hunter_table"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.WEAPON_TABLE)
                .pattern("X  ")
                .pattern("YYY")
                .pattern(" Z ")
                .define('X', BUCKET)
                .define('Y', IRON_INGOT)
                .define('Z', IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(IRON_INGOT))
                .save(output, hunter("weapon_table"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALCHEMICAL_CAULDRON)
                .pattern("XZX")
                .pattern("XXX")
                .pattern("Y Y")
                .define('X', IRON_INGOT)
                .define('Y', Blocks.STONE_BRICKS)
                .define('Z', GARLIC)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output, hunter("alchemical_cauldron"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.POTION_TABLE)
                .pattern("XXX")
                .pattern("Y Y")
                .pattern("ZZZ")
                .define('X', Items.GLASS_BOTTLE)
                .define('Y', PLANKS)
                .define('Z', IRON_INGOT)
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(output, hunter("potion_table"));
        shaped(RecipeCategory.COMBAT, ModBlocks.ALCHEMY_TABLE)
                .pattern("B  ")
                .pattern("BBB")
                .pattern("P P")
                .define('B', Blocks.BASALT)
                .define('P', PLANKS)
                .unlockedBy("has_basalt", has(Blocks.BASALT))
                .unlockedBy("has_planks", has(PLANKS))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.MED_CHAIR)
                .pattern("XYX")
                .pattern("XXX")
                .pattern("XZX")
                .define('X', IRON_INGOT)
                .define('Y', WOOL)
                .define('Z', Items.GLASS_BOTTLE)
                .unlockedBy("has_iron_ingot", has(IRON_INGOT))
                .save(output, hunter("item_med_chair"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING)
                .pattern(" X ")
                .pattern("YYY")
                .pattern(" Y ")
                .define('X', ModItems.VAMPIRE_BOOK)
                .define('Y', PLANKS)
                .unlockedBy("has_vampire_book", has(PLANKS))
                .save(output, general("altar_cleansing"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.ALTAR_CLEANSING)
                .pattern("XZX")
                .pattern("YYY")
                .pattern(" Y ")
                .define('X', ModItems.VAMPIRE_FANG)
                .define('Y', PLANKS)
                .define('Z', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output, general("altar_cleansing_new"));

        shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_NORMAL)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("OOO")
                .define('X', PLANKS)
                .define('Y', DIAMOND)
                .define('O', OBSIDIAN)
                .define('Z', ModItems.GARLIC_DIFFUSER_CORE)
                .unlockedBy("has_diamond", has(DIAMOND))
                .save(output, hunter("garlic_diffuser_normal"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GARLIC_DIFFUSER_IMPROVED)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("OOO")
                .define('X', PLANKS)
                .define('Y', DIAMOND)
                .define('Z', ModItems.GARLIC_DIFFUSER_CORE_IMPROVED)
                .define('O', OBSIDIAN)
                .unlockedBy("has_garlic_diffuser", has(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED))
                .save(output, hunter("garlic_diffuser_improved"));
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

        shaped(RecipeCategory.DECORATIONS, ModBlocks.TOTEM_BASE)
                .pattern("XYX")
                .pattern("XYX")
                .pattern("ZZZ")
                .define('X', PLANKS)
                .define('Y', OBSIDIAN)
                .define('Z', IRON_INGOT)
                .unlockedBy("has_obsidian", has(OBSIDIAN))
                .save(output, general("totem_base"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.TOTEM_TOP_CRAFTED)
                .pattern("X X")
                .pattern(" Y ")
                .pattern("XZX")
                .define('X', OBSIDIAN)
                .define('Y', DIAMOND)
                .define('Z', ModItems.VAMPIRE_BOOK)
                .unlockedBy("has_diamond", has(DIAMOND_BLOCK))
                .unlockedBy("has_obsidian", has(OBSIDIAN))
                .save(output, general("totem_top"));
    }

    private void recipesDecorationalBlocks() {
        shaped(RecipeCategory.DECORATIONS, ModBlocks.FIRE_PLACE)
                .pattern(" X ")
                .pattern("XYX")
                .define('X', LOG)
                .define('Y', COAL_BLOCK)
                .unlockedBy("has_logs", has(LOG))
                .save(output, general("fire_place"));
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
                .define('A', Blocks.CHAIN)
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
                .save(output, hunter("cross"));
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE1)
                .requires(ModBlocks.TOMBSTONE2)
                .unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2))
                .save(output, general("tombstone1"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE2)
                .pattern("XX ")
                .pattern("XYX")
                .pattern("XXX")
                .define('X', COBBLESTONE)
                .define('Y', STONE)
                .unlockedBy("has_coble", has(COBBLESTONE))
                .unlockedBy("has_stone", has(STONE))
                .save(output, general("tombstone2"));
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.TOMBSTONE3)
                .requires(ModBlocks.TOMBSTONE2)
                .requires(Blocks.COBBLESTONE)
                .unlockedBy("has_tomb", has(ModBlocks.TOMBSTONE2))
                .save(output, general("tombstone3"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.GRAVE_CAGE)
                .pattern(" X ")
                .pattern("XYX")
                .pattern("XYX")
                .define('X', IRON_INGOT)
                .define('Y', COAL)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .unlockedBy("has_coal", has(COAL))
                .save(output, general("grave_cage"));
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
                .save(output, general("vampire_rack"));
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
                .save(output, general("throne"));
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

        coffinFromWool(output, ModBlocks.COFFIN_WHITE, Items.WHITE_WOOL, vampire("coffin_white"));
        ColorListsUtil.COFFINS.forEach(coffinBlock -> {
            DyeColor color = coffinBlock.getColor();
            if (color != DyeColor.WHITE) {
                coffinFromWoolOrDye(output, coffinBlock, ColorListsUtil.DYED_WOOL.get(color), ColorListsUtil.DYE_ITEMS.get(color), vampire("coffin_" + color.getName()));
            }
        });
    }

    private void coffinFromWool(RecipeOutput consumer, ItemLike coffin, ItemLike wool, String path) {
        shaped(RecipeCategory.DECORATIONS, coffin)
                .pattern("XXX")
                .pattern("YYY")
                .pattern("XXX")
                .define('X', ItemTags.PLANKS)
                .define('Y', wool)
                .unlockedBy("has_wool", has(wool))
                .save(consumer, path);
    }

    private void coffinFromWoolOrDye(RecipeOutput consumer, ItemLike coffin, ItemLike wool, ItemLike dye, String path) {
        coffinFromWool(consumer, coffin, wool, path);
        shapeless(RecipeCategory.DECORATIONS, coffin)
                .requires(ModBlocks.COFFIN_WHITE)
                .requires(dye)
                .unlockedBy("has_coffin", has(ModBlocks.COFFIN_WHITE))
                .unlockedBy("has_dye", has(dye))
                .save(consumer, path + "_from_white");
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
                .save(output, general("garlic_bread"));

        shaped(RecipeCategory.MISC, ModItems.INJECTION_EMPTY)
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', GLASS)
                .define('Y', GLASS_PANE)
                .unlockedBy("has_glass", has(GLASS))
                .unlockedBy("has_glass_pane", has(GLASS_PANE))
                .save(output, general("injection_0"));
        shapeless(RecipeCategory.MISC, ModItems.INJECTION_GARLIC)
                .requires(ModItems.INJECTION_EMPTY)
                .requires(GARLIC)
                .unlockedBy("has_injection", has(ModItems.INJECTION_EMPTY))
                .save(output, general("injection_1"));
        shapeless(RecipeCategory.MISC, ModItems.INJECTION_SANGUINARE)
                .requires(ModItems.INJECTION_EMPTY).requires(ModItems.VAMPIRE_FANG, 8)
                .unlockedBy("has_injection", has(ModItems.INJECTION_EMPTY))
                .save(output, general("injection_2"));

        shapeless(RecipeCategory.MISC, Items.GLASS_BOTTLE)
                .requires(ModItems.BLOOD_BOTTLE)
                .unlockedBy("has_blood_bottle", has(ModItems.BLOOD_BOTTLE))
                .save(output, vampire("blood_bottle_to_glass"));

        ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.MISC, ItemDataUtils.createBloodBottle(0))
                .pattern("XYX")
                .pattern(" X ")
                .define('X', GLASS)
                .define('Y', Items.ROTTEN_FLESH)
                .unlockedBy("has_glass", has(GLASS))
                .save(output.withConditions(new NotCondition(new ConfigCondition("auto_convert"))), vampire("blood_bottle"));

        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_0)
                .requires(ModItems.PURE_BLOOD_1)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_1))
                .save(output, hunter("pure_blood0"));
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_1)
                .requires(ModItems.PURE_BLOOD_2)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_2))
                .save(output, hunter("pure_blood1"));
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_2)
                .requires(ModItems.PURE_BLOOD_3)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_3))
                .save(output, hunter("pure_blood2"));
        shapeless(RecipeCategory.MISC, ModItems.PURE_BLOOD_3)
                .requires(ModItems.PURE_BLOOD_4)
                .requires(ModItems.VAMPIRE_BLOOD_BOTTLE)
                .unlockedBy("has_pure_blood", has(ModItems.PURE_BLOOD_4))
                .save(output, hunter("pure_blood3"));

        shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_IRON_INGOT, 3)
                .requires(tag(IRON_INGOT), 3)
                .requires(ModItems.PURE_BLOOD_4)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output, vampire("blood_infused_enhanced_iron_ingot"));
        shapeless(RecipeCategory.MISC, ModItems.BLOOD_INFUSED_IRON_INGOT, 3)
                .requires(tag(IRON_INGOT), 3)
                .requires(Ingredient.of(ModItems.PURE_BLOOD_0, ModItems.PURE_BLOOD_1, ModItems.PURE_BLOOD_2, ModItems.PURE_BLOOD_3))
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(output, vampire("blood_infused_iron_ingot"));

        shapeless(RecipeCategory.DECORATIONS, ModItems.HOLY_WATER_BOTTLE_NORMAL, 2)
                .requires(ModItems.HOLY_WATER_BOTTLE_ENHANCED)
                .requires(ModItems.PURE_SALT_WATER)
                .unlockedBy("has_enhanced_holy_water", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED))
                .save(output, "holy_water_bottle_normal_from_enhanced");
        shapeless(RecipeCategory.DECORATIONS, ModItems.HOLY_WATER_BOTTLE_ENHANCED, 2)
                .requires(ModItems.HOLY_WATER_BOTTLE_ULTIMATE)
                .requires(ModItems.PURE_SALT_WATER)
                .unlockedBy("has_ultimate_holy_water", has(ModItems.HOLY_WATER_BOTTLE_ULTIMATE))
                .save(output, "holy_water_bottle_enhanced_from_ultimate");

        smeltingAndBlasting(RecipeCategory.MISC, "gold_nugget_from_accessory", new ItemLike[] { ModItems.AMULET, ModItems.RING }, Items.GOLD_NUGGET, 0.1f);
        shapeless(RecipeCategory.COMBAT, Items.LEATHER)
                .requires(ModItems.OBI_BELT)
                .unlockedBy("has_obi_belt", has(ModItems.OBI_BELT))
                .save(output, modString("leather_from_obi_belt"));
    }

    private void recipesTools() {
        shaped(RecipeCategory.MISC, ModItems.UMBRELLA)
                .pattern("###")
                .pattern("BAB")
                .pattern(" A ")
                .define('#', WOOL)
                .define('A', STICK)
                .define('B', ModBlocks.VAMPIRE_ORCHID)
                .unlockedBy("has_wool", has(WOOL))
                .save(output.withConditions(new ConfigCondition("umbrella")), general("umbrella"));

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
                .save(output, vampire("garlic_finder"));

        shaped(RecipeCategory.COMBAT, ModItems.STAKE)
                .pattern("X")
                .pattern("Y")
                .pattern("X")
                .define('X', STICK)
                .define('Y', PLANKS)
                .unlockedBy("has_sticks", has(STICK))
                .save(output, hunter("stake"));
        shaped(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL, 6)
                .pattern("X")
                .pattern("Y")
                .define('X', IRON_INGOT)
                .define('Y', STICK)
                .unlockedBy("has_iron_ingot", has(IRON_INGOT))
                .save(output, hunter("crossbow_arrow_normal"));
        shapeless(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_NORMAL)
                .requires(Items.ARROW)
                .unlockedBy("has_arrow", has(Items.ARROW))
                .save(output, hunter("crossbow_arrow_from_vanilla"));

        colorWithDye(ColorListsUtil.VAMPIRE_CLOAKS, RecipeCategory.COMBAT, this::vampire);
        ColorListsUtil.VAMPIRE_CLOAKS.keySet().forEach(dye -> {
            VampireCloakItem cloakItem = ColorListsUtil.VAMPIRE_CLOAKS.get(dye);
            Item woolItem = ColorListsUtil.DYED_WOOL.get(dye);
            if (cloakItem != null && woolItem != null) {
                vampireCloak(cloakItem, woolItem);
            }
        });

        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_LEGS)
                .pattern("XXX")
                .pattern("X X")
                .pattern("XYX")
                .define('X', Items.GRAY_WOOL)
                .define('Y', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_wool", has(Items.GRAY_WOOL))
                .save(output, vampire("vampire_clothing_legs"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_BOOTS)
                .pattern("XYX")
                .pattern("X X")
                .define('X', Items.BROWN_WOOL)
                .define('Y', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_wool", has(Items.BROWN_WOOL))
                .save(output, vampire("vampire_clothing_boots"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_HAT)
                .pattern("ZXX")
                .pattern(" Y ")
                .pattern("XXX")
                .define('X', Items.BLACK_WOOL)
                .define('Y', Items.RED_WOOL)
                .define('Z', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_wool", has(Items.BLACK_WOOL))
                .save(output, vampire("vampire_clothing_hat"));
        shaped(RecipeCategory.COMBAT, ModItems.VAMPIRE_CLOTHING_CROWN)
                .pattern("XYX")
                .pattern("XXX")
                .define('X', Items.GOLD_INGOT)
                .define('Y', tag(HEART))
                .unlockedBy("has_heart", has(HEART))
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(output, vampire("vampire_clothing_crown"));

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
                .save(output, hunter("crucifix"));
    }

    protected void vampireCloak(Item item, Item wool) {
        shaped(RecipeCategory.COMBAT, item)
                .define('W', wool)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('P', ModItemTags.PURE_BLOOD)
                .pattern("WDW")
                .pattern("WPW")
                .pattern("W W")
                .unlockedBy("has_pure_blood", has(ModItemTags.PURE_BLOOD)).save(output, vampire(BuiltInRegistries.ITEM.getKey(item).getPath()));
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
                .input(tag(ModItemTags.GARLIC))
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
                .input(tag(ModItemTags.HOLY_WATER))
                .save(output, modString("vampire_killer_oil"));
    }

    private void recipesAlchemyCauldron() {
        cauldronRecipe(ModItems.PURE_SALT, 4)
                .withIngredient(ModItemTags.GARLIC)
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
                .withIngredient(ItemTags.WOOL)
                .withFluid(ModItemTags.GARLIC)
                .withSkills(HunterSkills.GARLIC_DIFFUSER)
                .save(output);
        cauldronRecipe(ModItems.GARLIC_DIFFUSER_CORE_IMPROVED)
                .withIngredient(ModItems.GARLIC_DIFFUSER_CORE)
                .withFluid(ModItems.HOLY_WATER_BOTTLE_ULTIMATE)
                .withSkills(HunterSkills.GARLIC_DIFFUSER_IMPROVED)
                .experience(2.0f)
                .save(output);
        cauldronRecipe(ModItems.PURIFIED_GARLIC, 2)
                .withIngredient(ModItemTags.GARLIC)
                .withFluid(ModItemTags.HOLY_WATER)
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
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL)
                .lava(1)
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XXXX")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL)
                .lava(1)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', potion(Potions.SWIFTNESS))
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XXXX")
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', Tags.Items.LEATHERS)
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
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
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.LEATHERS)
                .define('Z', ModItemTags.GARLIC)
                .define('W', ModItems.VAMPIRE_FANG)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XWWX")
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XYYX")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.GEMS_DIAMOND)
                .define('Z', ModItemTags.GARLIC)
                .define('W', ModItems.VAMPIRE_FANG)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_CHEST_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XWWX")
                .pattern("XZZX")
                .pattern("XYYX")
                .pattern("XYYX")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.INGOTS_NETHERITE)
                .define('Z', ModItemTags.GARLIC)
                .define('W', ModItems.VAMPIRE_FANG)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_NORMAL)
                .lava(2)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Z', ModItemTags.GARLIC)
                .define('Y', Tags.Items.LEATHERS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Z', ModItemTags.GARLIC)
                .define('Y', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_LEGS_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("X  X")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Z', ModItemTags.GARLIC)
                .define('Y', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_NORMAL)
                .lava(2)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.LEATHERS)
                .define('Z', ModItemTags.GARLIC)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.GEMS_DIAMOND)
                .define('Z', ModItemTags.GARLIC)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_HEAD_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XYYX")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern("    ")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.INGOTS_NETHERITE)
                .define('Z', ModItemTags.GARLIC)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_NORMAL)
                .lava(2)
                .pattern("    ")
                .pattern("X  X")
                .pattern("XZZX")
                .pattern("XYYX")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.LEATHERS)
                .define('Z', ModItemTags.GARLIC)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_ENHANCED)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("    ")
                .pattern("X  X")
                .pattern("XZZX")
                .pattern("XYYX")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.GEMS_DIAMOND)
                .define('Z', ModItemTags.GARLIC)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_COAT_FEET_ULTIMATE)
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("    ")
                .pattern("X  X")
                .pattern("XZZX")
                .pattern("XYYX")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.INGOTS_NETHERITE)
                .define('Z', ModItemTags.GARLIC)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy("has_garlic", has(ModItemTags.GARLIC))
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
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.STRINGS)
                .define('Z', ItemTags.PLANKS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_DOUBLE_CROSSBOW)
                .lava(1)
                .skills(HunterSkills.WEAPON_TABLE)
                .pattern("YXXY")
                .pattern("YXXY")
                .pattern(" ZZ ")
                .pattern(" ZZ ")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.STRINGS)
                .define('Z', ItemTags.PLANKS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.BASIC_TECH_CROSSBOW)
                .lava(5)
                .skills(HunterSkills.WEAPON_TABLE)
                .pattern("YXXY")
                .pattern("XZZX")
                .pattern(" XX ")
                .pattern(" XX ")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.STRINGS)
                .define('Z', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);

        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_CROSSBOW)
                .lava(2)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("YXXY")
                .pattern(" XX ")
                .pattern(" XX ")
                .define('X', Tags.Items.INGOTS_IRON)
                .define('Y', Tags.Items.STRINGS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_DOUBLE_CROSSBOW)
                .lava(3)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("YXXY")
                .pattern("YXXY")
                .pattern(" XX ")
                .pattern(" XX ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', Tags.Items.STRINGS)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.ENHANCED_TECH_CROSSBOW)
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("YXXY")
                .pattern("XZZX")
                .pattern("XZZX")
                .pattern(" XX ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', Tags.Items.STRINGS)
                .define('Z', Tags.Items.GEMS_DIAMOND)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_TALL)
                .pattern(" YY ")
                .pattern(" YY ")
                .pattern("XXXX")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', Items.BLACK_WOOL)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.HUNTER_HAT_BROAD)
                .lava(1)
                .pattern(" YY ")
                .pattern("XXXX")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', Items.BLACK_WOOL)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.PITCHFORK)
                .pattern("X X")
                .pattern("YYY")
                .pattern(" Y ")
                .pattern(" Y ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', Tags.Items.RODS_WOODEN)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.QUARREL_POUCH)
                .lava(1)
                .pattern("ILLI")
                .pattern("PLLP")
                .pattern("ILLI")
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('L', Tags.Items.LEATHERS)
                .define('P', ItemTags.PLANKS)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_NORMAL.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 1))
                .lava(5)
                .pattern("XXZY")
                .pattern("XXZY")
                .pattern("  ZY")
                .pattern("  Z ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', ModItemTags.GARLIC)
                .define('Z', Tags.Items.RODS_WOODEN)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_ENHANCED.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 2))
                .lava(5)
                .skills(HunterSkills.MASTER_CRAFTSMANSHIP)
                .pattern("XWZY")
                .pattern("XWZY")
                .pattern("  ZY")
                .pattern("  Z ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', ModItemTags.GARLIC)
                .define('W', Tags.Items.GEMS_DIAMOND)
                .define('Z', Tags.Items.RODS_WOODEN)
                .save(output);
        shapedWeaponTable(RecipeCategory.COMBAT, ItemDataUtils.createEnchantment(ModItems.HUNTER_AXE_ULTIMATE.get(), enchantments.getOrThrow(Enchantments.KNOCKBACK), 3))
                .lava(5)
                .skills(HunterSkills.ARTISAN_CRAFTSMANSHIP)
                .pattern("XWZY")
                .pattern("XWZY")
                .pattern("  ZY")
                .pattern("  Z ")
                .define('X', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .define('Y', ModItemTags.GARLIC)
                .define('W', Tags.Items.INGOTS_NETHERITE)
                .define('Z', Tags.Items.RODS_WOODEN)
                .save(output);
        netheriteSmithing(ModItems.HUNTER_AXE_ENHANCED.get(), RecipeCategory.COMBAT, ModItems.HUNTER_AXE_ULTIMATE.get());

        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_SPITFIRE, 1)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 1)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.SPITFIRE), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "spitfire_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_SPITFIRE, 2)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 2)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.SPITFIRE), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "spitfire_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_SPITFIRE, 3)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 3)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.SPITFIRE), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "spitfire_arrow_3");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_TELEPORT, 1)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.TELEPORT), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output);
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_GARLIC, 1)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 1)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.GARLIC), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "garlic_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_GARLIC, 2)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 2)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.GARLIC), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "garlic_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_GARLIC, 3)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 3)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.GARLIC), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "garlic_arrow_3");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_BLEEDING, 1)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 1)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.BLEEDING), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "bleeding_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_BLEEDING, 2)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 2)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.BLEEDING), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "bleeding_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_BLEEDING, 3)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 3)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.BLEEDING), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "bleeding_arrow_3");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER, 1)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 1)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_KILLER), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "vampire_killer_arrow_1");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER, 2)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 2)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_KILLER), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "vampire_killer_arrow_2");
        shapelessWeaponTable(RecipeCategory.COMBAT, ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER, 3)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, 3)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_KILLER), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, "vampire_killer_arrow_3");
        shapedWeaponTable(RecipeCategory.COMBAT, ModItems.CRUCIFIX_ENHANCED)
                .pattern("XYYX")
                .pattern("YZAY")
                .pattern("XYYX")
                .pattern("XYYX")
                .define('X', ModItems.HOLY_WATER_BOTTLE_NORMAL)
                .define('Y', Tags.Items.INGOTS_IRON)
                .define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED)
                .define('A', ModItems.STAKE)
                .unlockedBy("iron", has(Tags.Items.INGOTS_IRON))
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
                .define('Y', Tags.Items.STORAGE_BLOCKS_GOLD)
                .define('Z', ModItems.HOLY_WATER_BOTTLE_ENHANCED)
                .define('A', ModItems.STAKE)
                .unlockedBy("fire", has(ModItems.ITEM_ALCHEMICAL_FIRE))
                .unlockedBy("gold", has(Tags.Items.STORAGE_BLOCKS_GOLD))
                .unlockedBy("holy_water", has(ModItems.HOLY_WATER_BOTTLE_ENHANCED))
                .unlockedBy("stake", has(ModItems.STAKE))
                .skills(HunterSkills.ULTIMATE_CRUCIFIX)
                .save(output);
    }

    private void recipesInfuser() {
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

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,0))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_seeker_normal_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,1))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_seeker_normal_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,2))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_seeker_normal_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,3))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_seeker_normal_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_NORMAL,4))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_seeker_normal_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,0))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_seeker_enhanced_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,1))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_seeker_enhanced_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,2))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_seeker_enhanced_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,3))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_seeker_enhanced_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ENHANCED,4))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_seeker_enhanced_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,0))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_seeker_ultimate_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,1))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_seeker_ultimate_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,2))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_seeker_ultimate_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,3))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_seeker_ultimate_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_SEEKER_ULTIMATE,4))
                .pattern("X")
                .pattern("X")
                .pattern("Y")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_seeker_ultimate_pure_4");


        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,0))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_striker_normal_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,1))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_striker_normal_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,2))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_striker_normal_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,3))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_striker_normal_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_NORMAL,4))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_IRON_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_iron_ingot", has(ModItems.BLOOD_INFUSED_IRON_INGOT))
                .save(output, "heart_striker_normal_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,0))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_striker_enhanced_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,1))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_striker_enhanced_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,2))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_striker_enhanced_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,3))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_striker_enhanced_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ENHANCED,4))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_DIAMOND))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_diamond", has(ModItems.BLOOD_INFUSED_DIAMOND))
                .save(output, "heart_striker_enhanced_pure_4");

        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,0))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(0), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_striker_ultimate_pure_0");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,1))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(1), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_striker_ultimate_pure_1");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,2))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(2), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_striker_ultimate_pure_2");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,3))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(3), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_striker_ultimate_pure_3");
        ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(ModItems.HEART_STRIKER_ULTIMATE,4))
                .pattern("XX")
                .pattern("XX")
                .pattern("YY")
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(4), ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_blood_infused_netherite_ingot", has(ModItems.BLOOD_INFUSED_NETHERITE_INGOT))
                .save(output, "heart_striker_ultimate_pure_4");

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


    private String general(String path) {
        return modString("general/" + path);
    }

    private String hunter(String path) {
        return modString("hunter/" + path);
    }

    private String vampire(String path) {
        return modString("vampire/" + path);
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
