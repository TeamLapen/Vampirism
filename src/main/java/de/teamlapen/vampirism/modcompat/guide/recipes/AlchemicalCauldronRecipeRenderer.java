package de.teamlapen.vampirism.modcompat.guide.recipes;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalCauldronRecipeRenderer extends IRecipeRenderer.RecipeRendererBase<AlchemicalCauldronRecipe> {

    private final SubTexture CRAFTING_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/alchemical_cauldron_recipe.png"), 0, 0, 110, 75);

    public AlchemicalCauldronRecipeRenderer(AlchemicalCauldronRecipe recipe) {
        super(recipe);
    }

    @Override
    public void draw(@NotNull PoseStack stack, RegistryAccess registryAccess, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen baseScreen, @NotNull Font fontRenderer, @NotNull IngredientCycler ingredientCycler) {

        CRAFTING_GRID.draw(stack, guiLeft + 60, guiTop + 42);
        baseScreen.drawCenteredStringWithoutShadow(stack, fontRenderer, UtilLib.translate(ModBlocks.ALCHEMICAL_CAULDRON.get().getDescriptionId()), guiLeft + baseScreen.xSize / 2, guiTop + 12, 0);
        baseScreen.drawCenteredStringWithoutShadow(stack, fontRenderer, "§o" + UtilLib.translate("guideapi.text.crafting.shaped") + "§r", guiLeft + baseScreen.xSize / 2, guiTop + 14 + fontRenderer.lineHeight, 0);

        int outputX = guiLeft + 150;
        int outputY = guiTop + 72;
        int in1X = guiLeft + 78;
        int in1Y = guiTop + 59;
        int in2X = guiLeft + 108;
        int in2Y = guiTop + 59;

        ItemStack itemStack = recipe.getResultItem(registryAccess);


        GuiHelper.drawItemStack(stack, itemStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(itemStack);
        }

        Ingredient input = recipe.getIngredients().get(0);
        ingredientCycler.getCycledIngredientStack(input, 0).ifPresent(inStack -> {

            GuiHelper.drawItemStack(stack, inStack, in2X, in2Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in2X, in2Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(inStack);
            }
        });


        Ingredient liquid = recipe.getFluid().map(ingredient -> ingredient, fluidStack -> Ingredient.of(fluidStack.getFluid().getBucket()));

        ingredientCycler.getCycledIngredientStack(liquid, 1).ifPresent(fluidStack -> {
            GuiHelper.drawItemStack(stack, fluidStack, in1X, in1Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in1X, in1Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(fluidStack);
            }
        });


        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            fontRenderer.draw(stack, level, guiLeft + 50, y, Color.GRAY.getRGB());
            y += fontRenderer.lineHeight + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            FormattedText newLine = Component.literal("\n");
            List<FormattedText> skills = new ArrayList<>();
            skills.add(Component.translatable("gui.vampirism.skill_required", "\n"));
            for (ISkill<?> skill : recipe.getRequiredSkills()) {
                skills.add(skill.getName().copy().withStyle(ChatFormatting.ITALIC));
                skills.add(newLine);
            }
            fontRenderer.drawWordWrap(stack, FormattedText.composite(skills), guiLeft + 50, y, 100, Color.GRAY.getRGB());
        }
    }


}
