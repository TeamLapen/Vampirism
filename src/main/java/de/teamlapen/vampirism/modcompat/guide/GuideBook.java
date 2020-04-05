package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.IGuideBook;
import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.BookBinder;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.category.CategoryItemStack;
import amerifrance.guideapi.page.PageImage;
import amerifrance.guideapi.page.PageText;
import amerifrance.guideapi.page.PageTextImage;
import com.google.common.collect.Maps;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.BloodBottleItem;
import de.teamlapen.vampirism.modcompat.guide.pages.PageHolderWithLinks;
import de.teamlapen.vampirism.modcompat.guide.pages.PageTable;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@amerifrance.guideapi.api.GuideBook
public class GuideBook implements IGuideBook {

    private static final Logger LOGGER = LogManager.getLogger();
    private final static String IMAGE_BASE = "vampirismguide:textures/images/";
    private static Book guideBook;
    private static Map<ResourceLocation, EntryAbstract> links = Maps.newHashMap();


    static void buildCategories(List<CategoryAbstract> categories) {
        LOGGER.debug("Building content");
        long start = System.currentTimeMillis();
        categories.add(new CategoryItemStack(buildOverview(), "guide.vampirism.overview.title", new ItemStack(ModItems.vampire_fang)));
        categories.add(new CategoryItemStack(buildVampire(), "guide.vampirism.vampire.title", BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT)));
        categories.add(new CategoryItemStack(buildHunter(), "guide.vampirism.hunter.title", new ItemStack(ModItems.human_heart)));
        categories.add(new CategoryItemStack(buildCreatures(), "guide.vampirism.entity.title", new ItemStack(Items.ZOMBIE_HEAD)));
        categories.add(new CategoryItemStack(buildWorld(), "guide.vampirism.world.title", new ItemStack(ModBlocks.cursed_earth)));
        categories.add(new CategoryItemStack(buildItems(), "guide.vampirism.items.title", new ItemStack(Items.APPLE)));
        categories.add(new CategoryItemStack(buildBlocks(), "guide.vampirism.blocks.title", new ItemStack(ModBlocks.castle_block_dark_brick)));
        LOGGER.debug("Built content in {} ms", System.currentTimeMillis() - start);
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
        introPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "intro.text")));
        entries.put(new ResourceLocation(base + "intro"), new EntryText(introPages, UtilLib.translate(base + "intro")));

        List<IPage> gettingStartedPages = new ArrayList<>();
        IPage p = new PageText(UtilLib.translate(base + "getting_started.text"));
        p = new PageHolderWithLinks(p).addLink("guide.vampirism.vampire.getting_started").addLink("guide.vampirism.hunter.getting_started");
        gettingStartedPages.add(p);
        entries.put(new ResourceLocation(base + "getting_started"), new EntryText(gettingStartedPages, UtilLib.translate(base + "getting_started")));

        List<IPage> configPages = new ArrayList<>();
        configPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "config.text")));
        configPages.addAll(GuideHelper.pagesForLongText(GuideHelper.append(base + "config.general.text", base + "config.general.examples")));
        configPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "config.balance.text")));
        entries.put(new ResourceLocation(base + "config"), new EntryText(configPages, UtilLib.translate(base + "config")));

        List<IPage> troublePages = new ArrayList<>();
        troublePages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "trouble.text")));
        GuideHelper.addLinks(troublePages, new PageHolderWithLinks.URLLink(UtilLib.translate(base + "trouble"), URI.create("https://github.com/TeamLapen/Vampirism/wiki/Troubleshooting")));
        entries.put(new ResourceLocation(base + "trouble"), new EntryText(troublePages, UtilLib.translate(base + "trouble")));

        List<IPage> devPages = new ArrayList<>();
        PageHolderWithLinks.URLLink helpLink = new PageHolderWithLinks.URLLink("How to help", URI.create("https://github.com/TeamLapen/Vampirism/wiki#how-you-can-help"));
        devPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(UtilLib.translate(base + "dev.text")), helpLink));
        entries.put(new ResourceLocation(base + "dev"), new EntryText(devPages, UtilLib.translate(base + "dev")));

        List<IPage> supportPages = new ArrayList<>();
        supportPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "support.text")));
        PageHolderWithLinks.URLLink linkPatreon = new PageHolderWithLinks.URLLink("Patreon", URI.create(REFERENCE.PATREON_LINK));
        PageHolderWithLinks.URLLink linkCurseForge = new PageHolderWithLinks.URLLink("CurseForge", URI.create(REFERENCE.CURSEFORGE_LINK));

        GuideHelper.addLinks(supportPages, linkPatreon, linkCurseForge, new ResourceLocation(base + "dev"));
        entries.put(new ResourceLocation(base + "support"), new EntryText(supportPages, UtilLib.translate(base + "support")));

        List<IPage> creditsPages = new ArrayList<>();
        String lang = VampLib.proxy.getActiveLanguage();
        String credits = "§lDeveloper:§r\nMaxanier\n\n§lInactive Developer:§r\nMistadon\nwildbill22\n\n§lTranslators:§r\n§b" + lang + "§r\n" + UtilLib.translate("text.vampirism.translators");
        creditsPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(credits)));
        entries.put(new ResourceLocation(base + "credits"), new EntryText(creditsPages, UtilLib.translate(base + "credits")));
        links.putAll(entries);
        return entries;
    }

    private static String loc(Block b) {
        return UtilLib.translate(b.getTranslationKey());
    }

    private static String loc(Item i) {
        return UtilLib.translate(i.getTranslationKey());
    }

    private static Map<ResourceLocation, EntryAbstract> buildVampire() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.vampire.";

        List<IPage> gettingStarted = new ArrayList<>();
        gettingStarted.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "getting_started.become")));
        gettingStarted.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "getting_started.as_vampire")));
        gettingStarted.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "getting_started.zombie")));
        gettingStarted.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "getting_started.blood", ModKeys.getKeyBinding(ModKeys.KEY.SUCK).getLocalizedName())));
        gettingStarted.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "getting_started.level") + "\n" + UtilLib.translate(base + "getting_started.level2")));

        entries.put(new ResourceLocation(base + "getting_started"), new EntryText(gettingStarted, UtilLib.translate(base + "getting_started")));

        List<IPage> bloodPages = new ArrayList<>();
        bloodPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "blood.text", UtilLib.translate(ModItems.blood_bottle.getTranslationKey() + ".name"), UtilLib.translate(Items.GLASS_BOTTLE.getTranslationKey() + ".name"))));
        bloodPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(UtilLib.translate(base + "blood.storage", UtilLib.translate(ModBlocks.blood_container.getTranslationKey()))), new ResourceLocation("guide.vampirism.blocks.blood_container")));
        bloodPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(UtilLib.translate(base + "blood.biteable_creatures")), new PageHolderWithLinks.URLLink("Biteable Creatures", URI.create("https://github.com/TeamLapen/Vampirism/wiki/Biteable-Creatures"))));
        entries.put(new ResourceLocation(base + "blood"), new EntryText(bloodPages, UtilLib.translate(base + "blood")));

        VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "leveling.intro")));
        String altarOfInspiration = "§l" + UtilLib.translate(ModBlocks.altar_inspiration.getTranslationKey()) + "§r\n§o" + UtilLib.translate(base + "leveling.inspiration.reach") + "§r\n";
        altarOfInspiration += UtilLib.translate(base + "leveling.inspiration.text") + "\n";
        altarOfInspiration += UtilLib.translate(base + "leveling.inspiration.requirements", levelingConf.getRequiredBloodForAltarInspiration(2), levelingConf.getRequiredBloodForAltarInspiration(3), levelingConf.getRequiredBloodForAltarInspiration(4));
        levelingPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(altarOfInspiration), new ResourceLocation("guide.vampirism.blocks.altar_inspiration")));

        String altarOfInfusion = "§l" + loc(ModBlocks.altar_infusion) + "§r\n§o" + UtilLib.translate(base + "leveling.infusion.reach") + "§r\n";
        altarOfInfusion += UtilLib.translate(base + "leveling.infusion.intro", loc(ModBlocks.altar_infusion), loc(ModBlocks.altar_pillar), loc(ModBlocks.altar_tip));
        levelingPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(altarOfInfusion), new ResourceLocation("guide.vampirism.blocks.altar_infusion")));
        StringBuilder blocks = new StringBuilder();
        for (AltarPillarBlock.EnumPillarType t : AltarPillarBlock.EnumPillarType.values()) {
            if (t == AltarPillarBlock.EnumPillarType.NONE) continue;
            blocks.append(UtilLib.translate(t.fillerBlock.getTranslationKey())).append("(").append(t.getValue()).append("),");
        }
        levelingPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "leveling.infusion.structure", blocks.toString())));
        String items = UtilLib.translate(ModItems.human_heart.getTranslationKey() + ".name") + ", " + UtilLib.translate(ModItems.pure_blood_0.getTranslationKey()) + ", " + UtilLib.translate(ModItems.vampire_book.getTranslationKey() + ".name");
        levelingPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(UtilLib.translate(base + "leveling.infusion.items", items)), new ResourceLocation("guide.vampirism.items.human_heart"), new ResourceLocation("guide.vampirism.items.pure_blood"), new ResourceLocation("guide.vampirism.items.vampire_book")));
        PageTable.Builder requirementsBuilder = new PageTable.Builder(5);
        requirementsBuilder.addUnlocLine("text.vampirism.level", base + "leveling.infusion.req.structure_points", ModItems.pure_blood_0.getTranslationKey(), base + "leveling.infusion.req.heart", base + "leveling.infusion.req.book");
        requirementsBuilder.addLine("5", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(5), "0", "5", "1");
        requirementsBuilder.addLine("6", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(6), "1 Purity(1)", "5", "1");
        requirementsBuilder.addLine("7", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(7), "1 Purity(1)", "10", "1");
        requirementsBuilder.addLine("8", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(8), "1 Purity(2)", "10", "1");
        requirementsBuilder.addLine("9", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(9), "1 Purity(2)", "10", "1");
        requirementsBuilder.addLine("10", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(10), "1 Purity(3)", "15", "1");
        requirementsBuilder.addLine("11", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(11), "1 Purity(3)", "15", "1");
        requirementsBuilder.addLine("12", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(12), "1 Purity(4)", "20", "1");
        requirementsBuilder.addLine("13", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(13), "2 Purity(4)", "20", "1");
        requirementsBuilder.addLine("14", VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(14), "2 Purity(5)", "25", "1");
        requirementsBuilder.setHeadline(UtilLib.translate(base + "leveling.infusion.req"));
        PageHolderWithLinks requirementTable = new PageHolderWithLinks(requirementsBuilder.build());
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.human_heart"));
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.vampire_book"));
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.pure_blood"));
        levelingPages.add(requirementTable);

        levelingPages.add(new PageTextImage(base + "leveling.infusion.image1", new ResourceLocation(IMAGE_BASE + "infusion1.png"), false));
        levelingPages.add(new PageTextImage(base + "leveling.infusion.image2", new ResourceLocation(IMAGE_BASE + "infusion2.png"), false));
        levelingPages.add(new PageTextImage(base + "leveling.infusion.image3", new ResourceLocation(IMAGE_BASE + "infusion3.png"), false));
        levelingPages.add(new PageTextImage(base + "leveling.infusion.image4", new ResourceLocation(IMAGE_BASE + "infusion4.png"), false));
        levelingPages.add(new PageTextImage(base + "leveling.infusion.image5", new ResourceLocation(IMAGE_BASE + "infusion5.png"), false));

        entries.put(new ResourceLocation(base + "leveling"), new EntryText(levelingPages, base + "leveling"));


        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "skills.text", ModKeys.getKeyBinding(ModKeys.KEY.SKILL).getLocalizedName())));
        skillPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "skills.actions", ModKeys.getKeyBinding(ModKeys.KEY.ACTION).getLocalizedName())));
        skillPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate("guide.vampirism.skills.bind_action")));
        skillPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "skills.actions2")));

        entries.put(new ResourceLocation(base + "skills"), new EntryText(skillPages, base + "skills"));

        List<IPage> unvampirePages = new ArrayList<>();
        unvampirePages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "unvampire.text", loc(ModBlocks.church_altar))));
        entries.put(new ResourceLocation(base + "unvampire"), new EntryText(unvampirePages, base + "unvampire"));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildHunter() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.hunter.";

        List<IPage> gettingStarted = new ArrayList<>();
        String become = UtilLib.translate(base + "getting_started.become", UtilLib.translate("entity." + ModEntities.hunter_trainer + ".name"), new ItemStack(ModItems.injection_garlic, 1).getDisplayName());
        gettingStarted.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(become), new ResourceLocation("guide.vampirism.items.injection")));
        gettingStarted.add(new PageImage(new ResourceLocation(IMAGE_BASE + "hunter_trainer.png")));
        gettingStarted.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "getting_started.as_hunter")));
        entries.put(new ResourceLocation(base + "getting_started"), new EntryText(gettingStarted, base + "getting_started"));

        HunterLevelingConf levelingConf = HunterLevelingConf.instance();
        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "leveling.intro")));
        String train1 = "§l" + UtilLib.translate(base + "leveling.to_reach", "2-4") + "§r\n";
        train1 += UtilLib.translate(base + "leveling.train1.text", levelingConf.getVampireBloodCountForBasicHunter(2), levelingConf.getVampireBloodCountForBasicHunter(3), levelingConf.getVampireBloodCountForBasicHunter(4));
        levelingPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(train1), new ResourceLocation("guide.vampirism.items.stake"), new ResourceLocation("guide.vampirism.items.vampire_blood_bottle")));

        String train2 = "§l" + UtilLib.translate(base + "leveling.to_reach", "5+") + "§r\n";
        train2 += UtilLib.translate(base + "leveling.train2.text", loc(ModBlocks.hunter_table));
        levelingPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(train2), new ResourceLocation("guide.vampirism.blocks.hunter_table")));
        PageTable.Builder builder = new PageTable.Builder(4);
        builder.addUnlocLine("text.vampirism.level", base + "leveling.train2.fang", loc(ModItems.pure_blood_0), loc(ModItems.vampire_book));
        for (int i = levelingConf.TABLE_MIN_LEVEL; i <= levelingConf.TABLE_MAX_LEVEL; i++) {
            int[] req = levelingConf.getItemRequirementsForTable(i);
            String pure = "";
            if (req[1] > 0) {
                pure = "" + req[1] + " Purity(" + (req[2] + 1) + ")";
            }
            builder.addLine(i, req[0], pure, req[3]);
        }

        builder.setHeadline(UtilLib.translate(base + "leveling.train2.req"));
        PageHolderWithLinks requirementsTable = new PageHolderWithLinks(builder.build());
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.vampire_fang"));
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.pure_blood"));
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.vampire_book"));
        levelingPages.add(requirementsTable);

        entries.put(new ResourceLocation(base + "leveling"), new EntryText(levelingPages, base + "leveling"));

        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "skills.intro", ModKeys.getKeyBinding(ModKeys.KEY.SKILL).getLocalizedName())));
        String disguise = String.format("§l%s§r\n", UtilLib.translate(HunterActions.disguise_hunter.getTranslationKey()));
        disguise += UtilLib.translate(base + "skills.disguise.text", ModKeys.getKeyBinding(ModKeys.KEY.ACTION).getLocalizedName());
        skillPages.addAll(GuideHelper.pagesForLongText(disguise));
        String bloodPotion = String.format("§l%s§r\n", loc(ModBlocks.blood_potion_table));
        bloodPotion += UtilLib.translate(base + "skills.blood_potion.text", ModKeys.getKeyBinding(ModKeys.KEY.BLOOD_POTION).getLocalizedName());
        skillPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(bloodPotion), new ResourceLocation("guide.vampirism.blocks.blood_potion_table")));
        String weaponTable = String.format("§l%s§r\n", loc(ModBlocks.weapon_table));
        weaponTable += UtilLib.translate(base + "skills.weapon_table.text");
        skillPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(weaponTable), new ResourceLocation("guide.vampirism.blocks.weapon_table")));
        entries.put(new ResourceLocation(base + "skills"), new EntryText(skillPages, base + "skills"));

        List<IPage> vampSlayerPages = new ArrayList<>();
        vampSlayerPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "vamp_slayer.intro")));
        String garlic = String.format("§l%s§r\n", loc(ModItems.item_garlic));
        garlic += UtilLib.translate(base + "vamp_slayer.garlic") + "\n" + UtilLib.translate(base + "vamp_slayer.garlic2") + "\n" + UtilLib.translate(base + "vamp_slayer.garlic.diffusor");
        vampSlayerPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(garlic), new ResourceLocation("guide.vampirism.blocks.garlic_beacon")));
        String holyWater = String.format("§l%s§r\n", loc(ModItems.holy_water_bottle_normal));
        holyWater += UtilLib.translate(base + "vamp_slayer.holy_water");
        vampSlayerPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(holyWater), new ResourceLocation("guide.vampirism.items.holy_water_bottle")));
        String fire = String.format("§l%s§r\n", loc(Blocks.FIRE));
        fire += UtilLib.translate(base + "vamp_slayer.fire");
        vampSlayerPages.addAll(GuideHelper.addLinks(GuideHelper.pagesForLongText(fire), new ResourceLocation("guide.vampirism.items.item_alchemical_fire"), new ResourceLocation("guide.vampirism.items.crossbow_arrow")));
        entries.put(new ResourceLocation(base + "vamp_slayer"), new EntryText(vampSlayerPages, base + "vamp_slayer"));

        List<IPage> unHunterPages = new ArrayList<>();
        unHunterPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "unhunter.text", new ItemStack(ModItems.injection_sanguinare).getDisplayName())));
        entries.put(new ResourceLocation(base + "unhunter"), new EntryText(unHunterPages, base + "unhunter"));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildCreatures() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.entity.";

        ArrayList<IPage> generalPages = new ArrayList<>(GuideHelper.pagesForLongText(UtilLib.translate(base + "general.text") + "\n" + UtilLib.translate(base + "general.text2")));
        entries.put(new ResourceLocation(base + "general"), new EntryText(generalPages, base + "general"));

        ArrayList<IPage> hunterPages = new ArrayList<>();
        hunterPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "hunter.png")));
        hunterPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "hunter.text", loc(ModItems.human_heart))));
        entries.put(new ResourceLocation(base + "hunter"), new EntryText(hunterPages, ModEntities.hunter.getTranslationKey()));

        ArrayList<IPage> vampirePages = new ArrayList<>();
        vampirePages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "vampire.png")));
        vampirePages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "vampire.text", loc(ModItems.vampire_fang), loc(ModItems.vampire_blood_bottle), loc(ModItems.stake))));
        entries.put(new ResourceLocation(base + "vampire"), new EntryText(vampirePages, ModEntities.vampire.getTranslationKey()));

        ArrayList<IPage> advancedHunterPages = new ArrayList<>();
        advancedHunterPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "advanced_hunter.png")));
        advancedHunterPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "advanced_hunter.text")));
        entries.put(new ResourceLocation(base + "advanced_hunter"), new EntryText(advancedHunterPages, ModEntities.advanced_hunter.getTranslationKey()));

        ArrayList<IPage> advancedVampirePages = new ArrayList<>();
        advancedVampirePages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "advanced_vampire.png")));
        advancedVampirePages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "advancedVampire.text", loc(ModItems.blood_bottle), loc(ModItems.vampire_blood_bottle))));
        entries.put(new ResourceLocation(base + "advanced_vampire"), new EntryText(advancedVampirePages, ModEntities.advanced_vampire.getTranslationKey()));

        ArrayList<IPage> vampireBaronPages = new ArrayList<>();
        vampireBaronPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "vampire_baron.png")));
        vampireBaronPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "vampire_baron.text", loc(ModItems.pure_blood_0))));
        GuideHelper.addLinks(vampireBaronPages, new ResourceLocation("guide.vampirism.world.vampire_forest"));
        entries.put(new ResourceLocation(base + "vampire_baron"), new EntryText(vampireBaronPages, ModEntities.vampire_baron.getTranslationKey()));

