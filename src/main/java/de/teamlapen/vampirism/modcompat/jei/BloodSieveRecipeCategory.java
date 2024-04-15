package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.modcompat.jei.recipes.BloodSieveRecipe;
import de.teamlapen.vampirism.modcompat.jei.recipes.GrinderRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BloodSieveRecipeCategory implements IRecipeCategory<BloodSieveRecipe> {
    private static final ResourceLocation PROGRESS = new ResourceLocation("jei", "textures/jei/gui/gui_vanilla.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/blood_grinder_jei.png");
    public static final int width = 78;
    public static final int height = 18;

    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;
    private final Component localizedName;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public BloodSieveRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, width, height).setTextureSize(width, height).build();
        this.slot = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableItemStack(ModBlocks.BLOOD_SIEVE.toStack());
        this.localizedName = Component.translatable("block.vampirism.blood_grinder");
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull IDrawableAnimated load(@NotNull Integer cookTime) {
                        return guiHelper.drawableBuilder(PROGRESS, 82, 128, 24, 17)
                                .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    @Override
    public @NotNull RecipeType<BloodSieveRecipe> getRecipeType() {
        return VampirismJEIPlugin.BLOOD_SIEVE_CONVERSION;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.localizedName;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BloodSieveRecipe recipe, @NotNull IFocusGroup focuses) {
        int capacity = FluidType.BUCKET_VOLUME;
        builder.addSlot(RecipeIngredientRole.INPUT, 1,  1).setFluidRenderer(capacity, true, 16,16)
                .addFluidStack(recipe.input().getRawFluid(), capacity);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 58, 1).setFluidRenderer(capacity, true, 16,16)
                .addFluidStack(ModFluids.BLOOD.get(), (int)(capacity * recipe.conversionRate())).setBackground(this.slot, -1,-1);
    }

    @Override
    public void draw(@NotNull BloodSieveRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics);
        this.cachedArrows.getUnchecked(50).draw(guiGraphics, 26, 1);
    }
}
