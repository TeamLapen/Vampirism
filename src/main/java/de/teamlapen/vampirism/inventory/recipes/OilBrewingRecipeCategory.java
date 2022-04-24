package de.teamlapen.vampirism.inventory.recipes;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.modcompat.jei.OilJeiBrewingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * copied and adapted from {@link mezz.jei.plugins.vanilla.brewing.BrewingRecipeCategory}
 */
public class OilBrewingRecipeCategory implements IRecipeCategory<OilJeiBrewingRecipe> {

    public static final ResourceLocation OIL_BREWING = new ResourceLocation(REFERENCE.MODID, "oil_brewing");
    private static final ResourceLocation vanillaRecipeTexture = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");

    private static final int brewPotionSlot1 = 0;
    private static final int brewPotionSlot2 = 1;
    private static final int brewPotionSlot3 = 2;
    private static final int brewIngredientSlot = 3;
    private static final int outputSlot = 4; // for display only

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final ITextComponent localizedName;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;
    private final IDrawableStatic blazeHeat;

    public OilBrewingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = vanillaRecipeTexture;
        background = guiHelper.drawableBuilder(location, 0, 0, 64, 60)
                .addPadding(1, 0, 0, 50)
                .build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.BREWING_STAND));
        localizedName = new TranslationTextComponent("gui.vampirism.jei.category.oil_brewing");

        arrow = guiHelper.drawableBuilder(location, 64, 0, 9, 28)
                .buildAnimated(400, IDrawableAnimated.StartDirection.TOP, false);

        ITickTimer bubblesTickTimer = new BrewingBubblesTickTimer(guiHelper);
        bubbles = guiHelper.drawableBuilder(location, 73, 0, 12, 29)
                .buildAnimated(bubblesTickTimer, IDrawableAnimated.StartDirection.BOTTOM);

        blazeHeat = guiHelper.createDrawable(location, 64, 29, 18, 4);

        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return OIL_BREWING;
    }

    @Nonnull
    @Override
    public Class<? extends OilJeiBrewingRecipe> getRecipeClass() {
        return OilJeiBrewingRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return getTitleAsTextComponent().getString();
    }

    @Nonnull
    @Override
    public ITextComponent getTitleAsTextComponent() {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(OilJeiBrewingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        blazeHeat.draw(matrixStack, 5, 30);
        bubbles.draw(matrixStack, 8, 0);
        arrow.draw(matrixStack, 42, 2);
    }

    @Override
    public void setIngredients(OilJeiBrewingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getInputs());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOilOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, OilJeiBrewingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(brewPotionSlot1, true, 0, 36);
        itemStacks.init(brewPotionSlot2, true, 23, 43);
        itemStacks.init(brewPotionSlot3, true, 46, 36);
        itemStacks.init(brewIngredientSlot, true, 23, 2);
        itemStacks.init(outputSlot, false, 80, 2);

        itemStacks.setBackground(outputSlot, slotDrawable);

        itemStacks.set(ingredients);
    }

    public static Collection<IJeiBrewingRecipe> getOilBrewingRecipes() {
        Set<IJeiBrewingRecipe> recipes = new HashSet<>();
        BrewingRecipeRegistry.getRecipes().stream()
                .filter(TagNBTBrewingRecipe.class::isInstance)
                .map(TagNBTBrewingRecipe.class::cast)
                .forEach(brewingRecipe -> recipes.add(new OilJeiBrewingRecipe(Arrays.asList(brewingRecipe.getIngredient()), Arrays.asList(brewingRecipe.getInput().getItems()), brewingRecipe.getOutput())));
        return recipes;
    }

    private static class BrewingBubblesTickTimer implements ITickTimer {
        /**
         * Similar to {@link net.minecraft.client.gui.screen.inventory.BrewingStandScreen#BUBBLELENGTHS}
         */
        @SuppressWarnings("JavadocReference")
        private static final int[] BUBBLE_LENGTHS = new int[]{29, 23, 18, 13, 9, 5, 0};
        private final ITickTimer internalTimer;

        public BrewingBubblesTickTimer(IGuiHelper guiHelper) {
            this.internalTimer = guiHelper.createTickTimer(14, BUBBLE_LENGTHS.length - 1, false);
        }

        @Override
        public int getValue() {
            int timerValue = this.internalTimer.getValue();
            return BUBBLE_LENGTHS[timerValue];
        }

        @Override
        public int getMaxValue() {
            return BUBBLE_LENGTHS[0];
        }
    }
}
