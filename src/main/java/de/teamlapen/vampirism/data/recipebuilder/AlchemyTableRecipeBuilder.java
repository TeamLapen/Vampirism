package de.teamlapen.vampirism.data.recipebuilder;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.recipes.AlchemyTableRecipe;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class AlchemyTableRecipeBuilder implements RecipeBuilder {

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull ItemStack stack) {
        return new AlchemyTableRecipeBuilder(stack);
    }

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull IOil oilStack) {
        return new AlchemyTableRecipeBuilder(OilUtils.createOilItem(oilStack));
    }

    public static @NotNull AlchemyTableRecipeBuilder builder(@NotNull Supplier<? extends IOil> oilStack) {
        return builder(oilStack.get());
    }

    protected final @NotNull ItemStack result;
    protected final @NotNull IOil resultOil;
    protected String group;
    protected Ingredient ingredient;
    protected final @NotNull IOil ingredientOil = ModOils.EMPTY.get();
    protected Ingredient input;
    protected final List<ISkill<?>> skills = new LinkedList<>();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlchemyTableRecipeBuilder(@NotNull ItemStack result) {
        this(result, OilUtils.getOil(result));
    }

    public AlchemyTableRecipeBuilder(@NotNull ItemStack result, @NotNull IOil resultOil) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(resultOil);
        this.result = result;
        this.resultOil = resultOil;
    }

    public @NotNull AlchemyTableRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder ingredient(@NotNull Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public @NotNull AlchemyTableRecipeBuilder oilIngredient(@NotNull IOil oil) {
        this.ingredient = NBTIngredient.of(true, ModItems.OIL_BOTTLE.get().withOil(oil));
        return this;
    }

    public AlchemyTableRecipeBuilder plantOilIngredient() {
        return ingredient(NBTIngredient.of(true, ModItems.OIL_BOTTLE.get().withOil(ModOils.PLANT.get()))).unlockedBy("has_bottles", has(ModItems.OIL_BOTTLE.get()));
    }

    public AlchemyTableRecipeBuilder bloodOilIngredient() {
        return ingredient(NBTIngredient.of(true, ModItems.OIL_BOTTLE.get().withOil(ModOils.VAMPIRE_BLOOD.get()))).unlockedBy("has_bottles", has(ModItems.OIL_BOTTLE.get()));
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
