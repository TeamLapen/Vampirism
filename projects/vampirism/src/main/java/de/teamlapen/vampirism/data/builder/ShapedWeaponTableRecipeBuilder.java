package de.teamlapen.vampirism.data.builder;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.common.world.items.recipes.ShapedWeaponTableRecipe;
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
    private final List<Holder<? extends ISkill<?>>> skills = new LinkedList<>();
    private int level = 1;

    public ShapedWeaponTableRecipeBuilder(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemLike item, int count) {
        super(holderGetter, category, item, count);
    }

    public ShapedWeaponTableRecipeBuilder(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemStackTemplate stack) {
        super(holderGetter, category, stack);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder define(@NotNull Character symbol, @NotNull ItemLike item) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, item);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder define(@NotNull Character symbol, @NotNull Ingredient ingredient) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, ingredient);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder define(@NotNull Character symbol, @NotNull TagKey<Item> tag) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, tag);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder group(@Nullable String group) {
        return (ShapedWeaponTableRecipeBuilder) super.group(group);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder pattern(@NotNull String pattern) {
        return (ShapedWeaponTableRecipeBuilder) super.pattern(pattern);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder showNotification(boolean show) {
        return (ShapedWeaponTableRecipeBuilder) super.showNotification(show);
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        return (ShapedWeaponTableRecipeBuilder) super.unlockedBy(name, criterion);
    }

    public @NotNull ShapedWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public @NotNull ShapedWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @SafeVarargs
    public final @NotNull ShapedWeaponTableRecipeBuilder skills(@NotNull Holder<? extends ISkill<?>>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }

    @Override
    public void save(RecipeOutput output, @NotNull ResourceKey<Recipe<?>> id) {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(key(), rows());
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.advancementBuilder.criteria.forEach(advancement::addCriterion);

        output.accept(id,
                new ShapedWeaponTableRecipe(
                        RecipeBuilder.createCraftingCommonInfo(true),
                        RecipeBuilder.createCraftingBookInfo(this.category, this.group),
                        pattern,
                        result,
                        this.level,
                        this.skills,
                        this.lava
                ),
                advancement.build(id.identifier().withPrefix("recipes/weapontable/"))
        );
    }
}
