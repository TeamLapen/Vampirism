package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.ShapelessWeaponTableRecipe;
import de.teamlapen.vampirism.util.REFERENCE;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Recipe category for {@link IWeaponTableRecipe}
 */
public class WeaponTableRecipeCategory implements IRecipeCategory<IWeaponTableRecipe> {

    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;
    private final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_clean.png");
    @Nonnull
    private final ICraftingGridHelper craftingGridHelper;

    public WeaponTableRecipeCategory(IGuiHelper guiHelper) {
        localizedName = UtilLib.translate(ModBlocks.weapon_table.getTranslationKey());
        background = guiHelper.createDrawable(location, 32, 14, 134, 77);
        craftingGridHelper = guiHelper.createCraftingGridHelper(1);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.weapon_table));
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Class<? extends IWeaponTableRecipe> getRecipeClass() {
        return IWeaponTableRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.WEAPON_TABLE_RECIPE_ID;
    }

    @Override
    public void setIngredients(IWeaponTableRecipe iWeaponTableRecipe, IIngredients iIngredients) {

    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, IWeaponTableRecipe iWeaponTableRecipe, IIngredients ingredients) {
        int craftOutputSlot = 0;
        IGuiItemStackGroup guiItemStackGroup = iRecipeLayout.getItemStacks();
        guiItemStackGroup.init(craftOutputSlot, false, 111, 31);
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                guiItemStackGroup.init(1 + x + y * 4, true, 1 + x * 19, 1 + y * 19);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        if (iWeaponTableRecipe instanceof ShapedWeaponTableRecipe) {
            ShapedWeaponTableRecipe wrapper = (ShapedWeaponTableRecipe) iWeaponTableRecipe;
            craftingGridHelper.setInputs(guiItemStackGroup, inputs, wrapper.getWidth(), wrapper.getHeight());

        } else if (iWeaponTableRecipe instanceof ShapelessWeaponTableRecipe) {
            iRecipeLayout.setShapeless();
        }
        guiItemStackGroup.set(craftOutputSlot, outputs.get(0));
    }
}