//        ArrayList<IPage> minionPages = new ArrayList<>();
//        minionPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "minion.png")));
//        minionPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "minion.text")));
//        entries.put(new ResourceLocation(base + "minion"), new EntryText(minionPages, "entity.vampirism." + ModEntities.VAMPIRE_MINION_SAVEABLE_NAME + ".name"));

        ArrayList<IPage> ghostPages = new ArrayList<>();
        ghostPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "ghost.png")));
        ghostPages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + "ghost.text")));
        entries.put(new ResourceLocation(base + "ghost"), new EntryText(ghostPages, "entity.vampirism." + ModEntities.ghost + ".name"));

        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildWorld() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.world.";

        List<IPage> vampireForestPages = new ArrayList<>(GuideHelper.pagesForLongText(UtilLib.translate(base + "vampire_forest.text")));
        entries.put(new ResourceLocation(base + "vampire_forest"), new EntryText(vampireForestPages, base + "vampire_forest"));

        List<IPage> villagePages = new ArrayList<>(GuideHelper.addLinks(GuideHelper.pagesForLongText(UtilLib.translate(base + "villages.text")), new ResourceLocation("guide.vampirism.blocks.totem_base"), new ResourceLocation("guide.vampirism.blocks.totem_top")));
        entries.put(new ResourceLocation(base + "villages"), new EntryText(villagePages, base + "villages"));



        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildItems() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.items.";
        //General
        new ItemInfoBuilder(ModItems.vampire_fang).build(entries);
        new ItemInfoBuilder(ModItems.human_heart).build(entries);
        new ItemInfoBuilder(ModItems.pure_blood_0).setFormats(UtilLib.translate(ModEntities.vampire_baron.getTranslationKey())).build(entries);
        new ItemInfoBuilder(ModItems.vampire_blood_bottle).setFormats(UtilLib.translate(ModEntities.vampire.getTranslationKey()), loc(ModItems.stake), UtilLib.translate(ModEntities.advanced_vampire.getTranslationKey())).build(entries);
        new ItemInfoBuilder(ModItems.vampire_book).build(entries);
        //Vampire
