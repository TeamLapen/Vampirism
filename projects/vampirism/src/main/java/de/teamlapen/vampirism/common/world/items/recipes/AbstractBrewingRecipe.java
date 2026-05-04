package de.teamlapen.vampirism.common.world.items.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class AbstractBrewingRecipe implements Recipe<BrewingRecipeInput> {

    protected final RecipeType<? extends AbstractBrewingRecipe> type;
    protected final CommonInfo commonInfo;
    protected final String group;
    protected final Ingredient ingredient;
    protected final Ingredient input;
    protected final ItemStackTemplate result;

    public AbstractBrewingRecipe(RecipeType<? extends AbstractBrewingRecipe> type, CommonInfo commonInfo, String group, Ingredient ingredient, Ingredient input, ItemStackTemplate result) {
        this.type = type;
        this.commonInfo = commonInfo;
        this.group = group;
        this.ingredient = ingredient;
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(BrewingRecipeInput inventory, Level level) {
        return this.ingredient.test(inventory.input());
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStackTemplate getResultItem() {
        return result;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput inventory) {
        return this.result.create();
    }

    @Override
    public RecipeType<? extends Recipe<BrewingRecipeInput>> getType() {
        return this.type;
    }
}
