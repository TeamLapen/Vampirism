package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronRecipeCategory extends BlankRecipeCategory<AlchemicalCauldronRecipeWrapper> {


    private final String localizedName;
    private final IDrawable background;
    private final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/alchemical_cauldron.png");

    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;


    public AlchemicalCauldronRecipeCategory(IGuiHelper guiHelper) {
        this.localizedName = ModBlocks.alchemicalCauldron.getLocalizedName();
        background = guiHelper.createDrawable(location, 38, 10, 120, 70, 0, 30, 10, 0);

        IDrawableStatic flameDrawable = guiHelper.createDrawable(location, 176, 0, 14, 14);
        flame = guiHelper.createAnimatedDrawable(flameDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 176, 14, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic bubblesDrawable = guiHelper.createDrawable(location, 176, 31, 12, 29);
        this.bubbles = guiHelper.createAnimatedDrawable(bubblesDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

    }


    @Override
    public void drawExtras(Minecraft minecraft) {
        flame.draw(minecraft, 29, 27);
        arrow.draw(minecraft, 51, 25);
        bubbles.draw(minecraft, 114, 19);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getUid() {
        return VampirismJEIPlugin.ALCHEMICAL_CAULDRON_RECIPE_UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AlchemicalCauldronRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, false, 87, 23);
        guiItemStackGroup.init(1, true, 15, 6);
        guiItemStackGroup.init(2, true, 39, 6);
        guiItemStackGroup.set(ingredients);

    }
}
