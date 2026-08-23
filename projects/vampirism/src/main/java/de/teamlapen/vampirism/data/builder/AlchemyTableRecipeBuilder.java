package de.teamlapen.vampirism.data.builder;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModOils;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import de.teamlapen.vampirism.common.world.items.recipes.AlchemyTableRecipe;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class AlchemyTableRecipeBuilder implements RecipeBuilder {

    public static @NotNull AlchemyTableRecipeBuilder builder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull ItemStackTemplate stack) {
        return new AlchemyTableRecipeBuilder(itemLookup, stack);
    }

    public static @NotNull AlchemyTableRecipeBuilder builder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull Holder<IOil> oilStack) {
        return new AlchemyTableRecipeBuilder(itemLookup, OilContent.createTemplate(ModItems.OIL_BOTTLE.get(), oilStack));
    }

    protected HolderLookup.RegistryLookup<Item> itemLookup;
    protected final @NotNull ItemStackTemplate result;
    protected String group;
    protected Ingredient ingredient;
    protected final @NotNull IOil ingredientOil = ModOils.EMPTY.get();
    protected Ingredient input;
    protected final List<ISkill<?>> skills = new LinkedList<>();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlchemyTableRecipeBuilder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull ItemStackTemplate result) {
        this.itemLookup = itemLookup;
        this.result = result;
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        @Nullable
        var oilContent = this.result.components().getPatch(ModDataComponents.OIL.get());
        if (oilContent != null && oilContent.isPresent()) {
            return ResourceKey.create(Registries.RECIPE, oilContent.get().oil().unwrapKey().orElseThrow().identifier().withSuffix("_oil"));
        }
        return RecipeBuilder.getDefaultRecipeId(this.result);
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
        return unlockedBy("has_bottles", has(this.itemLookup, ModItems.OIL_BOTTLE.get()));
    }

    public AlchemyTableRecipeBuilder plantOilIngredient() {
        return oilIngredient(ModOils.PLANT);
    }

    public AlchemyTableRecipeBuilder bloodOilIngredient() {
        return oilIngredient(ModOils.VAMPIRE_BLOOD);
    }

    public AlchemyTableRecipeBuilder sovereignOilIngredient() {
        return oilIngredient(ModOils.SOVEREIGN_BLOOD);
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
        Recipe.CommonInfo commonInfo = RecipeBuilder.createCraftingCommonInfo(true);
        var recipe = new AlchemyTableRecipe(commonInfo, Objects.requireNonNullElse(this.group, ""), this.ingredient, this.input, this.result, this.skills);
        recipeOutput.accept(resourceLocation, recipe, advancement.build(resourceLocation.identifier().withPrefix("recipes/alchemy_table/")));
    }

    @Override
    public @NotNull AlchemyTableRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(HolderGetter<Item> holderGetter, ItemLike p_125978_) {
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
