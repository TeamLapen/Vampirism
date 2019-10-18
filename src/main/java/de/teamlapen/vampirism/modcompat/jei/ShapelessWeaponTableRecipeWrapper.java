//package de.teamlapen.vampirism.modcompat.jei;
//
//import mezz.jei.api.ingredients.IIngredients;
//
//import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
//import net.minecraft.item.ItemStack;
//
//import javax.annotation.Nonnull;
//
//
//public class ShapelessWeaponTableRecipeWrapper extends WeaponTableRecipeWrapper {
//    private final @Nonnull
//    ShapelessWeaponTableRecipe recipe;
//
//    protected ShapelessWeaponTableRecipeWrapper(@Nonnull ShapelessWeaponTableRecipe recipe) {
//        super(recipe);
//        this.recipe = recipe;
//        for (Object input : this.recipe.recipeItems) {
//            if (input instanceof ItemStack) {
//                ItemStack itemStack = (ItemStack) input;
//                if (itemStack.getCount() != 1) {
//                    itemStack.setCount(1);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void getIngredients(IIngredients ingredients) {
//        super.getIngredients(ingredients);
//        ingredients.setInputs(ItemStack.class, recipe.recipeItems);
//        ItemStack recipeOutput = recipe.getRecipeOutput();
//        if (!recipeOutput.isEmpty()) {
//            ingredients.setOutput(ItemStack.class, recipeOutput);
//        }
//    }
//
//}
