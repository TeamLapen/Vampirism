package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.config.BloodValueLoaderDynamic;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.ModCompatLoader;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.config.BloodValues;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.*;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.VampirismEntitySelectors;
import de.teamlapen.vampirism.entity.action.ActionManagerEntity;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.inventory.recipes.ExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import de.teamlapen.vampirism.misc.VampirismLogger;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.network.ModPacketDispatcher;
import de.teamlapen.vampirism.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.player.actions.ActionManager;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.skills.SkillManager;
import de.teamlapen.vampirism.player.skills.SkillTreeManager;
import de.teamlapen.vampirism.player.vampire.BloodVision;
import de.teamlapen.vampirism.player.vampire.NightVision;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.proxy.ServerProxy;
import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.util.*;
import de.teamlapen.vampirism.world.VampirismWorld;
import de.teamlapen.vampirism.world.WorldGenManager;
import de.teamlapen.vampirism.world.biome.VampireForestBiome;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Optional;

/**
 * Main class for Vampirism
 */
@Mod(value = REFERENCE.MODID)
public class VampirismMod {

    public static final AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static final ItemGroup creativeTab = new ItemGroup(REFERENCE.MODID) {

        @Override
        public ItemStack makeIcon() {

            return new ItemStack(ModItems.VAMPIRE_FANG.get());
        }
//
//        @Override
//        public void fill(NonNullList<ItemStack> items) { //Sort based on registry name. Not ideal
//            ForgeRegistries.ITEMS.getValues().stream().sorted(Comparator.comparing(ForgeRegistryEntry::getRegistryName)).forEach(item->item.fillItemGroup(this,items));
//        }
    };
    private final static Logger LOGGER = LogManager.getLogger();
    /**
     * Hunter creatures are of this creature type. Use the instance in
     * {@link VReference} instead of this one. This is only here to init it as early
     * as possible
     */
    private final static EntityClassification HUNTER_CREATURE_TYPE = EntityClassification.create("vampirism_hunter", "vampirism_hunter", 25, false, false, 128);
    /**
     * Vampire creatures are of this creature type. Use the instance in
     * {@link VReference} instead of this one. This is only here to init it as early
     * as possible
     */
    private static final EntityClassification VAMPIRE_CREATURE_TYPE = EntityClassification.create("vampirism_vampire", "vampirism_vampire", 30, false, false, 128);
    /**
     * Vampire creatures have this attribute Vampire creatures are of this creature
     * type. Use the instance in {@link VReference} instead of this one. This is
     * only here to init it as early as possible
     */
    private static final CreatureAttribute VAMPIRE_CREATURE_ATTRIBUTE = new CreatureAttribute();

    public static VampirismMod instance;
    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static boolean inDev = false;
    public static boolean inDataGen = false;

    public static boolean isRealism() {
        return false;
    }

    public final ModCompatLoader modCompatLoader = new ModCompatLoader();
    private final RegistryManager registryManager;
    private VersionChecker.VersionInfo versionInfo;

    public VampirismMod() {
        instance = this;
        checkEnv();
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> VampirismLogger::init);

