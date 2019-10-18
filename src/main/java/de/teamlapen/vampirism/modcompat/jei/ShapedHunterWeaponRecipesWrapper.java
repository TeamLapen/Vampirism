//package de.teamlapen.vampirism.modcompat.jei;
//
//import mezz.jei.api.ingredients.IIngredients;
//
//import de.teamlapen.vampirism.inventory.ShapedHunterWeaponRecipe;
//import net.minecraft.item.ItemStack;
//
//import javax.annotation.Nonnull;
//import java.util.Arrays;
//
//
///**
// * Wraps {@link ShapedHunterWeaponRecipe}. Draws info about the required level and skill as well as an lava bucket icon if lava is required.
// */
//public class ShapedHunterWeaponRecipesWrapper extends WeaponTableRecipeWrapper {
//
//    private
//    @Nonnull
//    final ShapedHunterWeaponRecipe recipe;
//
//    public ShapedHunterWeaponRecipesWrapper(@Nonnull ShapedHunterWeaponRecipe recipe) {
//        super(recipe);
//        this.recipe = recipe;
//    }
//
//    public int getHeight() {
//        return recipe.recipeHeight;
//    }
//
//    @Override
//    public void getIngredients(IIngredients ingredients) {
//        super.getIngredients(ingredients);
//        ingredients.setInputs(ItemStack.class, Arrays.asList(recipe.recipeItems));
//        ItemStack output = recipe.getRecipeOutput();
//        if (!output.isEmpty()) {
//            ingredients.setOutput(ItemStack.class, output);
//        }
//    }
//
//
//    public int getWidth() {
//        return recipe.recipeWidth;
//    }
//}
