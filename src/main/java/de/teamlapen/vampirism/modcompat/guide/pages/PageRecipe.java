package de.teamlapen.vampirism.modcompat.guide.pages;

import amerifrance.guideapi.api.IRecipeRenderer;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.Page;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.gui.GuiBase;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 1.12
 *
 * @author maxanier
 */
public class PageRecipe<T> extends Page {

    public T recipe;
    public IRecipeRenderer iRecipeRenderer;

    /**
     * @param recipe          - Recipe to draw
     * @param iRecipeRenderer - Your custom Recipe drawer
     */
    public PageRecipe(T recipe, IRecipeRenderer iRecipeRenderer) {
        this.recipe = recipe;
        this.iRecipeRenderer = iRecipeRenderer;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        super.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
        iRecipeRenderer.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        super.drawExtras(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
        iRecipeRenderer.drawExtras(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }
}
