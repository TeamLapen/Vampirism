package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class AlchemicalCauldronRecipeCategory implements IRecipeCategory<AlchemicalCauldronRecipe> {


    private static final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/alchemical_cauldron.png");
    private final Component localizedName;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;


    AlchemicalCauldronRecipeCategory(IGuiHelper guiHelper) {
        this.localizedName = new TranslatableComponent(ModBlocks.alchemical_cauldron.get().getDescriptionId());
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.alchemical_cauldron.get()));
        background = guiHelper.drawableBuilder(location, 38, 10, 120, 70).addPadding(0, 33, 0, 0).build();

        IDrawableStatic flameDrawable = guiHelper.createDrawable(location, 176, 0, 14, 14);
        flame = guiHelper.createAnimatedDrawable(flameDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 176, 14, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic bubblesDrawable = guiHelper.createDrawable(location, 176, 31, 12, 29);
        this.bubbles = guiHelper.createAnimatedDrawable(bubblesDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

    }

    @Override
    public void draw(AlchemicalCauldronRecipe recipe, @Nonnull PoseStack stack, double mouseX, double mouseY) {
        flame.draw(stack, 19, 27);
        arrow.draw(stack, 41, 25);
        bubbles.draw(stack, 104, 19);
        Minecraft minecraft = Minecraft.getInstance();
        int x = 0;
        int y = 65;
        if (recipe.getRequiredLevel() > 1) {
            Component level = new TranslatableComponent("gui.vampirism.alchemical_cauldron.level", recipe.getRequiredLevel());
            minecraft.font.draw(stack, level, x, y, Color.GRAY.getRGB());
            y += minecraft.font.lineHeight + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            MutableComponent skillText = new TranslatableComponent("gui.vampirism.alchemical_cauldron.skill", " ");

            for (ISkill<?> s : recipe.getRequiredSkills()) {
                skillText.append(s.getName()).append(" ");
            }
            y += UtilLib.renderMultiLine(minecraft.font, stack, skillText, 132, x, y, Color.GRAY.getRGB());
        }
    }


    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public Class<? extends AlchemicalCauldronRecipe> getRecipeClass() {
        return AlchemicalCauldronRecipe.class;
    }


    @Nonnull
    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.ALCHEMICAL_CAULDRON_RECIPE_UID;
    }

    @Override
    public void setIngredients(AlchemicalCauldronRecipe recipe, IIngredients iIngredients) {
        List<Ingredient> ingredients = new ArrayList<>();

        recipe.getFluid().ifRight(fluidStack -> ingredients.add(Ingredient.of(fluidStack.getFluid().getBucket())));
        recipe.getFluid().ifLeft(ingredients::add);
        ingredients.addAll(recipe.getIngredients());
        iIngredients.setInputIngredients(ingredients);

        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @Nonnull AlchemicalCauldronRecipe recipeWrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(0, false, 77, 23);
        guiItemStackGroup.init(1, true, 5, 6);
        guiItemStackGroup.init(2, true, 29, 6);
        guiItemStackGroup.set(ingredients);

    }


}
