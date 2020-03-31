package de.teamlapen.vampirism.modcompat.guide.client;

import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.gui.EntryScreen;
import amerifrance.guideapi.gui.GuiEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            GuiEntry e = new GuiEntry(book, category, from, player, bookStack);
            e.pageNumber = fromPage;
            Minecraft.getMinecraft().displayGuiScreen(e);
        } else {
            super.actionPerformed(button);

        }
    }


}
