package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.GuideAPI;
import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.PageHelper;
import amerifrance.guideapi.category.CategoryItemStack;
import amerifrance.guideapi.page.PageText;
import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class GuideBook {

    public final static String TAG = "GuideBook";
    private final static String IMAGE_BASE = "vampirismguide:textures/images/";
    public static ItemStack bookStack;
    private static Book guideBook;
    private static Map<ResourceLocation, EntryAbstract> links = Maps.newHashMap();

    static void initBook() {
        guideBook = new Book();
        guideBook.setTitle("guide.vampirism.title");
        guideBook.setDisplayName("guide.vampirism.name");
        guideBook.setWelcomeMessage("guide.vampirism.welcome");
        guideBook.setAuthor("Maxanier");
        guideBook.setColor(Color.getHSBColor(0.5f, 0.2f, 0.5f));
        guideBook.setRegistryName(REFERENCE.MODID, "guide");
        guideBook.setOutlineTexture(new ResourceLocation("vampirismguide", "textures/gui/book_violet_border.png"));
        GameRegistry.register(guideBook);
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            GuideAPI.setModel(guideBook);
        }
        bookStack = GuideAPI.getStackFromBook(guideBook);
        VampirismMod.log.i(TAG, "Registered Guide Book");

    }

    static void buildCategories() {
        VampirismMod.log.d(TAG, "Building categories");
        long start = System.currentTimeMillis();
        guideBook.addCategory(new CategoryItemStack(buildOverview(), "guide.vampirism.overview.title", new ItemStack(ModItems.vampireFang)));
        guideBook.addCategory(new CategoryItemStack(buildVampire(), "guide.vampirism.vampire.title", new ItemStack(ModItems.bloodBottle, 1, ItemBloodBottle.AMOUNT)));
        guideBook.addCategory(new CategoryItemStack(buildHunter(), "guide.vampirism.hunter.title", new ItemStack(ModItems.humanHeart)));
        guideBook.addCategory(new CategoryItemStack(buildWorld(), "guide.vampirism.world.title", new ItemStack(Blocks.GRASS)));
        VampirismMod.log.d(TAG, "Finished building categories after %d ms", System.currentTimeMillis() - start);
    }

    /**
     * @return The entry registered with the given location. Can be null
     */
    public static
    @Nullable
    EntryAbstract getLinkedEntry(ResourceLocation location) {
        return links.get(location);
    }

    private static Map<ResourceLocation, EntryAbstract> buildOverview() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.overview.";

        List<IPage> introPages = new ArrayList<>();
        introPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "intro.text"), 340));
        PageHelper.setPagesToUnicode(introPages);
        entries.put(new ResourceLocation(base + "intro"), new EntryText(introPages, UtilLib.translate(base + "intro")));

        List<IPage> gettingStartedPages = new ArrayList<>();
        IPage p = new PageText(UtilLib.translate(base + "gettingStarted.text"));
        p = new PageHolderWithLinks(p).addLink("guide.vampirism.vampire.gettingStarted").addLink("guide.vampirism.hunter.gettingStarted");
        gettingStartedPages.add(p);
        PageHelper.setPagesToUnicode(gettingStartedPages);
        entries.put(new ResourceLocation(base + "gettingStarted"), new EntryText(gettingStartedPages, UtilLib.translate(base + "gettingStarted")));

        List<IPage> configPages = new ArrayList<>();
        configPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "config.text"), 340));
        configPages.addAll(PageHelper.pagesForLongText(GuideHelper.append(base + "config.general.text", base + "config.general.examples"), 340));
        configPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "config.balance.text"), 340));
        PageHelper.setPagesToUnicode(configPages);
        entries.put(new ResourceLocation(base + "config"), new EntryText(configPages, UtilLib.translate(base + "config")));

        List<IPage> troublePages = new ArrayList<>();
        troublePages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "trouble.text"), 340));
        PageHelper.setPagesToUnicode(troublePages);
        GuideHelper.addLinks(troublePages, new PageHolderWithLinks.URLLink(UtilLib.translate(base + "trouble"), URI.create("https://github.com/TeamLapen/Vampirism/wiki/Troubleshooting")));
        entries.put(new ResourceLocation(base + "trouble"), new EntryText(troublePages, UtilLib.translate(base + "trouble")));

        List<IPage> devPages = new ArrayList<>();
        devPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "dev.text"), 340));
        PageHelper.setPagesToUnicode(devPages);
        entries.put(new ResourceLocation(base + "dev"), new EntryText(devPages, UtilLib.translate(base + "dev")));

        List<IPage> supportPages = new ArrayList<>();
        supportPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "support.text"), 300));
        PageHolderWithLinks.URLLink linkPatreon = new PageHolderWithLinks.URLLink("Patreon", URI.create(REFERENCE.PATREON_LINK));
        PageHolderWithLinks.URLLink linkCurseForge = new PageHolderWithLinks.URLLink("CurseForge", URI.create(REFERENCE.CURSEFORGE_LINK));

        GuideHelper.addLinks(supportPages, linkPatreon, linkCurseForge, new ResourceLocation(base + "dev"));
        PageHelper.setPagesToUnicode(supportPages);
        entries.put(new ResourceLocation(base + "support"), new EntryText(supportPages, UtilLib.translate(base + "support")));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildVampire() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.vampire.";
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildHunter() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.hunter.";
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildWorld() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.world.";
        return entries;
    }
}
