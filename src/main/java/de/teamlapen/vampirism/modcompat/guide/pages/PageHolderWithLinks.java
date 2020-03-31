package de.teamlapen.vampirism.modcompat.guide.pages;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.gui.BaseScreen;
import amerifrance.guideapi.gui.EntryScreen;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.modcompat.guide.GuideBook;
import de.teamlapen.vampirism.modcompat.guide.client.LinkedEntryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.List;


public class PageHolderWithLinks implements IPage {
    private static final Logger LOGGER = LogManager.getLogger();


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
    public boolean canSee(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, EntryScreen guiEntry) {
        return page.canSee(book, category, entry, player, bookStack, guiEntry);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        page.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onInit(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, EntryScreen guiEntry) {
        while (lateLinks.size() > 0) {
            ResourceLocation s = lateLinks.remove(0);
            EntryAbstract e = GuideBook.getLinkedEntry(s);
            if (e == null) {
                LOGGER.warn("Failed to find linked entry {}", s);
            } else {
                addLink(e);
            }
        }
        page.onInit(book, category, entry, player, bookStack, guiEntry);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void onLeftClicked(Book book, CategoryAbstract category, EntryAbstract entry, double mouseX, double mouseY, PlayerEntity player, EntryScreen guiEntry) {
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRightClicked(Book book, CategoryAbstract category, EntryAbstract entry, double mouseX, double mouseY, PlayerEntity player, EntryScreen guiEntry) {
        page.onRightClicked(book, category, entry, mouseX, mouseY, player, guiEntry);
    }

    private static abstract class Link {
        public int width;

        public abstract String getDisplayName();

        @OnlyIn(Dist.CLIENT)
        public abstract void onClicked(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, int page);
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
        public void onClicked(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, int page) {
            try {
                Class<?> oclass = Class.forName("java.awt.Desktop");
                Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, link);
            } catch (Throwable throwable1) {
                Throwable throwable = throwable1.getCause();
                LOGGER.error("Couldn't open link: {}", link);
                LOGGER.catching(throwable);
                player.sendMessage(ForgeHooks.newChatWithLinks("Couldn't open link: " + link.toString()));
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

        @OnlyIn(Dist.CLIENT)
        @Override
        public void onClicked(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, int page) {
            openLinkedEntry(book, category, linkedEntry, player, bookStack, entry, page);
        }

        /**
         * Simply opens a gui screen with a GuiLinkedEntry. Not sure why the @SideOnly does not work, but this uses Class.forName to solve server side class not found issues
         */
        @OnlyIn(Dist.CLIENT)
        private void openLinkedEntry(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, EntryAbstract from, int fromPage) {
//        GuiScreen screen = null;
//        try {
//            screen = Class.forName("de.teamlapen.vampirism.client.gui.GuiLinkedEntry").asSubclass(GuiScreen.class).getConstructor(Book.class, CategoryAbstract.class, EntryAbstract.class, EntityPlayer.class, ItemStack.class, EntryAbstract.class, int.class).newInstance(book, category, entry, player, bookStack, from, fromPage);
//        } catch (Exception e) {
//            Logger.e("PHWithLink", e, "Failed to create a GuiLinkedEntry. This should not be impossible. But maybe the mod author has messed something up.");
//        }
            BaseScreen screen = new LinkedEntryScreen(book, category, entry, player, bookStack, from, fromPage);
            Minecraft.getInstance().displayGuiScreen(screen);
        }
    }


}
