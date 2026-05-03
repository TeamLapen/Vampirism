package de.teamlapen.vampirism.data.builder;

import de.teamlapen.vampirism.common.world.items.recipes.InfuserRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class InfuserRecipeBuilder implements RecipeBuilder {
    private final HolderLookup.RegistryLookup<Item> itemLookup;
    @Nullable
    private final ItemStackTemplate result;
    protected String group = "";
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private final List<Ingredient> ingredients = Arrays.asList(new Ingredient[4]);
    private Ingredient input;
    private final List<ItemStackTemplate> results = Arrays.asList(new ItemStackTemplate[3]);
    private int burnTime = 200;

    public InfuserRecipeBuilder(HolderLookup.RegistryLookup<Item> itemLookup, ItemStackTemplate result) {
        this.itemLookup = itemLookup;
        this.result = result;
    }

    public InfuserRecipeBuilder(HolderLookup.RegistryLookup<Item> itemLookup) {
        this.itemLookup = itemLookup;
        this.result = null;
    }

    @Override
    public @NotNull InfuserRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull InfuserRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    public InfuserRecipeBuilder ingredient(Ingredient ingredient) {
        int i = ingredients.indexOf(null);
        if (i == -1) {
            throw new IllegalArgumentException("Ingredients are already filled");
        }
        this.ingredients.set(i, ingredient);
        return this;
    }

    public InfuserRecipeBuilder ingredient(int number, Ingredient ingredient) {
        this.ingredients.set(number, ingredient);
        return this;
    }

    public InfuserRecipeBuilder ingredients(Ingredient ingredient) {
        Collections.fill(this.ingredients, ingredient);
        return this;
    }

    public InfuserRecipeBuilder input(Ingredient input) {
        this.input = input;
        return this;
    }

    public InfuserRecipeBuilder burnTime(int burnTime) {
        this.burnTime = burnTime;
        return this;
    }

    public InfuserRecipeBuilder result(ItemStackTemplate result) {
        int i = results.indexOf(null);
        if (i == -1) {
            throw new IllegalArgumentException("Results are already filled");
        }
        this.results.set(i, result);
        return this;
    }

    public InfuserRecipeBuilder result(int number, ItemStackTemplate result) {
        this.results.set(number, result);
        return this;
    }

    public InfuserRecipeBuilder results(ItemStackTemplate result) {
        Collections.fill(this.results, result);
        return this;
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> key) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        Recipe.CommonInfo commonInfo = RecipeBuilder.createCraftingCommonInfo(true);
        var recipe = new InfuserRecipe(commonInfo, this.group, this.ingredients.get(0), this.ingredients.get(1), this.ingredients.get(2), this.ingredients.get(3), this.input, this.results.get(0), this.results.get(1), this.results.get(2), Optional.ofNullable(this.result), this.burnTime);
        recipeOutput.accept(key, recipe, advancement.build(key.identifier().withPrefix("recipes/infuser/")));
    }

    public static InfuserRecipeBuilder infuserRecipe(HolderLookup.RegistryLookup<Item> itemLookup, ItemStackTemplate result) {
        return new InfuserRecipeBuilder(itemLookup, result);
    }

    public static InfuserRecipeBuilder infuserRecipe(HolderLookup.RegistryLookup<Item> itemLookup) {
        return new InfuserRecipeBuilder(itemLookup);
    }
}
