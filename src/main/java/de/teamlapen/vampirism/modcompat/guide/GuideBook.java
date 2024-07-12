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
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.ILordTitleProvider;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.items.BloodBottleItem;
import de.teamlapen.vampirism.modcompat.guide.pages.PagePotionTableMix;
import de.teamlapen.vampirism.modcompat.guide.pages.PageTable;
import de.teamlapen.vampirism.modcompat.guide.pages.PageTaskItemStack;
import de.teamlapen.vampirism.modcompat.guide.recipes.AlchemicalCauldronRecipeRenderer;
import de.teamlapen.vampirism.modcompat.guide.recipes.ShapedWeaponTableRecipeRenderer;
import de.teamlapen.vampirism.modcompat.guide.recipes.ShapelessWeaponTableRecipeRenderer;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;

@de.maxanier.guideapi.api.GuideBook
public class GuideBook implements IGuideBook {

    private static final Logger LOGGER = LogManager.getLogger();
    private final static String IMAGE_BASE = "vampirismguide:textures/images/";
    @SuppressWarnings("FieldCanBeLocal")
    private static Book guideBook;

    static void buildCategories(@NotNull List<CategoryAbstract> categories) {
        LOGGER.debug("Building content");
        long start = System.currentTimeMillis();
        BookHelper helper = new BookHelper.Builder(REFERENCE.MODID).setBaseKey("guide.vampirism").setLocalizer(GuideBook::translateComponent).setRecipeRendererSupplier(GuideBook::getRenderer).build();
        categories.add(new CategoryItemStack(buildOverview(helper), translateComponent("guide.vampirism.overview.title"), new ItemStack(ModItems.VAMPIRE_FANG.get())));
        categories.add(new CategoryItemStack(buildVampire(helper), translateComponent("guide.vampirism.vampire.title"), BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT)));
        categories.add(new CategoryItemStack(buildHunter(helper), translateComponent("guide.vampirism.hunter.title"), new ItemStack(ModItems.HUMAN_HEART.get())));
        categories.add(new CategoryItemStack(buildCreatures(helper), translateComponent("guide.vampirism.entity.title"), new ItemStack(Items.ZOMBIE_HEAD)));
        categories.add(new CategoryItemStack(buildWorld(helper), translateComponent("guide.vampirism.world.title"), new ItemStack(ModBlocks.CURSED_EARTH.get())));
        categories.add(new CategoryItemStack(buildItems(helper), translateComponent("guide.vampirism.items.title"), new ItemStack(Items.APPLE)));
        categories.add(new CategoryItemStack(buildBlocks(helper), translateComponent("guide.vampirism.blocks.title"), new ItemStack(ModBlocks.DARK_STONE_BRICKS.get())));
        categories.add(new CategoryItemStack(buildChangelog(helper), translateComponent("guide.vampirism.changelog.title"), new ItemStack(Items.WRITABLE_BOOK)));
        NeoForge.EVENT_BUS.post(new VampirismGuideBookCategoriesEvent(categories));
        helper.registerLinkablePages(categories);
        LOGGER.debug("Built content in {} ms", System.currentTimeMillis() - start);
    }

    @Nullable
    private static IRecipeRenderer getRenderer(Recipe<?> recipe) {
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


    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    private static @NotNull Map<ResourceLocation, EntryAbstract> buildOverview(@NotNull BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.overview.";

        List<IPage> introPages = new ArrayList<>();
        introPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "intro.text")));
        entries.put(VResourceLocation.mod(base + "intro"), new EntryText(introPages, translateComponent(base + "intro")));

        List<IPage> gettingStartedPages = new ArrayList<>();
        IPage p = new PageText(translateComponent(base + "getting_started.text"));
        p = new PageHolderWithLinks(helper, p).addLink(VResourceLocation.mod("guide.vampirism.vampire.getting_started")).addLink(VResourceLocation.mod("guide.vampirism.hunter.getting_started"));
        gettingStartedPages.add(p);
        entries.put(VResourceLocation.mod(base + "getting_started"), new EntryText(gettingStartedPages, translateComponent(base + "getting_started")));

        List<IPage> configPages = new ArrayList<>();
        configPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "config.text")));
        configPages.addAll(PageHelper.pagesForLongText(FormattedText.composite(translateComponent(base + "config.general.text"), translateComponent(base + "config.general.examples"))));
        configPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "config.balance.text")));
        entries.put(VResourceLocation.mod(base + "config"), new EntryText(configPages, translateComponent(base + "config")));

        List<IPage> troublePages = new ArrayList<>();
        troublePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "trouble.text")));
        helper.addLinks(troublePages, new PageHolderWithLinks.URLLink(translateComponent(base + "trouble"), URI.create("https://wiki.vampirism.dev/docs/wiki/troubleshooting")));
        entries.put(VResourceLocation.mod(base + "trouble"), new EntryText(troublePages, translateComponent(base + "trouble")));

        List<IPage> devPages = new ArrayList<>();
        PageHolderWithLinks.URLLink helpLink = new PageHolderWithLinks.URLLink(Component.literal("How to help"), URI.create("https://wiki.vampirism.dev/docs/wiki/intro"));
        devPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "dev.text")), helpLink));
        entries.put(VResourceLocation.mod(base + "dev"), new EntryText(devPages, translateComponent(base + "dev")));

        List<IPage> supportPages = new ArrayList<>();
        supportPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "support.text")));
        PageHolderWithLinks.URLLink linkCurseForge = new PageHolderWithLinks.URLLink("CurseForge", URI.create(REFERENCE.CURSEFORGE_LINK));
        PageHolderWithLinks.URLLink linkModrinth = new PageHolderWithLinks.URLLink("Modrinth", URI.create(REFERENCE.MODRINTH_LINK));

        helper.addLinks(supportPages, linkCurseForge, linkModrinth, VResourceLocation.mod(base + "dev"));
        entries.put(VResourceLocation.mod(base + "support"), new EntryText(supportPages, translateComponent(base + "support")));

        List<IPage> creditsPages = new ArrayList<>();
        String lang = VampLib.proxy.getActiveLanguage();
        String credits = "§lDeveloper:§r\nMaxanier\nCheaterpaul\n§lThanks to:§r\nMistadon\nwildbill22\n1LiterZinalco\nAlis\ndimensionpainter\nS_olace\nPiklach\n\n§lTranslators:§r\n§b" + lang + "§r\n" + translateComponent("text.vampirism.translators").getString();
        creditsPages.addAll(PageHelper.pagesForLongText(translateComponent(credits)));
        entries.put(VResourceLocation.mod(base + "credits"), new EntryText(creditsPages, translateComponent(base + "credits")));
        return entries;
    }

    private static Component loc(@NotNull Block b) {
        return b.getName();
    }

    private static Component loc(@NotNull Item i) {
        return i.getDescription();
    }

    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    private static @NotNull Map<ResourceLocation, EntryAbstract> buildVampire(@NotNull BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.vampire.";

        List<IPage> gettingStarted = new ArrayList<>();
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.become")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.as_vampire")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.zombie")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.blood", ModKeys.SUCK.getTranslatedKeyMessage())));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.infecting", VampireActions.INFECT.get().getName())));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.level").append(translateComponent(base + "getting_started.level2"))));

        entries.put(VResourceLocation.mod(base + "getting_started"), new EntryText(gettingStarted, translateComponent(base + "getting_started")));

        List<IPage> bloodPages = new ArrayList<>();
        bloodPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "blood.text", loc(ModItems.BLOOD_BOTTLE.get()), loc(Items.GLASS_BOTTLE))));
        bloodPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "blood.storage", loc(ModBlocks.BLOOD_CONTAINER.get()))), VResourceLocation.mod("guide.vampirism.blocks.blood_container")));
        bloodPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "blood.biteable_creatures")), new PageHolderWithLinks.URLLink("Biteable Creatures", URI.create("https://wiki.vampirism.dev/docs/wiki/content/entities/bitten_animal"))));
        entries.put(VResourceLocation.mod(base + "blood"), new EntryText(bloodPages, translateComponent(base + "blood")));

        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "leveling.intro")));
        MutableComponent altarOfInspiration = Component.literal(String.format("§l%s§r\n", loc(ModBlocks.ALTAR_INSPIRATION.get()).getString())).append(translate(base + "leveling.inspiration.reach")).append("§r\n");
        altarOfInspiration = altarOfInspiration.append(translate(base + "leveling.inspiration.text")).append("\n");
        altarOfInspiration = altarOfInspiration.append(translate(base + "leveling.inspiration.requirements", Arrays.stream(VampireLeveling.getInspirationRequirements()).map(VampireLeveling.AltarInspirationRequirement::bloodAmount).toArray()));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(altarOfInspiration), VResourceLocation.mod("guide.vampirism.blocks.altar_inspiration")));

        MutableComponent altarOfInfusion = Component.literal(String.format("§l%s§r\n", loc(ModBlocks.ALTAR_INFUSION.get()).getString())).append(translate(base + "leveling.infusion.reach")).append("§r\n");
        altarOfInfusion = altarOfInfusion.append(translate(base + "leveling.infusion.intro", loc(ModBlocks.ALTAR_INFUSION.get()), loc(ModBlocks.ALTAR_PILLAR.get()), loc(ModBlocks.ALTAR_TIP.get())));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(altarOfInfusion), VResourceLocation.mod("guide.vampirism.blocks.altar_infusion")));
        StringBuilder blocks = new StringBuilder();
        for (AltarPillarBlock.EnumPillarType t : AltarPillarBlock.EnumPillarType.values()) {
            if (t == AltarPillarBlock.EnumPillarType.NONE) continue;
            blocks.append(translate(t.fillerBlock.getDescriptionId())).append("(").append(t.getValue()).append("),");
        }
        levelingPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "leveling.infusion.structure", blocks.toString())));
        Component items = loc(ModItems.HUMAN_HEART.get()).copy().append(", ").append(loc(ModItems.PURE_BLOOD_0.get())).append(", ").append(loc(ModItems.VAMPIRE_BOOK.get()));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "leveling.infusion.items", items)), VResourceLocation.mod("guide.vampirism.items.human_heart"), VResourceLocation.mod("guide.vampirism.items.pure_blood_0"), VResourceLocation.mod("guide.vampirism.items.vampire_book")));
        PageTable.Builder requirementsBuilder = new PageTable.Builder(5);
        requirementsBuilder.addLine(Component.translatable("text.vampirism.level_short"), Component.translatable(base + "leveling.infusion.req.structure_points"), ModItems.PURE_BLOOD_0.get().getDescription(), Component.translatable(base + "leveling.infusion.req.heart"), Component.translatable(base + "leveling.infusion.req.book"));
        requirementsBuilder.addLine("5", VampireLeveling.getInfusionRequirement(5).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "0", "5", "1");
        requirementsBuilder.addLine("6", VampireLeveling.getInfusionRequirement(6).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "1 Purity(1)", "5", "1");
        requirementsBuilder.addLine("7", VampireLeveling.getInfusionRequirement(7).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "1 Purity(1)", "10", "1");
        requirementsBuilder.addLine("8", VampireLeveling.getInfusionRequirement(8).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "1 Purity(2)", "10", "1");
        requirementsBuilder.addLine("9", VampireLeveling.getInfusionRequirement(9).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "1 Purity(2)", "10", "1");
        requirementsBuilder.addLine("10", VampireLeveling.getInfusionRequirement(10).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "1 Purity(3)", "15", "1");
        requirementsBuilder.addLine("11", VampireLeveling.getInfusionRequirement(11).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "1 Purity(3)", "15", "1");
        requirementsBuilder.addLine("12", VampireLeveling.getInfusionRequirement(12).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "1 Purity(4)", "20", "1");
        requirementsBuilder.addLine("13", VampireLeveling.getInfusionRequirement(13).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "2 Purity(4)", "20", "1");
        requirementsBuilder.addLine("14", VampireLeveling.getInfusionRequirement(14).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElseThrow(), "2 Purity(5)", "25", "1");
        requirementsBuilder.setHeadline(translateComponent(base + "leveling.infusion.req"));
        PageHolderWithLinks requirementTable = new PageHolderWithLinks(helper, requirementsBuilder.build());
        requirementTable.addLink(VResourceLocation.mod("guide.vampirism.items.human_heart"));
        requirementTable.addLink(VResourceLocation.mod("guide.vampirism.items.vampire_book"));
        requirementTable.addLink(VResourceLocation.mod("guide.vampirism.items.pure_blood_0"));
        levelingPages.add(requirementTable);

        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image1"), ResourceLocation.parse(IMAGE_BASE + "infusion1.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image2"), ResourceLocation.parse(IMAGE_BASE + "infusion2.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image3"), ResourceLocation.parse(IMAGE_BASE + "infusion3.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image4"), ResourceLocation.parse(IMAGE_BASE + "infusion4.png"), false));
        levelingPages.add(new PageTextImage(translateComponent(base + "leveling.infusion.image5"), ResourceLocation.parse(IMAGE_BASE + "infusion5.png"), false));

        entries.put(VResourceLocation.mod(base + "leveling"), new EntryText(levelingPages, translateComponent(base + "leveling")));


        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "skills.text")), VResourceLocation.mod(base + "vampirism_menu")));
        skillPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "skills.actions", ModKeys.ACTION.getTranslatedKeyMessage())));
        skillPages.addAll(PageHelper.pagesForLongText(translateComponent("guide.vampirism.skills.bind_action")));
        skillPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "skills.actions2")));
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "skills.refinements")), VResourceLocation.mod("guide.vampirism.items.accessories")));

        entries.put(VResourceLocation.mod(base + "skills"), new EntryText(skillPages, translateComponent(base + "skills")));

        List<IPage> armorPages = new ArrayList<>(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "armor.text")), VResourceLocation.mod("guide.vampirism.items.accessories")));
        entries.put(VResourceLocation.mod(base + "armor"), new EntryText(armorPages, translateComponent(base + "armor")));
        List<IPage> dbnoPages = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base + "dbno.text", ModEffects.NEONATAL.get().getDisplayName())));
        entries.put(VResourceLocation.mod(base + "dbno"), new EntryText(dbnoPages, translateComponent(base + "dbno")));

        List<IPage> lordPages = new ArrayList<>();
        ILordTitleProvider titles = ModFactions.VAMPIRE.value().lordTiles();
        assert titles != null;
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.text", ModEntities.TASK_MASTER_VAMPIRE.get().getDescription(), titles.getLordTitle(1, IPlayableFaction.TitleGender.MALE), titles.getLordTitle(1, IPlayableFaction.TitleGender.FEMALE), titles.getLordTitle(ModFactions.VAMPIRE.value().getHighestLordLevel(), IPlayableFaction.TitleGender.MALE), titles.getLordTitle(ModFactions.VAMPIRE.value().getHighestLordLevel(), IPlayableFaction.TitleGender.FEMALE))), VResourceLocation.mod(("guide.vampirism.entity.taskmaster"))));
        PageTable.Builder lordTitleBuilder = new PageTable.Builder(3).setHeadline(translateComponent(base + "lord.titles"));
        lordTitleBuilder.addLine(Component.translatable("text.vampirism.level"), Component.translatable("text.vampirism.title"), Component.translatable("text.vampirism.title"));
        lordTitleBuilder.addLine(1, titles.getLordTitle(1, IPlayableFaction.TitleGender.MALE), titles.getLordTitle(1, IPlayableFaction.TitleGender.FEMALE));
        lordTitleBuilder.addLine(2, titles.getLordTitle(2, IPlayableFaction.TitleGender.MALE), titles.getLordTitle(2, IPlayableFaction.TitleGender.FEMALE));
        lordTitleBuilder.addLine(3, titles.getLordTitle(3, IPlayableFaction.TitleGender.MALE), titles.getLordTitle(3, IPlayableFaction.TitleGender.FEMALE));
        lordTitleBuilder.addLine(4, titles.getLordTitle(4, IPlayableFaction.TitleGender.MALE), titles.getLordTitle(4, IPlayableFaction.TitleGender.FEMALE));
        lordTitleBuilder.addLine(5, titles.getLordTitle(5, IPlayableFaction.TitleGender.MALE), titles.getLordTitle(5, IPlayableFaction.TitleGender.FEMALE));
        lordPages.add(lordTitleBuilder.build());
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.minion", loc(ModItems.VAMPIRE_MINION_BINDING.get()), loc(ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get()), loc(ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get()), loc(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get()))), VResourceLocation.mod("guide.vampirism.items.vampire_minion_binding")));
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent("guide.vampirism.common.minion_control", ModKeys.MINION.getTranslatedKeyMessage(), translate("text.vampirism.minion.call_single"), translate("text.vampirism.minion.respawn")))));
        entries.put(VResourceLocation.mod(base + "lord"), new EntryText(lordPages, Component.translatable(base + "lord")));


        List<IPage> vampirismMenu = new ArrayList<>(PageHelper.pagesForLongText(translateComponent("guide.vampirism.overview.vampirism_menu.text", ModKeys.VAMPIRISM_MENU.getTranslatedKeyMessage()).append(translateComponent("guide.vampirism.overview.vampirism_menu.text_vampire", translateComponent("guide.vampirism.items.accessories"))))); //Lang key shared with vampires
        entries.put(VResourceLocation.mod(base + "vampirism_menu"), new EntryText(vampirismMenu, translateComponent("guide.vampirism.overview.vampirism_menu")));


        List<IPage> unvampirePages = new ArrayList<>();
        unvampirePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "unvampire.text", loc(ModBlocks.ALTAR_CLEANSING.get()))));
        entries.put(VResourceLocation.mod(base + "unvampire"), new EntryText(unvampirePages, translateComponent(base + "unvampire")));

        return entries;
    }

    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    private static @NotNull Map<ResourceLocation, EntryAbstract> buildHunter(@NotNull BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.hunter.";

        List<IPage> gettingStarted = new ArrayList<>();
        Component become = translateComponent(base + "getting_started.become", translateComponent(ModEntities.HUNTER_TRAINER.get().getDescriptionId()), loc(ModItems.INJECTION_GARLIC.get()));
        gettingStarted.addAll(helper.addLinks(PageHelper.pagesForLongText(become), VResourceLocation.mod("guide.vampirism.items.injection_empty")));
        gettingStarted.add(new PageImage(ResourceLocation.parse(IMAGE_BASE + "hunter_trainer.png")));
        gettingStarted.addAll(PageHelper.pagesForLongText(translateComponent(base + "getting_started.as_hunter")));
        entries.put(VResourceLocation.mod(base + "getting_started"), new EntryText(gettingStarted, translateComponent(base + "getting_started")));

        List<IPage> levelingPages = new ArrayList<>();
        levelingPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "leveling.intro")));
        String train1 = "§l" + translate(base + "leveling.to_reach", "2-4") + "§r\n";
        train1 += translate(base + "leveling.train1.text", HunterLeveling.getBasicHunterRequirements().stream().map(HunterLeveling.BasicHunterRequirement::vampireBloodAmount).toArray());
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(Component.literal(train1)), VResourceLocation.mod("guide.vampirism.items.stake"), VResourceLocation.mod("guide.vampirism.items.vampire_blood_bottle")));

        String train2 = "§l" + translate(base + "leveling.to_reach", "5+") + "§r\n";
        train2 += translate(base + "leveling.train2.text", loc(ModBlocks.HUNTER_TABLE.get()), loc(ModBlocks.WEAPON_TABLE.get()), loc(ModBlocks.POTION_TABLE.get()), loc(ModBlocks.ALCHEMICAL_CAULDRON.get()));
        levelingPages.addAll(helper.addLinks(PageHelper.pagesForLongText(Component.translatable(train2)), VResourceLocation.mod("guide.vampirism.blocks.hunter_table"), VResourceLocation.mod("guide.vampirism.blocks.weapon_table"), VResourceLocation.mod("guide.vampirism.blocks.alchemical_cauldron"), VResourceLocation.mod("guide.vampirism.blocks.potion_table")));
        PageTable.Builder builder = new PageTable.Builder(4);
        builder.addLine(Component.translatable("text.vampirism.level"), Component.translatable(base + "leveling.train2.fang"), loc(ModItems.PURE_BLOOD_0.get()), loc(ModItems.VAMPIRE_BOOK.get()));
        HunterLeveling.getTrainerRequirements().forEach(requirement -> {
            var tableReq = requirement.tableRequirement();
            String pure = "";
            if (tableReq.pureBloodLevel() > 0) {
                pure = tableReq.pureBloodQuantity() + " Purity(" + (tableReq.pureBloodLevel() + 1) + ")";
            }
            builder.addLine(requirement.targetLevel(), tableReq.vampireFangQuantity(), pure, tableReq.vampireBookQuantity());
        });

        builder.setHeadline(translateComponent(base + "leveling.train2.req"));
        PageHolderWithLinks requirementsTable = new PageHolderWithLinks(helper, builder.build());
        requirementsTable.addLink(VResourceLocation.mod("guide.vampirism.items.vampire_fang"));
        requirementsTable.addLink(VResourceLocation.mod("guide.vampirism.items.pure_blood_0"));
        requirementsTable.addLink(VResourceLocation.mod("guide.vampirism.items.vampire_book"));
        levelingPages.add(requirementsTable);

        entries.put(VResourceLocation.mod(base + "leveling"), new EntryText(levelingPages, translateComponent(base + "leveling")));

        List<IPage> skillPages = new ArrayList<>();
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "skills.intro")), VResourceLocation.mod(base + "vampirism_menu")));
        String disguise = String.format("§l%s§r\n", HunterActions.DISGUISE_HUNTER.get().getName().getString());
        disguise += translate(base + "skills.disguise.text", ModKeys.ACTION.getTranslatedKeyMessage());
        skillPages.addAll(PageHelper.pagesForLongText(Component.literal(disguise)));
        String weaponTable = String.format("§l%s§r\n", loc(ModBlocks.WEAPON_TABLE.get()).getString());
        weaponTable += translate(base + "skills.weapon_table.text");
        skillPages.addAll(helper.addLinks(PageHelper.pagesForLongText(Component.literal(weaponTable)), VResourceLocation.mod("guide.vampirism.blocks.weapon_table")));
        entries.put(VResourceLocation.mod(base + "skills"), new EntryText(skillPages, translateComponent(base + "skills")));
        String potionTable = String.format("§l%s§r\n", loc(ModBlocks.POTION_TABLE.get()).getString());
        potionTable += translate(base + "skills.potion_table.text");
        List<IPage> potionTablePages = new ArrayList<>(PageHelper.pagesForLongText(Component.literal(potionTable)));
        potionTablePages.addAll(Arrays.asList(generatePotionMixes()));
        skillPages.addAll(helper.addLinks(potionTablePages, VResourceLocation.mod("guide.vampirism.blocks.potion_table")));
        entries.put(VResourceLocation.mod(base + "skills"), new EntryText(skillPages, Component.translatable(base + "skills")));

        List<IPage> vampSlayerPages = new ArrayList<>();
        vampSlayerPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "vamp_slayer.intro")));
        String garlic = String.format("§l%s§r\n", loc(ModBlocks.GARLIC.get()).getString());
        garlic += translate(base + "vamp_slayer.garlic") + "\n" + translate(base + "vamp_slayer.garlic2") + "\n" + translate(base + "vamp_slayer.garlic.diffuser");
        vampSlayerPages.addAll(helper.addLinks(PageHelper.pagesForLongText(Component.literal(garlic)), VResourceLocation.mod("guide.vampirism.blocks.garlic_diffuser")));
        String holyWater = String.format("§l%s§r\n", loc(ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).getString());
        holyWater += translate(base + "vamp_slayer.holy_water");
        vampSlayerPages.addAll(helper.addLinks(PageHelper.pagesForLongText(Component.literal(holyWater)), VResourceLocation.mod("guide.vampirism.items.holy_water_bottle")));
        String fire = String.format("§l%s§r\n", loc(Blocks.FIRE).getString());
        fire += translate(base + "vamp_slayer.fire");
        vampSlayerPages.addAll(helper.addLinks(PageHelper.pagesForLongText(Component.literal(fire)), VResourceLocation.mod("guide.vampirism.items.item_alchemical_fire"), VResourceLocation.mod("guide.vampirism.items.crossbow_arrow_normal")));
        entries.put(VResourceLocation.mod(base + "vamp_slayer"), new EntryText(vampSlayerPages, translateComponent(base + "vamp_slayer")));

        List<IPage> lordPages = new ArrayList<>();
        ILordTitleProvider titles = ModFactions.HUNTER.value().lordTiles();
        assert titles != null;
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.text", ModEntities.TASK_MASTER_HUNTER.get().getDescription(), titles.getLordTitle(1, IPlayableFaction.TitleGender.MALE), titles.getLordTitle(ModFactions.HUNTER.value().getHighestLordLevel(), IPlayableFaction.TitleGender.MALE))), VResourceLocation.mod("guide.vampirism.entity.taskmaster")));
        PageTable.Builder lordTitleBuilder = new PageTable.Builder(2);
        lordTitleBuilder.setHeadline(translateComponent(base + "lord.titles"));
        lordTitleBuilder.addLine(Component.translatable("text.vampirism.level"), "text.vampirism.title");
        lordTitleBuilder.addLine(1, titles.getLordTitle(1, IPlayableFaction.TitleGender.MALE));
        lordTitleBuilder.addLine(2, titles.getLordTitle(2, IPlayableFaction.TitleGender.MALE));
        lordTitleBuilder.addLine(3, titles.getLordTitle(3, IPlayableFaction.TitleGender.MALE));
        lordTitleBuilder.addLine(4, titles.getLordTitle(4, IPlayableFaction.TitleGender.MALE));
        lordTitleBuilder.addLine(5, titles.getLordTitle(5, IPlayableFaction.TitleGender.MALE));
        lordPages.add(lordTitleBuilder.build());
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "lord.minion", loc(ModItems.HUNTER_MINION_EQUIPMENT.get()), loc(ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get()), loc(ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get()), loc(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get()))), VResourceLocation.mod("guide.vampirism.items.hunter_minion_equipment")));
        lordPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent("guide.vampirism.common.minion_control", ModKeys.MINION.getTranslatedKeyMessage(), translate("text.vampirism.minion.call_single"), translate("text.vampirism.minion.respawn")))));
        entries.put(VResourceLocation.mod(base + "lord"), new EntryText(lordPages, Component.translatable(base + "lord")));

        List<IPage> vampirismMenu = new ArrayList<>(PageHelper.pagesForLongText(translateComponent("guide.vampirism.overview.vampirism_menu.text", ModKeys.VAMPIRISM_MENU.getTranslatedKeyMessage()))); //Lang key shared with vampires
        entries.put(VResourceLocation.mod(base + "vampirism_menu"), new EntryText(vampirismMenu, translateComponent("guide.vampirism.overview.vampirism_menu")));

        List<IPage> unHunterPages = new ArrayList<>();
        unHunterPages.addAll(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "unhunter.text", loc(ModItems.INJECTION_SANGUINARE.get()), loc(ModBlocks.MED_CHAIR.get()))), VResourceLocation.mod("guide.vampirism.items.injection_empty"), VResourceLocation.mod("guide.vampirism.blocks.item_med_chair")));
        entries.put(VResourceLocation.mod(base + "unhunter"), new EntryText(unHunterPages, translateComponent(base + "unhunter")));

        return entries;
    }

    private static @NotNull Map<ResourceLocation, EntryAbstract> buildCreatures(@NotNull BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.entity.";

        ArrayList<IPage> generalPages = new ArrayList<>(PageHelper.pagesForLongText(FormattedText.composite(translateComponent(base + "general.text"), translateComponent(base + "general.text2"))));
        entries.put(VResourceLocation.mod(base + "general"), new EntryText(generalPages, translateComponent(base + "general")));

        ArrayList<IPage> hunterPages = new ArrayList<>();
        hunterPages.add(new PageEntity((world) -> {
            BasicHunterEntity entity = ModEntities.HUNTER.get().create(world);
            entity.setEntityLevel(1);
            return entity;
        }));
        hunterPages.add(new PageEntity((world) -> {
            BasicHunterEntity entity = ModEntities.HUNTER.get().create(world);
            entity.setEntityLevel(0);
            return entity;
        }));
        hunterPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "hunter.text", loc(ModItems.HUMAN_HEART.get()))));
        entries.put(VResourceLocation.mod(base + "hunter"), new EntryText(hunterPages, ModEntities.HUNTER.get().getDescription()));

        ArrayList<IPage> vampirePages = new ArrayList<>();
        vampirePages.add(new PageEntity(ModEntities.VAMPIRE.get()));
        vampirePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "vampire.text", loc(ModItems.VAMPIRE_FANG.get()), loc(ModItems.VAMPIRE_BLOOD_BOTTLE.get()), loc(ModItems.STAKE.get()))));
        entries.put(VResourceLocation.mod(base + "vampire"), new EntryText(vampirePages, ModEntities.VAMPIRE.get().getDescription()));

        ArrayList<IPage> advancedHunterPages = new ArrayList<>();
        advancedHunterPages.add(new PageEntity(ModEntities.ADVANCED_HUNTER.get()));
        advancedHunterPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "advanced_hunter.text")));
        entries.put(VResourceLocation.mod(base + "advanced_hunter"), new EntryText(advancedHunterPages, ModEntities.ADVANCED_HUNTER.get().getDescription()));

        ArrayList<IPage> advancedVampirePages = new ArrayList<>();
        advancedVampirePages.add(new PageEntity(ModEntities.ADVANCED_VAMPIRE.get()));
        advancedVampirePages.addAll(PageHelper.pagesForLongText(translateComponent(base + "advanced_vampire.text", loc(ModItems.BLOOD_BOTTLE.get()), loc(ModItems.VAMPIRE_BLOOD_BOTTLE.get()))));
        entries.put(VResourceLocation.mod(base + "advanced_vampire"), new EntryText(advancedVampirePages, ModEntities.ADVANCED_VAMPIRE.get().getDescription()));

        ArrayList<IPage> vampireBaronPages = new ArrayList<>();
        vampireBaronPages.add(new PageEntity(ModEntities.VAMPIRE_BARON.get()));
        vampireBaronPages.add(new PageEntity((world) -> {
            VampireBaronEntity baron = ModEntities.VAMPIRE_BARON.get().create(world);
            baron.setLady(true);
            return baron;
        }, ModEntities.VAMPIRE_BARON.get().getDescription()));
        vampireBaronPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "vampire_baron.text", loc(ModItems.PURE_BLOOD_0.get()))));
        helper.addLinks(vampireBaronPages, VResourceLocation.mod("guide.vampirism.world.vampire_forest"));
        entries.put(VResourceLocation.mod(base + "vampire_baron"), new EntryText(vampireBaronPages, ModEntities.VAMPIRE_BARON.get().getDescription()));

        ArrayList<IPage> hunterTrainerPages = new ArrayList<>();
        hunterTrainerPages.add(new PageEntity(ModEntities.HUNTER_TRAINER.get()));
        hunterTrainerPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "hunter_trainer.text")));
        entries.put(VResourceLocation.mod(base + "hunter_trainer"), new EntryText(hunterTrainerPages, ModEntities.HUNTER_TRAINER.get().getDescription()));

        ArrayList<IPage> taskMasterPages = new ArrayList<>();
        taskMasterPages.add(new PageEntity(ModEntities.TASK_MASTER_VAMPIRE.get()));
        taskMasterPages.add(new PageEntity(ModEntities.TASK_MASTER_HUNTER.get()));
        taskMasterPages.addAll(PageHelper.pagesForLongText(translateComponent(base + "taskmaster.text")));
        taskMasterPages.add(new PageImage(ResourceLocation.parse(IMAGE_BASE + "taskscreen.png")));
        entries.put(VResourceLocation.mod(base + "taskmaster"), new EntryText(taskMasterPages, Component.translatable(base + "taskmaster")));


        return entries;
    }

    private static @NotNull Map<ResourceLocation, EntryAbstract> buildWorld(@NotNull BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.world.";

        List<IPage> vampireForestPages = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base + "vampire_forest.text")));
        entries.put(VResourceLocation.mod(base + "vampire_forest"), new EntryText(vampireForestPages, translateComponent(base + "vampire_forest")));

        List<IPage> villagePages = new ArrayList<>(helper.addLinks(PageHelper.pagesForLongText(translateComponent(base + "villages.text").append("\n").append(translateComponent(base + "villages.raids"))), VResourceLocation.mod("guide.vampirism.blocks.totem_base"), VResourceLocation.mod("guide.vampirism.blocks.totem_top_crafted")));
        entries.put(VResourceLocation.mod(base + "villages"), new EntryText(villagePages, translateComponent(base + "villages")));


        return entries;
    }

    private static @NotNull Map<ResourceLocation, EntryAbstract> buildItems(@NotNull BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.items.";
        //General
        helper.info(ModItems.VAMPIRE_FANG.get()).build(entries);
        helper.info(ModItems.HUMAN_HEART.get()).build(entries);
        helper.info(ModItems.PURE_BLOOD_0.get(), ModItems.PURE_BLOOD_1.get(), ModItems.PURE_BLOOD_2.get(), ModItems.PURE_BLOOD_3.get(), ModItems.PURE_BLOOD_4.get()).setFormats(translateComponent(ModEntities.VAMPIRE_BARON.get().getDescriptionId())).build(entries);
        helper.info(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setFormats(translateComponent(ModEntities.VAMPIRE.get().getDescriptionId()), translateComponent(ModEntities.ADVANCED_VAMPIRE.get().getDescriptionId(), loc(ModItems.STAKE.get()))).build(entries);
        helper.info(ModItems.VAMPIRE_BOOK.get()).build(entries);
        helper.info(ModItems.OBLIVION_POTION.get()).customPages(new PageTaskItemStack(ModTasks.OBLIVION_POTION)).build(entries);

        //Vampire
        helper.info(false, BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT)).build(entries);
        helper.info(ModItems.BLOOD_INFUSED_IRON_INGOT.get()).recipes("vampire/blood_infused_iron_ingot", "vampire/blood_infused_enhanced_iron_ingot").build(entries);
        helper.info(ModItems.HEART_SEEKER_NORMAL.get(), ModItems.HEART_SEEKER_ENHANCED.get(), ModItems.HEART_SEEKER_ULTIMATE.get()).recipes("vampire/heart_seeker_normal", "vampire/heart_seeker_enhanced").build(entries);
        helper.info(ModItems.HEART_STRIKER_NORMAL.get(), ModItems.HEART_STRIKER_ENHANCED.get(), ModItems.HEART_STRIKER_ULTIMATE.get()).recipes("vampire/heart_striker_normal", "vampire/heart_striker_normal").build(entries);
        helper.info(ModItems.FEEDING_ADAPTER.get()).customPages(new PageTaskItemStack(ModTasks.FEEDING_ADAPTER)).build(entries);
        helper.info(ModItems.VAMPIRE_MINION_BINDING.get(), ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get(), ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get(), ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get()).setFormats(loc(ModItems.VAMPIRE_MINION_BINDING.get()), loc(ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get()), ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get().getMinLevel() + 1, ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get().getMaxLevel() + 1, loc(ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get()), ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get().getMinLevel() + 1, ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get().getMaxLevel() + 1, loc(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get()), ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get().getMinLevel() + 1, ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get().getMaxLevel() + 1, translate(ModEntities.TASK_MASTER_VAMPIRE.get().getDescriptionId())).setLinks(VResourceLocation.mod("guide.vampirism.entity.taskmaster"), VResourceLocation.mod("guide.vampirism.vampire.lord")).build(entries);
        helper.info(ModItems.GARLIC_FINDER.get()).setLinks(VResourceLocation.mod("guide.vampirism.blocks.garlic_diffuser")).recipes("vampire/garlic_finder").build(entries);
        helper.info(ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get(), ModItems.VAMPIRE_CLOTHING_LEGS.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get()).useCustomEntryName().setKeyName("vampire_clothing").recipes("vampire/vampire_clothing_legs", "vampire/vampire_clothing_boots", "vampire/vampire_clothing_hat", "vampire/vampire_clothing_crown", "vampire/vampire_cloak_black_red", "vampire/vampire_cloak_black_blue", "vampire/vampire_cloak_black_white", "vampire/vampire_cloak_red_black", "vampire/vampire_cloak_white_black").build(entries);
        helper.info(ModItems.AMULET.get(), ModItems.RING.get(), ModItems.OBI_BELT.get()).setLinks(VResourceLocation.mod("guide.vampirism.vampire.vampirism_menu")).useCustomEntryName().setKeyName("accessories").build(entries);

        //Hunter
        helper.info(ModBlocks.GARLIC.get()).build(entries);
        helper.info(ModItems.INJECTION_EMPTY.get(), ModItems.INJECTION_GARLIC.get(), ModItems.INJECTION_SANGUINARE.get()).recipes("general/injection_0", "general/injection_1", "general/injection_2").build(entries);
        helper.info(ModItems.HUNTER_INTEL_0.get()).setLinks(VResourceLocation.mod("guide.vampirism.blocks.hunter_table")).setFormats(loc(ModBlocks.HUNTER_TABLE.get())).build(entries);
        helper.info(ModItems.PURIFIED_GARLIC.get()).setFormats(loc(ModBlocks.GARLIC_DIFFUSER_NORMAL.get())).setLinks(VResourceLocation.mod("guide.vampirism.blocks.garlic_diffuser")).recipes("purified_garlic").build(entries);
        helper.info(ModItems.PITCHFORK.get()).recipes("pitchfork").build(entries);
        helper.info(ModItems.STAKE.get()).setFormats(((int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100)) + "%").recipes("hunter/stake").build(entries);
        helper.info(ModItems.BASIC_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get()).setFormats(loc(ModItems.CROSSBOW_ARROW_NORMAL.get()), loc(ModItems.ARROW_CLIP.get())).setLinks(VResourceLocation.mod("guide.vampirism.items.crossbow_arrow_normal")).recipes("basic_crossbow", "enhanced_crossbow", "basic_double_crossbow", "enhanced_double_crossbow", "basic_tech_crossbow", "enhanced_tech_crossbow", "tech_crossbow_ammo_package").useCustomEntryName().setKeyName("crossbows").build(entries);
        helper.info(ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get()).recipes("hunter/crossbow_arrow_normal", "crossbow_arrow_spitfire", "crossbow_arrow_vampire_killer").build(entries);
        helper.info(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setLinks(VResourceLocation.mod("guide.vampirism.hunter.vamp_slayer"), VResourceLocation.mod("guide.vampirism.items.pure_salt")).setFormats(loc(ModItems.PURE_SALT_WATER.get()), loc(ModItems.PURE_SALT_WATER.get()), loc(ModItems.PURE_SALT.get())).brewingItems(ModItems.PURE_SALT_WATER.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get()).setKeyName("holy_water_bottle").build(entries);
        helper.info(ModItems.PURE_SALT.get()).setLinks(VResourceLocation.mod("guide.vampirism.items.holy_water_bottle")).setFormats(loc(ModItems.PURE_SALT.get()), loc(ModItems.PURE_SALT.get()), loc(ModBlocks.ALCHEMICAL_CAULDRON.get())).recipes("alchemical_cauldron/pure_salt").build(entries);
        helper.info(ModItems.ITEM_ALCHEMICAL_FIRE.get()).setLinks(VResourceLocation.mod("guide.vampirism.items.crossbow_arrow_normal")).recipes("alchemical_fire_4", "alchemical_fire_5", "alchemical_fire_6").build(entries);
        helper.info(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).recipes("armor_of_swiftness_chest_normal", "armor_of_swiftness_legs_normal", "armor_of_swiftness_head_normal", "armor_of_swiftness_feet_normal", "armor_of_swiftness_chest_enhanced", "armor_of_swiftness_legs_enhanced", "armor_of_swiftness_head_enhanced", "armor_of_swiftness_feet_enhanced").build(entries);
        helper.info(ModItems.HUNTER_COAT_CHEST_NORMAL.get(), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), ModItems.HUNTER_COAT_HEAD_NORMAL.get(), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), ModItems.HUNTER_COAT_FEET_NORMAL.get(), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).recipes("hunter_coat_chest_normal", "hunter_coat_legs_normal", "hunter_coat_head_normal", "hunter_coat_feet_normal", "hunter_coat_chest_enhanced", "hunter_coat_legs_enhanced", "hunter_coat_head_enhanced", "hunter_coat_feet_enhanced").build(entries);
        helper.info(ModItems.HUNTER_AXE_NORMAL.get(), ModItems.HUNTER_AXE_ENHANCED.get(), ModItems.HUNTER_AXE_ULTIMATE.get()).setKeyName("hunter_axe").recipes("hunter_axe_normal", "hunter_axe_enhanced").build(entries);
        helper.info(ModItems.HUNTER_MINION_EQUIPMENT.get(), ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get(), ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get(), ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get()).setFormats(loc(ModItems.HUNTER_MINION_EQUIPMENT.get()), loc(ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get()), ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get().getMinLevel() + 1, ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get().getMaxLevel() + 1, loc(ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get()), ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get().getMinLevel() + 1, ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get().getMaxLevel() + 1, loc(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get()), ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get().getMinLevel() + 1, ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get().getMaxLevel() + 1, translate(ModEntities.TASK_MASTER_HUNTER.get().getDescriptionId())).setLinks(VResourceLocation.mod("guide.vampirism.entity.taskmaster"), VResourceLocation.mod("guide.vampirism.hunter.lord")).build(entries);
        helper.info(ModItems.CRUCIFIX_NORMAL.get(), ModItems.CRUCIFIX_ENHANCED.get(), ModItems.CRUCIFIX_ULTIMATE.get()).setKeyName("crucifix").recipes("hunter/crucifix","crucifix_enhanced", "crucifix_ultimate").build(entries);
        return entries;
    }

    private static @NotNull Map<ResourceLocation, EntryAbstract> buildBlocks(@NotNull BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.blocks.";
        //General
        helper.info(ModBlocks.DARK_STONE_BRICKS.get()).recipes("general/castle_block_dark_brick_0", "general/castle_block_dark_brick_1", "general/castle_block_dark_stone", "general/castle_block_normal_brick", "general/castle_block_purple_brick", "general/castle_slab_dark_brick", "general/castle_stairs_dark_brick").build(entries);
        helper.info(ModBlocks.VAMPIRE_ORCHID.get()).build(entries);
        //Vampire
        helper.info(ModBlocks.BLOOD_CONTAINER.get()).recipes("vampire/blood_container").build(entries);
        helper.info(ModBlocks.ALTAR_INSPIRATION.get()).setLinks(VResourceLocation.mod("guide.vampirism.vampire.leveling")).recipes("vampire/altar_inspiration").build(entries);
        helper.info(ModBlocks.ALTAR_INFUSION.get()).setLinks(VResourceLocation.mod("guide.vampirism.vampire.leveling")).recipes("vampire/altar_infusion", "vampire/altar_pillar", "vampire/altar_tip").build(entries);
        helper.info(ModBlocks.COFFIN_RED.get()).setKeyName("blocks.coffin").useCustomEntryName().recipes("vampire/coffin_red").build(entries);
        helper.info(ModBlocks.ALTAR_CLEANSING.get()).build(entries);
        //Hunter
        helper.info(true, new ItemStack(ModBlocks.MED_CHAIR.get())).setFormats(loc(ModItems.INJECTION_GARLIC.get()), loc(ModItems.INJECTION_SANGUINARE.get())).recipes("hunter/item_med_chair").build(entries);
        helper.info(ModBlocks.HUNTER_TABLE.get()).setFormats(loc(ModItems.HUNTER_INTEL_0.get())).setLinks(VResourceLocation.mod("guide.vampirism.hunter.leveling"), VResourceLocation.mod("guide.vampirism.items.hunter_intel")).recipes("hunter/hunter_table").build(entries);
        helper.info(ModBlocks.WEAPON_TABLE.get()).recipes("hunter/weapon_table").build(entries);
        helper.info(ModBlocks.ALCHEMICAL_CAULDRON.get()).recipes("hunter/alchemical_cauldron").build(entries);
        int cn = VampirismConfig.BALANCE.hsGarlicDiffuserNormalDist.get() * 2 + 1;
        int ce = VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist.get() * 2 + 1;
        helper.info(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), ModBlocks.GARLIC_DIFFUSER_WEAK.get(), ModBlocks.GARLIC_DIFFUSER_WEAK.get()).setFormats(cn, cn, ce, ce, loc(ModItems.PURIFIED_GARLIC.get())).useCustomEntryName().setKeyName("garlic_diffuser").setLinks(VResourceLocation.mod("guide.vampirism.items.item_garlic"), VResourceLocation.mod("guide.vampirism.items.purified_garlic"), VResourceLocation.mod("guide.vampirism.items.holy_water_bottle")).recipes("hunter/garlic_diffuser_normal", "hunter/garlic_diffuser_improved", "alchemical_cauldron/garlic_diffuser_core", "alchemical_cauldron/garlic_diffuser_core_improved").build(entries);
        helper.info(ModBlocks.BLOOD_PEDESTAL.get()).recipes("vampire/blood_pedestal").build(entries);
        helper.info(ModBlocks.BLOOD_GRINDER.get()).recipes("general/blood_grinder").setFormats(loc(ModItems.HUMAN_HEART.get()), loc(Items.BEEF), loc(ModBlocks.BLOOD_SIEVE.get())).build(entries);
        helper.info(ModBlocks.BLOOD_SIEVE.get()).recipes("general/blood_sieve").setFormats(ModFluids.IMPURE_BLOOD_TYPE.get().getDescription(), loc(ModBlocks.BLOOD_GRINDER.get())).setLinks(VResourceLocation.mod("guide.vampirism.blocks.blood_grinder")).build(entries);
        helper.info(ModBlocks.TOTEM_TOP_CRAFTED.get(), ModBlocks.TOTEM_TOP.get()).setLinks(VResourceLocation.mod("guide.vampirism.blocks.totem_base"), VResourceLocation.mod("guide.vampirism.world.villages")).build(entries);
        helper.info(ModBlocks.TOTEM_BASE.get()).recipes("general/totem_base").setLinks(VResourceLocation.mod("guide.vampirism.blocks.totem_top_crafted"), VResourceLocation.mod("guide.vampirism.world.villages")).build(entries);
        helper.info(ModBlocks.POTION_TABLE.get()).recipes("hunter/potion_table").customPages(generatePotionMixes()).build(entries);
        ItemStack activatedOil = ModItems.OIL_BOTTLE.get().withOil(ModOils.VAMPIRE_BLOOD);
        helper.info(ModBlocks.ALCHEMY_TABLE.get()).recipes("alchemy_table").setFormats(ModItems.OIL_BOTTLE.get().getName(activatedOil)).build(entries);

        List<IPage> decorativeBlocks = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base + "decorative.text"), ModItems.ITEM_CANDELABRA.get()));
        decorativeBlocks.add(helper.getRecipePage(VResourceLocation.mod("vampire/candelabra")));
        decorativeBlocks.add(helper.getRecipePage(VResourceLocation.mod("vampire/chandelier")));
        decorativeBlocks.add(helper.getRecipePage(VResourceLocation.mod("hunter/cross")));
        decorativeBlocks.add(helper.getRecipePage(VResourceLocation.mod("general/tombstone1")));
        decorativeBlocks.add(helper.getRecipePage(VResourceLocation.mod("general/tombstone2")));
        decorativeBlocks.add(helper.getRecipePage(VResourceLocation.mod("general/tombstone3")));
        decorativeBlocks.add(helper.getRecipePage(VResourceLocation.mod("general/grave_cage")));

        entries.put(VResourceLocation.mod(base + "decorative"), new EntryItemStack(decorativeBlocks, Component.translatable(base + "decorative.title"), new ItemStack(ModItems.ITEM_CANDELABRA.get())));
        return entries;
    }

    public static @NotNull Map<ResourceLocation, EntryAbstract> buildChangelog(BookHelper helper) {
        Map<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<>();
        String base = "guide.vampirism.changelog.";
        entries.put(VResourceLocation.mod(base + "v1_8"), buildChangelog1_8());
        entries.put(VResourceLocation.mod(base + "v1_9"), buildChangelog1_9());
        return entries;
    }

    public static @NotNull EntryAbstract buildChangelog1_8() {
        String base = "guide.vampirism.changelog.";
        String base1_8 = base + "v1_8.";

        //Vampirism 1.8
        List<IPage> v1_8 = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base1_8 + "overview.text")));
        //vampirism menu
        List<IPage> vampirism_menu = PageHelper.pagesForLongText(translateComponent(base1_8 + "vampirism_menu.text", ModKeys.VAMPIRISM_MENU.getTranslatedKeyMessage()));
        vampirism_menu.add(new PageTextImage(translateComponent(base1_8 + "vampirism_menu.image"), ResourceLocation.parse(IMAGE_BASE + "vampirism_menu.png"), false));
        v1_8.addAll(vampirism_menu);
        //vampire accessories
        List<IPage> accessories = PageHelper.pagesForLongText(translateComponent(base1_8 + "accessories.text"));
        accessories.add(new PageTextImage(translateComponent(base1_8 + "accessories.image"), ResourceLocation.parse(IMAGE_BASE + "vampire_accessories.png"), false));
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
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.vista.image"), VResourceLocation.mod("textures/skills/vampire_forest_fog.png"), false));
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.neonatal.image"), VResourceLocation.mod("textures/skills/neonatal_decrease.png"), false));
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.dbno.image"), VResourceLocation.mod("textures/skills/dbno_duration.png"), false));
        skills.add(new PageTextImage(translateComponent(base1_8 + "skills.hissing.image"), VResourceLocation.mod("textures/actions/hissing.png"), false));
        v1_8.addAll(skills);
        //balancing
        List<IPage> balancing = PageHelper.pagesForLongText(translateComponent(base1_8 + "balancing.text"));
        v1_8.addAll(balancing);
        //misc
        List<IPage> misc = PageHelper.pagesForLongText(translateComponent(base1_8 + "misc.text"));
        v1_8.addAll(misc);
        return new EntryResourceLocation(v1_8, Component.literal("Vampirism 1.8"), VResourceLocation.mc("textures/item/writable_book.png"));
    }

    public static EntryAbstract buildChangelog1_9() {
        String base = "guide.vampirism.changelog.";
        String base1_9 = base + "v1_9.";

        //Vampirism 1.9
        List<IPage> v1_9 = new ArrayList<>(PageHelper.pagesForLongText(translateComponent(base1_9 + "overview.text")));

        //general
        List<IPage> general = PageHelper.pagesForLongText(translateComponent(base1_9 + "general.text"));
        v1_9.addAll(general);

        //weapon oils
        List<IPage> oils = PageHelper.pagesForLongText(translateComponent(base1_9 + "oils.text"));
        v1_9.addAll(oils);

        //lord skills
        List<IPage> skills = PageHelper.pagesForLongText(translateComponent(base1_9 + "skills.text"));
        v1_9.addAll(skills);

        //item blessing
        List<IPage> blessing = PageHelper.pagesForLongText(translateComponent(base1_9 + "blessing.text"));
        v1_9.addAll(blessing);

        //crucifix
        List<IPage> crucifix = PageHelper.pagesForLongText(translateComponent(base1_9 + "crucifix.text"));
        v1_9.addAll(crucifix);

        //vampire infection
        List<IPage> infection = PageHelper.pagesForLongText(translateComponent(base1_9 + "infection.text"));
        v1_9.addAll(infection);

        //crossbows
        List<IPage> crossbows = PageHelper.pagesForLongText(translateComponent(base1_9 + "crossbows.text"));
        v1_9.addAll(crossbows);

        //curing creatures
        List<IPage> curing = PageHelper.pagesForLongText(translateComponent(base1_9 + "curing.text"));
        v1_9.addAll(curing);

        return new EntryResourceLocation(v1_9, Component.literal("Vampirism 1.9"), VResourceLocation.mc("textures/item/writable_book.png"));
    }

    private static IPage @NotNull [] generatePotionMixes() {
        IPage[] pages = new IPage[6];
        pages[0] = new PagePotionTableMix(HunterSkills.DURABLE_BREWING.get().getName(), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.durable && !mix.concentrated && !mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[1] = new PagePotionTableMix(HunterSkills.CONCENTRATED_BREWING.get().getName(), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.concentrated && !mix.durable && !mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[2] = new PagePotionTableMix(HunterSkills.DURABLE_BREWING.get().getName().copy().append("\n").append(HunterSkills.EFFICIENT_BREWING.get().getName()), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.durable && !mix.concentrated && mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[3] = new PagePotionTableMix(HunterSkills.CONCENTRATED_BREWING.get().getName().copy().append("\n").append(HunterSkills.EFFICIENT_BREWING.get().getName()), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.concentrated && !mix.durable && mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[4] = new PagePotionTableMix(HunterSkills.MASTER_BREWER.get().getName(), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.master && !mix.durable && !mix.concentrated && !mix.efficient).toArray(ExtendedPotionMix[]::new));
        pages[5] = new PagePotionTableMix(HunterSkills.MASTER_BREWER.get().getName().copy().append("\n").append(HunterSkills.EFFICIENT_BREWING.get().getName()), VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().filter(mix -> mix.master && !mix.durable && !mix.concentrated && mix.efficient).toArray(ExtendedPotionMix[]::new));
        return pages;
    }

    public static @NotNull MutableComponent translateComponent(String key, Object... format) {
        String result = Component.translatable(key, format).getString();
        return Component.literal(result.replaceAll("\\\\n", Matcher.quoteReplacement("\n"))); //Fix legacy newlines. //Probably shouldn't use new StringTextComponent here, but don't want to rewrite everything
    }

    public static @NotNull String translate(String key, Object... format) {
        String result = Component.translatable(key, format).getString();
        return result.replaceAll("\\\\n", Matcher.quoteReplacement("\n"));
    }


    @Nullable
    @Override
    public Book buildBook() {
        BookBinder binder = new BookBinder(VResourceLocation.mod("guidebook"));
        binder.setGuideTitleKey("guide.vampirism.title");
        binder.setItemNameKey("guide.vampirism");
        binder.setHeaderKey("guide.vampirism.welcome");
        binder.setAuthor(Component.literal("Maxanier"));
        binder.setColor(Color.WHITE.getRGB());
        binder.setOutlineTexture(ResourceLocation.fromNamespaceAndPath("vampirismguide", "textures/gui/book_violet_border.png"));
        binder.setSpawnWithBook();
        binder.setContentProvider(GuideBook::buildCategories);
        return guideBook = binder.build();
    }

    @Nullable
    @Override
    public ResourceLocation getModel() {
        return VResourceLocation.mod("item/guidebook");
    }
}
