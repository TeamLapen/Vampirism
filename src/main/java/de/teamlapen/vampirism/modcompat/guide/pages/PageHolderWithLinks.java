package de.teamlapen.vampirism.modcompat.guide.pages;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.gui.GuiBase;
import amerifrance.guideapi.gui.GuiEntry;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.modcompat.guide.GuideBook;
import de.teamlapen.vampirism.modcompat.guide.client.GuiLinkedEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.net.URI;
import java.util.List;


public class PageHolderWithLinks implements IPage {

    private final IPage page;
    private final List<ResourceLocation> lateLinks = Lists.newArrayList();
    private final List<Link> links = Lists.newArrayList();
    private long lastLinkClick = 0;

    public PageHolderWithLinks(IPage page) {
        this.page = page;
    }

    /**
     * Add a link
     *
     * @return This
     */
    public PageHolderWithLinks addLink(EntryAbstract entry) {
        links.add(new EntryLink(entry));
        return this;
    }

    /**
     * Add a resource location of an entry to be linked
     *
     * @return This
     */
    public PageHolderWithLinks addLink(ResourceLocation entry) {
        lateLinks.add(entry);
        return this;
    }

    /**
     * Add a URL link
     *
     * @return This
     */
    public PageHolderWithLinks addLink(URLLink link) {
        links.add(link);
        return this;
    }

    /**
     * Adds a resource location of an entry to be linked
     *
     * @return This
     */
    public PageHolderWithLinks addLink(String resourceLocation) {
        addLink(new ResourceLocation(resourceLocation));
        return this;
    }

    @Override
    public boolean canSee(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack, GuiEntry guiEntry) {
        return page.canSee(book, category, entry, player, bookStack, guiEntry);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        page.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        int ll = guiLeft + guiBase.xSize - 5;
        int y = guiTop + 10;
        for (Link l : links) {
            fontRendererObj.drawStringWithShadow(l.getDisplayName(), ll, y, 0xFFFFFF);
            if (l.width == 0) {
                l.width = fontRendererObj.getStringWidth(l.getDisplayName());
            }
            y += 20;
        }
        page.drawExtras(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onInit(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack, GuiEntry guiEntry) {
        while (lateLinks.size() > 0) {
            ResourceLocation s = lateLinks.remove(0);
            EntryAbstract e = GuideBook.getLinkedEntry(s);
            if (e == null) {
                VampirismMod.log.w(GuideBook.TAG, "Failed to find linked entry %s", s);
            } else {
                addLink(e);
            }
        }
        page.onInit(book, category, entry, player, bookStack, guiEntry);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onLeftClicked(Book book, CategoryAbstract category, EntryAbstract entry, int mouseX, int mouseY, EntityPlayer player, GuiEntry guiEntry) {
        if (mouseX > guiEntry.guiLeft + guiEntry.xSize) {
            //Avoid double/triple execution per click
            long lastClock = System.currentTimeMillis() / 4;
            if (lastClock != lastLinkClick) {
                lastLinkClick = lastClock;
                for (int i = 0; i < links.size(); i++) {
                    if (GuiHelper.isMouseBetween(mouseX, mouseY, guiEntry.guiLeft + guiEntry.xSize, guiEntry.guiTop + 10 + 20 * i, links.get(i).width, 20)) {
                        links.get(i).onClicked(book, category, entry, player, guiEntry.bookStack, guiEntry.pageNumber);
                        return;
                    }
                }
            }

        }
        page.onLeftClicked(book, category, entry, mouseX, mouseY, player, guiEntry);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onRightClicked(Book book, CategoryAbstract category, EntryAbstract entry, int mouseX, int mouseY, EntityPlayer player, GuiEntry guiEntry) {
        page.onRightClicked(book, category, entry, mouseX, mouseY, player, guiEntry);
    }

    private static abstract class Link {
        public int width;

        public abstract String getDisplayName();

        @SideOnly(Side.CLIENT)
        public abstract void onClicked(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack, int page);
    }

    public static class URLLink extends Link {
        private final String name;
        private final URI link;

        public URLLink(String name, URI link) {
            this.name = name;
            this.link = link;
        }

        @Override
        public String getDisplayName() {
            return name;
        }

        @Override
        public void onClicked(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack, int page) {
            try {
                Class<?> oclass = Class.forName("java.awt.Desktop");
                Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, link);
            } catch (Throwable throwable1) {
                Throwable throwable = throwable1.getCause();
                VampirismMod.log.e(GuideBook.TAG, throwable, "Couldn\'t open link: {%s}", link);
                player.sendMessage(ForgeHooks.newChatWithLinks("Couldn\'t open link: " + link.toString()));
            }
        }
    }

    private class EntryLink extends Link {
        private final EntryAbstract linkedEntry;

        private EntryLink(EntryAbstract entry) {
            this.linkedEntry = entry;
        }

        @Override
        public String getDisplayName() {
            return linkedEntry.getLocalizedName();
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void onClicked(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack, int page) {
            openLinkedEntry(book, category, linkedEntry, player, bookStack, entry, page);
        }

        /**
         * Simply opens a gui screen with a GuiLinkedEntry. Not sure why the @SideOnly does not work, but this uses Class.forName to solve server side class not found issues
         */
        @SideOnly(Side.CLIENT)
        private void openLinkedEntry(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack, EntryAbstract from, int fromPage) {
//        GuiScreen screen = null;
//        try {
//            screen = Class.forName("de.teamlapen.vampirism.client.gui.GuiLinkedEntry").asSubclass(GuiScreen.class).getConstructor(Book.class, CategoryAbstract.class, EntryAbstract.class, EntityPlayer.class, ItemStack.class, EntryAbstract.class, int.class).newInstance(book, category, entry, player, bookStack, from, fromPage);
//        } catch (Exception e) {
//            Logger.e("PHWithLink", e, "Failed to create a GuiLinkedEntry. This should not be impossible. But maybe the mod author has messed something up.");
//        }
            GuiScreen screen = new GuiLinkedEntry(book, category, entry, player, bookStack, from, fromPage);
            Minecraft.getMinecraft().displayGuiScreen(screen);
        }
    }


}
