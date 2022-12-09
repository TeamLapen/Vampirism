package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.advancements.critereon.SkillUnlockedCriterionTrigger;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.ShapedRecipeBuilderAccessor;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShapedWeaponTableRecipeBuilder extends ShapedRecipeBuilder {

    public static @NotNull ShapedWeaponTableRecipeBuilder shapedWeaponTable(@NotNull RecipeCategory category, @NotNull ItemLike item) {
        return new ShapedWeaponTableRecipeBuilder(category, item, 1, null);
    }

    public static @NotNull ShapedWeaponTableRecipeBuilder shapedWeaponTable(@NotNull RecipeCategory category, @NotNull ItemLike item, int count) {
        return new ShapedWeaponTableRecipeBuilder(category, item, count, null);
    }

    public static @NotNull ShapedWeaponTableRecipeBuilder shapedWeaponTable(@NotNull RecipeCategory category, @NotNull ItemLike item, int count, @NotNull JsonObject nbt) {
        return new ShapedWeaponTableRecipeBuilder(category, item, count, nbt);
    }

    private final @Nullable JsonObject extraNbt;
    private int lava = 1;
    private ISkill<?>[] skills;
    private int level = 1;

    public ShapedWeaponTableRecipeBuilder(@NotNull RecipeCategory category, @NotNull ItemLike item, int count, @Nullable JsonObject extraNbt) {
        super(category, item, count);
        this.extraNbt = extraNbt;
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

    public @NotNull ShapedWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public @NotNull ShapedWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "weapontable/" + id.getPath());
        this.advancement.addCriterion("has_skill", SkillUnlockedCriterionTrigger.builder(this.skills != null && this.skills.length >= 1 ? this.skills[0] : HunterSkills.WEAPON_TABLE.get()));
        this.ensureValid(id);
        this.advancement
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group, determineBookCategory(((ShapedRecipeBuilderAccessor)this).getRecipeCategory()), this.rows, this.key, this.advancement, id.withPath("recipes/" + ((ShapedRecipeBuilderAccessor)this).getRecipeCategory().getFolderName() + "/" + id.getPath()), this.lava, this.skills != null ? this.skills : new ISkill[]{}, this.level, this.extraNbt));
    }

    @NotNull
    @Override
    public ShapedWeaponTableRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance criterionIn) {
        return (ShapedWeaponTableRecipeBuilder) super.unlockedBy(name, criterionIn);
    }

    public @NotNull ShapedWeaponTableRecipeBuilder skills(@NotNull ISkill<?>... skills) {
        this.skills = skills;
        return this;
    }

    private static class Result extends ShapedRecipeBuilder.Result {
        private final int lava;
        private final ISkill<?>[] skills;
        private final int level;
        private final @Nullable JsonObject extraNbt;

        public Result(@NotNull ResourceLocation id, @NotNull Item item, int count, @NotNull String group, CraftingBookCategory category, @NotNull List<String> pattern, @NotNull Map<Character, Ingredient> ingredients, @NotNull Advancement.Builder advancementBuilder, @NotNull ResourceLocation advancementId, int lava, @NotNull ISkill<?>[] skills, int level, @Nullable JsonObject extraNbt) {
            super(id, item, count, group, category, pattern, ingredients, advancementBuilder, advancementId);
            this.lava = lava;
            this.skills = skills;
            this.level = level;
            this.extraNbt = extraNbt;
        }

        @NotNull
        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.SHAPED_CRAFTING_WEAPONTABLE.get();
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject jsonObject) {
            super.serializeRecipeData(jsonObject);
            jsonObject.addProperty("lava", this.lava);
            JsonArray skills = new JsonArray();
            for (ISkill<?> skill : this.skills) {
                skills.add(RegUtil.id(skill).toString());
            }
            jsonObject.add("skill", skills);
            jsonObject.addProperty("level", this.level);
            if (extraNbt != null) {
                jsonObject.get("result").getAsJsonObject().add("nbt", extraNbt);
            }
        }
    }
}
