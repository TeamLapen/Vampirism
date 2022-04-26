package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class AlchemicalTableRecipeBuilder {

    public static AlchemicalTableRecipeBuilder builder(ItemStack stack) {
        return new AlchemicalTableRecipeBuilder(stack);
    }

    private final ItemStack result;
    private final IOil resultOil;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;
    private Ingredient ingredient;
    private IOil ingredientOil = ModOils.empty;
    private Ingredient input;
    private ISkill[] skills;

    public AlchemicalTableRecipeBuilder(ItemStack result) {
        this(result, OilUtils.getOil(result));
    }

    public AlchemicalTableRecipeBuilder(ItemStack result, IOil resultOil) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(resultOil);
        this.result = result;
        this.resultOil = resultOil;
    }

    public AlchemicalTableRecipeBuilder group(String group){
        this.group = group;
        return this;
    }

    public AlchemicalTableRecipeBuilder ingredient(Ingredient ingredient){
        this.ingredient = ingredient;
        return this;
    }

    public AlchemicalTableRecipeBuilder ingredient(Ingredient ingredient, IOil ingredientOil){
        this.ingredient = ingredient;
        this.ingredientOil = ingredientOil;
        return this;
    }

    public AlchemicalTableRecipeBuilder input(Ingredient input){
        this.input = input;
        return this;
    }

    public AlchemicalTableRecipeBuilder withCriterion(String name, ICriterionInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public AlchemicalTableRecipeBuilder withSkills(ISkill... skills) {
        this.skills = skills;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id){
        id = new ResourceLocation(id.getNamespace(), "alchemical_table/" + id.getPath());
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.group != null ? this.group : "", this.ingredient, this.ingredientOil, this.input, this.result, this.resultOil, this.skills != null ? this.skills : new ISkill[0], new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
    }

    private void validate(ResourceLocation id) {
        if (this.ingredient == null) {
            throw new IllegalStateException("No ingredients defined for alchemical table recipe " + id + "!");
        } else if (this.input == null) {
            throw new IllegalStateException("No input defined for alchemical table recipe " + id + "!");
        }
    }

    private static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final IOil resultOil;
        private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
        private final String group;
        private final Ingredient ingredient;
        private final IOil ingredientOil;
        private final Ingredient input;
        private final ISkill[] skills;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, String group, Ingredient ingredient, IOil ingredientOil, Ingredient input, ItemStack result, IOil resultOil, ISkill[] skills, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.resultOil = resultOil;
            this.group = group;
            this.ingredient = ingredient;
            this.ingredientOil = ingredientOil;
            this.input = input;
            this.skills = skills;
            this.advancementId = advancementId;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }

        @Nonnull
        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getType() {
            return ModRecipes.alchemical_table;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public void serializeRecipeData(@Nonnull JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            JsonObject result = new JsonObject();
            result.addProperty("item", this.result.getItem().getRegistryName().toString());
            if (this.result.getCount() > 1) {
                result.addProperty("count", this.result.getCount());
            }
            json.add("result", result);
            if (this.resultOil != ModOils.empty) {
                json.addProperty("result_oil", this.resultOil.getRegistryName().toString());
            }

            json.add("ingredient", this.ingredient.toJson());
            if (this.ingredientOil != ModOils.empty) {
                json.addProperty("ingredient_oil", this.ingredientOil.getRegistryName().toString());
            }
            json.add("input", this.input.toJson());

            JsonArray skills = new JsonArray();
            for (ISkill skill : this.skills) {
                skills.add(skill.getRegistryName().toString());
            }
            json.add("skill", skills);
        }
    }
}