//        new ItemInfoBuilder(BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT), false).build(entries);
//        new ItemInfoBuilder(ModItems.blood_infused_iron_ingot).recipes("vampire/blood_infused_iron_ingot","vampire/blood_infused_enhanced_iron_ingot").build(entries);
//        addItemWithTier(ModItems.heart_seeker, WORKBENCH).setLinks(new ResourceLocation("guide.vampirism.blocks.blood_pedestal"), new ResourceLocation("guide.vampirism.items.blood_infused_iron_ingot")).build(entries);
//        addItemWithTier(ModItems.heart_striker, WORKBENCH).setLinks(new ResourceLocation("guide.vampirism.blocks.blood_pedestal"), new ResourceLocation("guide.vampirism.items.blood_infused_iron_ingot")).build(entries);
//        new ItemInfoBuilder(ModItems.vampire_cloak).craftableStacks(new ItemStack(ModItems.vampire_cloak, 1, 0), WORKBENCH, new ItemStack(ModItems.vampire_cloak, 1, 1), WORKBENCH, new ItemStack(ModItems.vampire_cloak, 1, 2), WORKBENCH, new ItemStack(ModItems.vampire_cloak, 1, 3), WORKBENCH, new ItemStack(ModItems.vampire_cloak, 1, 4), WORKBENCH).build(entries);
//
//        //Hunter
//        new ItemInfoBuilder(ModItems.injection).craftableStacks(new ItemStack(ModItems.injection, 1, 0), WORKBENCH, new ItemStack(ModItems.injection, 1, ItemInjection.META_GARLIC), WORKBENCH, new ItemStack(ModItems.injection, 1, ItemInjection.META_SANGUINARE), WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModItems.hunter_intel).setLinks(new ResourceLocation("guide.vampirism.blocks.hunter_table")).setFormats(ModBlocks.hunter_table.getLocalizedName()).build(entries);
//        new ItemInfoBuilder(ModItems.item_garlic).build(entries);
//        new ItemInfoBuilder(ModItems.purified_garlic).setFormats(ModBlocks.garlic_beacon.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.blocks.garlic_beacon")).craftable(ALCHEMICAL_CAULDRON).build(entries);
//        new ItemInfoBuilder(ModItems.pitchfork).craftable(WEAPON_TABLE).build(entries);
//        new ItemInfoBuilder(ModItems.stake).setFormats(((int) (Balance.hps.INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC * 100)) + "%").craftable(WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModItems.basic_crossbow).setFormats(ModItems.crossbow_arrow.getLocalizedName(), ModItems.tech_crossbow_ammo_package.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.items.crossbow_arrow")).craftableStacks(ModItems.basic_crossbow, WEAPON_TABLE, ModItems.basic_double_crossbow, WEAPON_TABLE, ModItems.enhanced_crossbow, WEAPON_TABLE, ModItems.enhanced_double_crossbow, WEAPON_TABLE, ModItems.basic_tech_crossbow, WEAPON_TABLE, ModItems.tech_crossbow_ammo_package, WEAPON_TABLE).setName("crossbows").customName().build(entries);
//        new ItemInfoBuilder(ModItems.crossbow_arrow).craftableStacks(ModItems.crossbow_arrow.getStack(ItemCrossbowArrow.EnumArrowType.NORMAL), WORKBENCH, ModItems.crossbow_arrow.getStack(ItemCrossbowArrow.EnumArrowType.VAMPIRE_KILLER), WEAPON_TABLE, ModItems.crossbow_arrow.getStack(ItemCrossbowArrow.EnumArrowType.SPITFIRE), WEAPON_TABLE).build(entries);
//        new ItemInfoBuilder(ModItems.holy_water_bottle).setLinks(new ResourceLocation("guide.vampirism.hunter.vamp_slayer"), new ResourceLocation("guide.vampirism.items.holy_salt")).setFormats(ModItems.holy_salt_water.getLocalizedName(), ModItems.holy_salt_water.getLocalizedName(), ModItems.holy_salt.getLocalizedName()).craftableStacks(ModItems.holy_salt_water, BREWING_STAND, ModItems.holy_water_bottle.setTier(new ItemStack(ModItems.holy_water_splash_bottle), IItemWithTier.TIER.NORMAL), BREWING_STAND).build(entries);
//        new ItemInfoBuilder(ModItems.holy_salt).setLinks(new ResourceLocation("guide.vampirism.items.holy_water_bottle")).setFormats(ModItems.pure_salt.getLocalizedName(), ModItems.pure_salt.getLocalizedName(), ModBlocks.alchemical_cauldron.getLocalizedName()).craftableStacks(ModItems.pure_salt, ALCHEMICAL_CAULDRON).build(entries);
//        new ItemInfoBuilder(ModItems.item_alchemical_fire).setLinks(new ResourceLocation("guide.vampirism.items.crossbow_arrow")).craftable(ALCHEMICAL_CAULDRON).build(entries);
//
//        addArmorWithTier(entries, "armor_of_swiftness", ModItems.armor_of_swiftness_head, ModItems.armor_of_swiftness_chest, ModItems.armor_of_swiftness_legs, ModItems.armor_of_swiftness_feet, WEAPON_TABLE);
//        addArmorWithTier(entries, "hunter_coat", ModItems.hunter_coat_head, ModItems.hunter_coat_chest, ModItems.hunter_coat_legs, ModItems.hunter_coat_feet, WEAPON_TABLE);
//        addArmorWithTier(entries, "obsidian_armor", ModItems.obsidian_armor_head, ModItems.obsidian_armor_chest, ModItems.obsidian_armor_legs, ModItems.obsidian_armor_feet, WEAPON_TABLE);
//        addItemWithTier(ModItems.hunter_axe, WEAPON_TABLE).build(entries);


        links.putAll(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildBlocks() {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.blocks.";
        //General
        new ItemInfoBuilder(ModBlocks.castle_block_dark_brick).recipes("general/castle_block_dark_brick_0", "general/castle_block_dark_brick_1", "general/castle_block_dark_stone", "general/castle_block_normal_brick", "general/castle_block_purple_brick", "general/castle_slab_dark_brick", "general/castle_stairs_dark_brick").build(entries);
        new ItemInfoBuilder(ModBlocks.vampire_orchid).build(entries);
        //Vampire
        new ItemInfoBuilder(ModBlocks.blood_container).recipes("vampire/blood_container").build(entries);
        new ItemInfoBuilder(ModBlocks.altar_inspiration).setLinks(new ResourceLocation("guide.vampirism.vampire.leveling")).recipes("vampire/altar_inspiration").build(entries);
//        new ItemInfoBuilder(ModBlocks.altar_infusion).setLinks(new ResourceLocation("guide.vampirism.vampire.leveling")).craftableStacks(new ItemStack(ModBlocks.altar_infusion), WORKBENCH, new ItemStack(ModBlocks.altar_pillar), WORKBENCH, new ItemStack(ModBlocks.altar_tip), WORKBENCH).build(entries);
//        new ItemInfoBuilder(new ItemStack(ModItems.item_coffin), true).craftable(WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModBlocks.church_altar).build(entries);
//        //Hunter
//        new ItemInfoBuilder(new ItemStack(ModItems.item_med_chair), true).setFormats((new ItemStack(ModItems.injection, 1, 1)).getDisplayName(), (new ItemStack(ModItems.injection, 1, 2)).getDisplayName()).craftable(WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModBlocks.hunter_table).setFormats(ModItems.hunter_intel.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.hunter.leveling"), new ResourceLocation("guide.vampirism.items.hunter_intel")).craftable(WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModBlocks.weapon_table).craftable(WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModBlocks.blood_potion_table).craftable(WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModBlocks.alchemical_cauldron).craftable(WORKBENCH).build(entries);
//        int cn = Balance.hps.GARLIC_DIFFUSOR_NORMAL_DISTANCE * 2 + 1;
//        int ce = Balance.hps.GARLIC_DIFFUSOR_ENHANCED_DISTANCE * 2 + 1;
//        new ItemInfoBuilder(ModBlocks.garlic_beacon).setFormats(cn, cn, ce, ce, ModItems.purified_garlic.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.items.item_garlic"), new ResourceLocation("guide.vampirism.items.purified_garlic"), new ResourceLocation("guide.vampirism.items.holy_water_bottle")).craftableStacks(ModBlocks.garlic_beacon, WORKBENCH, new ItemStack(ModBlocks.garlic_beacon, 1, BlockGarlicBeacon.Type.IMPROVED.getId()), WORKBENCH, ModItems.garlic_beacon_core, ALCHEMICAL_CAULDRON, ModItems.garlic_beacon_core_improved, ALCHEMICAL_CAULDRON).build(entries);
//        new ItemInfoBuilder(ModBlocks.blood_pedestal).craftable(WORKBENCH).build(entries);
//        new ItemInfoBuilder(ModBlocks.blood_grinder).craftable(WORKBENCH).setFormats(ModItems.human_heart.getLocalizedName(), UtilLib.translate(Items.BEEF.getTranslationKey() + ".name"), ModBlocks.blood_sieve.getLocalizedName()).build(entries);
//        new ItemInfoBuilder(ModBlocks.blood_sieve).craftable(WORKBENCH).setFormats(UtilLib.translate(ModFluids.impure_blood.getUnlocalizedName()), ModBlocks.blood_grinder.getLocalizedName()).setLinks(new ResourceLocation("guide.vampirism.blocks.blood_grinder")).build(entries);
//        new ItemInfoBuilder(ModBlocks.totem_top).craftable(WORKBENCH).setLinks(new ResourceLocation("guide.vampirism.blocks.totem_base"), new ResourceLocation("guide.vampirism.world.villages")).build(entries);
//        new ItemInfoBuilder(ModBlocks.totem_base).craftable(WORKBENCH).setLinks(new ResourceLocation("guide.vampirism.blocks.totem_top"), new ResourceLocation("guide.vampirism.world.villages")).build(entries);
        links.putAll(entries);
        return entries;
    }


    @Nullable
    @Override
    public Book buildBook() {
        BookBinder binder = new BookBinder(new ResourceLocation("vampirism", "guidebook"));
        binder.setGuideTitle("guide.vampirism.title");
        binder.setItemName("guide.vampirism.name");
        binder.setHeader("guide.vampirism.welcome");
        binder.setAuthor("Maxanier");
        binder.setColor(Color.getHSBColor(0.5f, 0.2f, 0.5f));
        binder.setOutlineTexture(new ResourceLocation("vampirismguide", "textures/gui/book_violet_border.png"));
        binder.setSpawnWithBook();
        binder.setContentProvider(GuideBook::buildCategories);
        return guideBook = binder.build();
    }
}
