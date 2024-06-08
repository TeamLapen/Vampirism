package de.teamlapen.vampirism.data.recipebuilder;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AlchemicalCauldronRecipeBuilder implements RecipeBuilder {
    public static @NotNull AlchemicalCauldronRecipeBuilder cauldronRecipe(@NotNull Item item) {
        return AlchemicalCauldronRecipeBuilder.cauldronRecipe(item, 1);
    }

    public static @NotNull AlchemicalCauldronRecipeBuilder cauldronRecipe(@NotNull Item item, int count) {
        return new AlchemicalCauldronRecipeBuilder(item, count);
    }

    protected final @NotNull ItemStack result;
    protected String group;
    protected Ingredient ingredient;
    protected Either<Ingredient, FluidStack> fluid;
    protected List<Holder<ISkill<?>>> skills = new LinkedList<>();
    protected int reqLevel = 1;
    protected int cookTime = 200;
    protected float exp = 0.2f;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlchemicalCauldronRecipeBuilder(@NotNull Item result, int count) {
        this.result = new ItemStack(result, count);
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceLocation resourceLocation) {
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        var recipe = new AlchemicalCauldronRecipe(Objects.requireNonNullElse(this.group, ""), CookingBookCategory.MISC, this.ingredient, this.fluid, this.result, this.skills, this.reqLevel, this.cookTime, this.exp);
        recipeOutput.accept(resourceLocation, recipe, builder.build(resourceLocation.withPrefix("recipes/alchemical_cauldron/")));
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return result.getItem();
    }

    public @NotNull AlchemicalCauldronRecipeBuilder cookTime(int cookTime) {
        this.cookTime = cookTime;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder experience(float exp) {
        this.exp = exp;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder requireLevel(int level) {
        this.reqLevel = level;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withFluid(@NotNull TagKey<Item> tag) {
        this.fluid = Either.left(Ingredient.of(tag));
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withFluid(@NotNull FluidStack fluid) {
        this.fluid = Either.right(fluid);
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withFluid(@NotNull ItemLike... item) {
        this.fluid = Either.left(Ingredient.of(item));
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withFluid(@NotNull ItemStack... stacks) {
        this.fluid = Either.left(Ingredient.of(stacks));
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withIngredient(@NotNull ItemLike... items) {
        this.ingredient = Ingredient.of(items);
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withIngredient(@NotNull ItemStack... stacks) {
        this.ingredient = Ingredient.of(stacks);
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withIngredient(@NotNull TagKey<Item> tag) {
        this.ingredient = Ingredient.of(tag);
        return this;
    }

    @SuppressWarnings("unchecked")
    public @NotNull AlchemicalCauldronRecipeBuilder withSkills(@NotNull Holder<ISkill<?>>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }

}
