package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AlchemicalCauldronRecipeBuilder {
    public static @NotNull AlchemicalCauldronRecipeBuilder cauldronRecipe(@NotNull Item item) {
        return AlchemicalCauldronRecipeBuilder.cauldronRecipe(item, 1);
    }

    public static @NotNull AlchemicalCauldronRecipeBuilder cauldronRecipe(@NotNull Item item, int count) {
        return new AlchemicalCauldronRecipeBuilder(item, count);
    }

    private final @NotNull ItemStack result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;
    private Ingredient ingredient;
    private Either<Ingredient, FluidStack> fluid;
    private ISkill<?>[] skills;
    private int reqLevel = 1;
    private int cookTime = 200;
    private float exp = 0.2f;

    public AlchemicalCauldronRecipeBuilder(@NotNull Item result, int count) {
        this.result = new ItemStack(result, count);
    }

    public void build(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "alchemical_cauldron/" + id.getPath());
        this.validate(id);
        this.advancementBuilder
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(id, this.group != null ? this.group : "", this.ingredient, this.fluid, this.result, this.skills != null ? skills : new ISkill[]{HunterSkills.BASIC_ALCHEMY.get()}, this.reqLevel, this.cookTime, this.exp, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));
    }

    public @NotNull AlchemicalCauldronRecipeBuilder cookTime(int cookTime) {
        this.cookTime = cookTime;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder experience(float exp) {
        this.exp = exp;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder group(@NotNull String group) {
        this.group = group;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder requireLevel(int level) {
        this.reqLevel = level;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withCriterion(@NotNull String name, @NotNull CriterionTriggerInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
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

    public @NotNull AlchemicalCauldronRecipeBuilder withSkills(@NotNull ISkill<?>... skills) {
        this.skills = skills;
        return this;
    }

    private void validate(@NotNull ResourceLocation id) {
        if (this.ingredient == null) {
            throw new IllegalStateException("No ingredients defined for alchemical cauldron recipe " + id + "!");
        } else if (this.fluid == null) {
            throw new IllegalStateException("No fluid defined for alchemical cauldron recipe " + id + "!");
        }
    }

    public static class Result implements FinishedRecipe {
        private final @NotNull ResourceLocation id;
        private final @NotNull String group;
        private final @NotNull Ingredient ingredient;
        private final @NotNull Either<Ingredient, FluidStack> fluid;
        private final @NotNull ItemStack result;
        private final ISkill<?>[] skills;
        private final int reqLevel;
        private final int cookTimeIn;
        private final float exp;
        private final Advancement.@NotNull Builder advancementBuilder;
        private final @NotNull ResourceLocation advancementId;

        public Result(@NotNull ResourceLocation idIn, @NotNull String groupIn, @NotNull Ingredient ingredientIn, @NotNull Either<Ingredient, FluidStack> fluidIn, @NotNull ItemStack resultIn, @NotNull ISkill<?>[] skillsIn, int reqLevelIn, int cookTimeIn, float exp, @NotNull Advancement.Builder advancementBuilderIn, @NotNull ResourceLocation advancementId) {
            this.id = idIn;
            this.group = groupIn;
            this.ingredient = ingredientIn;
            this.fluid = fluidIn;
            this.result = resultIn;
            this.skills = skillsIn;
            this.reqLevel = reqLevelIn;
            this.cookTimeIn = cookTimeIn;
            this.exp = exp;
            this.advancementBuilder = advancementBuilderIn;
            this.advancementId = advancementId;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }

        @NotNull
        @Override
        public ResourceLocation getId() {
            return id;
        }

        @NotNull
        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.ALCHEMICAL_CAULDRON.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }

            JsonObject result = new JsonObject();
            result.addProperty("item", RegUtil.id(this.result.getItem()).toString());
            if (this.result.getCount() > 1) {
                result.addProperty("count", this.result.getCount());
            }
            jsonObject.add("result", result);

            jsonObject.add("ingredient", this.ingredient.toJson());

            this.fluid.ifLeft(ingredient1 -> jsonObject.add("fluidItem", ingredient1.toJson()));
            this.fluid.ifRight(fluidStack -> {
                JsonObject fluid = new JsonObject();
                fluid.addProperty("fluid", RegUtil.id(fluidStack.getFluid()).toString());
                jsonObject.add("fluid", fluid);
            });

            JsonArray skills = new JsonArray();
            for (ISkill<?> skill : this.skills) {
                skills.add(RegUtil.id(skill).toString());
            }
            jsonObject.add("skill", skills);

            jsonObject.addProperty("cookTime", this.cookTimeIn);
            jsonObject.addProperty("experience", this.exp);
            jsonObject.addProperty("reqLevel", this.reqLevel);
        }
    }
}
