package de.teamlapen.vampirism.data.recipebuilder;

import de.teamlapen.vampirism.advancements.critereon.SkillUnlockedCriterionTrigger;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShapelessWeaponTableRecipeBuilder extends ShapelessRecipeBuilder {

    public static @NotNull ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(@NotNull RecipeCategory category, @NotNull ItemLike result, int count) {
        return new ShapelessWeaponTableRecipeBuilder(category, result, count);
    }

    public static @NotNull ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(@NotNull RecipeCategory category, @NotNull ItemLike result) {
        return new ShapelessWeaponTableRecipeBuilder(category, result, 1);
    }

    private int lava = 1;
    private final List<Holder<ISkill<?>>> skills = new LinkedList<>();
    private int level = 1;

    public ShapelessWeaponTableRecipeBuilder(@NotNull RecipeCategory category, @NotNull ItemLike resultIn, int countIn) {
        super(category, resultIn, countIn);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder group(@Nullable String groupIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.group(groupIn);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull ItemLike itemIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(itemIn);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull ItemLike itemIn, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(itemIn, quantity);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull Ingredient ingredientIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(ingredientIn);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull Ingredient ingredientIn, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(ingredientIn, quantity);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull TagKey<Item> tagIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(tagIn);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        this.ensureValid(id);
        Advancement.Builder advancement$builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        advancement$builder.addCriterion("has_skill", ModAdvancements.TRIGGER_SKILL_UNLOCKED.get().createCriterion(new SkillUnlockedCriterionTrigger.TriggerInstance(Optional.empty(), HunterSkills.WEAPON_TABLE.get())));
        this.skills.forEach(skill -> {
            advancement$builder.addCriterion("has_skill_" + skill.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElseThrow().replace(":", "_"), ModAdvancements.TRIGGER_SKILL_UNLOCKED.get().createCriterion(new SkillUnlockedCriterionTrigger.TriggerInstance(Optional.empty(), skill.value())));
        });
        this.criteria.forEach(advancement$builder::addCriterion);
        ShapelessWeaponTableRecipe shapelessrecipe = new ShapelessWeaponTableRecipe(
                Objects.requireNonNullElse(this.group, ""),
                RecipeBuilder.determineBookCategory(this.category),
                this.ingredients,
                new ItemStack(this.result, this.count),
                level,
                lava,
                skills
        );
        output.accept(id, shapelessrecipe, advancement$builder.build(id.withPrefix("recipes/weapontable/")));
    }

    public @NotNull ShapelessWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public @NotNull ShapelessWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public @NotNull ShapelessWeaponTableRecipeBuilder unlockedBy(String p_176781_, Criterion<?> p_300897_) {
        return (ShapelessWeaponTableRecipeBuilder) super.unlockedBy(p_176781_, p_300897_);
    }

    @SafeVarargs
    public final @NotNull ShapelessWeaponTableRecipeBuilder skills(@NotNull Holder<ISkill<?>>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }
}
