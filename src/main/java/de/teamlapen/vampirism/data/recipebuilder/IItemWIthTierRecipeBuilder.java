package de.teamlapen.vampirism.data.recipebuilder;

import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.mixin.ShapedRecipeBuilderAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class IItemWIthTierRecipeBuilder extends ShapedRecipeBuilder {
    public IItemWIthTierRecipeBuilder(@NotNull RecipeCategory category, @NotNull ItemLike resultIn, int countIn) {
        super(category, resultIn, countIn);
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumerIn, @NotNull ResourceLocation id) {
        this.ensureValid(id);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumerIn.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group,  determineBookCategory(((ShapedRecipeBuilderAccessor) this).getRecipeCategory()), this.rows, this.key, this.advancement, id.withPath("recipes/" + ((ShapedRecipeBuilderAccessor) this).getRecipeCategory().getFolderName() + "/" + id.getPath())));

    }

    private static class Result extends ShapedRecipeBuilder.Result {
        public Result(@NotNull ResourceLocation idIn, @NotNull Item resultIn, int countIn, @NotNull String groupIn, @NotNull CraftingBookCategory category, @NotNull List<String> patternIn, @NotNull Map<Character, Ingredient> keyIn, @NotNull Advancement.Builder advancementBuilderIn, @NotNull ResourceLocation advancementIdIn) {
            super(idIn, resultIn, countIn, groupIn, category, patternIn, keyIn, advancementBuilderIn, advancementIdIn);
        }

        @NotNull
        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.REPAIR_IITEMWITHTIER.get();
        }
    }
}
