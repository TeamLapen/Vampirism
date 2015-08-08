package de.teamlapen.vampirism.guide;

import amerifrance.guideapi.api.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.abstraction.EntryAbstract;
import amerifrance.guideapi.api.abstraction.IPage;
import amerifrance.guideapi.api.base.Book;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.gui.GuiBase;
import amerifrance.guideapi.gui.GuiEntry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * IPage whichs acts like a IPage specified in the contructor but adds links to other entries at the gui border
 */
public class PageHolderWithLinks implements IPage {
    private IPage page;
    private List<Link> links;
    private List<String> lateLinks;

    private class Link {
        public final EntryAbstract entry;
        public int width = 0;

        public Link(EntryAbstract entry) {
            this.entry = entry;
        }
    }

    public PageHolderWithLinks addLink(EntryAbstract entry) {
        links.add(new Link(entry));
        return this;
    }

    public PageHolderWithLinks addLink(String identifier) {
        lateLinks.add(identifier);
        return this;
    }

    public PageHolderWithLinks(IPage wrappedPage) {
        page = wrappedPage;
        links = new ArrayList<Link>();
        lateLinks = new ArrayList<String>();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int i, int i1, int i2, int i3, GuiBase guiBase, FontRenderer fontRenderer) {
        page.draw(book, categoryAbstract, entryAbstract, i, i1, i2, i3, guiBase, fontRenderer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
        int ll = guiLeft + guiBase.xSize - 5;
        int y = guiTop + 10;
        for (Link l : links) {
            fontRenderer.drawStringWithShadow(l.entry.getLocalizedName(), ll, y, 0xFFFFFF);
            if (l.width == 0) {
                l.width = fontRenderer.getStringWidth(l.entry.getLocalizedName());
            }
            y += 20;
        }
        page.drawExtras(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRenderer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canSee(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, EntityPlayer entityPlayer, ItemStack itemStack, GuiEntry guiEntry) {
        return page.canSee(book, categoryAbstract, entryAbstract, entityPlayer, itemStack, guiEntry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLeftClicked(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int mouseX, int mouseY, EntityPlayer entityPlayer, GuiEntry guiEntry) {
        if (mouseX > guiEntry.guiLeft + guiEntry.xSize) {
            for (int i = 0; i < links.size(); i++) {
                if (GuiHelper.isMouseBetween(mouseX, mouseY, guiEntry.guiLeft + guiEntry.xSize, guiEntry.guiTop + 10 + 20 * i, links.get(i).width, 20)) {
                    //VampirismMod.proxy.openLinkedEntry(book, categoryAbstract, links.get(i).entry, entityPlayer, guiEntry.bookStack, entryAbstract, guiEntry.pageNumber);
                    openLinkedEntry(book, categoryAbstract, links.get(i).entry, entityPlayer, guiEntry.bookStack, entryAbstract, guiEntry.pageNumber);
                    return;
                }
            }
        }
        page.onLeftClicked(book, categoryAbstract, entryAbstract, mouseX, mouseY, entityPlayer, guiEntry);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onRightClicked(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int i, int i1, EntityPlayer entityPlayer, GuiEntry guiEntry) {
        page.onRightClicked(book, categoryAbstract, entryAbstract, i, i1, entityPlayer, guiEntry);
    }

    /**
     * Simply opens a gui screen with a GuiLinkedEntry. Not sure why the @SideOnly does not work, but this uses Class.forName to solve server side class not found issues
     *
     * @param book
     * @param category
     * @param entry
     * @param player
     * @param bookStack
     * @param from
     * @param fromPage
     */
    @SideOnly(Side.CLIENT)
    public void openLinkedEntry(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack, EntryAbstract from, int fromPage) {
        GuiScreen screen = null;
        try {
            screen = Class.forName("de.teamlapen.vampirism.client.gui.GuiLinkedEntry").asSubclass(GuiScreen.class).getConstructor(Book.class, CategoryAbstract.class, EntryAbstract.class, EntityPlayer.class, ItemStack.class, EntryAbstract.class, int.class).newInstance(book, category, entry, player, bookStack, from, fromPage);
        } catch (Exception e) {
            Logger.e("PHWithLink", e, "Failed to create a GuiLinkedEntry. This should not be impossible. But maybe the mod author has messed something up.");
        }
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }

    public void onInit(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, EntityPlayer entityPlayer, ItemStack itemStack, GuiEntry guiEntry) {
        while (lateLinks.size() > 0) {
            String s = lateLinks.remove(0);
            EntryAbstract e = VampirismGuide.getLinkedEntry(s);
            if (e != null) {
                links.add(new Link(e));
            } else {
                Logger.w("PageWithLinks", "Failed to find linked entry %s", s);
            }
        }
        page.onInit(book, categoryAbstract, entryAbstract, entityPlayer, itemStack, guiEntry);
    }
}