        Optional<? extends net.minecraftforge.fml.ModContainer> opt = ModList.get().getModContainerById(REFERENCE.MODID);
        if (opt.isPresent()) {
            REFERENCE.VERSION = opt.get().getModInfo().getVersion();
        } else {
            LOGGER.warn("Cannot get version from mod info");
        }

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        modbus.addListener(this::setup);
        modbus.addListener(this::enqueueIMC);
        modbus.addListener(this::processIMC);
        modbus.addListener(this::loadComplete);
        modbus.addListener(this::gatherData);
        modbus.addGenericListener(Block.class, this::finalizeConfiguration);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modbus.addListener(ClientEventHandler::onModelBakeEvent);
            modbus.addListener(this::setupClient);
            modbus.addListener(ClientEventHandler::onModelRegistry);
        });
        VampirismConfig.init();
        MinecraftForge.EVENT_BUS.register(this);
        addModCompats();
        registryManager = new RegistryManager();
        modbus.register(registryManager);
        MinecraftForge.EVENT_BUS.register(registryManager);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ModBiomes::onBiomeLoadingEventAdditions);
        MinecraftForge.EVENT_BUS.addListener(VampireForestBiome::addFeatures);
        MinecraftForge.EVENT_BUS.register(SitHandler.class);

        prepareAPI();
        
        RegistryManager.setupRegistries(modbus);

        if (OptifineHandler.isOptifineLoaded()) {
            LOGGER.warn("Using Optifine. Expect visual glitches and reduces blood vision functionality if using shaders.");
        }
    }

    public VersionChecker.VersionInfo getVersionInfo() {

        return versionInfo;
    }

    @SubscribeEvent
    public void onAddReloadListenerEvent(AddReloadListenerEvent event) {
        SkillTreeManager.getInstance().getSkillTree().initRootSkills();//Load root skills here, so even if data pack reload fail, the root skills are available #622
        event.addListener(SkillTreeManager.getInstance());
        event.addListener(new BloodValues());

    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        ModCommands.registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        VampirismWorldGen.addVillageStructures(event.getServer().registryAccess());
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
        for (BloodValueLoaderDynamic loader : BloodValues.getDynamicLoader()) {
            loader.onServerStarting(event.getServer());
        }
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        int missing = ModLootTables.checkAndResetInsertedAll();
        if (missing > 0) {
            LOGGER.warn("LootTables Failed to inject {} loottables", missing);
        }
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        for (BloodValueLoaderDynamic loader : BloodValues.getDynamicLoader()) {
            loader.onServerStopping();
        }
    }

    private void addModCompats() {

    }

    private void checkEnv() {
        String launchTarget = System.getenv().get("target");
        if (launchTarget != null && launchTarget.contains("dev")) {
            inDev = true;
        }
        if (launchTarget != null && launchTarget.contains("data")) {
            inDataGen = true;
        }
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

        HelperRegistry.registerPlayerEventReceivingCapability(VampirePlayer.CAP, VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability(HunterPlayer.CAP, HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability(ExtendedCreature.CAP, REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability(VampirePlayer.CAP, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(HunterPlayer.CAP, REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(FactionPlayerHandler.CAP, REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.class);

    }

    private void finalizeConfiguration(RegistryEvent<Block> event) {
        VampirismConfig.finalizeAndRegisterConfig();
    }

    /**
     * Finish API during InterModProcessEvent
     */
    private void finishAPI() {
        ((FactionRegistry) VampirismAPI.factionRegistry()).finish();
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).finishRegistration();
    }

    private void gatherData(final GatherDataEvent event) {
        registryManager.onGatherData(event);
        DataGenerator gen = event.getGenerator();
        if (event.includeServer()) {
            TagGenerator.register(gen, event.getExistingFileHelper());
            gen.addProvider(new LootTablesGenerator(gen));
            gen.addProvider(new AdvancementGenerator(gen));
            gen.addProvider(new RecipesGenerator(gen));
            gen.addProvider(new SkillNodeGenerator(gen));
        }
        if (event.includeClient()) {
            gen.addProvider(new BlockStateGenerator(event.getGenerator(), event.getExistingFileHelper()));
            gen.addProvider(new ItemModelGenerator(event.getGenerator(), event.getExistingFileHelper()));
        }
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        registryManager.onInitStep(IInitListener.Step.LOAD_COMPLETE, event);
        proxy.onInitStep(IInitListener.Step.LOAD_COMPLETE, event);
        modCompatLoader.onInitStep(IInitListener.Step.LOAD_COMPLETE, event);
        VampirismAPI.skillManager().registerSkillType(SkillType.LEVEL);
        VampirismAPI.skillManager().registerSkillType(SkillType.LORD);
    }

    /**
     * Called during constructor to setup the API as well as VReference
     */
    private void prepareAPI() {
        FactionRegistry factionRegistry = new FactionRegistry();
        SundamageRegistry sundamageRegistry = new SundamageRegistry();
        VampirismEntityRegistry biteableRegistry = new VampirismEntityRegistry();
        ActionManager actionManager = new ActionManager();
        SkillManager skillManager = new SkillManager();
        GeneralRegistryImpl generalRegistry = new GeneralRegistryImpl();
        ActionManagerEntity entityActionManager = new ActionManagerEntity();
        WorldGenManager worldGenRegistry = new WorldGenManager();
        ExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry = new ExtendedBrewingRecipeRegistry();

        biteableRegistry.setDefaultConvertingHandlerCreator(DefaultConvertingHandler::new);
        VampirismAPI.setUpRegistries(factionRegistry, sundamageRegistry, biteableRegistry, actionManager, skillManager, generalRegistry, entityActionManager, worldGenRegistry, extendedBrewingRecipeRegistry);


        VReference.VAMPIRE_FACTION = VampirismAPI.factionRegistry()
                .createPlayableFaction(REFERENCE.VAMPIRE_PLAYER_KEY, IVampirePlayer.class, () -> VampirePlayer.CAP)
                .color(Color.MAGENTA.darker().darker())
                .chatColor(TextFormatting.DARK_PURPLE)
                .name("text.vampirism.vampire")
                .namePlural("text.vampirism.vampires")
                .hostileTowardsNeutral()
                .highestLevel(REFERENCE.HIGHEST_VAMPIRE_LEVEL)
                .lordLevel(REFERENCE.HIGHEST_VAMPIRE_LORD)
                .lordTitle(LordTitles::getVampireTitle)
                .village(VampireVillageData::vampireVillage)
                .refinementItems(VampireRefinementItem::getItemForType)
                .enableLordSkills()
                .register();
        VReference.HUNTER_FACTION = VampirismAPI.factionRegistry()
                .createPlayableFaction(REFERENCE.HUNTER_PLAYER_KEY, IHunterPlayer.class, () -> HunterPlayer.CAP)
                .color(Color.BLUE)
                .chatColor(TextFormatting.BLUE)
                .name("text.vampirism.hunter")
                .namePlural("text.vampirism.hunters")
                .highestLevel(REFERENCE.HIGHEST_HUNTER_LEVEL)
                .lordLevel(REFERENCE.HIGHEST_HUNTER_LORD)
                .lordTitle(LordTitles::getHunterTitle)
                .village(HunterVillageData::hunterVillage)
                .enableLordSkills()
                .register();
        VReference.HUNTER_CREATURE_TYPE = HUNTER_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_TYPE = VAMPIRE_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_ATTRIBUTE = VAMPIRE_CREATURE_ATTRIBUTE;
        VReference.vision_nightVision = VampirismAPI.vampireVisionRegistry().registerVision("nightVision", new NightVision());
        VReference.vision_bloodVision = VampirismAPI.vampireVisionRegistry().registerVision("bloodVision", new BloodVision());

        VampirismAPI.onSetupComplete();
    }

    private void processIMC(final InterModProcessEvent event) {
        finishAPI();
        registryManager.onInitStep(IInitListener.Step.PROCESS_IMC, event);
        IMCHandler.handleInterModMessage(event);
        if (inDev) {
            Tests.runBackgroundTests();
        }
        VampirismWorldGen.createJigsawPool();
    }

    private void setup(final FMLCommonSetupEvent event) {
        HunterPlayer.registerCapability();
        VampirePlayer.registerCapability();
        FactionPlayerHandler.registerCapability();
        ExtendedCreature.registerCapability();
        VampirismWorld.registerCapability();
        modCompatLoader.onInitStep(IInitListener.Step.COMMON_SETUP, event);

        dispatcher.registerPackets();
        registryManager.onInitStep(IInitListener.Step.COMMON_SETUP, event);
        proxy.onInitStep(IInitListener.Step.COMMON_SETUP, event);

        if (!VampirismConfig.COMMON.versionCheck.get()) {
            versionInfo = new VersionChecker.VersionInfo(REFERENCE.VERSION);
        } else {
            versionInfo = VersionChecker.executeVersionCheck(REFERENCE.VERSION_UPDATE_FILE, REFERENCE.VERSION, !inDev && VampirismConfig.COMMON.collectStats.get());
        }

        GeneralEventHandler eventHandler = new GeneralEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());
        MinecraftForge.EVENT_BUS.addListener(ModLootTables::onLootLoad);

        SupporterManager.getInstance().initAsync();
        VampireBookManager.getInstance().init();
        Permissions.init();
        VampirismEntitySelectors.registerSelectors();

    }

    private void setupClient(FMLClientSetupEvent event) {
        registryManager.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
        proxy.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
        modCompatLoader.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
    }

}
