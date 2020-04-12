package de.teamlapen.vampirism.modcompat.guide.client;

import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.button.ButtonBack;
import amerifrance.guideapi.gui.EntryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Simple GuiEntry which back button leads to a previous entry and not to the category page
 */
public class LinkedEntryScreen extends EntryScreen {

    private final EntryAbstract from;
    private final int fromPage;

    public LinkedEntryScreen(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, EntryAbstract from, int fromPage) {
        super(book, category, entry, player, bookStack);
        this.from = from;
        this.fromPage = fromPage;
    }

    @Override
    public void init() {
        super.init();
        this.buttons.remove(this.buttonBack);
        this.children.remove(this.buttonBack);
        this.addButton(this.buttonBack = new ButtonBack(this.guiLeft + this.xSize / 6, this.guiTop, (btn) -> {
            EntryScreen e = new EntryScreen(book, category, from, player, bookStack);
            e.pageNumber = fromPage;
            this.minecraft.displayGuiScreen(e);
        }, this));
    }
}
