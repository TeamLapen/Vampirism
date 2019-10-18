package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.util.REFERENCE;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;


public class AlchemicalCauldronRecipeCategory implements IRecipeCategory<AlchemicalCauldronRecipe> {


    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;

    private final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/alchemical_cauldron.png");

    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;


    public AlchemicalCauldronRecipeCategory(IGuiHelper guiHelper) {
        this.localizedName = UtilLib.translate(ModBlocks.alchemical_cauldron.getTranslationKey());
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.alchemical_cauldron));
        background = guiHelper.createDrawable(location, 38, 10, 120, 70);

        IDrawableStatic flameDrawable = guiHelper.createDrawable(location, 176, 0, 14, 14);
        flame = guiHelper.createAnimatedDrawable(flameDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 176, 14, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic bubblesDrawable = guiHelper.createDrawable(location, 176, 31, 12, 29);
        this.bubbles = guiHelper.createAnimatedDrawable(bubblesDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

    }

    @Override
    public void draw(AlchemicalCauldronRecipe recipe, double mouseX, double mouseY) {
        flame.draw(29, 27);
        arrow.draw(51, 25);
        bubbles.draw(114, 19);
    }


    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Class<? extends AlchemicalCauldronRecipe> getRecipeClass() {
        return AlchemicalCauldronRecipe.class;
    }


    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.ALCHEMICAL_CAULDRON_RECIPE_UID;
    }

    @Override
    public void setIngredients(AlchemicalCauldronRecipe alchemicalCauldronRecipeWrapper, IIngredients iIngredients) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AlchemicalCauldronRecipe recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, false, 87, 23);
        guiItemStackGroup.init(1, true, 15, 6);
        guiItemStackGroup.init(2, true, 39, 6);
        guiItemStackGroup.set(ingredients);

    }
}
