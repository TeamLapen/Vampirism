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
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BasicWeaponTableRecipeRenderer<T extends IWeaponTableRecipe> extends IRecipeRenderer.RecipeRendererBase<T> {

    private final SubTexture CRAFTING_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/weapon_table_recipe.png"), 0, 0, 110, 75);

    public BasicWeaponTableRecipeRenderer(T recipe) {
        super(recipe);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(@NotNull PoseStack stack, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen baseScreen, @NotNull Font fontRenderer, IngredientCycler ingredientCycler) {


        CRAFTING_GRID.draw(stack, guiLeft + 62, guiTop + 43);
        baseScreen.drawCenteredStringWithoutShadow(stack, fontRenderer, ModBlocks.WEAPON_TABLE.get().getName(), guiLeft + baseScreen.xSize / 2, guiTop + 12, 0);
        baseScreen.drawCenteredStringWithoutShadow(stack, fontRenderer, getRecipeName().withStyle(style -> style.withItalic(true)), guiLeft + baseScreen.xSize / 2, guiTop + 14 + fontRenderer.lineHeight, 0);

        int outputX = guiLeft + 152;
        int outputY = guiTop + 72;

        ItemStack itemStack = recipe.getResultItem();


        GuiHelper.drawItemStack(stack, itemStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(recipe.getResultItem());
        }

        if (recipe.getRequiredLavaUnits() > 0) {
            GuiHelper.drawItemStack(stack, new ItemStack(Items.LAVA_BUCKET), outputX - 16, outputY + 21);
        }

        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            fontRenderer.draw(stack, level, guiLeft + 40, y, Color.GRAY.getRGB());
            y += fontRenderer.lineHeight + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            FormattedText newLine = Component.literal("\n");
            List<FormattedText> skills = new ArrayList<>();
            skills.add(Component.translatable("gui.vampirism.hunter_weapon_table.skill", "\n"));
            for (ISkill<?> skill : recipe.getRequiredSkills()) {
                skills.add(skill.getName().copy().withStyle(ChatFormatting.ITALIC));
                skills.add(newLine);
            }
            fontRenderer.drawWordWrap(FormattedText.composite(skills), guiLeft + 40, y, 110, Color.GRAY.getRGB());
        }
    }

    protected MutableComponent getRecipeName() {
        return Component.translatable("guideapi.text.crafting.shaped");
    }


}
