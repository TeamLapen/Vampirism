package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.config.BloodValueLoaderDynamic;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.ModCompatLoader;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.lib.util.Color;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
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
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.util.*;
import de.teamlapen.vampirism.world.WorldGenManager;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import net.minecraft.ChatFormatting;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Main class for Vampirism
 */
@Mod(value = REFERENCE.MODID)
public class VampirismMod {

    public static final AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static final CreativeModeTab creativeTab = new CreativeModeTab(REFERENCE.MODID) {

        @Nonnull
        @Override
        public ItemStack makeIcon() {

            return new ItemStack(ModItems.vampire_fang);
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
    private final static MobCategory HUNTER_CREATURE_TYPE = MobCategory.create("vampirism_hunter", "vampirism_hunter", 25, false, false, 128);
    /**
     * Vampire creatures are of this creature type. Use the instance in
     * {@link VReference} instead of this one. This is only here to init it as early
     * as possible
     */
    private static final MobCategory VAMPIRE_CREATURE_TYPE = MobCategory.create("vampirism_vampire", "vampirism_vampire", 30, false, false, 128);
    /**
     * Vampire creatures have this attribute Vampire creatures are of this creature
     * type. Use the instance in {@link VReference} instead of this one. This is
     * only here to init it as early as possible
     */
    @SuppressWarnings("InstantiationOfUtilityClass")
    private static final MobType VAMPIRE_CREATURE_ATTRIBUTE = new MobType();

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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCapabilities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::finalizeConfiguration);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            //TODO 1.17 maybe cleanup
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::onModelBakeEvent);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntitiesRender::onRegisterRenderers);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntitiesRender::onRegisterLayers);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntitiesRender::onAddLayers);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ModBlocksRender::registerBlockEntityRenderers);
        });
        VampirismConfig.init();
        MinecraftForge.EVENT_BUS.register(this);
        addModCompats();
        registryManager = new RegistryManager();
        FMLJavaModLoadingContext.get().getModEventBus().register(registryManager);
        MinecraftForge.EVENT_BUS.register(registryManager);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ModBiomes::onBiomeLoadingEventAdditions);

        prepareAPI();

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
        event.addListener(BloodValues.ENTITIES);
        event.addListener(BloodValues.ITEMS);
        event.addListener(BloodValues.FLUIDS);

    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        ModCommands.registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        VampirismWorldGen.addVillageStructures();
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
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

    private void checkEnv() {
        String launchTarget = System.getProperty("vampirism_target");
        if (launchTarget != null && launchTarget.contains("dev")) {
            inDev = true;
        }
        if (launchTarget != null && launchTarget.contains("data")) {
            inDataGen = true;
        }
    }

    @SuppressWarnings("unchecked")
    private void enqueueIMC(final InterModEnqueueEvent event) {
        onInitStep(IInitListener.Step.ENQUEUE_IMC, event);
        HelperRegistry.registerPlayerEventReceivingCapability((Capability<IPlayerEventListener>) (Object) VampirePlayer.CAP, VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability((Capability<IPlayerEventListener>) (Object) HunterPlayer.CAP, HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) ExtendedCreature.CAP, REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) VampirePlayer.CAP, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) HunterPlayer.CAP, REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) FactionPlayerHandler.CAP, REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.class);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IExtendedCreatureVampirism.class);
        event.register(IFactionPlayerHandler.class);
        event.register(IHunterPlayer.class);
        event.register(IVampirePlayer.class);
        event.register(IVampirismWorld.class);
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
        onInitStep(IInitListener.Step.LOAD_COMPLETE, event);
    }

    /**
     * Called during constructor to set up the API as well as VReference
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


        VReference.VAMPIRE_FACTION = VampirismAPI.factionRegistry().registerPlayableFaction(REFERENCE.VAMPIRE_PLAYER_KEY, IVampirePlayer.class, Color.MAGENTA_DARK.getRGB(), true, () -> VampirePlayer.CAP, REFERENCE.HIGHEST_VAMPIRE_LEVEL, REFERENCE.HIGHEST_VAMPIRE_LORD, LordTitles::getVampireTitle, new VampireVillageData());
        VReference.VAMPIRE_FACTION.setChatColor(ChatFormatting.DARK_PURPLE).setTranslationKeys("text.vampirism.vampire", "text.vampirism.vampires");
        VReference.HUNTER_FACTION = VampirismAPI.factionRegistry().registerPlayableFaction(REFERENCE.HUNTER_PLAYER_KEY, IHunterPlayer.class, Color.BLUE.getRGB(), false, () -> HunterPlayer.CAP, REFERENCE.HIGHEST_HUNTER_LEVEL, REFERENCE.HIGHEST_HUNTER_LORD, LordTitles::getHunterTitle, new HunterVillageData());
        VReference.HUNTER_FACTION.setChatColor(ChatFormatting.BLUE).setTranslationKeys("text.vampirism.hunter", "text.vampirism.hunters");
        VReference.HUNTER_CREATURE_TYPE = HUNTER_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_TYPE = VAMPIRE_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_ATTRIBUTE = VAMPIRE_CREATURE_ATTRIBUTE;
        VReference.vision_nightVision = VampirismAPI.vampireVisionRegistry().registerVision("nightVision", new NightVision());
        VReference.vision_bloodVision = VampirismAPI.vampireVisionRegistry().registerVision("bloodVision", new BloodVision());

        VampirismAPI.onSetupComplete();
    }

    private void processIMC(final InterModProcessEvent event) {
        finishAPI();
        onInitStep(IInitListener.Step.PROCESS_IMC, event);
        IMCHandler.handleInterModMessage(event);
        if (inDev) {
            Tests.runBackgroundTests();
        }
        VampirismWorldGen.initVillageStructures();
    }

    private void setup(final FMLCommonSetupEvent event) {
        dispatcher.registerPackets();
        onInitStep(IInitListener.Step.COMMON_SETUP, event);
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
        onInitStep(IInitListener.Step.CLIENT_SETUP, event);
    }

    private void onInitStep(IInitListener.Step step, ParallelDispatchEvent event) {
        registryManager.onInitStep(step, event);
        proxy.onInitStep(step, event);
        modCompatLoader.onInitStep(step, event);
    }

}
