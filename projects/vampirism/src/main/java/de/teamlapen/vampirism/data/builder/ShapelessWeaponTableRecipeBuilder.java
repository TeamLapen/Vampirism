package de.teamlapen.vampirism.data.builder;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.advancements.criterion.SkillUnlockedCriterionTrigger;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.recipes.ShapelessWeaponTableRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShapelessWeaponTableRecipeBuilder extends ShapelessRecipeBuilder {

    public static @NotNull ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemLike result, int count) {
        return new ShapelessWeaponTableRecipeBuilder(holderGetter, category, new ItemStackTemplate(result.asItem(), count));
    }

    public static @NotNull ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemLike result) {
        return new ShapelessWeaponTableRecipeBuilder(holderGetter, category, new ItemStackTemplate(result.asItem()));
    }

    private int lava = 1;
    private final List<Holder<ISkill<?>>> skills = new LinkedList<>();
    private int level = 1;

    public ShapelessWeaponTableRecipeBuilder(HolderGetter<Item> holderGetter, @NotNull RecipeCategory category, @NotNull ItemStackTemplate result) {
        super(holderGetter, category, result);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder group(@Nullable String group) {
        return (ShapelessWeaponTableRecipeBuilder) super.group(group);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull ItemLike item) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(item);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull ItemLike item, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(item, quantity);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull Ingredient ingredient) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(ingredient);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull Ingredient ingredient, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(ingredient, quantity);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(@NotNull TagKey<Item> tag) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(tag);
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return (ShapelessWeaponTableRecipeBuilder) super.unlockedBy(name, criterion);
    }

    public @NotNull ShapelessWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public @NotNull ShapelessWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @SafeVarargs
    public final @NotNull ShapelessWeaponTableRecipeBuilder skills(@NotNull Holder<ISkill<?>>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        this.ensureValid(id);
        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        advancementBuilder.addCriterion("has_skill", FactionAdvancements.TRIGGER_SKILL_UNLOCKED.get().createCriterion(
                new SkillUnlockedCriterionTrigger.TriggerInstance(Optional.empty(), HunterSkills.WEAPON_TABLE.get())));
        this.skills.forEach(skill -> advancementBuilder.addCriterion(
                "has_skill_" + skill.unwrapKey().map(ResourceKey::identifier).map(Identifier::toString).orElseThrow().replace(":", "_"),
                FactionAdvancements.TRIGGER_SKILL_UNLOCKED.get().createCriterion(
                        new SkillUnlockedCriterionTrigger.TriggerInstance(Optional.empty(), skill.value()))));
        this.advancementBuilder.criteria.forEach(advancementBuilder::addCriterion);

        output.accept(id,
                new ShapelessWeaponTableRecipe(
                        RecipeBuilder.createCraftingCommonInfo(true),
                        RecipeBuilder.createCraftingBookInfo(this.category, this.group),
                        NonNullList.copyOf(this.ingredients),
                        this.result,
                        this.level,
                        this.lava,
                        this.skills
                ),
                advancementBuilder.build(id.identifier().withPrefix("recipes/weapontable/"))
        );
    }

    private void ensureValid(ResourceKey<Recipe<?>> id) {
        if (this.advancementBuilder.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id.identifier());
        }
    }
}
