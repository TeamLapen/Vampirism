package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.items.BlessableItem;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class BlessingRecipeCategory implements IRecipeCategory<BlessableItem.Recipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public BlessingRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(new ResourceLocation(REFERENCE.MODID, "textures/gui/blessing_recipe_jei.png"), 0, 0, 83, 49).setTextureSize(83, 49).addPadding(10, 20, 20, 20).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CHURCH_ALTAR.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.BLESSING_RECIPE_UID;
    }

    @Override
    public Class<? extends BlessableItem.Recipe> getRecipeClass() {
        return BlessableItem.Recipe.class;
    }

    @Override
    public String getTitle() {
        return UtilLib.translate("text.vampirism.blessing");
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
    public void setIngredients(BlessableItem.Recipe recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.input));
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(recipe.output));
    }

    @Override
    public void draw(BlessableItem.Recipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        if (recipe.enhanced) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.font.draw(matrixStack, new TranslationTextComponent("gui.vampirism.skill_required"), 15, 52, Color.gray.getRGB());
            minecraft.font.draw(matrixStack, HunterSkills.ENHANCED_BLESSING.get().getName(), 15, 52 + minecraft.font.lineHeight + 2, Color.gray.getRGB());

        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BlessableItem.Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, false, 75, 26);
        guiItemStackGroup.init(1, true, 23, 26);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        guiItemStackGroup.set(0, outputs.stream().flatMap(Collection::stream).collect(Collectors.toList()));
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        guiItemStackGroup.set(1, inputs.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }
}
