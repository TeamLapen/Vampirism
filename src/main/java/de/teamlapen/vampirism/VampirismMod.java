package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.config.BloodValueLoaderDynamic;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.ModCompatLoader;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
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
import de.teamlapen.vampirism.potion.blood.BloodPotionRegistry;
import de.teamlapen.vampirism.potion.blood.BloodPotions;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.proxy.ServerProxy;
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.util.*;
import de.teamlapen.vampirism.world.GarlicChunkHandler;
import de.teamlapen.vampirism.world.WorldGenManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.Optional;

/**
 * Main class for Vampirism
 */
@Mod(value = REFERENCE.MODID)
public class VampirismMod {

    public static final AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static final ItemGroup creativeTab = new ItemGroup(REFERENCE.MODID) {

        @Override
        public ItemStack createIcon() {

            return new ItemStack(ModItems.vampire_fang);
        }
    };
    private final static Logger LOGGER = LogManager.getLogger();
    /**
     * Hunter creatures are of this creature type. Use the instance in
     * {@link VReference} instead of this one. This is only here to init it as early
     * as possible
     */
    private final static EntityClassification HUNTER_CREATURE_TYPE = EntityClassification.create("vampirism_hunter", "vampirism_hunter", 25, false, false);
    /**
     * Vampire creatures are of this creature type. Use the instance in
     * {@link VReference} instead of this one. This is only here to init it as early
     * as possible
     */
    private static final EntityClassification VAMPIRE_CREATURE_TYPE = EntityClassification.create("vampirism_vampire", "vampirism_vampire", 30, false, false);
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

    public final ModCompatLoader modCompatLoader = new ModCompatLoader(REFERENCE.MODID + "/vampirism_mod_compat.cfg");
    private final RegistryManager registryManager;
    private VersionChecker.VersionInfo versionInfo;

    public VampirismMod() {
        instance = this;
        checkDevEnv();
        checkDataEnv();

        Optional<? extends net.minecraftforge.fml.ModContainer> opt = ModList.get().getModContainerById(REFERENCE.MODID);
        if (opt.isPresent()) {
            REFERENCE.VERSION = opt.get().getModInfo().getVersion();
        } else {
            LOGGER.warn("Cannot get version from mod info");
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::onModelBakeEvent));
        VampirismConfig.registerConfigs();
        MinecraftForge.EVENT_BUS.register(this);
        addModCompats();
        registryManager = new RegistryManager();
        FMLJavaModLoadingContext.get().getModEventBus().register(registryManager);
        MinecraftForge.EVENT_BUS.register(registryManager);

        setupAPI1();
        setupAPI2();


