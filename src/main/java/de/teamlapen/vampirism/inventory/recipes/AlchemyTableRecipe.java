package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class AlchemyTableRecipe extends AbstractBrewingRecipe {

    private final IOil ingredientOil;
    private final ISkill[] requiredSkills;

    public AlchemyTableRecipe(ResourceLocation id, String group, Ingredient ingredient, IOil ingredientOil, Ingredient input, ItemStack result, ISkill[] skills) {
        super(ModRecipes.ALCHEMICAL_TABLE_TYPE,id, group, ingredient, input, result);
        this.ingredientOil = ingredientOil;
        this.requiredSkills = skills;
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

    public ISkill[] getRequiredSkills() {
        return requiredSkills;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(Arrays.stream(this.ingredient.getItems()).map(stack -> OilUtils.setOil(stack, this.ingredientOil))));
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.alchemical_table;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlchemyTableRecipe> {

        @Nonnull
        @Override
        public AlchemyTableRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
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
                ResourceLocation oil = new ResourceLocation(json.get("ingredient_oil").getAsString());
                ingredientOil = ModRegistries.OILS.getValue(oil);
            }
            ISkill[] skills = VampirismRecipeHelper.deserializeSkills(JSONUtils.getAsJsonArray(json, "skill", null));
            return new AlchemyTableRecipe(recipeId, group, ingredient, ingredientOil, input, result, skills);
        }

        @Nullable
        @Override
        public AlchemyTableRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            String group = buffer.readUtf();
            ItemStack result = buffer.readItem();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ResourceLocation ingredientOil = buffer.readResourceLocation();
            Ingredient input = Ingredient.fromNetwork(buffer);
            ISkill[] skills = new ISkill[buffer.readVarInt()];
            if (skills.length != 0) {
                for (int i = 0; i < skills.length; i++) {
                    skills[i] = ModRegistries.SKILLS.getValue(new ResourceLocation(buffer.readUtf(32767)));
                }
            }
            return new AlchemyTableRecipe(recipeId, group, ingredient,ModRegistries.OILS.getValue(ingredientOil), input, result, skills);
        }

        @Override
        public void toNetwork(@Nonnull PacketBuffer buffer, @Nonnull AlchemyTableRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.getResultItem());
            recipe.ingredient.toNetwork(buffer);
            //noinspection ConstantConditions
            buffer.writeResourceLocation(recipe.ingredientOil.getRegistryName());
            recipe.input.toNetwork(buffer);
            buffer.writeVarInt(recipe.requiredSkills.length);
            if (recipe.requiredSkills.length != 0) {
                for (ISkill skill : recipe.requiredSkills) {
                    buffer.writeUtf(skill.getRegistryName().toString());
                }
            }
        }
    }
}
