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
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.*;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class AlchemyTableRecipeBuilder {

    public static AlchemyTableRecipeBuilder builder(ItemStack stack) {
        return new AlchemyTableRecipeBuilder(stack);
    }
    public static AlchemyTableRecipeBuilder builder(IOil oilStack) {
        return new AlchemyTableRecipeBuilder(OilUtils.createOilItem(oilStack));
    }

    public static AlchemyTableRecipeBuilder builder(Supplier<? extends IOil> oilStack) {
        return builder(oilStack.get());
    }

    private final ItemStack result;
    private final IOil resultOil;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;
    private Ingredient ingredient;
    private IOil ingredientOil = ModOils.EMPTY.get();
    private Ingredient input;
    private ISkill[] skills;

    public AlchemyTableRecipeBuilder(ItemStack result) {
        this(result, OilUtils.getOil(result));
    }

    public AlchemyTableRecipeBuilder(ItemStack result, IOil resultOil) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(resultOil);
        this.result = result;
        this.resultOil = resultOil;
    }

    public AlchemyTableRecipeBuilder group(String group){
        this.group = group;
        return this;
    }

    public AlchemyTableRecipeBuilder ingredient(Ingredient ingredient){
        this.ingredient = ingredient;
        return this;
    }

    public AlchemyTableRecipeBuilder oilIngredient(IOil oil) {
        this.ingredient = new NBTIngredient(ModItems.OIL_BOTTLE.get().withOil(oil));
        return this;
    }

    public AlchemyTableRecipeBuilder plantOilIngredient() {
        return ingredient(new NBTIngredient(ModItems.OIL_BOTTLE.get().withOil(ModOils.PLANT.get()))).withCriterion("has_bottles", has(ModItems.OIL_BOTTLE.get()));
    }
    public AlchemyTableRecipeBuilder bloodOilIngredient() {
        return ingredient(new NBTIngredient(ModItems.OIL_BOTTLE.get().withOil(ModOils.VAMPIRE_BLOOD.get()))).withCriterion("has_bottles", has(ModItems.OIL_BOTTLE.get()));
    }

    public AlchemyTableRecipeBuilder input(Ingredient input){
        this.input = input;
        return this;
    }

    public AlchemyTableRecipeBuilder withCriterion(String name, ICriterionInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public AlchemyTableRecipeBuilder withSkills(ISkill... skills) {
        this.skills = skills;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id){
        id = new ResourceLocation(id.getNamespace(), "alchemy_table/" + id.getPath());
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.group != null ? this.group : "", this.ingredient, this.ingredientOil, this.input, this.result, this.resultOil, this.skills != null ? this.skills : new ISkill[0], new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath()), this.advancementBuilder));
    }

    private void validate(ResourceLocation id) {
        if (this.ingredient == null) {
            throw new IllegalStateException("No ingredients defined for alchemical table recipe " + id + "!");
        } else if (this.input == null) {
            throw new IllegalStateException("No input defined for alchemical table recipe " + id + "!");
        }
    }

    protected static InventoryChangeTrigger.Instance has(IItemProvider p_200403_0_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_200403_0_).build());
    }

    protected static InventoryChangeTrigger.Instance has(ITag<Item> p_200409_0_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_200409_0_).build());
    }

    protected static InventoryChangeTrigger.Instance inventoryTrigger(ItemPredicate... p_200405_0_) {
        return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, p_200405_0_);
    }

    private static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final IOil resultOil;
        private final Advancement.Builder advancementBuilder;
        private final String group;
        private final Ingredient ingredient;
        private final IOil ingredientOil;
        private final Ingredient input;
        private final ISkill[] skills;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, String group, Ingredient ingredient, IOil ingredientOil, Ingredient input, ItemStack result, IOil resultOil, ISkill[] skills, ResourceLocation advancementId, Advancement.Builder advancementBuilder) {
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

        @Nonnull
        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getType() {
            return ModRecipes.ALCHEMICAL_TABLE.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public void serializeRecipeData(@Nonnull JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("result", item(this.result));
            json.add("ingredient", this.ingredient.toJson());
            json.add("input", this.input.toJson());

            JsonArray skills = new JsonArray();
            for (ISkill skill : this.skills) {
                skills.add(skill.getRegistryName().toString());
            }
            json.add("skill", skills);
        }

        private JsonObject item(ItemStack stack) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", stack.getItem().getRegistryName().toString());
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
