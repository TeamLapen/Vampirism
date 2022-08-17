package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.NBTIngredient;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AlchemyTableRecipeBuilder {

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull ItemStack stack) {
        return new AlchemyTableRecipeBuilder(stack);
    }

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull IOil oilStack) {
        return new AlchemyTableRecipeBuilder(OilUtils.createOilItem(oilStack));
    }

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull Supplier<? extends IOil> oilStack) {
        return builder(oilStack.get());
    }

    private final @NotNull ItemStack result;
    private final @NotNull IOil resultOil;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;
    private Ingredient ingredient;
    private final @NotNull IOil ingredientOil = ModOils.EMPTY.get();
    private Ingredient input;
    private ISkill[] skills;

    public AlchemyTableRecipeBuilder(@NotNull ItemStack result) {
        this(result, OilUtils.getOil(result));
    }

    public AlchemyTableRecipeBuilder(@NotNull ItemStack result, @NotNull IOil resultOil) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(resultOil);
        this.result = result;
        this.resultOil = resultOil;
    }

    public @NotNull AlchemyTableRecipeBuilder group(@NotNull String group) {
        this.group = group;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder ingredient(@NotNull Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder oilIngredient(@NotNull IOil oil) {
        this.ingredient = new NBTIngredient(ModItems.OIL_BOTTLE.get().withOil(oil));
        return this;
    }

    public AlchemyTableRecipeBuilder plantOilIngredient() {
        return ingredient(new NBTIngredient(ModItems.OIL_BOTTLE.get().withOil(ModOils.PLANT.get()))).withCriterion("has_bottles", has(ModItems.OIL_BOTTLE.get()));
    }

    public AlchemyTableRecipeBuilder bloodOilIngredient() {
        return ingredient(new NBTIngredient(ModItems.OIL_BOTTLE.get().withOil(ModOils.VAMPIRE_BLOOD.get()))).withCriterion("has_bottles", has(ModItems.OIL_BOTTLE.get()));
    }

    public @NotNull AlchemyTableRecipeBuilder input(@NotNull Ingredient input) {
        this.input = input;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder withCriterion(@NotNull String name, @NotNull CriterionTriggerInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder withSkills(@NotNull ISkill... skills) {
        this.skills = skills;
        return this;
    }

    public void build(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "alchemy_table/" + id.getPath());
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(id, this.group != null ? this.group : "", this.ingredient, this.ingredientOil, this.input, this.result, this.resultOil, this.skills != null ? this.skills : new ISkill[0], new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath()), this.advancementBuilder));
    }

    private void validate(@NotNull ResourceLocation id) {
        if (this.ingredient == null) {
            throw new IllegalStateException("No ingredients defined for alchemical table recipe " + id + "!");
        } else if (this.input == null) {
            throw new IllegalStateException("No input defined for alchemical table recipe " + id + "!");
        }
    }

    protected static InventoryChangeTrigger.@NotNull TriggerInstance has(@NotNull ItemLike p_200403_0_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_200403_0_).build());
    }

    protected static InventoryChangeTrigger.@NotNull TriggerInstance has(@NotNull TagKey<Item> p_200409_0_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_200409_0_).build());
    }

    protected static InventoryChangeTrigger.@NotNull TriggerInstance inventoryTrigger(@NotNull ItemPredicate @NotNull ... p_200405_0_) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, p_200405_0_);
    }

    private static class Result implements FinishedRecipe {
        private final @NotNull ResourceLocation id;
        private final @NotNull ItemStack result;
        private final @NotNull IOil resultOil;
        private final Advancement.@NotNull Builder advancementBuilder;
        private final @NotNull String group;
        private final @NotNull Ingredient ingredient;
        private final @NotNull IOil ingredientOil;
        private final @NotNull Ingredient input;
        private final ISkill[] skills;
        private final @NotNull ResourceLocation advancementId;

        public Result(@NotNull ResourceLocation id, @NotNull String group, @NotNull Ingredient ingredient, @NotNull IOil ingredientOil, @NotNull Ingredient input, @NotNull ItemStack result, @NotNull IOil resultOil, @NotNull ISkill[] skills, @NotNull ResourceLocation advancementId, @NotNull Advancement.Builder advancementBuilder) {
            this.id = id;
            this.result = result;
            this.resultOil = resultOil;
            this.group = group;
            this.ingredient = ingredient;
            this.ingredientOil = ingredientOil;
            this.input = input;
            this.skills = skills;
            this.advancementId = advancementId;
            this.advancementBuilder = advancementBuilder;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }

        @NotNull
        @Override
        public ResourceLocation getId() {
            return id;
        }

        @NotNull
        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.ALCHEMICAL_TABLE.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("result", item(this.result));
            json.add("ingredient", this.ingredient.toJson());
            json.add("input", this.input.toJson());

            JsonArray skills = new JsonArray();
            for (ISkill skill : this.skills) {
                skills.add(RegUtil.id(skill).toString());
            }
            json.add("skill", skills);
        }

        private @NotNull JsonObject item(@NotNull ItemStack stack) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", RegUtil.id(stack.getItem()).toString());
            if (stack.getCount() > 1) {
                obj.addProperty("count", stack.getCount());
            }
            if (stack.hasTag()) {
                obj.add("nbt", new JsonParser().parse(stack.getTag().toString()).getAsJsonObject());
            }
            return obj;
        }
    }
}
