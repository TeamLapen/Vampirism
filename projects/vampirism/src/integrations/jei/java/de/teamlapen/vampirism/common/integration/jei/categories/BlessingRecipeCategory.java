package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.integration.jei.recipes.BlessableRecipe;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.awt.*;


public class BlessingRecipeCategory implements IRecipeCategory<BlessableRecipe> {

    private static final int BACKGROUND_WIDTH = 83;

    private final IDrawable background;
    private final IDrawable icon;

    public BlessingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(VIdentifier.mod("textures/gui/blessing_recipe_jei.png"), 0, 0, BACKGROUND_WIDTH, 49).setTextureSize(83, 49).addPadding(10, 20, 20, 20).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALTAR_CLEANSING.get()));
    }

    @Override
    public IRecipeType<BlessableRecipe> getRecipeType() {
        return VampirismJEIPlugin.BLESSING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.vampirism.jei.category.blessing");
    }


    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 79;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(BlessableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics, (getWidth() - BACKGROUND_WIDTH)/2, 0);
        if (recipe.requiredSkill() != null) {
            graphics.textWithWordWrap(Minecraft.getInstance().font, Component.translatable("gui.vampirism.skill_required", recipe.requiredSkill().value().getName()), 7, 52, 100, Color.gray.getRGB(), false);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BlessableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 22, 17).add(recipe.item().asItem().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 17).add(recipe.output().getDefaultInstance());
    }
}
