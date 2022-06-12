package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.advancements.SkillUnlockedTrigger;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ShapelessWeaponTableRecipeBuilder extends ShapelessRecipeBuilder {

    public static ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(ItemLike result, int count) {
        return new ShapelessWeaponTableRecipeBuilder(result, count);
    }

    public static ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(ItemLike result) {
        return new ShapelessWeaponTableRecipeBuilder(result, 1);
    }

    private int lava = 1;
    private ISkill<?>[] skills;
    private int level = 1;

    public ShapelessWeaponTableRecipeBuilder(ItemLike resultIn, int countIn) {
        super(resultIn, countIn);
    }

    @Nonnull
    @Override
    public ShapelessWeaponTableRecipeBuilder group(@Nullable String groupIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.group(groupIn);
    }

    @Nonnull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(ItemLike itemIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(itemIn);
    }

    @Nonnull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(ItemLike itemIn, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(itemIn, quantity);
    }

    @Nonnull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(Ingredient ingredientIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(ingredientIn);
    }

    @Nonnull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(Ingredient ingredientIn, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(ingredientIn, quantity);
    }

    @Nonnull
    @Override
    public ShapelessWeaponTableRecipeBuilder requires(TagKey<Item> tagIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.requires(tagIn);
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
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

    public ShapelessWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public ShapelessWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Nonnull
    @Override
    public ShapelessWeaponTableRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.unlockedBy(name, criterionIn);
    }

    public ShapelessWeaponTableRecipeBuilder skills(ISkill<?>... skills) {
        this.skills = skills;
        return this;
    }

    private static class Result extends ShapelessRecipeBuilder.Result {
        private final int lava;
        private final ISkill<?>[] skills;
        private final int level;

        public Result(ResourceLocation idIn, Item resultIn, int countIn, String groupIn, List<Ingredient> ingredientsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, int lavaIn, ISkill<?>[] skillsIn, int levelIn) {
            super(idIn, resultIn, countIn, groupIn, ingredientsIn, advancementBuilderIn, advancementIdIn);
            this.lava = lavaIn;
            this.skills = skillsIn;
            this.level = levelIn;
        }

        @Nonnull
        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.SHAPELESS_CRAFTING_WEAPONTABLE.get();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);
            json.addProperty("lava", this.lava);
            JsonArray skills = new JsonArray();
            for (ISkill<?> skill : this.skills) {
                skills.add(skill.getRegistryName().toString());
            }
            json.add("skill", skills);
            json.addProperty("level", this.level);
        }
    }
}
