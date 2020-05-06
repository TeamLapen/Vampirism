package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShapelessWeaponTableRecipeBuilder extends ShapelessRecipeBuilder {

    private int lava = 1;
    private ISkill[] skills;
    private int level = 1;

    public ShapelessWeaponTableRecipeBuilder(IItemProvider resultIn, int countIn) {
        super(resultIn, countIn);
    }

    public static ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(IItemProvider result, int count) {
        return new ShapelessWeaponTableRecipeBuilder(result, count);
    }

    public static ShapelessWeaponTableRecipeBuilder shapelessWeaponTable(IItemProvider result) {
        return new ShapelessWeaponTableRecipeBuilder(result, 1);
    }

    public ShapelessWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public ShapelessWeaponTableRecipeBuilder skills(ISkill... skills) {
        this.skills = skills;
        return this;
    }

    public ShapelessWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public ShapelessWeaponTableRecipeBuilder addIngredient(Tag<Item> tagIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.addIngredient(tagIn);
    }

    @Override
    public ShapelessWeaponTableRecipeBuilder addIngredient(IItemProvider itemIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.addIngredient(itemIn);
    }

    @Override
    public ShapelessWeaponTableRecipeBuilder addIngredient(IItemProvider itemIn, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.addIngredient(itemIn, quantity);
    }

    @Override
    public ShapelessWeaponTableRecipeBuilder addIngredient(Ingredient ingredientIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.addIngredient(ingredientIn);
    }

    @Override
    public ShapelessWeaponTableRecipeBuilder addIngredient(Ingredient ingredientIn, int quantity) {
        return (ShapelessWeaponTableRecipeBuilder) super.addIngredient(ingredientIn, quantity);
    }

    @Override
    public ShapelessWeaponTableRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.addCriterion(name, criterionIn);
    }

    @Override
    public ShapelessWeaponTableRecipeBuilder setGroup(String groupIn) {
        return (ShapelessWeaponTableRecipeBuilder) super.setGroup(groupIn);
    }

    @Override
    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumerIn.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath()), this.lava, this.skills != null ? this.skills : new ISkill[]{}, this.level));
    }

    private class Result extends ShapelessRecipeBuilder.Result {
        private final int lava;
        private final ISkill[] skills;
        private final int level;

        public Result(ResourceLocation idIn, Item resultIn, int countIn, String groupIn, List<Ingredient> ingredientsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, int lavaIn, ISkill[] skillsIn, int levelIn) {
            super(idIn, resultIn, countIn, groupIn, ingredientsIn, advancementBuilderIn, advancementIdIn);
            this.lava = lavaIn;
            this.skills = skillsIn;
            this.level = levelIn;
        }

        @Override
        public void serialize(JsonObject json) {
            super.serialize(json);
            json.addProperty("lava", this.lava);
            JsonArray skills = new JsonArray();
            for (ISkill skill : this.skills) {
                skills.add(skill.getRegistryName().toString());
            }
            json.add("skill", skills);
            json.addProperty("level", this.level);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return ModRecipes.shapeless_crafting_weapontable;
        }
    }
}
