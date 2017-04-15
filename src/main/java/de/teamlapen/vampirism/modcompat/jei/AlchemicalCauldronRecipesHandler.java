package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronRecipesHandler implements IRecipeHandler<IAlchemicalCauldronRecipe> {

    private final IStackHelper stackHelper;

    public AlchemicalCauldronRecipesHandler(IStackHelper stackHelper) {
        this.stackHelper = stackHelper;
    }


    @Override
    public String getRecipeCategoryUid(IAlchemicalCauldronRecipe recipe) {
        return VampirismJEIPlugin.ALCHEMICAL_CAULDRON_RECIPE_UID;
    }

    @Override
    public Class<IAlchemicalCauldronRecipe> getRecipeClass() {
        return IAlchemicalCauldronRecipe.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(IAlchemicalCauldronRecipe recipe) {
        return new AlchemicalCauldronRecipeWrapper(recipe, stackHelper);
    }

    @Override
    public boolean isRecipeValid(IAlchemicalCauldronRecipe recipe) {
        return true;
    }
}
