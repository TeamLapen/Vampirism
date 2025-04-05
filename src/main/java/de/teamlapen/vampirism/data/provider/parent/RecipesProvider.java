package de.teamlapen.vampirism.data.provider.parent;

import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import de.teamlapen.vampirism.data.recipebuilder.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class RecipesProvider extends RecipeProvider {

    protected List<Item> DYES = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE, Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);

    protected HolderLookup.RegistryLookup<Item> itemLookup = this.registries.lookupOrThrow(Registries.ITEM);

    public RecipesProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @SafeVarargs
    protected final @NotNull Ingredient potion(Holder<Potion> @NotNull ... potion) {
        return CompoundIngredient.of(Arrays.stream(potion).map(PotionContents::new).map(s -> DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, s, Items.POTION)).toArray(Ingredient[]::new));
    }

    protected @NotNull Ingredient potion(@NotNull Holder<Potion> potion) {
        return DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(potion), Items.POTION);
    }

    protected void coffinFromWoolOrDye(RecipeOutput consumer, ItemLike coffin, ItemLike wool, ItemLike dye, String path) {
        coffinFromWool(consumer, coffin, wool, path);
        shapeless(RecipeCategory.DECORATIONS, coffin).requires(ModBlocks.COFFIN_WHITE.get()).requires(dye).unlockedBy("has_coffin", has(ModBlocks.COFFIN_WHITE.get())).unlockedBy("has_dye", has(dye)).save(consumer, path + "_from_white");
    }

    protected void coffinFromWool(RecipeOutput consumer, ItemLike coffin, ItemLike wool, String path) {
        shaped(RecipeCategory.DECORATIONS, coffin).pattern("XXX").pattern("YYY").pattern("XXX").define('X', ItemTags.PLANKS).define('Y', wool).unlockedBy("has_wool", has(wool)).save(consumer, path);
    }

    protected void colorWithDye(List<Item> dyeableItems, RecipeCategory category, Function<String, String> folder) {
        for (int i = 0; i < DYES.size(); i++) {
            Item dyeItem = DYES.get(i);
            Item dyedItem = dyeableItems.get(i);
            Stream<Item> stream = dyeableItems.stream().filter(item -> !item.equals(dyedItem));

            this.shapeless(category, dyedItem).requires(dyeItem).requires(Ingredient.of(stream)).unlockedBy("has_needed_dye", has(dyeItem)).save(output, folder.apply("dye_" + BuiltInRegistries.ITEM.getKey(dyedItem).getPath()));
        }
    }

    private void enchantment(ItemStack stack, int level, @NotNull Holder<Enchantment> enchantment) {
        stack.enchant(enchantment, level);
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

    protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemStack unpacked, RecipeCategory packedCategory, ItemStack packed, String pathSuffix) {
        this.nineBlockStorageRecipes(unpackedCategory, unpacked, packedCategory, packed, BuiltInRegistries.ITEM.getKey(packed.getItem()).withSuffix(pathSuffix), null, BuiltInRegistries.ITEM.getKey(unpacked.getItem()).withSuffix(pathSuffix), null);
    }

    protected void nineBlockStorageRecipes(
            RecipeCategory unpackedCategory,
            ItemStack unpacked,
            RecipeCategory packedCategory,
            ItemStack packed,
            ResourceLocation packedName,
            @Nullable String packedGroup,
            ResourceLocation unpackedName,
            @Nullable String unpackedGroup
    ) {
        this.shapeless(unpackedCategory, unpacked)
                .requires(DataComponentIngredient.of(false, packed))
                .group(unpackedGroup)
                .unlockedBy(getHasName(packed.getItem()), this.has(packed.getItem()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, unpackedName));
        ShapedRecipeBuilder.shaped(this.itemLookup, packedCategory, packed)
                .define('#', DataComponentIngredient.of(false, unpacked))
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(packedGroup)
                .unlockedBy(getHasName(unpacked.getItem()), this.has(unpacked.getItem()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, packedName));
    }

    protected void netheriteSmithing(Ingredient ingredient, RecipeCategory category, Ingredient material, ItemStack resultItem, String pathSuffix) {
        ModdedSmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        ingredient,
                        material,
                        category,
                        resultItem
                )
                .unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS))
                .save(this.output, ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(resultItem.getItem()).withSuffix(pathSuffix).withSuffix("_smithing")));
    }
}
