package de.teamlapen.vampirism.data.builder;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.common.world.items.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.misc.mixin.accessor.ShapedRecipeBuilderAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ShapedWeaponTableRecipeBuilder extends ShapedRecipeBuilder {

    public static @NotNull ShapedWeaponTableRecipeBuilder shapedWeaponTable(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemLike item) {
        return new ShapedWeaponTableRecipeBuilder(holderGetter, category, item, 1);
    }

    public static @NotNull ShapedWeaponTableRecipeBuilder shapedWeaponTable(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemLike item, int count) {
        return new ShapedWeaponTableRecipeBuilder(holderGetter, category, item, count);
    }

    public static @NotNull ShapedWeaponTableRecipeBuilder shapedWeaponTable(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemStackTemplate stack) {
        return new ShapedWeaponTableRecipeBuilder(holderGetter, category, stack);
    }

    private int lava = 1;
    private final List<Holder<ISkill<?>>> skills = new LinkedList<>();
    private int level = 1;

    public ShapedWeaponTableRecipeBuilder(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemLike item, int count) {
        super(holderGetter, category, item, count);
    }

    public ShapedWeaponTableRecipeBuilder(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemStackTemplate itemStack) {
        super(holderGetter, category, itemStack);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder define(@NotNull Character symbol, @NotNull ItemLike itemIn) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, itemIn);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder define(@NotNull Character symbol, @NotNull Ingredient ingredientIn) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, ingredientIn);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder define(@NotNull Character symbol, @NotNull TagKey<Item> tagIn) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, tagIn);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder group(@Nullable String groupIn) {
        return (ShapedWeaponTableRecipeBuilder) super.group(groupIn);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder pattern(@NotNull String patternIn) {
        return (ShapedWeaponTableRecipeBuilder) super.pattern(patternIn);
    }

    @Override
    public @NotNull ShapedWeaponTableRecipeBuilder showNotification(boolean p_273326_) {
        return (ShapedWeaponTableRecipeBuilder) super.showNotification(p_273326_);
    }

    public @NotNull ShapedWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public @NotNull ShapedWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public void save(RecipeOutput output, @NotNull ResourceKey<Recipe<?>> recipeId) {
        ShapedRecipePattern shapedRecipePattern = ShapedRecipePattern.of(key(), rows());
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.advancementBuilder().criteria.forEach(advancement::addCriterion);
        ShapedWeaponTableRecipe recipe = new ShapedWeaponTableRecipe(
                RecipeBuilder.createCraftingCommonInfo(this.showNotification),
                RecipeBuilder.createCraftingBookInfo(this.category, this.group), shapedRecipePattern, result, level, skills, lava);
        output.accept(recipeId, recipe, advancement.build(recipeId.identifier().withPrefix("recipes/weapontable/")));
    }

    @Override
    public @NotNull ShapedWeaponTableRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        return (ShapedWeaponTableRecipeBuilder) super.unlockedBy(name, criterion);
    }

    @SafeVarargs
    public final @NotNull ShapedWeaponTableRecipeBuilder skills(@NotNull Holder<ISkill<?>>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }

}
