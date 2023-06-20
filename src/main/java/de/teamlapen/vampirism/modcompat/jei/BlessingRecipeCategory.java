package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.items.BlessableItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


public class BlessingRecipeCategory implements IRecipeCategory<BlessableItem.Recipe> {

    private final @NotNull IDrawable background;
    private final @NotNull IDrawable icon;

    public BlessingRecipeCategory(@NotNull IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(new ResourceLocation(REFERENCE.MODID, "textures/gui/blessing_recipe_jei.png"), 0, 0, 83, 49).setTextureSize(83, 49).addPadding(10, 20, 20, 20).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALTAR_CLEANSING.get()));
    }

    @Override
    public @NotNull RecipeType<BlessableItem.Recipe> getRecipeType() {
        return VampirismJEIPlugin.BLESSING;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("text.vampirism.blessing");
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
    public void draw(BlessableItem.@NotNull Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        if (recipe.enhanced) {
            Minecraft minecraft = Minecraft.getInstance();
            graphics.drawString(minecraft.font, Component.translatable("gui.vampirism.skill_required"), 15, 52, Color.gray.getRGB(), false);
            graphics.drawString(minecraft.font, HunterSkills.ENHANCED_BLESSING.get().getName(), 15, 52 + minecraft.font.lineHeight + 2, Color.gray.getRGB(), false);
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, BlessableItem.@NotNull Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 27).addItemStack(recipe.input.getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 27).addItemStack(recipe.output.getDefaultInstance());
    }
}
