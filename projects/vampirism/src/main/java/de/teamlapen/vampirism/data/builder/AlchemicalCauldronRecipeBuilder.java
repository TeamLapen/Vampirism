package de.teamlapen.vampirism.data.builder;

import com.mojang.datafixers.util.Either;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.common.world.items.recipes.AlchemicalCauldronRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class AlchemicalCauldronRecipeBuilder implements RecipeBuilder {

    public static @NotNull AlchemicalCauldronRecipeBuilder cauldronRecipe(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull Item item) {
        return AlchemicalCauldronRecipeBuilder.cauldronRecipe(itemLookup, item, 1);
    }

    public static @NotNull AlchemicalCauldronRecipeBuilder cauldronRecipe(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull Item item, int count) {
        return new AlchemicalCauldronRecipeBuilder(itemLookup, item, count);
    }

    private final HolderLookup.RegistryLookup<Item> itemLookup;
    protected final @NotNull ItemStack result;
    protected String group;
    protected Ingredient ingredient;
    protected Either<Ingredient, FluidStack> fluid;
    protected List<Holder<ISkill<?>>> skills = new LinkedList<>();
    protected int reqLevel = 1;
    protected int cookTime = 200;
    protected float exp = 0.2f;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlchemicalCauldronRecipeBuilder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull Item result, int count) {
        this.itemLookup = itemLookup;
        this.result = new ItemStack(result, count);
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceLocation) {
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        Recipe.CommonInfo commonInfo = RecipeBuilder.createCraftingCommonInfo(true);
        var recipe = new AlchemicalCauldronRecipe(commonInfo, Objects.requireNonNullElse(this.group, ""), this.ingredient, this.fluid, this.result, this.skills, this.reqLevel, this.cookTime, this.exp);
        recipeOutput.accept(resourceLocation, recipe, builder.build(resourceLocation.identifier().withPrefix("recipes/alchemical_cauldron/")));
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
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
        this.fluid = Either.left(Ingredient.of(this.itemLookup.getOrThrow(tag)));
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

    public @NotNull AlchemicalCauldronRecipeBuilder withIngredient(@NotNull ItemLike... items) {
        this.ingredient = Ingredient.of(items);
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withIngredient(@NotNull TagKey<Item> tag) {
        this.ingredient = Ingredient.of(this.itemLookup.getOrThrow(tag));
        return this;
    }

    @SafeVarargs
    public final @NotNull AlchemicalCauldronRecipeBuilder withSkills(@NotNull Holder<ISkill<?>>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }

}
