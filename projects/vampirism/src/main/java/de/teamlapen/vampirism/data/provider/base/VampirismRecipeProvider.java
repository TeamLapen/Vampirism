package de.teamlapen.vampirism.data.provider.base;

import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import de.teamlapen.vampirism.common.util.RegUtil;
import de.teamlapen.vampirism.common.world.items.PureBloodItem;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import de.teamlapen.vampirism.data.builder.*;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VIdentifier.modString;

public abstract class VampirismRecipeProvider extends RecipeProvider {

    protected static final TagKey<Item> COPPER_INGOT = Tags.Items.INGOTS_COPPER;
    protected static final TagKey<Item> IRON_INGOT = Tags.Items.INGOTS_IRON;
    protected static final TagKey<Item> GOLD_INGOT = Tags.Items.INGOTS_GOLD;
    protected static final TagKey<Item> COAL = ItemTags.COALS;
    protected static final TagKey<Item> DIAMOND = Tags.Items.GEMS_DIAMOND;
    protected static final TagKey<Item> NETHERITE_INGOT = Tags.Items.INGOTS_NETHERITE;
    protected static final TagKey<Item> REDSTONE_DUST = Tags.Items.DUSTS_REDSTONE;
    protected static final TagKey<Item> COPPER_BLOCK = Tags.Items.STORAGE_BLOCKS_COPPER;
    protected static final TagKey<Item> IRON_BLOCK = Tags.Items.STORAGE_BLOCKS_IRON;
    protected static final TagKey<Item> GOLD_BLOCK = Tags.Items.STORAGE_BLOCKS_GOLD;
    protected static final TagKey<Item> COAL_BLOCK = Tags.Items.STORAGE_BLOCKS_COAL;
    protected static final TagKey<Item> DIAMOND_BLOCK = Tags.Items.STORAGE_BLOCKS_DIAMOND;
    protected static final TagKey<Item> COPPER_NUGGET = Tags.Items.NUGGETS_COPPER;
    protected static final TagKey<Item> IRON_NUGGET = Tags.Items.NUGGETS_IRON;
    protected static final TagKey<Item> GOLD_NUGGET = Tags.Items.NUGGETS_GOLD;
    protected static final TagKey<Item> GARLIC = ModItemTags.GARLIC;
    protected static final TagKey<Item> BREAD = Tags.Items.FOODS_BREAD;
    protected static final TagKey<Item> HEART = ModItemTags.HEART;
    protected static final TagKey<Item> PURE_BLOOD = ModItemTags.PURE_BLOOD;
    protected static final TagKey<Item> HOLY_WATER = ModItemTags.HOLY_WATER;
    protected static final TagKey<Item> BUCKET = Tags.Items.BUCKETS_EMPTY;
    protected static final TagKey<Item> PLANKS = ItemTags.PLANKS;
    protected static final TagKey<Item> LOG = ItemTags.LOGS;
    protected static final TagKey<Item> STICK = Tags.Items.RODS_WOODEN;
    protected static final TagKey<Item> LEATHER = Tags.Items.LEATHERS;
    protected static final TagKey<Item> STRING = Tags.Items.STRINGS;
    protected static final TagKey<Item> STONE = Tags.Items.STONES;
    protected static final TagKey<Item> COBBLESTONE = Tags.Items.COBBLESTONES;
    protected static final TagKey<Item> GLASS = Tags.Items.GLASS_BLOCKS;
    protected static final TagKey<Item> GLASS_PANE = Tags.Items.GLASS_PANES;
    protected static final TagKey<Item> OBSIDIAN = Tags.Items.OBSIDIANS;
    protected static final TagKey<Item> WOOL = ItemTags.WOOL;
    protected static final TagKey<Item> BED = ItemTags.BEDS;

    protected HolderLookup.RegistryLookup<Item> itemLookup = this.registries.lookupOrThrow(Registries.ITEM);

