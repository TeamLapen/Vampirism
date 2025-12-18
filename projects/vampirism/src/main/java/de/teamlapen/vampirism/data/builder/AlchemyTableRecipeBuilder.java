package de.teamlapen.vampirism.data.builder;

import de.teamlapen.factions.api.factions.skills.ISkill;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModOils;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import de.teamlapen.vampirism.common.world.items.recipes.AlchemyTableRecipe;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AlchemyTableRecipeBuilder implements RecipeBuilder {

    public static @NotNull AlchemyTableRecipeBuilder builder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull ItemStack stack) {
        return new AlchemyTableRecipeBuilder(itemLookup, stack);
    }

    public static @NotNull AlchemyTableRecipeBuilder builder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull Holder<IOil> oilStack) {
        return new AlchemyTableRecipeBuilder(itemLookup, OilContent.createItemStack(ModItems.OIL_BOTTLE.get(), oilStack));
    }

    protected HolderLookup.RegistryLookup<Item> itemLookup;
    protected final @NotNull ItemStack result;
    protected String group;
    protected Ingredient ingredient;
    protected final @NotNull IOil ingredientOil = ModOils.EMPTY.get();
    protected Ingredient input;
    protected final List<ISkill<?>> skills = new LinkedList<>();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlchemyTableRecipeBuilder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull ItemStack result) {
        this.itemLookup = itemLookup;
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
        return ingredient(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.PLANT), ModItems.OIL_BOTTLE.get())).unlockedBy("has_bottles", has(this.itemLookup, ModItems.OIL_BOTTLE.get()));
    }

    public AlchemyTableRecipeBuilder bloodOilIngredient() {
        return ingredient(DataComponentIngredient.of(false, ModDataComponents.OIL, new OilContent(ModOils.VAMPIRE_BLOOD), ModItems.OIL_BOTTLE.get())).unlockedBy("has_bottles", has(this.itemLookup, ModItems.OIL_BOTTLE.get()));
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
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceLocation) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        var recipe = new AlchemyTableRecipe(Objects.requireNonNullElse(this.group, ""), this.ingredient, this.input, this.result, this.skills);
        recipeOutput.accept(resourceLocation, recipe, advancement.build(resourceLocation.location().withPrefix("recipes/alchemy_table/")));
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

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(HolderGetter<Item> holderGetter,  ItemLike p_125978_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(holderGetter, p_125978_));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(HolderGetter<Item> holderGetter, TagKey<Item> p_206407_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(holderGetter, p_206407_));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... p_299111_) {
        return inventoryTrigger(Arrays.stream(p_299111_).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... p_126012_) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(p_126012_)));
    }
}
