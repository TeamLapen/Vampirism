package de.teamlapen.vampirism.data.recipebuilder;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.items.component.OilContent;
import de.teamlapen.vampirism.recipes.AlchemyTableRecipe;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AlchemyTableRecipeBuilder implements RecipeBuilder {

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull ItemStack stack) {
        return new AlchemyTableRecipeBuilder(stack);
    }

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull Holder<IOil> oilStack) {
        return new AlchemyTableRecipeBuilder(OilContent.createItemStack(ModItems.OIL_BOTTLE.get(), oilStack));
    }

    protected final @NotNull ItemStack result;
    protected String group;
    protected Ingredient ingredient;
    protected final @NotNull IOil ingredientOil = ModOils.EMPTY.get();
    protected Ingredient input;
    protected final List<ISkill<?>> skills = new LinkedList<>();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlchemyTableRecipeBuilder(@NotNull ItemStack result) {
        this.result = result;
    }

    public @NotNull AlchemyTableRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder ingredient(@NotNull Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder oilIngredient(@NotNull Holder<IOil> oil) {
        this.ingredient = DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(oil), ModItems.OIL_BOTTLE.get());
        return this;
    }

    public AlchemyTableRecipeBuilder plantOilIngredient() {
        return ingredient(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.PLANT), ModItems.OIL_BOTTLE.get())).unlockedBy("has_bottles", has(ModItems.OIL_BOTTLE.get()));
    }

    public AlchemyTableRecipeBuilder bloodOilIngredient() {
        return ingredient(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_BLOOD), ModItems.OIL_BOTTLE.get())).unlockedBy("has_bottles", has(ModItems.OIL_BOTTLE.get()));
    }

    public @NotNull AlchemyTableRecipeBuilder input(@NotNull Ingredient input) {
        this.input = input;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder withSkills(@NotNull ISkill<?>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation resourceLocation) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        var recipe = new AlchemyTableRecipe(Objects.requireNonNullElse(this.group,""), this.ingredient, this.input, this.result, this.skills);
        recipeOutput.accept(resourceLocation, recipe, advancement.build(resourceLocation.withPrefix("recipes/alchemy_table/")));
    }

    @Override
    public @NotNull AlchemyTableRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result.getItem();
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike p_125978_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_125978_));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> p_206407_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... p_299111_) {
        return inventoryTrigger(Arrays.stream(p_299111_).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... p_126012_) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(p_126012_)));
    }
}
