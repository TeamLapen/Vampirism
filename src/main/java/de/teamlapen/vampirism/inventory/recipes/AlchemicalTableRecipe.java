package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.items.IOilItem;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemicalTableRecipe extends AbstractBrewingRecipe {

    private final IOil ingredientOil;

    public AlchemicalTableRecipe(ResourceLocation id, String group, Ingredient ingredient, IOil ingredientOil, Ingredient input, ItemStack result) {
        super(ModRecipes.ALCHEMICAL_TABLE_TYPE,id, group, ingredient, input, result);
        this.ingredientOil = ingredientOil;
    }

    public boolean isInput(@Nonnull ItemStack input) {
        return UtilLib.matchesItem(this.input, input);
    }

    public boolean isIngredient(@Nonnull ItemStack ingredient) {
        return this.ingredient.test(ingredient) && (!(ingredient.getItem() instanceof IOilItem) || OilUtils.getOil(ingredient) == ingredientOil);
    }

    @Nonnull
    public ItemStack getResult(@Nonnull ItemStack input, @Nonnull ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? this.result.copy() : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.alchemical_table;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlchemicalTableRecipe> {

        @Nonnull
        @Override
        public AlchemicalTableRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            String group = JSONUtils.getAsString(json, "group", "");
            Ingredient ingredient = Ingredient.fromJson(JSONUtils.isArrayNode(json, "ingredient") ? JSONUtils.getAsJsonArray(json, "ingredient") : JSONUtils.getAsJsonObject(json, "ingredient"));
            Ingredient input = Ingredient.fromJson(JSONUtils.isArrayNode(json, "input") ? JSONUtils.getAsJsonArray(json, "input") : JSONUtils.getAsJsonObject(json, "input"));
            JsonObject resultJson = JSONUtils.getAsJsonObject(json, "result");
            ItemStack result = CraftingHelper.getItemStack(resultJson, true);
            if (json.has("result_oil")) {
                ResourceLocation oil = new ResourceLocation(json.get("result_oil").getAsString());
                //noinspection ConstantConditions
                OilUtils.setOil(result, ModRegistries.OILS.getValue(oil));
            }
            IOil ingredientOil = ModOils.empty;
            if (json.has("ingredient_oil")){
                ResourceLocation oil = new ResourceLocation(json.get("oil").getAsString());
                ingredientOil = ModRegistries.OILS.getValue(oil);
            }
            return new AlchemicalTableRecipe(recipeId, group, ingredient, ingredientOil, input, result);
        }

        @Nullable
        @Override
        public AlchemicalTableRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            String group = buffer.readUtf();
            ItemStack result = buffer.readItem();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ResourceLocation ingredientOil = buffer.readResourceLocation();
            Ingredient input = Ingredient.fromNetwork(buffer);
            return new AlchemicalTableRecipe(recipeId, group, ingredient,ModRegistries.OILS.getValue(ingredientOil), input, result);
        }

        @Override
        public void toNetwork(@Nonnull PacketBuffer buffer, @Nonnull AlchemicalTableRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.getResultItem());
            recipe.ingredient.toNetwork(buffer);
            //noinspection ConstantConditions
            buffer.writeResourceLocation(recipe.ingredientOil.getRegistryName());
            recipe.input.toNetwork(buffer);
        }
    }
}
