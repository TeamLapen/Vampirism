//package de.teamlapen.vampirism.modcompat.jei;
//
//
//import mezz.jei.api.helpers.IStackHelper;
//
///**
// * 1.10
// *
// * @author maxanier
// */
//public class AlchemicalCauldronRecipesHandler implements IRecipeHandler {
//
//    private final IStackHelper stackHelper;
//
//    public AlchemicalCauldronRecipesHandler(IStackHelper stackHelper) {
//        this.stackHelper = stackHelper;
//    }
//
//
//    @Override
//    public String getRecipeCategoryUid(IAlchemicalCauldronRecipe recipe) {
//        return VampirismJEIPlugin.ALCHEMICAL_CAULDRON_RECIPE_UID;
//    }
//
//    @Override
//    public Class<IAlchemicalCauldronRecipe> getRecipeClass() {
//        return IAlchemicalCauldronRecipe.class;
//    }
//
//    @Override
//    public IRecipeWrapper getRecipeWrapper(IAlchemicalCauldronRecipe recipe) {
//        return new AlchemicalCauldronRecipeWrapper(recipe, stackHelper);
//    }
//
//    @Override
//    public boolean isRecipeValid(IAlchemicalCauldronRecipe recipe) {
//        return true;
//    }
//}
