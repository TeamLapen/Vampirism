package de.teamlapen.vampirism.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableRecipe extends AbstractBrewingRecipe {

    private final ISkill[] requiredSkills;

    public AlchemyTableRecipe(ResourceLocation id, String group, Ingredient ingredient, Ingredient input, ItemStack result, ISkill[] skills) {
        super(ModRecipes.ALCHEMICAL_TABLE_TYPE.get(), id, group, ingredient, input, result);
        this.requiredSkills = skills;
    }

    public boolean isInput(@NotNull ItemStack input) {
        return UtilLib.matchesItem(this.input, input);
    }

    public boolean isIngredient(@NotNull ItemStack ingredient) {
        return this.ingredient.test(ingredient);
    }

    @NotNull
    public ItemStack getResult(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? this.result.copy() : ItemStack.EMPTY;
    }

    public ISkill[] getRequiredSkills() {
        return requiredSkills;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMICAL_TABLE.get();
    }

    public static class Serializer implements RecipeSerializer<AlchemyTableRecipe> {

        @NotNull
        @Override
        public AlchemyTableRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            ISkill[] skills = VampirismRecipeHelper.deserializeSkills(GsonHelper.getAsJsonArray(json, "skill", null));
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new AlchemyTableRecipe(recipeId, group, ingredient, input, result, skills);
        }

        @Nullable
        @Override
        public AlchemyTableRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            ItemStack result = buffer.readItem();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient input = Ingredient.fromNetwork(buffer);
            ISkill[] skills = new ISkill[buffer.readVarInt()];
            if (skills.length != 0) {
                for (int i = 0; i < skills.length; i++) {
                    skills[i] = RegUtil.getSkill(new ResourceLocation(buffer.readUtf(32767)));
                }
            }
            return new AlchemyTableRecipe(recipeId, group, ingredient, input, result, skills);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull AlchemyTableRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.getResultItem());
            recipe.ingredient.toNetwork(buffer);
            recipe.input.toNetwork(buffer);
            buffer.writeVarInt(recipe.requiredSkills.length);
            if (recipe.requiredSkills.length != 0) {
                for (ISkill skill : recipe.requiredSkills) {
                    buffer.writeResourceLocation(RegUtil.id(skill));
                }
            }
        }
    }
}
