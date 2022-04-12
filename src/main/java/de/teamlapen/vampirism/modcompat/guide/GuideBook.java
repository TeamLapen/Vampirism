package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.api.IGuideBook;
import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.BookBinder;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.BookHelper;
import de.maxanier.guideapi.api.util.PageHelper;
import de.maxanier.guideapi.category.CategoryItemStack;
import de.maxanier.guideapi.entry.EntryItemStack;
import de.maxanier.guideapi.entry.EntryResourceLocation;
import de.maxanier.guideapi.page.*;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.inventory.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.ShapelessWeaponTableRecipe;
import de.teamlapen.vampirism.items.BloodBottleItem;
import de.teamlapen.vampirism.modcompat.guide.pages.PagePotionTableMix;
import de.teamlapen.vampirism.modcompat.guide.pages.PageTable;
import de.teamlapen.vampirism.modcompat.guide.recipes.AlchemicalCauldronRecipeRenderer;
import de.teamlapen.vampirism.modcompat.guide.recipes.ShapedWeaponTableRecipeRenderer;
import de.teamlapen.vampirism.modcompat.guide.recipes.ShapelessWeaponTableRecipeRenderer;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.Color;
import java.net.URI;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;


@de.maxanier.guideapi.api.GuideBook
public class GuideBook implements IGuideBook {

    private static final Logger LOGGER = LogManager.getLogger();
    private final static String IMAGE_BASE = "vampirismguide:textures/images/";
    private static Book guideBook;

