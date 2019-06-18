package de.teamlapen.vampirism.modcompat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Recipe category for {@link IWeaponTableRecipe}
 */
public class HunterWeaponRecipeCategory extends BlankRecipeCategory {

    private final String localizedName;
    private final IDrawable background;
    private final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_clean.png");
    @Nonnull
    private final ICraftingGridHelper craftingGridHelper;

    public HunterWeaponRecipeCategory(IGuiHelper guiHelper) {
        localizedName = UtilLib.translate("gui.vampirism.hunter_weapon_table");
        background = guiHelper.createDrawable(location, 32, 14, 134, 77, 0, 30, 0, 0);
        craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);

    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public String getModName() {
        return REFERENCE.NAME;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public String getUid() {
        return VampirismJEIPlugin.HUNTER_WEAPON_RECIPE_UID;
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        int craftOutputSlot = 0;
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(craftOutputSlot, false, 111, 31);
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                guiItemStackGroup.init(1 + x + y * 4, true, 1 + x * 19, 1 + y * 19);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

        if (recipeWrapper instanceof ShapedHunterWeaponRecipesWrapper) {
            ShapedHunterWeaponRecipesWrapper wrapper = (ShapedHunterWeaponRecipesWrapper) recipeWrapper;
            craftingGridHelper.setInputs(guiItemStackGroup, inputs, ((ShapedHunterWeaponRecipesWrapper) recipeWrapper).getWidth(), ((ShapedHunterWeaponRecipesWrapper) recipeWrapper).getHeight());

        } else if (recipeWrapper instanceof ShapelessWeaponTableRecipeWrapper) {
            recipeLayout.setShapeless();
        }
        guiItemStackGroup.set(craftOutputSlot, outputs.get(0));
    }
}
