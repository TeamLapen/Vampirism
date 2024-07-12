package de.teamlapen.vampirism.modcompat.guide.recipes;

import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BasicWeaponTableRecipeRenderer<T extends IWeaponTableRecipe> extends IRecipeRenderer.RecipeRendererBase<T> {

    private final SubTexture CRAFTING_GRID = new SubTexture(ResourceLocation.fromNamespaceAndPath("vampirismguide", "textures/gui/weapon_table_recipe.png"), 0, 0, 110, 75);

    public BasicWeaponTableRecipeRenderer(T recipe) {
        super(recipe);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(@NotNull GuiGraphics guiGraphics, RegistryAccess registryAccess, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen baseScreen, @NotNull Font fontRenderer, IngredientCycler ingredientCycler) {


        CRAFTING_GRID.draw(guiGraphics, guiLeft + 62, guiTop + 43);
        baseScreen.drawCenteredStringWithoutShadow(guiGraphics, fontRenderer, ModBlocks.WEAPON_TABLE.get().getName(), guiLeft + baseScreen.xSize / 2, guiTop + 12, 0);
        baseScreen.drawCenteredStringWithoutShadow(guiGraphics, fontRenderer, getRecipeName().withStyle(style -> style.withItalic(true)), guiLeft + baseScreen.xSize / 2, guiTop + 14 + fontRenderer.lineHeight, 0);

        int outputX = guiLeft + 152;
        int outputY = guiTop + 72;

        ItemStack itemStack = recipe.getResultItem(registryAccess);


        GuiHelper.drawItemStack(guiGraphics, itemStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(recipe.getResultItem(registryAccess));
        }

        if (recipe.getRequiredLavaUnits() > 0) {
            GuiHelper.drawItemStack(guiGraphics, new ItemStack(Items.LAVA_BUCKET), outputX - 16, outputY + 21);
        }

        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            guiGraphics.drawString(fontRenderer, level, guiLeft + 40, y, Color.GRAY.getRGB(), false);
            y += fontRenderer.lineHeight + 2;
        }
        if (!recipe.getRequiredSkills().isEmpty()) {
            FormattedText newLine = Component.literal("\n");
            List<FormattedText> skills = new ArrayList<>();
            skills.add(Component.translatable("gui.vampirism.skill_required", "\n"));
            for (Holder<ISkill<?>> skill : recipe.getRequiredSkills()) {
                skills.add(skill.value().getName().copy().withStyle(ChatFormatting.ITALIC));
                skills.add(newLine);
            }
            guiGraphics.drawWordWrap(fontRenderer, FormattedText.composite(skills), guiLeft + 40, y, 110, Color.GRAY.getRGB());
        }
    }

    protected MutableComponent getRecipeName() {
        return Component.translatable("guideapi.text.crafting.shaped");
    }


}
