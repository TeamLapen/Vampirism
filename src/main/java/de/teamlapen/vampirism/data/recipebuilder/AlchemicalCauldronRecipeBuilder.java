package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlchemicalCauldronRecipeBuilder {
    public static AlchemicalCauldronRecipeBuilder cauldronRecipe(Item item) {
        return AlchemicalCauldronRecipeBuilder.cauldronRecipe(item, 1);
    }

    public static AlchemicalCauldronRecipeBuilder cauldronRecipe(Item item, int count) {
        return new AlchemicalCauldronRecipeBuilder(item, count);
    }
    private final ItemStack result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;
    private Ingredient ingredient;
    private Either<Ingredient, FluidStack> fluid;
    private ISkill[] skills;
    private int reqLevel = 1;
    private int cookTime = 200;
    private float exp = 0.2f;

    public AlchemicalCauldronRecipeBuilder(Item result, int count) {
        this.result = new ItemStack(result, count);
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "alchemical_cauldron/" + id.getPath());
        this.validate(id);
        this.advancementBuilder
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.group != null ? this.group : "", this.ingredient, this.fluid, this.result, this.skills != null ? skills : new ISkill[]{HunterSkills.BASIC_ALCHEMY.get()}, this.reqLevel, this.cookTime, this.exp, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
    }

    public AlchemicalCauldronRecipeBuilder cookTime(int cookTime) {
        this.cookTime = cookTime;
        return this;
    }

    public AlchemicalCauldronRecipeBuilder experience(float exp) {
        this.exp = exp;
        return this;
    }

    public AlchemicalCauldronRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public AlchemicalCauldronRecipeBuilder requireLevel(int level) {
        this.reqLevel = level;
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withCriterion(String name, ICriterionInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withFluid(ITag<Item> tag) {
        this.fluid = Either.left(Ingredient.of(tag));
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withFluid(FluidStack fluid) {
        this.fluid = Either.right(fluid);
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withFluid(IItemProvider... item) {
        this.fluid = Either.left(Ingredient.of(item));
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withFluid(ItemStack... stacks) {
        this.fluid = Either.left(Ingredient.of(stacks));
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withIngredient(IItemProvider... items) {
        this.ingredient = Ingredient.of(items);
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withIngredient(ItemStack... stacks) {
        this.ingredient = Ingredient.of(stacks);
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withIngredient(ITag<Item> tag) {
        this.ingredient = Ingredient.of(tag);
        return this;
    }

    public AlchemicalCauldronRecipeBuilder withSkills(ISkill... skills) {
        this.skills = skills;
        return this;
    }

    private void validate(ResourceLocation id) {
        if (this.ingredient == null) {
            throw new IllegalStateException("No ingredients defined for alchemical cauldron recipe " + id + "!");
        } else if (this.fluid == null) {
            throw new IllegalStateException("No fluid defined for alchemical cauldron recipe " + id + "!");
        }
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final Either<Ingredient, FluidStack> fluid;
        private final ItemStack result;
        private final ISkill[] skills;
        private final int reqLevel;
        private final int cookTimeIn;
        private final float exp;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;

        public Result(@Nonnull ResourceLocation idIn, @Nonnull String groupIn, @Nonnull Ingredient ingredientIn, @Nonnull Either<Ingredient, FluidStack> fluidIn, @Nonnull ItemStack resultIn, @Nonnull ISkill[] skillsIn, int reqLevelIn, int cookTimeIn, float exp, Advancement.Builder advancementBuilderIn, ResourceLocation advancementId) {
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

        @Nonnull
        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getType() {
            return ModRecipes.ALCHEMICAL_CAULDRON.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }

            JsonObject result = new JsonObject();
            result.addProperty("item", this.result.getItem().getRegistryName().toString());
            if (this.result.getCount() > 1) {
                result.addProperty("count", this.result.getCount());
            }
            jsonObject.add("result", result);

            jsonObject.add("ingredient", this.ingredient.toJson());

            this.fluid.ifLeft(ingredient1 -> jsonObject.add("fluidItem", ingredient1.toJson()));
            this.fluid.ifRight(fluidStack -> {
                JsonObject fluid = new JsonObject();
                fluid.addProperty("fluid", fluidStack.getFluid().getRegistryName().toString());
                jsonObject.add("fluid", fluid);
            });

            JsonArray skills = new JsonArray();
            for (ISkill skill : this.skills) {
                skills.add(skill.getRegistryName().toString());
            }
            jsonObject.add("skill", skills);

            jsonObject.addProperty("cookTime", this.cookTimeIn);
            jsonObject.addProperty("experience", this.exp);
            jsonObject.addProperty("reqLevel", this.reqLevel);
        }
    }
}
