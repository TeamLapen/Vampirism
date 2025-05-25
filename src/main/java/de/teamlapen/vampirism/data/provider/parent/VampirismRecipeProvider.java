package de.teamlapen.vampirism.data.provider.parent;

import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.data.builder.*;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.items.component.PureLevel;
import de.teamlapen.vampirism.util.ColorListsUtil;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VResourceLocation.modString;

public abstract class VampirismRecipeProvider extends RecipeProvider {

    protected HolderLookup.RegistryLookup<Item> itemLookup = this.registries.lookupOrThrow(Registries.ITEM);

    public VampirismRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @SafeVarargs
    protected final @NotNull Ingredient potion(Holder<Potion> @NotNull ... potion) {
        return CompoundIngredient.of(Arrays.stream(potion).map(PotionContents::new).map(s -> DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, s, Items.POTION)).toArray(Ingredient[]::new));
    }

    protected @NotNull Ingredient potion(@NotNull Holder<Potion> potion) {
        return DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(potion), Items.POTION);
    }

    protected void colorWithDye(Map<DyeColor, ? extends Item> dyeableItems, RecipeCategory category, Function<String, String> folder) {
        dyeableItems.keySet().forEach(dye -> {
            Item dyedItem = dyeableItems.get(dye);
            Item dyeItem = ColorListsUtil.DYE_ITEMS.get(dye);
            Stream<Item> stream = dyeableItems.values().stream().map(Item::asItem).filter(item -> !item.equals(dyedItem));
            if (dyedItem != null && dyeItem != null) {
                this.shapeless(category, dyedItem).requires(dyeItem).requires(Ingredient.of(stream)).unlockedBy("has_needed_dye", has(dyeItem)).save(output, folder.apply("dye_" + BuiltInRegistries.ITEM.getKey(dyedItem).getPath()));
            }
        });
    }

    protected void smeltingAndBlasting(RecipeCategory category, String name, ItemLike toSmelt, ItemLike result, float experience) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(toSmelt), category, result, experience, 200)
                .unlockedBy("has_" + RegUtil.id(toSmelt.asItem()).getPath(), has(toSmelt))
                .save(output, modString(name + "_from_smelting"));

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(toSmelt), category, result, experience, 100)
                .unlockedBy("has_" + RegUtil.id(toSmelt.asItem()).getPath(), has(toSmelt))
                .save(output, modString(name + "_from_blasting"));
    }

    protected void smeltingAndBlasting(RecipeCategory category, String name, ItemLike[] toSmelt, ItemLike result, float experience) {
        SimpleCookingRecipeBuilder smelting = SimpleCookingRecipeBuilder.smelting(Ingredient.of(toSmelt), category, result, experience, 200);
        for (ItemLike item : toSmelt) {
            smelting.unlockedBy("has_" + RegUtil.id(item.asItem()).getPath(), has(item));
        }
        smelting.save(output, modString(name + "_from_smelting"));

        SimpleCookingRecipeBuilder blasting = SimpleCookingRecipeBuilder.blasting(Ingredient.of(toSmelt), category, result, experience, 100);
        for (ItemLike item : toSmelt) {
            blasting.unlockedBy("has_" + RegUtil.id(item.asItem()).getPath(), has(item));
        }
        blasting.save(output, modString(name + "_from_blasting"));
    }

    protected AlchemyTableRecipeBuilder alchemyTable(@NotNull Holder<IOil> oilStack) {
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
        String ingredientName = BuiltInRegistries.ITEM.getKey(ingredientItem.asItem()).getPath();
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
        String ingredientName = BuiltInRegistries.ITEM.getKey(rawIngredient.asItem()).getPath();
        String resultName = BuiltInRegistries.ITEM.getKey(result.asItem()).getPath();
        SimpleCookingRecipeBuilder
                .smelting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), rawIngredient), RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(result, level), (float) Math.pow(2F, level), 200 + level * 100)
                .unlockedBy("has_" + resultName, has(rawIngredient))
                .save(this.output, modString(ingredientName + "_pure_" + level + "_smelting"));
        SimpleCookingRecipeBuilder
                .blasting(DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), rawIngredient), RecipeCategory.BUILDING_BLOCKS, PureLevel.pureBlood(result, level), (float) Math.pow(2F, level), 100 + level * 50)
                .unlockedBy("has_" + resultName, has(rawIngredient))
                .save(this.output, modString(ingredientName + "_pure_" + level + "_blasting"));
    }

    /**
     * X - The metal or gem used in crafting.
     * Y - Stick.
     */
    protected void fiveTieredInfusedSwordCrafting(ItemLike result, ItemLike metalIngredient, String pattern) {
        for (int i = 0; i < 5; i++) {
            infusedSwordCrafting(result, metalIngredient, pattern, i);
        }
    }

    /**
     * X - The metal or gem used in crafting.
     * Y - Stick.
     */
    protected void infusedSwordCrafting(ItemLike result, ItemLike metalIngredient, String pattern, int level) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(this.itemLookup, RecipeCategory.COMBAT, PureLevel.pureBlood(result, level));
        for (String row : pattern.split("\n")) {
            builder.pattern(row);
        }
        builder
                .define('X', DataComponentIngredient.of(false, ModDataComponents.PURE_LEVEL, new PureLevel(level), metalIngredient))
                .define('Y', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_" + RegUtil.id(metalIngredient), has(metalIngredient))
                .save(output, RegUtil.id(result) + "_pure_" + level);
    }

    protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemStack unpacked, RecipeCategory packedCategory, ItemStack packed, String pathSuffix) {
        this.nineBlockStorageRecipes(unpackedCategory, unpacked, packedCategory, packed, BuiltInRegistries.ITEM.getKey(packed.getItem()).withSuffix(pathSuffix), null, BuiltInRegistries.ITEM.getKey(unpacked.getItem()).withSuffix(pathSuffix), null);
    }

    protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemStack unpacked, RecipeCategory packedCategory, ItemStack packed, ResourceLocation packedName, @Nullable String packedGroup, ResourceLocation unpackedName, @Nullable String unpackedGroup) {
        this.shapeless(unpackedCategory, unpacked).requires(DataComponentIngredient.of(false, packed)).group(unpackedGroup).unlockedBy(getHasName(packed.getItem()), this.has(packed.getItem())).save(this.output, ResourceKey.create(Registries.RECIPE, unpackedName));
        ShapedRecipeBuilder.shaped(this.itemLookup, packedCategory, packed).define('#', DataComponentIngredient.of(false, unpacked)).pattern("###").pattern("###").pattern("###").group(packedGroup).unlockedBy(getHasName(unpacked.getItem()), this.has(unpacked.getItem())).save(this.output, ResourceKey.create(Registries.RECIPE, packedName));
    }

    protected void netheriteSmithing(Ingredient ingredient, RecipeCategory category, Ingredient material, ItemStack resultItem, String pathSuffix) {
        ModdedSmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), ingredient, material, category, resultItem).unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS)).save(this.output, ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(resultItem.getItem()).withSuffix(pathSuffix).withSuffix("_smithing")));
    }
}