    static void buildCategories(List<CategoryAbstract> categories) {
        LOGGER.debug("Building content");
        long start = System.currentTimeMillis();
        BookHelper helper = new BookHelper.Builder(REFERENCE.MODID).setBaseKey("guide.vampirism").setLocalizer(GuideBook::translateComponent).setRecipeRendererSupplier(GuideBook::getRenderer).build();
        categories.add(new CategoryItemStack(buildOverview(helper), translateComponent("guide.vampirism.overview.title"), new ItemStack(ModItems.vampire_fang)));
        categories.add(new CategoryItemStack(buildVampire(helper), translateComponent("guide.vampirism.vampire.title"), BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT)));
        categories.add(new CategoryItemStack(buildHunter(helper), translateComponent("guide.vampirism.hunter.title"), new ItemStack(ModItems.human_heart)));
        categories.add(new CategoryItemStack(buildCreatures(helper), translateComponent("guide.vampirism.entity.title"), new ItemStack(Items.ZOMBIE_HEAD)));
        categories.add(new CategoryItemStack(buildWorld(helper), translateComponent("guide.vampirism.world.title"), new ItemStack(ModBlocks.cursed_earth)));
        categories.add(new CategoryItemStack(buildItems(helper), translateComponent("guide.vampirism.items.title"), new ItemStack(Items.APPLE)));
        categories.add(new CategoryItemStack(buildBlocks(helper), translateComponent("guide.vampirism.blocks.title"), new ItemStack(ModBlocks.castle_block_dark_brick)));
        categories.add(new CategoryItemStack(buildChangelog(helper), translateComponent("guide.vampirism.changelog.title"), new ItemStack(Items.WRITABLE_BOOK)));
        MinecraftForge.EVENT_BUS.post(new VampirismGuideBookCategoriesEvent(categories));
        helper.registerLinkablePages(categories);
        LOGGER.debug("Built content in {} ms", System.currentTimeMillis() - start);
    }

    @Nullable
    private static IRecipeRenderer getRenderer(IRecipe<?> recipe) {
        IRecipeRenderer recipeRenderer = PageIRecipe.getRenderer(recipe);
        if (recipeRenderer != null) return recipeRenderer;
        if (recipe instanceof ShapedWeaponTableRecipe) {
            return new ShapedWeaponTableRecipeRenderer((ShapedWeaponTableRecipe) recipe);
        } else if (recipe instanceof ShapelessWeaponTableRecipe) {
            return new ShapelessWeaponTableRecipeRenderer((ShapelessWeaponTableRecipe) recipe);
        } else if (recipe instanceof AlchemicalCauldronRecipe) {
            return new AlchemicalCauldronRecipeRenderer((AlchemicalCauldronRecipe) recipe);
        }
        LOGGER.warn("Did not find renderer for recipe {}", recipe);
        return null;
    }


    private static Map<ResourceLocation, EntryAbstract> buildOverview(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.overview.";

        List<IPage> introPages = new ArrayList<>();
        introPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "intro.text")));
        entries.put(new ResourceLocation(base + "intro"), new EntryText(introPages, translateComponent(base + "intro")));

        List<IPage> gettingStartedPages = new ArrayList<>();
        IPage p = new PageText(translateComponent(base + "getting_started.text"));
        p = new PageHolderWithLinks(helper, p).addLink("guide.vampirism.vampire.getting_started").addLink("guide.vampirism.hunter.getting_started");
        gettingStartedPages.add(p);
        entries.put(new ResourceLocation(base + "getting_started"), new EntryText(gettingStartedPages, translateComponent(base + "getting_started")));

        List<IPage> configPages = new ArrayList<>();
        configPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "config.text")));
        configPages.addAll(PageHelper.pagesForLongText(ITextProperties.composite(translateComponent(base + "config.general.text"), translateComponent(base + "config.general.examples"))));
        configPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "config.balance.text")));
        entries.put(new ResourceLocation(base + "config"), new EntryText(configPages, translateComponent(base + "config")));

        List<IPage> troublePages = new ArrayList<>();
        troublePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "trouble.text")));
        helper.addLinks(troublePages, new GuideHelper.URLLink(translateComponent(base + "trouble"), URI.create("https://github.com/TeamLapen/Vampirism/wiki/Troubleshooting")));
        entries.put(new ResourceLocation(base + "trouble"), new EntryText(troublePages, translateComponent(base + "trouble")));

        List<IPage> devPages = new ArrayList<>();
        PageHolderWithLinks.URLLink helpLink = new GuideHelper.URLLink(new StringTextComponent("How to help"), URI.create("https://github.com/TeamLapen/Vampirism/wiki#how-you-can-help"));
        devPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "dev.text")), helpLink));
        entries.put(new ResourceLocation(base + "dev"), new EntryText(devPages, translateComponent(base + "dev")));

        List<IPage> supportPages = new ArrayList<>();
        supportPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "support.text")));
        PageHolderWithLinks.URLLink linkCurseForge = new PageHolderWithLinks.URLLink("CurseForge", URI.create(REFERENCE.CURSEFORGE_LINK));

        helper.addLinks(supportPages, linkCurseForge, new ResourceLocation(base + "dev"));
        entries.put(new ResourceLocation(base + "support"), new EntryText(supportPages, translateComponent(base + "support")));

        List<IPage> creditsPages = new ArrayList<>();
        String lang = VampLib.proxy.getActiveLanguage();
        String credits = "§lDeveloper:§r\nMaxanier\nCheaterpaul\n§lThanks to:§r\nMistadon\nwildbill22\n1LiterZinalco\nAlis\ndimensionpainter\nS_olace\nPiklach\n\n§lTranslators:§r\n§b" + lang + "§r\n" + translateComponent("text.vampirism.translators").getString();
        creditsPages.addAll(PageHelper.pagesForLongText(translateComponent(credits)));
        entries.put(new ResourceLocation(base + "credits"), new EntryText(creditsPages, translateComponent(base + "credits")));
        return entries;
    }

    private static String loc(Block b) {
        return UtilLib.translate(b.getDescriptionId());
    }

    private static String loc(Item i) {
        return UtilLib.translate(i.getDescriptionId());
    }

    private static Map<ResourceLocation, EntryAbstract> buildVampire(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.vampire.";

        List<IPage> gettingStarted = new ArrayList<>();
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.become")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.as_vampire")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.zombie")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.blood", new TranslationTextComponent(ModKeys.getKeyBinding(ModKeys.KEY.SUCK).saveString()))));
        gettingStarted.addAll(PageHelper.pagesForLongText(ITextProperties.composite(translateComponent(base + "getting_started.level"), translateComponent(base + "getting_started.level2"))));

        entries.put(new ResourceLocation(base + "getting_started"), new EntryText(gettingStarted, translateComponent(base + "getting_started")));

        List<IPage> bloodPages = new ArrayList<>();
        bloodPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "blood.text", loc(ModItems.blood_bottle), loc(Items.GLASS_BOTTLE))));
        bloodPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "blood.storage", loc(ModBlocks.blood_container))), new ResourceLocation("guide.vampirism.blocks.blood_container")));
        bloodPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "blood.biteable_creatures")), new PageHolderWithLinks.URLLink("Biteable Creatures", URI.create("https://github.com/TeamLapen/Vampirism/wiki/Biteable-Creatures"))));
        entries.put(new ResourceLocation(base + "blood"), new EntryText(bloodPages, translateComponent(base + "blood")));

        VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "leveling.intro")));
        String altarOfInspiration = "§l" + loc(ModBlocks.altar_inspiration) + "§r\n§o" + translate(base + "leveling.inspiration.reach") + "§r\n";
        altarOfInspiration += translate(base + "leveling.inspiration.text") + "\n";
        altarOfInspiration += translate(base + "leveling.inspiration.requirements", levelingConf.getRequiredBloodForAltarInspiration(2), levelingConf.getRequiredBloodForAltarInspiration(3), levelingConf.getRequiredBloodForAltarInspiration(4));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new StringTextComponent(altarOfInspiration)), new ResourceLocation("guide.vampirism.blocks.altar_inspiration")));

        String altarOfInfusion = "§l" + loc(ModBlocks.altar_infusion) + "§r\n§o" + translate(base + "leveling.infusion.reach") + "§r\n";
        altarOfInfusion += translate(base + "leveling.infusion.intro", loc(ModBlocks.altar_infusion), loc(ModBlocks.altar_pillar), loc(ModBlocks.altar_tip));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new StringTextComponent(altarOfInfusion)), new ResourceLocation("guide.vampirism.blocks.altar_infusion")));
        StringBuilder blocks = new StringBuilder();
        for (AltarPillarBlock.EnumPillarType t : AltarPillarBlock.EnumPillarType.values()) {
            if (t == AltarPillarBlock.EnumPillarType.NONE) continue;
            blocks.append(translate(t.fillerBlock.getDescriptionId())).append("(").append(t.getValue()).append("),");
        }
        levelingPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "leveling.infusion.structure", blocks.toString())));
        String items = loc(ModItems.human_heart) + ", " + loc(ModItems.pure_blood_0) + ", " + loc(ModItems.vampire_book);
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "leveling.infusion.items", items)), new ResourceLocation("guide.vampirism.items.human_heart"), new ResourceLocation("guide.vampirism.items.pure_blood_0"), new ResourceLocation("guide.vampirism.items.vampire_book")));
        PageTable.Builder requirementsBuilder = new PageTable.Builder(5);
        requirementsBuilder.addUnlocLine("text.vampirism.level_short", base + "leveling.infusion.req.structure_points", ModItems.pure_blood_0.getDescriptionId(), base + "leveling.infusion.req.heart", base + "leveling.infusion.req.book");
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
        requirementsBuilder.setHeadline(translateComponent(base + "leveling.infusion.req"));
        PageHolderWithLinks requirementTable = new PageHolderWithLinks(helper, requirementsBuilder.build());
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.human_heart"));
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.vampire_book"));
        requirementTable.addLink(new ResourceLocation("guide.vampirism.items.pure_blood_0"));
        levelingPages.add(requirementTable);

        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image1"), new ResourceLocation(IMAGE_BASE + "infusion1.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image2"), new ResourceLocation(IMAGE_BASE + "infusion2.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image3"), new ResourceLocation(IMAGE_BASE + "infusion3.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image4"), new ResourceLocation(IMAGE_BASE + "infusion4.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image5"), new ResourceLocation(IMAGE_BASE + "infusion5.png"), false));

        entries.put(new ResourceLocation(base + "leveling"), new EntryText(levelingPages, translateComponent(base + "leveling")));


        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "skills.text")), new ResourceLocation(base + "vampirism_menu")));
        skillPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "skills.actions", UtilLib.translate(ModKeys.getKeyBinding(ModKeys.KEY.ACTION).saveString()))));
        skillPages.addAll(PageHelper.pagesForLongText(translateComponent("guide.vampirism.skills.bind_action")));
        skillPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "skills.actions2")));
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "skills.refinements")), new ResourceLocation("guide.vampirism.items.accessories")));

        entries.put(new ResourceLocation(base + "skills"), new EntryText(skillPages, translateComponent(base + "skills")));

        List<IPage> armorPages = new ArrayList<>(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "armor.text")), new ResourceLocation("guide.vampirism.items.accessories")));
        entries.put(new ResourceLocation(base + "armor"), new EntryText(armorPages, translateComponent(base + "armor")));
        List<IPage> dbnoPages = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base + "dbno.text", ModEffects.neonatal.getDisplayName())));
        entries.put(new ResourceLocation(base + "dbno"), new EntryText(dbnoPages, translateComponent(base + "dbno")));

        List<IPage> lordPages = new ArrayList<>();
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.text", ModEntities.task_master_vampire.getDescription().getString(), VReference.VAMPIRE_FACTION.getLordTitle(1, false).getString(), VReference.VAMPIRE_FACTION.getLordTitle(1, true).getString(), VReference.VAMPIRE_FACTION.getLordTitle(VReference.VAMPIRE_FACTION.getHighestLordLevel(), false).getString(), VReference.VAMPIRE_FACTION.getLordTitle(VReference.VAMPIRE_FACTION.getHighestLordLevel(), true).getString())), new ResourceLocation("guide.vampirism.entity.taskmaster")));
        PageTable.Builder lordTitleBuilder = new PageTable.Builder(3).setHeadline(translateComponent(base + "lord.titles"));
        lordTitleBuilder.addUnlocLine("text.vampirism.level", "text.vampirism.title", "text.vampirism.title");
        lordTitleBuilder.addLine(1, VReference.VAMPIRE_FACTION.getLordTitle(1, false).getString(), VReference.VAMPIRE_FACTION.getLordTitle(1, true).getString());
        lordTitleBuilder.addLine(2, VReference.VAMPIRE_FACTION.getLordTitle(2, false).getString(), VReference.VAMPIRE_FACTION.getLordTitle(2, true).getString());
        lordTitleBuilder.addLine(3, VReference.VAMPIRE_FACTION.getLordTitle(3, false).getString(), VReference.VAMPIRE_FACTION.getLordTitle(3, true).getString());
        lordTitleBuilder.addLine(4, VReference.VAMPIRE_FACTION.getLordTitle(4, false).getString(), VReference.VAMPIRE_FACTION.getLordTitle(4, true).getString());
        lordTitleBuilder.addLine(5, VReference.VAMPIRE_FACTION.getLordTitle(5, false).getString(), VReference.VAMPIRE_FACTION.getLordTitle(5, true).getString());
        lordPages.add(lordTitleBuilder.build());
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.minion", loc(ModItems.vampire_minion_binding), loc(ModItems.vampire_minion_upgrade_simple), loc(ModItems.vampire_minion_upgrade_enhanced), loc(ModItems.vampire_minion_upgrade_special))), new ResourceLocation("guide.vampirism.items.vampire_minion_binding")));
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent("guide.vampirism.common.minion_control", translate(ModKeys.getKeyBinding(ModKeys.KEY.MINION).saveString()), translate("text.vampirism.minion.call_single"), translate("text.vampirism.minion.respawn")))));
        entries.put(new ResourceLocation(base + "lord"), new EntryText(lordPages, new TranslationTextComponent(base + "lord")));


        List<IPage> vampirismMenu = new ArrayList<>(PageHelper.pagesForLongText(translateComponent("guide.vampirism.overview.vampirism_menu.text", translateComponent(ModKeys.getKeyBinding(ModKeys.KEY.SKILL).saveString())).append(translateComponent("guide.vampirism.overview.vampirism_menu.text_vampire", translateComponent("guide.vampirism.items.accessories"))))); //Lang key shared with vampires
        entries.put(new ResourceLocation(base + "vampirism_menu"), new EntryText(vampirismMenu, translateComponent("guide.vampirism.overview.vampirism_menu")));


        List<IPage> unvampirePages = new ArrayList<>();
        unvampirePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "unvampire.text", loc(ModBlocks.church_altar))));
        entries.put(new ResourceLocation(base + "unvampire"), new EntryText(unvampirePages, translateComponent(base + "unvampire")));

        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildHunter(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.hunter.";

        List<IPage> gettingStarted = new ArrayList<>();
        ITextComponent become = translateComponent(base + "getting_started.become", translateComponent(ModEntities.hunter_trainer.getDescriptionId()), loc(ModItems.injection_garlic));
        gettingStarted.addAll(helper.addLinks(PageHelper.pagesForLongText(become), new ResourceLocation("guide.vampirism.items.injection_empty")));
        gettingStarted.add(new PageImage(new ResourceLocation(IMAGE_BASE + "hunter_trainer.png")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.as_hunter")));
        entries.put(new ResourceLocation(base + "getting_started"), new EntryText(gettingStarted, translateComponent(base + "getting_started")));

        HunterLevelingConf levelingConf = HunterLevelingConf.instance();
        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "leveling.intro")));
        String train1 = "§l" + translate(base + "leveling.to_reach", "2-4") + "§r\n";
        train1 += translate(base + "leveling.train1.text", levelingConf.getVampireBloodCountForBasicHunter(2), levelingConf.getVampireBloodCountForBasicHunter(3), levelingConf.getVampireBloodCountForBasicHunter(4));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new StringTextComponent(train1)), new ResourceLocation("guide.vampirism.items.stake"), new ResourceLocation("guide.vampirism.items.vampire_blood_bottle")));

        String train2 = "§l" + translate(base + "leveling.to_reach", "5+") + "§r\n";
        train2 += translate(base + "leveling.train2.text", loc(ModBlocks.hunter_table), loc(ModBlocks.weapon_table), loc(ModBlocks.potion_table), loc(ModBlocks.alchemical_cauldron));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new TranslationTextComponent(train2)), new ResourceLocation("guide.vampirism.blocks.hunter_table"), new ResourceLocation("guide.vampirism.blocks.weapon_table"), new ResourceLocation("guide.vampirism.blocks.alchemical_cauldron"), new ResourceLocation("guide.vampirism.blocks.potion_table")));
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

        builder.setHeadline(translateComponent(base + "leveling.train2.req"));
        PageHolderWithLinks requirementsTable = new PageHolderWithLinks(helper, builder.build());
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.vampire_fang"));
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.pure_blood_0"));
        requirementsTable.addLink(new ResourceLocation("guide.vampirism.items.vampire_book"));
        levelingPages.add(requirementsTable);

        entries.put(new ResourceLocation(base + "leveling"), new EntryText(levelingPages, translateComponent(base + "leveling")));

        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "skills.intro")), new ResourceLocation(base + "vampirism_menu")));
        String disguise = String.format("§l%s§r\n", HunterActions.disguise_hunter.getName().getString());
        disguise += translate(base + "skills.disguise.text", ModKeys.getKeyBinding(ModKeys.KEY.ACTION).saveString());
        skillPages.addAll(PageHelper.pagesForLongText(new StringTextComponent(disguise)));
        String weaponTable = String.format("§l%s§r\n", loc(ModBlocks.weapon_table));
        weaponTable += translate(base + "skills.weapon_table.text");
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new StringTextComponent(weaponTable)), new ResourceLocation("guide.vampirism.blocks.weapon_table")));
        entries.put(new ResourceLocation(base + "skills"), new EntryText(skillPages, translateComponent(base + "skills")));
        String potionTable = String.format("§l%s§r\n", loc(ModBlocks.potion_table));
        potionTable += translate(base + "skills.potion_table.text");
        List<IPage> potionTablePages = new ArrayList<>(PageHelper.pagesForLongText(new StringTextComponent(potionTable)));
        potionTablePages.addAll(Arrays.asList(generatePotionMixes()));
        skillPages.addAll(helper.addLinks(potionTablePages, new ResourceLocation("guide.vampirism.blocks.potion_table")));
        entries.put(new ResourceLocation(base + "skills"), new EntryText(skillPages, new TranslationTextComponent(base + "skills")));

        List<IPage> vampSlayerPages = new ArrayList<>();
        vampSlayerPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "vamp_slayer.intro")));
        String garlic = String.format("§l%s§r\n", loc(ModItems.item_garlic));
        garlic += translate(base + "vamp_slayer.garlic") + "\n" + translate(base + "vamp_slayer.garlic2") + "\n" + translate(base + "vamp_slayer.garlic.diffusor");
        vampSlayerPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new StringTextComponent(garlic)), new ResourceLocation("guide.vampirism.blocks.garlic_beacon_normal")));
        String holyWater = String.format("§l%s§r\n", loc(ModItems.holy_water_bottle_normal));
        holyWater += translate(base + "vamp_slayer.holy_water");
        vampSlayerPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new StringTextComponent(holyWater)), new ResourceLocation("guide.vampirism.items.holy_water_bottle")));
        String fire = String.format("§l%s§r\n", loc(Blocks.FIRE));
        fire += translate(base + "vamp_slayer.fire");
        vampSlayerPages.addAll(helper.addLinks(PageHelper.pagesForLongText(new StringTextComponent(fire)), new ResourceLocation("guide.vampirism.items.item_alchemical_fire"), new ResourceLocation("guide.vampirism.items.crossbow_arrow_normal")));
        entries.put(new ResourceLocation(base + "vamp_slayer"), new EntryText(vampSlayerPages, translateComponent(base + "vamp_slayer")));

        List<IPage> lordPages = new ArrayList<>();
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.text", ModEntities.task_master_hunter.getDescription().getString(), VReference.HUNTER_FACTION.getLordTitle(1, false).getString(), VReference.HUNTER_FACTION.getLordTitle(VReference.HUNTER_FACTION.getHighestLordLevel(), false).getString())), new ResourceLocation("guide.vampirism.entity.taskmaster")));
        PageTable.Builder lordTitleBuilder = new PageTable.Builder(2);
        lordTitleBuilder.setHeadline(translateComponent(base + "lord.titles"));
        lordTitleBuilder.addUnlocLine("text.vampirism.level", "text.vampirism.title");
        lordTitleBuilder.addLine(1, VReference.HUNTER_FACTION.getLordTitle(1, false).getString());
        lordTitleBuilder.addLine(2, VReference.HUNTER_FACTION.getLordTitle(2, false).getString());
        lordTitleBuilder.addLine(3, VReference.HUNTER_FACTION.getLordTitle(3, false).getString());
        lordTitleBuilder.addLine(4, VReference.HUNTER_FACTION.getLordTitle(4, false).getString());
        lordTitleBuilder.addLine(5, VReference.HUNTER_FACTION.getLordTitle(5, false).getString());
        lordPages.add(lordTitleBuilder.build());
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.minion", loc(ModItems.hunter_minion_equipment), loc(ModItems.hunter_minion_upgrade_simple), loc(ModItems.hunter_minion_upgrade_enhanced), loc(ModItems.hunter_minion_upgrade_special))), new ResourceLocation("guide.vampirism.items.hunter_minion_equipment")));
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent("guide.vampirism.common.minion_control", translate(ModKeys.getKeyBinding(ModKeys.KEY.MINION).saveString()), translate("text.vampirism.minion.call_single"), translate("text.vampirism.minion.respawn")))));
        entries.put(new ResourceLocation(base + "lord"), new EntryText(lordPages, new TranslationTextComponent(base + "lord")));

        List<IPage> vampirismMenu = new ArrayList<>(PageHelper.pagesForLongText(translateComponent("guide.vampirism.overview.vampirism_menu.text", translate(ModKeys.getKeyBinding(ModKeys.KEY.SKILL).saveString())))); //Lang key shared with vampires
        entries.put(new ResourceLocation(base + "vampirism_menu"), new EntryText(vampirismMenu, translateComponent("guide.vampirism.overview.vampirism_menu")));

        List<IPage> unHunterPages = new ArrayList<>();
        unHunterPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "unhunter.text", loc(ModItems.injection_sanguinare), loc(ModBlocks.med_chair))), new ResourceLocation("guide.vampirism.items.injection_empty"), new ResourceLocation("guide.vampirism.blocks.item_med_chair")));
        entries.put(new ResourceLocation(base + "unhunter"), new EntryText(unHunterPages, translateComponent(base + "unhunter")));

        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildCreatures(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.entity.";

        ArrayList<IPage> generalPages = new ArrayList<>(PageHelper.pagesForLongText(ITextProperties.composite(translateComponent(base + "general.text"), translateComponent(base + "general.text2"))));
        entries.put(new ResourceLocation(base + "general"), new EntryText(generalPages, translateComponent(base + "general")));

        ArrayList<IPage> hunterPages = new ArrayList<>();
        hunterPages.add(new PageEntity((world) -> {
            BasicHunterEntity entity = ModEntities.hunter.create(world);
            entity.setLevel(1);
            return entity;
        }));
        hunterPages.add(new PageEntity((world) -> {
            BasicHunterEntity entity = ModEntities.hunter.create(world);
            entity.setLevel(0);
            return entity;
        }));
        hunterPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "hunter.text", loc(ModItems.human_heart))));
        entries.put(new ResourceLocation(base + "hunter"), new EntryText(hunterPages, ModEntities.hunter.getDescription()));

        ArrayList<IPage> vampirePages = new ArrayList<>();
        vampirePages.add(new PageEntity(ModEntities.vampire));
        vampirePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "vampire.text", loc(ModItems.vampire_fang), loc(ModItems.vampire_blood_bottle), loc(ModItems.stake))));
        entries.put(new ResourceLocation(base + "vampire"), new EntryText(vampirePages, ModEntities.vampire.getDescription()));

        ArrayList<IPage> advancedHunterPages = new ArrayList<>();
        advancedHunterPages.add(new PageEntity(ModEntities.advanced_hunter));
        advancedHunterPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "advanced_hunter.text")));
        entries.put(new ResourceLocation(base + "advanced_hunter"), new EntryText(advancedHunterPages, ModEntities.advanced_hunter.getDescription()));

        ArrayList<IPage> advancedVampirePages = new ArrayList<>();
        advancedVampirePages.add(new PageEntity(ModEntities.advanced_vampire));
        advancedVampirePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "advanced_vampire.text", loc(ModItems.blood_bottle), loc(ModItems.vampire_blood_bottle))));
        entries.put(new ResourceLocation(base + "advanced_vampire"), new EntryText(advancedVampirePages, ModEntities.advanced_vampire.getDescription()));

        ArrayList<IPage> vampireBaronPages = new ArrayList<>();
        vampireBaronPages.add(new PageEntity(ModEntities.vampire_baron));
        vampireBaronPages.add(new PageEntity((world) -> {
            VampireBaronEntity baron = ModEntities.vampire_baron.create(world);
            baron.setLady(true);
            return baron;
        }, ModEntities.vampire_baron.getDescription()));
        vampireBaronPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "vampire_baron.text", loc(ModItems.pure_blood_0))));
        helper.addLinks(vampireBaronPages, new ResourceLocation("guide.vampirism.world.vampire_forest"));
        entries.put(new ResourceLocation(base + "vampire_baron"), new EntryText(vampireBaronPages, ModEntities.vampire_baron.getDescription()));

        ArrayList<IPage> hunterTrainerPages = new ArrayList<>();
        hunterTrainerPages.add(new PageEntity(ModEntities.hunter_trainer));
        hunterTrainerPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "hunter_trainer.text")));
        entries.put(new ResourceLocation(base + "hunter_trainer"), new EntryText(hunterTrainerPages, ModEntities.hunter_trainer.getDescription()));

        ArrayList<IPage> taskMasterPages = new ArrayList<>();
        taskMasterPages.add(new PageEntity(ModEntities.task_master_vampire));
        taskMasterPages.add(new PageEntity(ModEntities.task_master_hunter));
        taskMasterPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "taskmaster.text")));
        taskMasterPages.add(new PageImage(new ResourceLocation(IMAGE_BASE + "taskscreen.png")));
        entries.put(new ResourceLocation(base + "taskmaster"), new EntryText(taskMasterPages, new TranslationTextComponent(base + "taskmaster")));


        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildWorld(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.world.";

        List<IPage> vampireForestPages = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base + "vampire_forest.text")));
        entries.put(new ResourceLocation(base + "vampire_forest"), new EntryText(vampireForestPages, translateComponent(base + "vampire_forest")));

        List<IPage> villagePages = new ArrayList<>(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "villages.text").append("\n").append(translateComponent(base + "villages.raids"))), new ResourceLocation("guide.vampirism.blocks.totem_base"), new ResourceLocation("guide.vampirism.blocks.totem_top_crafted")));
        entries.put(new ResourceLocation(base + "villages"), new EntryText(villagePages, translateComponent(base + "villages")));


        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildItems(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.items.";
        //General
        helper.info(ModItems.vampire_fang).build(entries);
        helper.info(ModItems.human_heart).build(entries);
        helper.info(ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4).setFormats(translateComponent(ModEntities.vampire_baron.getDescriptionId())).build(entries);
        helper.info(ModItems.vampire_blood_bottle).setFormats(translateComponent(ModEntities.vampire.getDescriptionId()), translateComponent(ModEntities.advanced_vampire.getDescriptionId(), loc(ModItems.stake))).build(entries);
        helper.info(ModItems.vampire_book).build(entries);
        helper.info(ModItems.oblivion_potion).customPages(GuideHelper.createItemTaskDescription(ModTasks.oblivion_potion)).build(entries);

        //Vampire
        helper.info(false, BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT)).build(entries);
        helper.info(ModItems.blood_infused_iron_ingot).recipes("vampire/blood_infused_iron_ingot", "vampire/blood_infused_enhanced_iron_ingot").build(entries);
        helper.info(ModItems.heart_seeker_normal, ModItems.heart_seeker_enhanced, ModItems.heart_seeker_ultimate).recipes("vampire/heart_seeker_normal", "vampire/heart_seeker_enhanced").build(entries);
        helper.info(ModItems.heart_striker_normal, ModItems.heart_striker_enhanced, ModItems.heart_striker_ultimate).recipes("vampire/heart_striker_normal", "vampire/heart_striker_normal").build(entries);
        helper.info(ModItems.feeding_adapter).customPages(GuideHelper.createItemTaskDescription(ModTasks.feeding_adapter)).build(entries);
        helper.info(ModItems.vampire_minion_binding, ModItems.vampire_minion_upgrade_simple, ModItems.vampire_minion_upgrade_enhanced, ModItems.vampire_minion_upgrade_special).setFormats(loc(ModItems.vampire_minion_binding), loc(ModItems.vampire_minion_upgrade_simple), ModItems.vampire_minion_upgrade_simple.getMinLevel() + 1, ModItems.vampire_minion_upgrade_simple.getMaxLevel() + 1, loc(ModItems.vampire_minion_upgrade_enhanced), ModItems.vampire_minion_upgrade_enhanced.getMinLevel() + 1, ModItems.vampire_minion_upgrade_enhanced.getMaxLevel() + 1, loc(ModItems.vampire_minion_upgrade_special), ModItems.vampire_minion_upgrade_special.getMinLevel() + 1, ModItems.vampire_minion_upgrade_special.getMaxLevel() + 1, translate(ModEntities.task_master_vampire.getDescriptionId())).setLinks(new ResourceLocation("guide.vampirism.entity.taskmaster"), new ResourceLocation("guide.vampirism.vampire.lord")).build(entries);
        helper.info(ModItems.garlic_finder).setLinks(new ResourceLocation("guide.vampirism.blocks.garlic_diffusor")).recipes("vampire/garlic_finder").build(entries);
        helper.info(ModItems.vampire_clothing_crown, ModItems.vampire_clothing_hat, ModItems.vampire_clothing_legs, ModItems.vampire_clothing_boots, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_black_red, ModItems.vampire_cloak_black_blue, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_black_white, ModItems.vampire_cloak_white_black).useCustomEntryName().setKeyName("vampire_clothing").recipes("vampire/vampire_clothing_legs", "vampire/vampire_clothing_boots", "vampire/vampire_clothing_hat", "vampire/vampire_clothing_crown", "vampire/vampire_cloak_black_red", "vampire/vampire_cloak_black_blue", "vampire/vampire_cloak_black_white", "vampire/vampire_cloak_red_black", "vampire/vampire_cloak_white_black").build(entries);
        helper.info(ModItems.amulet, ModItems.ring, ModItems.obi_belt).setLinks(new ResourceLocation("guide.vampirism.vampire.vampirism_menu")).useCustomEntryName().setKeyName("accessories").build(entries);

        //Hunter
        helper.info(ModItems.injection_empty, ModItems.injection_garlic, ModItems.injection_sanguinare).recipes("general/injection_0", "general/injection_1", "general/injection_2").build(entries);
        helper.info(ModItems.hunter_intel_0).setLinks(new ResourceLocation("guide.vampirism.blocks.hunter_table")).setFormats(loc(ModBlocks.hunter_table)).build(entries);
        helper.info(ModItems.item_garlic).build(entries);
        helper.info(ModItems.purified_garlic).setFormats(loc(ModBlocks.garlic_beacon_normal)).setLinks(new ResourceLocation("guide.vampirism.blocks.garlic_beacon_normal")).recipes("alchemical_cauldron/purified_garlic").build(entries);
        helper.info(ModItems.pitchfork).recipes("weapontable/pitchfork").build(entries);
        helper.info(ModItems.stake).setFormats(((int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100)) + "%").recipes("hunter/stake").build(entries);
        helper.info(ModItems.basic_crossbow, ModItems.enhanced_crossbow, ModItems.basic_double_crossbow, ModItems.enhanced_double_crossbow, ModItems.basic_tech_crossbow, ModItems.enhanced_tech_crossbow).setFormats(loc(ModItems.crossbow_arrow_normal), loc(ModItems.tech_crossbow_ammo_package)).setLinks(new ResourceLocation("guide.vampirism.items.crossbow_arrow_normal")).recipes("weapontable/basic_crossbow", "weapontable/enhanced_crossbow", "weapontable/basic_double_crossbow", "weapontable/enhanced_double_crossbow", "weapontable/basic_tech_crossbow", "weapontable/enhanced_tech_crossbow", "weapontable/tech_crossbow_ammo_package").useCustomEntryName().setKeyName("crossbows").build(entries);
        helper.info(ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_spitfire, ModItems.crossbow_arrow_vampire_killer).recipes("hunter/crossbow_arrow_normal", "weapontable/crossbow_arrow_spitfire", "weapontable/crossbow_arrow_vampire_killer").build(entries);
        helper.info(ModItems.holy_water_bottle_normal, ModItems.holy_water_bottle_enhanced, ModItems.holy_water_bottle_ultimate).setLinks(new ResourceLocation("guide.vampirism.hunter.vamp_slayer"), new ResourceLocation("guide.vampirism.items.holy_salt")).setFormats(loc(ModItems.holy_salt_water), loc(ModItems.holy_salt_water), loc(ModItems.holy_salt)).brewingItems(ModItems.holy_salt_water, ModItems.holy_water_splash_bottle_normal).setKeyName("holy_water_bottle").build(entries);
        helper.info(ModItems.holy_salt).setLinks(new ResourceLocation("guide.vampirism.items.holy_water_bottle")).setFormats(loc(ModItems.pure_salt), loc(ModItems.pure_salt), loc(ModBlocks.alchemical_cauldron)).recipes("alchemical_cauldron/pure_salt").build(entries);
        helper.info(ModItems.item_alchemical_fire).setLinks(new ResourceLocation("guide.vampirism.items.crossbow_arrow_normal")).recipes("alchemical_cauldron/alchemical_fire_4", "alchemical_cauldron/alchemical_fire_5", "alchemical_cauldron/alchemical_fire_6").build(entries);
        helper.info(ModItems.armor_of_swiftness_chest_normal, ModItems.armor_of_swiftness_chest_enhanced, ModItems.armor_of_swiftness_chest_enhanced, ModItems.armor_of_swiftness_legs_normal, ModItems.armor_of_swiftness_legs_enhanced, ModItems.armor_of_swiftness_legs_ultimate, ModItems.armor_of_swiftness_head_normal, ModItems.armor_of_swiftness_head_enhanced, ModItems.armor_of_swiftness_head_ultimate, ModItems.armor_of_swiftness_feet_normal, ModItems.armor_of_swiftness_feet_enhanced, ModItems.armor_of_swiftness_feet_ultimate).recipes("weapontable/armor_of_swiftness_chest_normal", "weapontable/armor_of_swiftness_legs_normal", "weapontable/armor_of_swiftness_head_normal", "weapontable/armor_of_swiftness_feet_normal", "weapontable/armor_of_swiftness_chest_enhanced", "weapontable/armor_of_swiftness_legs_enhanced", "weapontable/armor_of_swiftness_head_enhanced", "weapontable/armor_of_swiftness_feet_enhanced").build(entries);
        helper.info(ModItems.hunter_coat_chest_normal, ModItems.hunter_coat_chest_enhanced, ModItems.hunter_coat_chest_enhanced, ModItems.hunter_coat_legs_normal, ModItems.hunter_coat_legs_enhanced, ModItems.hunter_coat_legs_ultimate, ModItems.hunter_coat_head_normal, ModItems.hunter_coat_head_enhanced, ModItems.hunter_coat_head_ultimate, ModItems.hunter_coat_feet_normal, ModItems.hunter_coat_feet_enhanced, ModItems.hunter_coat_feet_ultimate).recipes("weapontable/hunter_coat_chest_normal", "weapontable/hunter_coat_legs_normal", "weapontable/hunter_coat_head_normal", "weapontable/hunter_coat_feet_normal", "weapontable/hunter_coat_chest_enhanced", "weapontable/hunter_coat_legs_enhanced", "weapontable/hunter_coat_head_enhanced", "weapontable/hunter_coat_feet_enhanced").build(entries);
        helper.info(ModItems.hunter_axe_normal, ModItems.hunter_axe_enhanced, ModItems.hunter_axe_ultimate).recipes("weapontable/hunter_axe_normal", "weapontable/hunter_axe_enhanced").build(entries);
        helper.info(ModItems.hunter_minion_equipment, ModItems.hunter_minion_upgrade_simple, ModItems.hunter_minion_upgrade_enhanced, ModItems.hunter_minion_upgrade_special).setFormats(loc(ModItems.hunter_minion_equipment), loc(ModItems.hunter_minion_upgrade_simple), ModItems.hunter_minion_upgrade_simple.getMinLevel() + 1, ModItems.hunter_minion_upgrade_simple.getMaxLevel() + 1, loc(ModItems.hunter_minion_upgrade_enhanced), ModItems.hunter_minion_upgrade_enhanced.getMinLevel() + 1, ModItems.hunter_minion_upgrade_enhanced.getMaxLevel() + 1, loc(ModItems.hunter_minion_upgrade_special), ModItems.hunter_minion_upgrade_special.getMinLevel() + 1, ModItems.hunter_minion_upgrade_special.getMaxLevel() + 1, translate(ModEntities.task_master_hunter.getDescriptionId())).setLinks(new ResourceLocation("guide.vampirism.entity.taskmaster"), new ResourceLocation("guide.vampirism.hunter.lord")).build(entries);
        return entries;
    }

    private static Map<ResourceLocation, EntryAbstract> buildBlocks(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.blocks.";
        //General
        helper.info(ModBlocks.castle_block_dark_brick).recipes("general/castle_block_dark_brick_0", "general/castle_block_dark_brick_1", "general/castle_block_dark_stone", "general/castle_block_normal_brick", "general/castle_block_purple_brick", "general/castle_slab_dark_brick", "general/castle_stairs_dark_brick").build(entries);
        helper.info(ModBlocks.vampire_orchid).build(entries);
        //Vampire
        helper.info(ModBlocks.blood_container).recipes("vampire/blood_container").build(entries);
        helper.info(ModBlocks.altar_inspiration).setLinks(new ResourceLocation("guide.vampirism.vampire.leveling")).recipes("vampire/altar_inspiration").build(entries);
        helper.info(ModBlocks.altar_infusion).setLinks(new ResourceLocation("guide.vampirism.vampire.leveling")).recipes("vampire/altar_infusion", "vampire/altar_pillar", "vampire/altar_tip").build(entries);
        helper.info(ModBlocks.coffin_red).recipes("vampire/coffin_red").build(entries);
        helper.info(ModBlocks.church_altar).build(entries);
        //Hunter
        helper.info(true, new ItemStack(ModBlocks.med_chair)).setFormats(loc(ModItems.injection_garlic), loc(ModItems.injection_sanguinare)).recipes("hunter/item_med_chair").build(entries);
        helper.info(ModBlocks.hunter_table).setFormats(loc(ModItems.hunter_intel_0)).setLinks(new ResourceLocation("guide.vampirism.hunter.leveling"), new ResourceLocation("guide.vampirism.items.hunter_intel")).recipes("hunter/hunter_table").build(entries);
        helper.info(ModBlocks.weapon_table).recipes("hunter/weapon_table").build(entries);
        helper.info(ModBlocks.alchemical_cauldron).recipes("hunter/alchemical_cauldron").build(entries);
        int cn = VampirismConfig.BALANCE.hsGarlicDiffusorNormalDist.get() * 2 + 1;
        int ce = VampirismConfig.BALANCE.hsGarlicDiffusorEnhancedDist.get() * 2 + 1;
        helper.info(ModBlocks.garlic_beacon_normal, ModBlocks.garlic_beacon_weak, ModBlocks.garlic_beacon_weak).setFormats(cn, cn, ce, ce, loc(ModItems.purified_garlic)).setLinks(new ResourceLocation("guide.vampirism.items.item_garlic"), new ResourceLocation("guide.vampirism.items.purified_garlic"), new ResourceLocation("guide.vampirism.items.holy_water_bottle")).recipes("hunter/garlic_beacon_normal", "hunter/garlic_beacon_improved", "alchemical_cauldron/garlic_beacon_core", "alchemical_cauldron/garlic_beacon_core_improved").build(entries);
        helper.info(ModBlocks.blood_pedestal).recipes("vampire/blood_pedestal").build(entries);
        helper.info(ModBlocks.blood_grinder).recipes("general/blood_grinder").setFormats(loc(ModItems.human_heart), loc(Items.BEEF), loc(ModBlocks.blood_sieve)).build(entries);
        helper.info(ModBlocks.blood_sieve).recipes("general/blood_sieve").setFormats(translateComponent(ModFluids.impure_blood.getAttributes().getTranslationKey()), loc(ModBlocks.blood_grinder)).setLinks(new ResourceLocation("guide.vampirism.blocks.blood_grinder")).build(entries);
        helper.info(ModBlocks.totem_top_crafted, ModBlocks.totem_top).setLinks(new ResourceLocation("guide.vampirism.blocks.totem_base"), new ResourceLocation("guide.vampirism.world.villages")).build(entries);
        helper.info(ModBlocks.totem_base).recipes("general/totem_base").setLinks(new ResourceLocation("guide.vampirism.blocks.totem_top_crafted"), new ResourceLocation("guide.vampirism.world.villages")).build(entries);
        helper.info(ModBlocks.potion_table).recipes("hunter/potion_table").customPages(generatePotionMixes()).build(entries);

        List<IPage> decorativeBlocks = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base + "decorative.text"), ModItems.item_candelabra));
        decorativeBlocks.add(helper.getRecipePage(new ResourceLocation(REFERENCE.MODID, "vampire/candelabra")));
        decorativeBlocks.add(helper.getRecipePage(new ResourceLocation(REFERENCE.MODID, "vampire/chandelier")));
        decorativeBlocks.add(helper.getRecipePage(new ResourceLocation(REFERENCE.MODID, "hunter/cross")));
        decorativeBlocks.add(helper.getRecipePage(new ResourceLocation(REFERENCE.MODID, "general/tombstone1")));
        decorativeBlocks.add(helper.getRecipePage(new ResourceLocation(REFERENCE.MODID, "general/tombstone2")));
        decorativeBlocks.add(helper.getRecipePage(new ResourceLocation(REFERENCE.MODID, "general/tombstone3")));
        decorativeBlocks.add(helper.getRecipePage(new ResourceLocation(REFERENCE.MODID, "general/grave_cage")));

        entries.put(new ResourceLocation(base + "decorative"), new EntryItemStack(decorativeBlocks, new TranslationTextComponent(base + "decorative.title"), new ItemStack(ModItems.item_candelabra)));
        return entries;
    }

    public static Map<ResourceLocation, EntryAbstract> buildChangelog(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.changelog.";
        entries.put(new ResourceLocation(base + "v1_8"), buildChangelog1_8());
        return entries;
    }

    public static EntryAbstract buildChangelog1_8() {
        String base = "guide.vampirism.changelog.";
        String base1_8 = base + "v1_8.";

        //Vampirism 1.8
        List<IPage> v1_8 = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base1_8 + "overview.text")));
        //vampirism menu
        List<IPage> vampirism_menu = PageHelper.pagesForLongText(translateComponent(base1_8 + "vampirism_menu.text", ModKeys.getKeyBinding(ModKeys.KEY.SKILL).getTranslatedKeyMessage()));
        vampirism_menu.add(new PageTextImage(translateComponent(base1_8 + "vampirism_menu.image"), new ResourceLocation(IMAGE_BASE + "vampirism_menu.png"), false));
        v1_8.addAll(vampirism_menu);
        //vampire accessories
        List<IPage> accessories = PageHelper.pagesForLongText(translateComponent(base1_8 + "accessories.text"));
        accessories.add(new PageTextImage(translateComponent(base1_8 + "accessories.image"), new ResourceLocation(IMAGE_BASE + "vampire_accessories.png"), false));
        v1_8.addAll(accessories);
        //vampire armor
        List<IPage> armor = PageHelper.pagesForLongText(translateComponent(base1_8 + "vampire_armor.text"));
        v1_8.addAll(armor);
        //vampire immortality
        List<IPage> immortality = PageHelper.pagesForLongText(translateComponent(base1_8 + "vampire_immortality.text"));
        v1_8.addAll(immortality);
        //task changes
        List<IPage> task_changes = PageHelper.pagesForLongText(translateComponent(base1_8 + "tasks.text"));
        v1_8.addAll(task_changes);
        //raids
        List<IPage> raids = PageHelper.pagesForLongText(translateComponent(base1_8 + "raids.text"));
        v1_8.addAll(raids);
        //skills
        List<IPage> skills = PageHelper.pagesForLongText(translateComponent(base1_8 + "skills.text"));
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.vista.image"), new ResourceLocation(REFERENCE.MODID, "textures/skills/vampire_forest_fog.png"), false));
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.neonatal.image"), new ResourceLocation(REFERENCE.MODID, "textures/skills/neonatal_decrease.png"), false));
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.dbno.image"), new ResourceLocation(REFERENCE.MODID, "textures/skills/dbno_duration.png"), false));
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.hissing.image"), new ResourceLocation(REFERENCE.MODID, "textures/actions/hissing.png"), false));
        v1_8.addAll(skills);
        //balancing
        List<IPage> balancing = PageHelper.pagesForLongText(translateComponent(base1_8 + "balancing.text"));
        v1_8.addAll(balancing);
        //misc
        List<IPage> misc = PageHelper.pagesForLongText(translateComponent(base1_8 + "misc.text"));
        v1_8.addAll(misc);
        return new EntryResourceLocation(v1_8, translateComponent(base + "v1_8"), new ResourceLocation("textures/item/writable_book.png"));
    }

    private static IPage[] generatePotionMixes() {
        IPage[] pages = new IPage[6];
        pages[0] = new PagePotionTableMix(HunterSkills.durable_brewing.getName(), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.durable && !mix.concentrated && !mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[1] = new PagePotionTableMix(HunterSkills.concentrated_brewing.getName(), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.concentrated && !mix.durable && !mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[2] = new PagePotionTableMix(HunterSkills.durable_brewing.getName().copy().append("\n").append(HunterSkills.efficient_brewing.getName()), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.durable && !mix.concentrated && mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[3] = new PagePotionTableMix(HunterSkills.concentrated_brewing.getName().copy().append("\n").append(HunterSkills.efficient_brewing.getName()), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.concentrated && !mix.durable && mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[4] = new PagePotionTableMix(HunterSkills.master_brewer.getName(), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.master && !mix.durable && !mix.concentrated && !mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[5] = new PagePotionTableMix(HunterSkills.master_brewer.getName().copy().append("\n").append(HunterSkills.efficient_brewing.getName()), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.master && !mix.durable && !mix.concentrated && mix.efficient).toArray(ExtendedPotionMix[]::new));
        return pages;
    }

    public static IFormattableTextComponent translateComponent(String key, Object... format) {
        String result = UtilLib.translate(key, format);
        return new StringTextComponent(result.replaceAll("\\\\n", Matcher.quoteReplacement("\n"))); //Fix legacy newlines. //Probably shouldn't use new StringTextComponent here, but don't want to rewrite everything
    }

    public static String translate(String key, Object... format) {
        String result = UtilLib.translate(key, format);
        return result.replaceAll("\\\\n", Matcher.quoteReplacement("\n"));
    }


    @Nullable
    @Override
    public Book buildBook() {
        BookBinder binder = new BookBinder(new ResourceLocation("vampirism", "guidebook"));
        binder.setGuideTitleKey("guide.vampirism.title");
        binder.setItemNameKey("guide.vampirism");
        binder.setHeaderKey("guide.vampirism.welcome");
        binder.setAuthor(new StringTextComponent("Maxanier"));
        binder.setColor(Color.WHITE);
        binder.setOutlineTexture(new ResourceLocation("vampirismguide", "textures/gui/book_violet_border.png"));
        binder.setSpawnWithBook();
        binder.setContentProvider(GuideBook::buildCategories);
        return guideBook = binder.build();
    }

    @Nullable
    @Override
    public ResourceLocation getModel() {
        return new ResourceLocation(REFERENCE.MODID, "guidebook");
    }
}
