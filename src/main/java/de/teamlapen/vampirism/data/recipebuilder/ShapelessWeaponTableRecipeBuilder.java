package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.advancements.SkillUnlockedTrigger;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ShapelessWeaponTableRecipeBuilder extends ShapelessRecipeBuilder {

    public static @NotNull ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(@NotNull ItemLike result, int count) {
        return new ShapelessWeaponTableRecipeBuilder(result, count);
    }

    public static @NotNull ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(@NotNull ItemLike result) {
        return new ShapelessWeaponTableRecipeBuilder(result, 1);
    }

    private int lava = 1;
    private ISkill<?>[] skills;
    private int level = 1;

    public ShapelessWeaponTableRecipeBuilder(@NotNull ItemLike resultIn, int countIn) {
        super(resultIn, countIn);
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
    public void save(@NotNull Consumer<FinishedRecipe> consumerIn,@NotNull ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "weapontable/" + id.getPath());
        this.advancement.addCriterion("has_skill", SkillUnlockedTrigger.builder(this.skills != null && this.skills.length >= 1 ? this.skills[0] : HunterSkills.WEAPON_TABLE.get()));
        this.ensureValid(id);
        this.advancement
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);
        consumerIn.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath()), this.lava, this.skills != null ? this.skills : new ISkill[]{}, this.level));
    }

    public @NotNull ShapelessWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public @NotNull ShapelessWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @NotNull
    @Override
    public ShapelessWeaponTableRecipeBuilder unlockedBy(@NotNull String name,@NotNull CriterionTriggerInstance criterionIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.unlockedBy(name, criterionIn);
    }

    public @NotNull ShapelessWeaponTableRecipeBuilder skills(@NotNull ISkill<?>... skills) {
        this.skills = skills;
        return this;
    }

    private static class Result extends ShapelessRecipeBuilder.Result {
        private final int lava;
        private final ISkill<?>[] skills;
        private final int level;

        public Result(@NotNull ResourceLocation idIn,@NotNull Item resultIn, int countIn,@NotNull String groupIn,@NotNull List<Ingredient> ingredientsIn,@NotNull Advancement.Builder advancementBuilderIn,@NotNull ResourceLocation advancementIdIn, int lavaIn,@NotNull ISkill<?>[] skillsIn, int levelIn) {
            super(idIn, resultIn, countIn, groupIn, ingredientsIn, advancementBuilderIn, advancementIdIn);
            this.lava = lavaIn;
            this.skills = skillsIn;
            this.level = levelIn;
        }

        @NotNull
        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.SHAPELESS_CRAFTING_WEAPONTABLE.get();
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            super.serializeRecipeData(json);
            json.addProperty("lava", this.lava);
            JsonArray skills = new JsonArray();
            for (ISkill<?> skill : this.skills) {
                skills.add(RegUtil.id(skill) .toString());
            }
            json.add("skill", skills);
            json.addProperty("level", this.level);
        }
    }
}
