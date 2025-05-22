package de.teamlapen.vampirism.data.provider.parent;

import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.data.builder.*;
import de.teamlapen.vampirism.items.VampireCloakItem;
import de.teamlapen.vampirism.util.ColorListsUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class ExtendedRecipeProvider extends RecipeProvider {

    protected HolderLookup.RegistryLookup<Item> itemLookup = this.registries.lookupOrThrow(Registries.ITEM);

    public ExtendedRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
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

    protected void nineBlockStorageRecipes(RecipeCategory unpackedCategory, ItemStack unpacked, RecipeCategory packedCategory, ItemStack packed, ResourceLocation packedName, @Nullable String packedGroup, ResourceLocation unpackedName, @Nullable String unpackedGroup) {
        this.shapeless(unpackedCategory, unpacked).requires(DataComponentIngredient.of(false, packed)).group(unpackedGroup).unlockedBy(getHasName(packed.getItem()), this.has(packed.getItem())).save(this.output, ResourceKey.create(Registries.RECIPE, unpackedName));
        ShapedRecipeBuilder.shaped(this.itemLookup, packedCategory, packed).define('#', DataComponentIngredient.of(false, unpacked)).pattern("###").pattern("###").pattern("###").group(packedGroup).unlockedBy(getHasName(unpacked.getItem()), this.has(unpacked.getItem())).save(this.output, ResourceKey.create(Registries.RECIPE, packedName));
    }

    protected void netheriteSmithing(Ingredient ingredient, RecipeCategory category, Ingredient material, ItemStack resultItem, String pathSuffix) {
        ModdedSmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), ingredient, material, category, resultItem).unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS)).save(this.output, ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(resultItem.getItem()).withSuffix(pathSuffix).withSuffix("_smithing")));
    }
}
