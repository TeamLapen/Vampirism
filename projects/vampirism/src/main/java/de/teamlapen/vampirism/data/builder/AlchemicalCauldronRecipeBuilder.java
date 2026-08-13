package de.teamlapen.vampirism.data.builder;

import com.mojang.datafixers.util.Either;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.common.world.items.recipes.AlchemicalCauldronRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class AlchemicalCauldronRecipeBuilder implements RecipeBuilder {

    public static @NotNull AlchemicalCauldronRecipeBuilder cauldronRecipe(HolderLookup.RegistryLookup<Item> itemLookup, ItemStackTemplate item) {
        return new AlchemicalCauldronRecipeBuilder(itemLookup, item);
    }

    private final HolderLookup.RegistryLookup<Item> itemLookup;
    protected final @NotNull ItemStackTemplate result;
    protected String group;
    protected Ingredient ingredient;
    protected Either<Ingredient, FluidStackTemplate> fluid;
    protected List<Holder<? extends ISkill<?>>> skills = new LinkedList<>();
    protected int reqLevel = 1;
    protected int cookTime = 200;
    protected float exp = 0.2f;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlchemicalCauldronRecipeBuilder(HolderLookup.RegistryLookup<Item> itemLookup, @NotNull ItemStackTemplate result) {
        this.itemLookup = itemLookup;
        this.result = result;
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceLocation) {
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        Recipe.CommonInfo commonInfo = RecipeBuilder.createCraftingCommonInfo(true);
        var recipe = new AlchemicalCauldronRecipe(commonInfo, Objects.requireNonNullElse(this.group, ""), this.ingredient, this.fluid, this.result, this.skills, this.reqLevel, this.cookTime, this.exp);
        recipeOutput.accept(resourceLocation, recipe, builder.build(resourceLocation.identifier().withPrefix("recipes/alchemical_cauldron/")));
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    @Override
    public @NotNull AlchemicalCauldronRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder cookTime(int cookTime) {
        this.cookTime = cookTime;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder experience(float exp) {
        this.exp = exp;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder requireLevel(int level) {
        this.reqLevel = level;
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withFluid(@NotNull TagKey<Item> tag) {
        this.fluid = Either.left(Ingredient.of(this.itemLookup.getOrThrow(tag)));
        return unlockedBy("has_fluid", has(this.itemLookup, tag));
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withFluid(@NotNull FluidStackTemplate fluid) {
        this.fluid = Either.right(fluid);
        return this;
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withFluid(@NotNull ItemLike... item) {
        this.fluid = Either.left(Ingredient.of(item));
        return unlockedBy("has_fluid", has(this.itemLookup, item));
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withIngredient(@NotNull ItemLike... items) {
        this.ingredient = Ingredient.of(items);
        return unlockedBy("has_ingredient", has(this.itemLookup, items));
    }

    public @NotNull AlchemicalCauldronRecipeBuilder withIngredient(@NotNull TagKey<Item> tag) {
        this.ingredient = Ingredient.of(this.itemLookup.getOrThrow(tag));
        return unlockedBy("has_ingredient", has(this.itemLookup, tag));
    }

    @SafeVarargs
    public final @NotNull AlchemicalCauldronRecipeBuilder withSkills(@NotNull Holder<? extends ISkill<?>>... skills) {
        this.skills.addAll(Arrays.asList(skills));
        return this;
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(HolderGetter<Item> holderGetter, ItemLike... items) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(holderGetter, items));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(HolderGetter<Item> holderGetter, TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(holderGetter, tag));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... builders) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, Arrays.stream(builders).map(ItemPredicate.Builder::build).toList()));
    }
}
