package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.GuideAPI;
import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.PageHelper;
import amerifrance.guideapi.category.CategoryItemStack;
import amerifrance.guideapi.page.PageImage;
import amerifrance.guideapi.page.PageText;
import amerifrance.guideapi.page.PageTextImage;
import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BlockAltarPillar;
import de.teamlapen.vampirism.blocks.BlockGarlicBeacon;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.items.ItemCrossbowArrow;
import de.teamlapen.vampirism.items.ItemInjection;
import de.teamlapen.vampirism.modcompat.guide.pages.PageHolderWithLinks;
import de.teamlapen.vampirism.modcompat.guide.pages.PageTable;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.teamlapen.vampirism.modcompat.guide.GuideHelper.RECIPE_TYPE.*;
import static de.teamlapen.vampirism.modcompat.guide.GuideHelper.addArmorWithTier;
import static de.teamlapen.vampirism.modcompat.guide.GuideHelper.addItemWithTier;


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
        guideBook.setSpawnWithBook(true);
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
        guideBook.addCategory(new CategoryItemStack(buildCreatures(), "guide.vampirism.entity.title", new ItemStack(Items.SKULL)));
        guideBook.addCategory(new CategoryItemStack(buildWorld(), "guide.vampirism.world.title", new ItemStack(ModBlocks.cursedEarth)));
        guideBook.addCategory(new CategoryItemStack(buildItems(), "guide.vampirism.items.title", new ItemStack(Items.APPLE)));
        guideBook.addCategory(new CategoryItemStack(buildBlocks(), "guide.vampirism.blocks.title", new ItemStack(ModBlocks.castleBlock)));
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
        PageHolderWithLinks.URLLink helpLink = new PageHolderWithLinks.URLLink("How to help", URI.create("https://github.com/TeamLapen/Vampirism/wiki#how-you-can-help"));
        devPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(UtilLib.translate(base + "dev.text"), 340), helpLink));
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

        List<IPage> gettingStarted = new ArrayList<>();
        gettingStarted.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "gettingStarted.become"), 300));
        gettingStarted.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "gettingStarted.asVampire"), 300));
        gettingStarted.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "gettingStarted.blood", Keyboard.getKeyName(ModKeys.getKeyCode(ModKeys.KEY.SUCK))), 300));
        gettingStarted.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "gettingStarted.level") + "\n" + UtilLib.translate(base + "gettingStarted.level2"), 300));

        entries.put(new ResourceLocation(base + "gettingStarted"), new EntryText(gettingStarted, UtilLib.translate(base + "gettingStarted")));

        List<IPage> bloodPages = new ArrayList<>();
        bloodPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "blood.text", UtilLib.translate(ModItems.bloodBottle.getUnlocalizedName() + ".name"), UtilLib.translate(Items.GLASS_BOTTLE.getUnlocalizedName() + ".name")), 250));
        bloodPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "blood.storage", ModBlocks.bloodContainer.getLocalizedName()), 250), new ResourceLocation("guide.vampirism.blocks.bloodContainer")));
        bloodPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(UtilLib.translate(base + "blood.biteableCreatures")), new PageHolderWithLinks.URLLink("Biteable Creatures", URI.create("https://github.com/TeamLapen/Vampirism/wiki/Biteable-Creatures"))));
        entries.put(new ResourceLocation(base + "blood"), new EntryText(bloodPages, UtilLib.translate(base + "blood")));

        VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "leveling.intro"), 300));
        String altarOfInspiration = "§l" + ModBlocks.altarInspiration.getLocalizedName() + "§r\n§o" + UtilLib.translate(base + "leveling.inspiration.reach") + "§r\n";
        altarOfInspiration += UtilLib.translate(base + "leveling.inspiration.text") + "\n";
        altarOfInspiration += UtilLib.translateFormatted(base + "leveling.inspiration.requirements", levelingConf.getRequiredBloodForAltarInspiration(2), levelingConf.getRequiredBloodForAltarInspiration(3), levelingConf.getRequiredBloodForAltarInspiration(4));
        levelingPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(altarOfInspiration, 250), new ResourceLocation("guide.vampirism.blocks.altarInspiration")));

        String altarOfInfusion = "§l" + ModBlocks.altarInfusion.getLocalizedName() + "§r\n§o" + UtilLib.translate(base + "leveling.infusion.reach") + "§r\n";
        altarOfInfusion += UtilLib.translateFormatted(base + "leveling.infusion.intro", ModBlocks.altarInfusion.getLocalizedName(), ModBlocks.altarPillar.getLocalizedName(), ModBlocks.altarTip.getLocalizedName());
        levelingPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(altarOfInfusion, 300), new ResourceLocation("guide.vampirism.blocks.altarInfusion")));
        String blocks = "";
        for (BlockAltarPillar.EnumPillarType t : BlockAltarPillar.EnumPillarType.values()) {
            if (t == BlockAltarPillar.EnumPillarType.NONE) continue;
            blocks += t.fillerBlock.getLocalizedName() + "(" + t.getValue() + "),";
        }
        levelingPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "leveling.infusion.structure", blocks), 250));
        String items = UtilLib.translate(ModItems.humanHeart.getUnlocalizedName() + ".name") + ", " + UtilLib.translate(ModItems.pureBlood.getUnlocalizedName() + ".name") + ", " + UtilLib.translate(ModItems.vampireBook.getUnlocalizedName() + ".name");
        levelingPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "leveling.infusion.items", items), 300), new ResourceLocation("guide.vampirism.items.humanHeart"), new ResourceLocation("guide.vampirism.items.pureBlood"), new ResourceLocation("guide.vampirism.items.vampireBook")));
        PageTable.Builder requirementsBuilder = new PageTable.Builder(5);
        requirementsBuilder.addUnlocLine("text.vampirism.level", base + "leveling.infusion.req.structurePoints", ModItems.pureBlood.getUnlocalizedName() + ".name", base + "leveling.infusion.req.heart", base + "leveling.infusion.req.book");
        requirementsBuilder.addLine("5", "8", "0", "5", "1");
        requirementsBuilder.addLine("6", "17", "1 Purity(1)", "5", "1");
        requirementsBuilder.addLine("7", "21", "1 Purity(1)", "10", "1");
        requirementsBuilder.addLine("8", "26", "1 Purity(2)", "10", "1");
        requirementsBuilder.addLine("9", "35", "1 Purity(2)", "10", "1");
        requirementsBuilder.addLine("10", "44", "1 Purity(3)", "15", "1");
        requirementsBuilder.addLine("11", "54", "1 Purity(3)", "15", "1");
        requirementsBuilder.addLine("12", "63", "1 Purity(4)", "20", "1");
        requirementsBuilder.addLine("13", "72", "2 Purity(4)", "20", "1");
        requirementsBuilder.addLine("14", "92", "2 Purity(5)", "25", "1");
        requirementsBuilder.setHeadline(UtilLib.translate(base + "leveling.infusion.req"));
        PageHolderWithLinks requirementTable = new PageHolderWithLinks(requirementsBuilder.build());
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.humanHeart"));
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.vampireBook"));
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.pureBlood"));
        levelingPages.add(requirementTable);

        levelingPages.add(new PageTextImage(UtilLib.translate(base + "leveling.infusion.image1"), new ResourceLocation(IMAGE_BASE + "infusion1.png"), false));
        levelingPages.add(new PageTextImage(UtilLib.translate(base + "leveling.infusion.image2"), new ResourceLocation(IMAGE_BASE + "infusion2.png"), false));
        levelingPages.add(new PageTextImage(UtilLib.translate(base + "leveling.infusion.image3"), new ResourceLocation(IMAGE_BASE + "infusion3.png"), false));
        levelingPages.add(new PageTextImage(UtilLib.translate(base + "leveling.infusion.image4"), new ResourceLocation(IMAGE_BASE + "infusion4.png"), false));
        levelingPages.add(new PageTextImage(UtilLib.translate(base + "leveling.infusion.image5"), new ResourceLocation(IMAGE_BASE + "infusion5.png"), false));

        entries.put(new ResourceLocation(base + "leveling"), new EntryText(levelingPages, base + "leveling"));


        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "skills.text", Keyboard.getKeyName(ModKeys.getKeyCode(ModKeys.KEY.SKILL))), 300));
        skillPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "skills.actions", Keyboard.getKeyName(ModKeys.getKeyCode(ModKeys.KEY.ACTION))), 300));
        skillPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "skills.actions2"), 300));

        entries.put(new ResourceLocation(base + "skills"), new EntryText(skillPages, base + "skills"));

        List<IPage> unvampirePages = new ArrayList<>();
        unvampirePages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "unvampire.text", ModBlocks.churchAltar.getLocalizedName()), 300));
        entries.put(new ResourceLocation(base + "unvampire"), new EntryText(unvampirePages, base + "unvampire"));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildHunter() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.hunter.";

        List<IPage> gettingStarted = new ArrayList<>();
        String become = UtilLib.translateFormatted(base + "gettingStarted.become", UtilLib.translate("entity." + ModEntities.HUNTER_TRAINER + ".name"), new ItemStack(ModItems.injection, 1, ItemInjection.META_GARLIC).getDisplayName());
        gettingStarted.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(become, 300), new ResourceLocation("guide.vampirism.items.injection")));
        gettingStarted.add(new PageImage(new ResourceLocation(IMAGE_BASE + "hunterTrainer.png")));
        gettingStarted.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "gettingStarted.asHunter"), 300));
        entries.put(new ResourceLocation(base + "gettingStarted"), new EntryText(gettingStarted, base + "gettingStarted"));

        HunterLevelingConf levelingConf = HunterLevelingConf.instance();
        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "leveling.intro"), 300));
        String train1 = "§l" + UtilLib.translateFormatted(base + "leveling.toReach", "2-4") + "§r\n";
        train1 += UtilLib.translateFormatted(base + "leveling.train1.text", levelingConf.getVampireBloodCountForBasicHunter(2), levelingConf.getVampireBloodCountForBasicHunter(3), levelingConf.getVampireBloodCountForBasicHunter(4));
        levelingPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(train1, 250), new ResourceLocation("guide.vampirism.items.stake"), new ResourceLocation("guide.vampirism.items.vampireBloodBottle")));

        String train2 = "§l" + UtilLib.translateFormatted(base + "leveling.toReach", "5+") + "§r\n";
        train2 += UtilLib.translateFormatted(base + "leveling.train2.text", ModBlocks.hunterTable.getLocalizedName());
        levelingPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(train2, 250), new ResourceLocation("guide.vampirism.blocks.hunterTable")));
        PageTable.Builder builder = new PageTable.Builder(4);
        builder.addUnlocLine("text.vampirism.level", base + "leveling.train2.fang", ModItems.pureBlood.getLocalizedName(), ModItems.vampireBook.getLocalizedName());
        for (int i = levelingConf.TABLE_MIN_LEVEL; i <= levelingConf.TABLE_MAX_LEVEL; i++) {
            int[] req = levelingConf.getItemRequirementsForTable(i);
            String pure = "";
            if (req[1] > 0) {
                pure = "" + req[1] + " Purity(" + (req[2] + 1) + ")";
            }
            builder.addLine(i, req[0], pure, req[3]);
        }

        builder.setHeadline(base + "leveling.train2.req");
        PageHolderWithLinks requirementsTable = new PageHolderWithLinks(builder.build());
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.vampireFang"));
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.pureBlood"));
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.vampireBook"));
        levelingPages.add(requirementsTable);

        entries.put(new ResourceLocation(base + "leveling"), new EntryText(levelingPages, base + "leveling"));

        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "skills.intro", Keyboard.getKeyName(ModKeys.getKeyCode(ModKeys.KEY.SKILL))), 250));
        String disguise = String.format("§l%s§r\n", UtilLib.translate(HunterActions.disguiseAction.getUnlocalizedName()));
        disguise += UtilLib.translateFormatted(base + "skills.disguise.text", Keyboard.getKeyName(ModKeys.getKeyCode(ModKeys.KEY.ACTION)));
        skillPages.addAll(PageHelper.pagesForLongText(disguise, 250));
        String bloodPotion = String.format("§l%s§r\n", ModBlocks.bloodPotionTable.getLocalizedName());
        bloodPotion += UtilLib.translateFormatted(base + "skills.bloodPotion.text", Keyboard.getKeyName(ModKeys.getKeyCode(ModKeys.KEY.BLOOD_POTION)));
        skillPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(bloodPotion, 250), new ResourceLocation("guide.vampirism.blocks.bloodPotionTable")));
        String weaponTable = String.format("§l%s§r\n", ModBlocks.weaponTable.getLocalizedName());
        weaponTable += UtilLib.translate(base + "skills.weaponTable.text");
        skillPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(weaponTable, 250), new ResourceLocation("guide.vampirism.blocks.weaponTable")));
        entries.put(new ResourceLocation(base + "skills"), new EntryText(skillPages, base + "skills"));

        List<IPage> vampSlayerPages = new ArrayList<>();
        vampSlayerPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "vamp_slayer.intro")));
        String garlic = String.format("§l%s§r\n", ModItems.itemGarlic.getLocalizedName());
        garlic += UtilLib.translate(base + "vamp_slayer.garlic") + "\n" + UtilLib.translate(base + "vamp_slayer.garlic2") + "\n" + UtilLib.translate(base + "vamp_slayer.garlic.diffusor");
        vampSlayerPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(garlic, 250), new ResourceLocation("guide.vampirism.blocks.garlicBeacon")));
        String holyWater = String.format("§l%s§r\n", ModItems.holyWaterBottle.getLocalizedName());
        holyWater += UtilLib.translate(base + "vamp_slayer.holy_water");
        vampSlayerPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(holyWater, 250), new ResourceLocation("guide.vampirism.items.holy_water_bottle")));
        String fire = String.format("§l%s§r\n", Blocks.FIRE.getLocalizedName());
        fire += UtilLib.translate(base + "vamp_slayer.fire");
        vampSlayerPages.addAll(GuideHelper.addLinks(PageHelper.pagesForLongText(fire, 250), new ResourceLocation("guide.vampirism.items.itemAlchemicalFire"), new ResourceLocation("guide.vampirism.items.crossbowArrow")));
        entries.put(new ResourceLocation(base + "vamp_slayer"), new EntryText(vampSlayerPages, base + "vamp_slayer"));

        List<IPage> unHunterPages = new ArrayList<>();
        unHunterPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "unhunter.text", new ItemStack(ModItems.injection, 1, ItemInjection.META_SANGUINARE).getDisplayName()), 250));
        entries.put(new ResourceLocation(base + "unhunter"), new EntryText(unHunterPages, base + "unhunter"));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildCreatures() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.entity.";

        ArrayList<IPage> generalPages = new ArrayList<>();
        generalPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "general.text"), 250));
        entries.put(new ResourceLocation(base + "general"), new EntryText(generalPages, base + "general"));

        ArrayList<IPage> hunterPages = new ArrayList<>();
        hunterPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "hunter.png")));
        hunterPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "hunter.text", ModItems.humanHeart.getLocalizedName())));
        entries.put(new ResourceLocation(base + "hunter"), new EntryText(hunterPages, "entity." + ModEntities.BASIC_HUNTER_NAME + ".name"));

        ArrayList<IPage> vampirePages = new ArrayList<>();
        vampirePages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "vampire.png")));
        vampirePages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "vampire.text", ModItems.vampireFang.getLocalizedName(), ModItems.vampireBlood.getLocalizedName(), ModItems.stake.getLocalizedName())));
        entries.put(new ResourceLocation(base + "vampire"), new EntryText(vampirePages, "entity." + ModEntities.BASIC_VAMPIRE_NAME + ".name"));

        ArrayList<IPage> advancedHunterPages = new ArrayList<>();
        advancedHunterPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "advancedHunter.png")));
        advancedHunterPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "advancedHunter.text")));
        entries.put(new ResourceLocation(base + "advancedHunter"), new EntryText(advancedHunterPages, "entity." + ModEntities.ADVANCED_HUNTER + ".name"));

        ArrayList<IPage> advancedVampirePages = new ArrayList<>();
        advancedVampirePages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "advancedVampire.png")));
        advancedVampirePages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "advancedVampire.text", ModItems.bloodBottle.getLocalizedName(), ModItems.vampireBlood.getLocalizedName())));
        entries.put(new ResourceLocation(base + "advancedVampire"), new EntryText(advancedVampirePages, "entity." + ModEntities.ADVANCED_VAMPIRE + ".name"));

        ArrayList<IPage> vampireBaronPages = new ArrayList<>();
        vampireBaronPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "vampireBaron.png")));
        vampireBaronPages.addAll(PageHelper.pagesForLongText(UtilLib.translateFormatted(base + "vampireBaron.text", ModItems.pureBlood.getLocalizedName())));
        GuideHelper.addLinks(vampireBaronPages, new ResourceLocation("guide.vampirism.world.vampireForest"));
        entries.put(new ResourceLocation(base + "vampireBaron"), new EntryText(vampireBaronPages, "entity." + ModEntities.VAMPIRE_BARON + ".name"));

        ArrayList<IPage> minionPages = new ArrayList<>();
        minionPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "minion.png")));
        minionPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "minion.text")));
        entries.put(new ResourceLocation(base + "minion"), new EntryText(minionPages, "entity." + ModEntities.VAMPIRE_MINION_SAVEABLE_NAME + ".name"));

        ArrayList<IPage> ghostPages = new ArrayList<>();
        ghostPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "ghost.png")));
        ghostPages.addAll(PageHelper.pagesForLongText(UtilLib.translate(base + "ghost.text")));
        entries.put(new ResourceLocation(base + "ghost"), new EntryText(ghostPages, "entity." + ModEntities.GHOST_NAME + ".name"));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildWorld() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.world.";

        List<IPage> vampireForestPages = new ArrayList<>();
        vampireForestPages.addAll(PageHelper.pagesForLongText(base + "vampireForest.text", 250));
        entries.put(new ResourceLocation(base + "vampireForest"), new EntryText(vampireForestPages, base + "vampireForest"));

        List<IPage> wipPages = new ArrayList<>();
        wipPages.addAll(PageHelper.pagesForLongText(base + "wip.text", 250));
        entries.put(new ResourceLocation(base + "wip"), new EntryText(wipPages, base + "wip"));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildItems() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.items.";
        //General
        new ItemInfoBuilder(ModItems.vampireFang).build(entries);
        new ItemInfoBuilder(ModItems.humanHeart).build(entries);
        new ItemInfoBuilder(ModItems.pureBlood).setFormats(UtilLib.translate("entity." + ModEntities.VAMPIRE_BARON + ".name")).build(entries);
        new ItemInfoBuilder(ModItems.vampireBlood).setFormats(UtilLib.translate("entity." + ModEntities.BASIC_VAMPIRE_NAME + ".name"), ModItems.stake.getLocalizedName(), UtilLib.translate("entity." + ModEntities.ADVANCED_VAMPIRE + ".name")).build(entries);
        new ItemInfoBuilder(ModItems.vampireBook).build(entries);
        //Vampire
        new ItemInfoBuilder(new ItemStack(ModItems.bloodBottle, 1, ItemBloodBottle.AMOUNT), false).build(entries);
        //Hunter
        new ItemInfoBuilder(ModItems.injection).craftableStacks(new ItemStack(ModItems.injection, 1, 0), WORKBENCH, new ItemStack(ModItems.injection, 1, ItemInjection.META_GARLIC), WORKBENCH, new ItemStack(ModItems.injection, 1, ItemInjection.META_SANGUINARE), WORKBENCH).build(entries);
        new ItemInfoBuilder(ModItems.hunterIntel).setLinks(new ResourceLocation("guide.vampirism.blocks.hunterTable")).setFormats(ModBlocks.hunterTable.getLocalizedName()).build(entries);
        new ItemInfoBuilder(ModItems.itemGarlic).build(entries);
        new ItemInfoBuilder(ModItems.purifiedGarlic).setFormats(ModBlocks.garlicBeacon.getLocalizedName()).setLinks("guide.vampirism.blocks.garlicBeacon").craftable(ALCHEMICAL_CAULDRON).build(entries);
        new ItemInfoBuilder(ModItems.pitchfork).craftable(WEAPON_TABLE).build(entries);
        new ItemInfoBuilder(ModItems.stake).setFormats(((int) (Balance.hps.INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC * 100)) + "%").craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModItems.basicCrossbow).setFormats(ModItems.crossbowArrow.getLocalizedName(), ModItems.techCrossbowAmmoPackage.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.items.crossbowArrow")).craftableStacks(ModItems.basicCrossbow, WEAPON_TABLE, ModItems.basicDoubleCrossbow, WEAPON_TABLE, ModItems.enhancedCrossbow, WEAPON_TABLE, ModItems.enhancedDoubleCrossbow, WEAPON_TABLE, ModItems.basicTechCrossbow, WEAPON_TABLE, ModItems.techCrossbowAmmoPackage, WEAPON_TABLE).setName("crossbows").customName().build(entries);
        new ItemInfoBuilder(ModItems.crossbowArrow).craftableStacks(ModItems.crossbowArrow.getStack(ItemCrossbowArrow.EnumArrowType.NORMAL), WORKBENCH, ModItems.crossbowArrow.getStack(ItemCrossbowArrow.EnumArrowType.VAMPIRE_KILLER), WEAPON_TABLE, ModItems.crossbowArrow.getStack(ItemCrossbowArrow.EnumArrowType.SPITFIRE), WEAPON_TABLE).build(entries);
        new ItemInfoBuilder(ModItems.holyWaterBottle).setLinks(new ResourceLocation("guide.vampirism.hunter.vamp_slayer"), new ResourceLocation("guide.vampirism.items.holy_salt")).setFormats(ModItems.holySaltWater.getLocalizedName(), ModItems.holySaltWater.getLocalizedName(), ModItems.holySalt.getLocalizedName()).craftableStacks(ModItems.holySaltWater, WORKBENCH).build(entries);
        new ItemInfoBuilder(ModItems.holySalt).setLinks(new ResourceLocation("guide.vampirism.items.holy_water_bottle")).setFormats(ModItems.pureSalt.getLocalizedName(), ModItems.pureSalt.getLocalizedName(), ModBlocks.alchemicalCauldron.getLocalizedName()).craftableStacks(ModItems.pureSalt, ALCHEMICAL_CAULDRON).build(entries);
        new ItemInfoBuilder(ModItems.itemAlchemicalFire).setLinks(new ResourceLocation("guide.vampirism.items.crossbowArrow")).craftable(ALCHEMICAL_CAULDRON).build(entries);

        addArmorWithTier(entries, "armorOfSwiftness", ModItems.armorOfSwiftness_helmet, ModItems.armorOfSwiftness_chest, ModItems.armorOfSwiftness_legs, ModItems.armorOfSwiftness_boots, WEAPON_TABLE);
        addArmorWithTier(entries, "hunterCoat", ModItems.hunterCoat_helmet, ModItems.hunterCoat_chest, ModItems.hunterCoat_legs, ModItems.hunterCoat_boots, WEAPON_TABLE);
        addArmorWithTier(entries, "obsidianArmor", ModItems.obsidianArmor_helmet, ModItems.obsidianArmor_chest, ModItems.obsidianArmor_legs, ModItems.obsidianArmor_boots, WEAPON_TABLE);
        addItemWithTier(entries, ModItems.hunterAxe, WEAPON_TABLE);
        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildBlocks() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.blocks.";
        //General
        new ItemInfoBuilder(ModBlocks.castleBlock).craftableStacks(new ItemStack(ModBlocks.castleBlock, 1, 0), WORKBENCH, new ItemStack(ModBlocks.castleBlock, 1, 1), WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.vampirismFlower).build(entries);
        //Vampire
        new ItemInfoBuilder(ModBlocks.bloodContainer).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.altarInspiration).setLinks(new ResourceLocation("guide.vampirism.vampire.leveling")).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.altarInfusion).setLinks(new ResourceLocation("guide.vampirism.vampire.leveling")).craftableStacks(new ItemStack(ModBlocks.altarInfusion), WORKBENCH, new ItemStack(ModBlocks.altarPillar), WORKBENCH, new ItemStack(ModBlocks.altarTip), WORKBENCH).build(entries);
        new ItemInfoBuilder(new ItemStack(ModItems.itemCoffin), true).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.churchAltar).build(entries);
        //Hunter
        new ItemInfoBuilder(new ItemStack(ModItems.itemMedChair), true).setFormats((new ItemStack(ModItems.injection, 1, 1)).getDisplayName(), (new ItemStack(ModItems.injection, 1, 2)).getDisplayName()).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.hunterTable).setFormats(ModItems.hunterIntel.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.hunter.leveling"), new ResourceLocation("guide.vampirism.items.hunterIntel")).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.weaponTable).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.bloodPotionTable).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.alchemicalCauldron).craftable(WORKBENCH).build(entries);
        new ItemInfoBuilder(ModBlocks.garlicBeacon).setFormats(ModItems.purifiedGarlic.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.items.itemGarlic"), new ResourceLocation("guide.vampirism.items.purified_garlic"), new ResourceLocation("guide.vampirism.items.holy_water_bottle")).craftableStacks(ModBlocks.garlicBeacon, WORKBENCH, new ItemStack(ModBlocks.garlicBeacon, 1, BlockGarlicBeacon.Type.IMPROVED.getId()), WORKBENCH, ModItems.garlicBeaconCore, ALCHEMICAL_CAULDRON, ModItems.garlicBeaconCoreImproved, ALCHEMICAL_CAULDRON).build(entries);

        links.putAll(entries);
        return entries;
    }




}