        File vampConfigDir = new File(FMLPaths.CONFIGDIR.get().toFile(), REFERENCE.MODID);

    }

    public VersionChecker.VersionInfo getVersionInfo() {

        return versionInfo;
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        SkillTreeManager.getInstance().getSkillTree().initRootSkills();//Load root skills here, so even if data pack reload fail, the root skills are available #622
        event.getServer().getResourceManager().addReloadListener(SkillTreeManager.getInstance());
        event.getServer().getResourceManager().addReloadListener(BloodValues.ENTITIES);
        event.getServer().getResourceManager().addReloadListener(BloodValues.ITEMS);
        event.getServer().getResourceManager().addReloadListener(BloodValues.FLUIDS);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
        ModCommands.registerCommands(event.getCommandDispatcher());
        for (BloodValueLoaderDynamic loader : BloodValues.getDynamicLoader()) {
            loader.onServerStarting(event.getServer());
        }
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        if (!ModLootTables.checkAndResetInsertedAll()) {
            LOGGER.warn("LootTables -------------------------------");
            LOGGER.warn("LootTables Failed to inject all loottables");
            LOGGER.warn("LootTables -------------------------------");
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

    private void checkDevEnv() {
        String launchTarget = System.getenv().get("target");
        if (launchTarget != null && launchTarget.contains("dev")) {
            inDev = true;
        }
    }

    private void checkDataEnv() {
        String launchTarget = System.getenv().get("target");
        if(launchTarget != null && launchTarget.contains("data")) {
            inDataGen = true;
        }
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        finishAPI1();


        HelperRegistry.registerPlayerEventReceivingCapability(VampirePlayer.CAP, VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability(HunterPlayer.CAP, HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability(ExtendedCreature.CAP, REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability(VampirePlayer.CAP, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(HunterPlayer.CAP, REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(FactionPlayerHandler.CAP, REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.class);

    }

    /**
     * Finish during init
     */
    private void finishAPI1() {
        ((FactionRegistry) VampirismAPI.factionRegistry()).finish();
    }

    /**
     * Finish during post-init
     */
    private void finishAPI2() {
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).finishRegistration();
        ((BloodPotionRegistry) VampirismAPI.bloodPotionRegistry()).finish();
    }

    private void gatherData(final GatherDataEvent event) {
        registryManager.onInitStep(IInitListener.Step.GATHER_DATA, event);
        DataGenerator gen = event.getGenerator();
        if (event.includeServer()) {
            TagGenerator.register(gen);
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
    }

    private void processIMC(final InterModProcessEvent event) {
        finishAPI2();
        registryManager.onInitStep(IInitListener.Step.PROCESS_IMC, event);
        IMCHandler.handleInterModMessage(event);
        if (inDev) {
            Tests.runBackgroundTests();
        }
        ModWorld.addVillageStructures();
    }

    private void setup(final FMLCommonSetupEvent event) {
        HunterPlayer.registerCapability();
        VampirePlayer.registerCapability();
        FactionPlayerHandler.registerCapability();
        ExtendedCreature.registerCapability();


        modCompatLoader.onInitStep(IInitListener.Step.COMMON_SETUP, event);
        setupAPI3();

        dispatcher.registerPackets();
        registryManager.onInitStep(IInitListener.Step.COMMON_SETUP, event);
        proxy.onInitStep(IInitListener.Step.COMMON_SETUP, event);

        if (!VampirismConfig.COMMON.versionCheck.get()) {
            versionInfo = new VersionChecker.VersionInfo(REFERENCE.VERSION);
        } else {
            versionInfo = VersionChecker.executeVersionCheck(REFERENCE.VERSION_UPDATE_FILE, REFERENCE.VERSION, !inDev && VampirismConfig.COMMON.collectStats.get());
        }

        ModEventHandler eventHandler = new ModEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());
        MinecraftForge.EVENT_BUS.addListener(ModLootTables::onLootLoad);

        SupporterManager.getInstance().initAsync();
        VampireBookManager.getInstance().init();
        BloodPotions.register();
        Permissions.init();
        VampirismEntitySelectors.registerSelectors();

    }

    /**
     * Called during construction
     */
    private void setupAPI1() {
        FactionRegistry factionRegistry = new FactionRegistry();
        SundamageRegistry sundamageRegistry = new SundamageRegistry();
        VampirismEntityRegistry biteableRegistry = new VampirismEntityRegistry();
        ActionManager actionManager = new ActionManager();
        SkillManager skillManager = new SkillManager();
        GeneralRegistryImpl generalRegistry = new GeneralRegistryImpl();
        ActionManagerEntity entityActionManager = new ActionManagerEntity();
        WorldGenManager worldGenRegistry = new WorldGenManager();

        biteableRegistry.setDefaultConvertingHandlerCreator(DefaultConvertingHandler::new);
        BloodPotionRegistry bloodPotionRegistry = new BloodPotionRegistry();
        VampirismAPI.setUpRegistries(factionRegistry, sundamageRegistry, biteableRegistry, actionManager, skillManager, generalRegistry, bloodPotionRegistry, entityActionManager, worldGenRegistry);
        VampirismAPI.setUpAccessors(new GarlicChunkHandler.Provider());
    }

    /**
     * Setup API during pre-init before configs are loaded
     */
    private void setupAPI2() {
        VReference.VAMPIRE_FACTION = VampirismAPI.factionRegistry().registerPlayableFaction(REFERENCE.VAMPIRE_PLAYER_KEY, IVampirePlayer.class, Color.MAGENTA.darker().darker(), true, () -> VampirePlayer.CAP, REFERENCE.HIGHEST_VAMPIRE_LEVEL);
        VReference.VAMPIRE_FACTION.setChatColor(TextFormatting.DARK_PURPLE).setTranslationKeys("text.vampirism.vampire", "text.vampirism.vampires");
        VReference.HUNTER_FACTION = VampirismAPI.factionRegistry().registerPlayableFaction(REFERENCE.HUNTER_PLAYER_KEY, IHunterPlayer.class, Color.BLUE, false, () -> HunterPlayer.CAP, REFERENCE.HIGHEST_HUNTER_LEVEL);
        VReference.HUNTER_FACTION.setChatColor(TextFormatting.DARK_BLUE).setTranslationKeys("text.vampirism.hunter", "text.vampirism.hunters");
        VReference.HUNTER_CREATURE_TYPE = HUNTER_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_TYPE = VAMPIRE_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_ATTRIBUTE = VAMPIRE_CREATURE_ATTRIBUTE;
    }

    /**
     * Setup API during pre-init after configs are loaded
     */
    private void setupAPI3() {
        VReference.vision_nightVision = VampirismAPI.vampireVisionRegistry().registerVision("nightVision", new NightVision());
        VReference.vision_bloodVision = VampirismAPI.vampireVisionRegistry().registerVision("bloodVision", new BloodVision());
    }

    private void setupClient(FMLClientSetupEvent event) {
        registryManager.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
        proxy.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
        modCompatLoader.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
    }

}
