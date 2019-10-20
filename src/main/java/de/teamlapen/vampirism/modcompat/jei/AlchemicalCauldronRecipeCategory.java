package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.util.REFERENCE;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class AlchemicalCauldronRecipeCategory implements IRecipeCategory<AlchemicalCauldronRecipe> {


    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;

    private static final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/alchemical_cauldron.png");

    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;


    AlchemicalCauldronRecipeCategory(IGuiHelper guiHelper) {
        this.localizedName = UtilLib.translate(ModBlocks.alchemical_cauldron.getTranslationKey());
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.alchemical_cauldron));
        background = guiHelper.drawableBuilder(location, 38, 10, 120, 70).addPadding(0,33,0,0).build();

        IDrawableStatic flameDrawable = guiHelper.createDrawable(location, 176, 0, 14, 14);
        flame = guiHelper.createAnimatedDrawable(flameDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 176, 14, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic bubblesDrawable = guiHelper.createDrawable(location, 176, 31, 12, 29);
        this.bubbles = guiHelper.createAnimatedDrawable(bubblesDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

    }

    @Override
    public void draw(AlchemicalCauldronRecipe recipe, double mouseX, double mouseY) {
        flame.draw(19, 27);
        arrow.draw(41, 25);
        bubbles.draw(104, 19);
        Minecraft minecraft = Minecraft.getInstance();
        int x=0;
        int y=65;
        if(recipe.getRequiredLevel() > 1){
            String level = UtilLib.translate("gui.vampirism.alchemical_cauldron.level", recipe.getRequiredLevel());
            minecraft.fontRenderer.drawString(level,x,y, Color.gray.getRGB());
            y += minecraft.fontRenderer.FONT_HEIGHT+2;
        }
        if(recipe.getRequiredSkills().length>0){
            StringBuilder skills = new StringBuilder();
            for(ISkill s:recipe.getRequiredSkills()){
                skills.append(UtilLib.translate(s.getTranslationKey())).append(" ");
            }
            String skillText = UtilLib.translate("gui.vampirism.alchemical_cauldron.skill",skills.toString());
            minecraft.fontRenderer.drawSplitString(skillText,x,y,132,Color.gray.getRGB());
        }
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
    public void setIngredients(AlchemicalCauldronRecipe recipe, IIngredients iIngredients) {
        List<Ingredient> ingredients = new ArrayList<>();

        recipe.getFluid().ifRight(fluidStack -> ingredients.add(Ingredient.fromItems(fluidStack.getFluid().getFilledBucket())));
        recipe.getFluid().ifLeft(ingredients::add);
        ingredients.addAll(recipe.getIngredients());
        iIngredients.setInputIngredients(ingredients);

        iIngredients.setOutput(VanillaTypes.ITEM,recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AlchemicalCauldronRecipe recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, false, 77, 23);
        guiItemStackGroup.init(1, true, 5, 6);
        guiItemStackGroup.init(2, true, 29, 6);
        guiItemStackGroup.set(ingredients);

    }


}
