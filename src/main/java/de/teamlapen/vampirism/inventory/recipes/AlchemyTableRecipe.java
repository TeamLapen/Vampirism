package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemyTableRecipe extends AbstractBrewingRecipe {

    private final ISkill[] requiredSkills;

    public AlchemyTableRecipe(ResourceLocation id, String group, Ingredient ingredient, Ingredient input, ItemStack result, ISkill[] skills) {
        super(ModRecipes.ALCHEMICAL_TABLE_TYPE,id, group, ingredient, input, result);
        this.requiredSkills = skills;
    }

    public boolean isInput(@Nonnull ItemStack input) {
        return UtilLib.matchesItem(this.input, input);
    }

    public boolean isIngredient(@Nonnull ItemStack ingredient) {
        return this.ingredient.test(ingredient);
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
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMICAL_TABLE.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlchemyTableRecipe> {

        @Nonnull
        @Override
        public AlchemyTableRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            String group = JSONUtils.getAsString(json, "group", "");
            ISkill[] skills = VampirismRecipeHelper.deserializeSkills(JSONUtils.getAsJsonArray(json, "skill", null));
            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "ingredient"));
            Ingredient input = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "input"));
            ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            return new AlchemyTableRecipe(recipeId, group, ingredient, input, result, skills);
        }

        @Nullable
        @Override
        public AlchemyTableRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            String group = buffer.readUtf();
            ItemStack result = buffer.readItem();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient input = Ingredient.fromNetwork(buffer);
            ISkill[] skills = new ISkill[buffer.readVarInt()];
            if (skills.length != 0) {
                for (int i = 0; i < skills.length; i++) {
                    skills[i] = ModRegistries.SKILLS.getValue(new ResourceLocation(buffer.readUtf(32767)));
                }
            }
            return new AlchemyTableRecipe(recipeId, group, ingredient, input, result, skills);
        }

        @Override
        public void toNetwork(@Nonnull PacketBuffer buffer, @Nonnull AlchemyTableRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.getResultItem());
            recipe.ingredient.toNetwork(buffer);
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