    public VampirismRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @SafeVarargs
    protected final Ingredient potion(Holder<Potion> ... potion) {
        return CompoundIngredient.of(Arrays.stream(potion).map(PotionContents::new).map(s -> DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, s, Items.POTION)).toArray(Ingredient[]::new));
    }

    protected Ingredient potion(Holder<Potion> potion) {
        return DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(potion), Items.POTION);
    }

    protected void colorWithDye(Map<DyeColor, ? extends Item> dyeableItems, RecipeCategory category, Function<String, String> folder) {
        dyeableItems.keySet().forEach(dye -> {
            Item dyedItem = dyeableItems.get(dye);
            Item dyeItem = ColorListsUtil.DYE_ITEMS.get(dye);
            Stream<Item> stream = dyeableItems.values().stream().map(Item::asItem).filter(item -> !item.equals(dyedItem));
            if (dyedItem != null && dyeItem != null) {
                this.shapeless(category, dyedItem).requires(dyeItem).requires(Ingredient.of(stream)).unlockedBy("has_needed_dye", has(dyeItem)).save(output, folder.apply("dye_" + RegUtil.id(dyedItem).getPath()));
            }
        });
    }

    protected void smeltingAndBlasting(RecipeCategory category, String name, ItemLike toSmelt, ItemLike result, float experience) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(toSmelt), category, CookingBookCategory.MISC, result, experience, 200)
                .unlockedBy("has_" + RegUtil.id(toSmelt.asItem()).getPath(), has(toSmelt))
                .save(output, modString(name + "_from_smelting"));

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(toSmelt), category, CookingBookCategory.MISC,result, experience, 100)
                .unlockedBy("has_" + RegUtil.id(toSmelt.asItem()).getPath(), has(toSmelt))
                .save(output, modString(name + "_from_blasting"));
    }

    protected void smeltingAndBlasting(RecipeCategory category, String name, ItemLike[] toSmelt, ItemLike result, float experience) {
        SimpleCookingRecipeBuilder smelting = SimpleCookingRecipeBuilder.smelting(Ingredient.of(toSmelt), category,CookingBookCategory.MISC, result, experience, 200);
        for (ItemLike item : toSmelt) {
            smelting.unlockedBy("has_" + RegUtil.id(item.asItem()).getPath(), has(item));
        }
        smelting.save(output, modString(name + "_from_smelting"));

        SimpleCookingRecipeBuilder blasting = SimpleCookingRecipeBuilder.blasting(Ingredient.of(toSmelt), category,CookingBookCategory.MISC, result, experience, 100);
        for (ItemLike item : toSmelt) {
            blasting.unlockedBy("has_" + RegUtil.id(item.asItem()).getPath(), has(item));
        }
        blasting.save(output, modString(name + "_from_blasting"));
    }

    protected AlchemyTableRecipeBuilder alchemyTable(Holder<IOil> oilStack) {
        return AlchemyTableRecipeBuilder.builder(this.itemLookup, oilStack);
    }

    protected AlchemicalCauldronRecipeBuilder cauldronRecipe(ItemLike item) {
        return AlchemicalCauldronRecipeBuilder.cauldronRecipe(this.itemLookup, item.asItem());
    }

    protected AlchemicalCauldronRecipeBuilder cauldronRecipe(ItemLike item, int count) {
        return AlchemicalCauldronRecipeBuilder.cauldronRecipe(this.itemLookup, item.asItem(), count);
    }

    protected ShapedWeaponTableRecipeBuilder shapedWeaponTable(RecipeCategory category, ItemLike item) {
        return ShapedWeaponTableRecipeBuilder.shapedWeaponTable(this.itemLookup, category, item);
    }

    protected ShapedWeaponTableRecipeBuilder shapedWeaponTable(RecipeCategory category, ItemStack stack) {
        return ShapedWeaponTableRecipeBuilder.shapedWeaponTable(this.itemLookup, category, stack);
    }

    protected ShapedWeaponTableRecipeBuilder shapedWeaponTable(RecipeCategory category, ItemLike item, int count) {
        return ShapedWeaponTableRecipeBuilder.shapedWeaponTable(this.itemLookup, category, item, count);
    }

    protected ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(RecipeCategory category, ItemLike item) {
        return ShapelessWeaponTableRecipeBuilder.shapelessWeaponTable(this.itemLookup, category, item);
    }

    protected ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(RecipeCategory category, ItemLike item, int count) {
        return ShapelessWeaponTableRecipeBuilder.shapelessWeaponTable(this.itemLookup, category, item, count);
    }

    protected InfuserRecipeBuilder infuser(ItemStack output) {
        return InfuserRecipeBuilder.infuserRecipe(this.itemLookup, output);
    }

    protected InfuserRecipeBuilder infuserUpgrade() {
        return InfuserRecipeBuilder.infuserRecipe(this.itemLookup);
    }

    protected void fiveTieredMetalInfusionRecipe(ItemLike ingredientItem, ItemLike result) {
        for (int i = 0; i < 5; i++) {
            metalInfusionRecipe(ingredientItem, result, i);
        }
    }

    protected void fiveTieredMetalInfusionRecipe(TagKey<Item> ingredientTag, ItemLike result) {
        for (int i = 0; i < 5; i++) {
            metalInfusionRecipe(ingredientTag, result, i);
        }
    }

    protected void metalInfusionRecipe(Ingredient inputIngredient, String ingredientName, ItemLike result, int level, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger) {
        ItemLike pureBloodRequired = PureBloodItem.getBloodItemForLevel(level);
        ItemLike leftoverBloodItem = level == 0 ? ModItems.VAMPIRE_BLOOD_BOTTLE : PureBloodItem.getBloodItemForLevel(level - 1);
        int burnTime = 200 + 100 * level;

        infuser(PureLevel.pureBlood(result.asItem().getDefaultInstance(), level))
                .ingredients(Ingredient.of(pureBloodRequired))
                .input(inputIngredient)
                .results(leftoverBloodItem.asItem().getDefaultInstance())
                .burnTime(burnTime)
                .unlockedBy(ingredientName, hasIngredientTrigger)
                .unlockedBy("has_pure_blood", has(pureBloodRequired))
                .save(this.output, modString(ingredientName + "_pure_" + level));
    }

    protected void metalInfusionRecipe(ItemLike ingredientItem, ItemLike result, int level) {
        String ingredientName = RegUtil.id(ingredientItem.asItem()).getPath();
        metalInfusionRecipe(Ingredient.of(ingredientItem), ingredientName, result, level, has(ingredientItem));
    }

    protected void metalInfusionRecipe(TagKey<Item> ingredientTag, ItemLike result, int level) {
        metalInfusionRecipe(tag(ingredientTag), getTagName(ingredientTag), result, level, has(ingredientTag));
    }

    protected static String getTagName(TagKey<Item> tag) {
        String path = tag.location().getPath();
        int lastSeparator = path.lastIndexOf('/');
        return lastSeparator == -1 ? path : path.substring(lastSeparator + 1);
    }

    protected void fiveTieredInfusedMetalSmeltingRecipe(ItemLike rawIngredient, ItemLike result) {
        for (int i = 0; i < 5; i++) {
            infusedMetalSmeltingRecipe(rawIngredient, result, i);
        }
    }

    protected void infusedMetalSmeltingRecipe(ItemLike rawIngredient, ItemLike result, int level) {
        String ingredientName = RegUtil.id(rawIngredient.asItem()).getPath();
        String resultName = RegUtil.id(result.asItem()).getPath();
        SimpleCookingRecipeBuilder
                .smelting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), rawIngredient), RecipeCategory.BUILDING_BLOCKS,CookingBookCategory.MISC, PureLevel.template(result, level), (float) Math.pow(2F, level), 200 + level * 100)
                .unlockedBy("has_" + resultName, has(rawIngredient))
                .save(this.output, modString(ingredientName + "_pure_" + level + "_smelting"));
        SimpleCookingRecipeBuilder
                .blasting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), rawIngredient), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.MISC, PureLevel.template(result, level), (float) Math.pow(2F, level), 100 + level * 50)
                .unlockedBy("has_" + resultName, has(rawIngredient))
                .save(this.output, modString(ingredientName + "_pure_" + level + "_blasting"));
    }

    /**
     * The pattern is written the same as in the shaped recipe builder, but all three lines should be in one line using {@code \n} separators. For example: {@code "X\nX\nY}.
     * <p>
     * X - The metal or gem used in crafting.
     * Y - Stick.
     */
    protected void fiveTieredInfusedSwordCrafting(ItemLike result, ItemLike metalIngredient, String pattern) {
        for (int i = 0; i < 5; i++) {
            infusedSwordCrafting(result, metalIngredient, pattern, i);
        }
    }

    /**
     * The pattern is written the same as in the shaped recipe builder, but all three lines should be in one line using {@code \n} separators.
     * X - The metal or gem used in crafting.
     * Y - Stick.
     */
    protected void infusedSwordCrafting(ItemLike result, ItemLike metalIngredient, String pattern, int level) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.template(result, level));
        for (String row : pattern.split("\n")) {
            builder.pattern(row);
        }
        builder
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), metalIngredient))
                .define('Y', STICK)
                .unlockedBy("has_" + RegUtil.id(metalIngredient), has(metalIngredient))
                .save(output, RegUtil.id(result) + "_pure_" + level);
    }

    /**
     * Generates three recipes for a crossbow arrow with an oil effect with one, two and three arrows per oil bottle.
     */
    protected void upToThreeCrossbowArrowRecipe(ItemLike arrow, Holder<IOil> oil) {
        for (int i = 1; i <= 3; i++) {
            crossbowArrowRecipe(arrow, oil, i);
        }
    }

    /**
     * Generates a recipe for a crossbow arrow with an oil effect. {@code quantity} refers to the number of arrows in the recipe for one oil item. It is recommended to make three recipes for one, two and three arrows (use {@link #upToThreeCrossbowArrowRecipe(net.minecraft.world.level.ItemLike, net.minecraft.core.Holder)}), although it depends on the oil value. Teleport arrow, for example, only allows one arrow for one oil bottle.
     */
    protected void crossbowArrowRecipe(ItemLike arrow, Holder<IOil> oil, int quantity) {
        shapelessWeaponTable(RecipeCategory.COMBAT, arrow, quantity)
                .lava(1)
                .requires(ModItems.CROSSBOW_ARROW_NORMAL, quantity)
                .requires(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(oil), ModItems.OIL_BOTTLE))
                .unlockedBy("has_crossbow_arrow_normal", has(ModItems.CROSSBOW_ARROW_NORMAL))
                .save(output, RegUtil.id(arrow) + "_" + quantity);
    }

    protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemStackTemplate unpacked, RecipeCategory packedCategory, ItemStackTemplate packed, String pathSuffix) {
        this.nineBlockStorageRecipes(unpackedCategory, unpacked, packedCategory, packed, packed.item().getKey().identifier().withSuffix(pathSuffix), null, unpacked.item().getKey().identifier().withSuffix(pathSuffix), null);
    }

    protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemStackTemplate unpacked, RecipeCategory packedCategory, ItemStackTemplate packed, Identifier packedName, @Nullable String packedGroup, Identifier unpackedName, @Nullable String unpackedGroup) {
        this.shapeless(unpackedCategory, unpacked)
                .requires(DataComponentIngredient.of(false, packed))
                .group(unpackedGroup).unlockedBy(getHasName(packed.item().value()), this.has(packed.item().value()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, unpackedName));
        ShapedRecipeBuilder
                .shaped(this.itemLookup, packedCategory, packed)
                .define('#', DataComponentIngredient.of(false, unpacked))
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(packedGroup)
                .unlockedBy(getHasName(unpacked.item().value()), this.has(unpacked.item().value()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, packedName));
    }

    protected void netheriteSmithing(Ingredient ingredient, RecipeCategory category, Ingredient material, Item resultItem, DataComponentPatch patch, String pathSuffix) {
        ModdedSmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), ingredient, material, category, resultItem, patch)
                .unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS))
                .save(this.output, ResourceKey.create(Registries.RECIPE, RegUtil.id(resultItem).withSuffix(pathSuffix).withSuffix("_smithing")));
    }

    // Original method with added namespace on save.
    @Override
    protected void stonecutterResultFromBase(RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
                .unlockedBy(getHasName(material), this.has(material))
                .save(this.output, RegUtil.id(result).getNamespace() + ":" + getConversionRecipeName(result, material) + "_stonecutting");
    }

    @Override
    protected void netheriteSmithing(Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), this.tag(ItemTags.NETHERITE_TOOL_MATERIALS), category, resultItem)
                .unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS))
                .save(this.output, RegUtil.id(resultItem).getNamespace() + ":" + getItemName(resultItem) + "_smithing");
    }
}
