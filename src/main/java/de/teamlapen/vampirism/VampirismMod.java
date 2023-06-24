package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.util.Color;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismCapabilities;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import de.teamlapen.vampirism.client.core.ClientRegistryHandler;
import de.teamlapen.vampirism.config.BloodValues;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.reloadlistener.SingleJigsawGeneration;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.action.ActionManagerEntity;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.entity.player.actions.ActionManager;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.skills.SkillManager;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeManager;
import de.teamlapen.vampirism.entity.player.vampire.BloodVision;
import de.teamlapen.vampirism.entity.player.vampire.NightVision;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import de.teamlapen.vampirism.items.crossbow.CrossbowArrowHandler;
import de.teamlapen.vampirism.misc.SettingsProvider;
import de.teamlapen.vampirism.misc.VampirismLogger;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.modcompat.terrablender.TerraBlenderCompat;
import de.teamlapen.vampirism.network.ModPacketDispatcher;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.proxy.ServerProxy;
import de.teamlapen.vampirism.recipes.ExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.util.*;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.gen.VanillaStructureModifications;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Main class for Vampirism
 */
@Mod(value = REFERENCE.MODID)
public class VampirismMod {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static VampirismMod instance;
    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static boolean inDev = false;
    public static boolean inDataGen = false;

    private final @NotNull RegistryManager registryManager = new RegistryManager();


    public VampirismMod() {
        instance = this;
        checkEnv();

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        modbus.addListener(this::setup);
        modbus.addListener(this::enqueueIMC);
        modbus.addListener(this::processIMC);
        modbus.addListener(this::loadComplete);
        modbus.addListener(this::registerCapabilities);
        modbus.addListener(this::finalizeConfiguration);
        modbus.addListener(VersionUpdater::catchModVersionMismatch);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientRegistryHandler::init);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modbus.addListener(this::setupClient);
        });

        MinecraftForge.EVENT_BUS.register(Permissions.class);
        MinecraftForge.EVENT_BUS.register(SitHandler.class);
        MinecraftForge.EVENT_BUS.register(new GeneralEventHandler());
        MinecraftForge.EVENT_BUS.addListener(this::onCommandsRegister);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListenerEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(VersionUpdater::checkVersionUpdated);

        VampirismConfig.init();

        prepareAPI();
        RegistryManager.setupRegistries(modbus);
        modbus.addListener(ModItems::registerOtherCreativeTabItems);

        if (OptifineHandler.isOptifineLoaded()) {
            LOGGER.warn("Using Optifine. Expect visual glitches and reduces blood vision functionality if using shaders.");
        }
    }

    public void onAddReloadListenerEvent(@NotNull AddReloadListenerEvent event) {
        SkillTreeManager.getInstance().getSkillTree().initRootSkills();//Load root skills here, so even if data pack reload fail, the root skills are available #622
        event.addListener(SkillTreeManager.getInstance());
        event.addListener(new BloodValues());
        event.addListener(new SingleJigsawGeneration());
    }

    public void onCommandsRegister(@NotNull RegisterCommandsEvent event) {
        ModCommands.registerCommands(event.getDispatcher(), event.getBuildContext());
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
    private void enqueueIMC(final @NotNull InterModEnqueueEvent event) {
        onInitStep(IInitListener.Step.ENQUEUE_IMC, event);
        HelperRegistry.registerPlayerEventReceivingCapability((Capability<IPlayerEventListener>) (Object) VampirePlayer.CAP, VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability((Capability<IPlayerEventListener>) (Object) HunterPlayer.CAP, HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) ExtendedCreature.CAP, REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) VampirePlayer.CAP, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) HunterPlayer.CAP, REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) FactionPlayerHandler.CAP, REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.class);
    }

    private void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        event.register(IExtendedCreatureVampirism.class);
        event.register(IFactionPlayerHandler.class);
        event.register(IHunterPlayer.class);
        event.register(IVampirePlayer.class);
        event.register(IVampirismWorld.class);
    }

    private void onServerStarting(@NotNull ServerAboutToStartEvent event) {
        VanillaStructureModifications.addVillageStructures(event.getServer().registryAccess());
    }

    private void finalizeConfiguration(RegisterEvent event) {
        VampirismConfig.finalizeAndRegisterConfig();
    }

    /**
     * Finish API during InterModProcessEvent
     */
    private void finishAPI() {
        ((FactionRegistry) VampirismAPI.factionRegistry()).finish();
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).finishRegistration();
    }

    private void loadComplete(final @NotNull FMLLoadCompleteEvent event) {
        onInitStep(IInitListener.Step.LOAD_COMPLETE, event);
        event.enqueueWork(OverworldModifications::addBiomesToOverworldUnsafe);
        VampirismAPI.skillManager().registerSkillType(SkillType.LEVEL);
        VampirismAPI.skillManager().registerSkillType(SkillType.LORD);
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> VampirismLogger::init);
    }


    /**
     * Called during constructor to set up the API as well as VReference
     */
    private void prepareAPI() {

        VampirismAPI.setUpRegistries(new FactionRegistry(), new SundamageRegistry(), new VampirismEntityRegistry().setDefaultConvertingHandlerCreator(DefaultConvertingHandler::new), new ActionManager(), new SkillManager(), new VampireVisionRegistry(), new ActionManagerEntity(), new ExtendedBrewingRecipeRegistry(), new SettingsProvider(REFERENCE.SETTINGS_API, REFERENCE.SETTINGS_API_VERSION));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> proxy::setupAPIClient);

        VReference.VAMPIRE_FACTION = VampirismAPI.factionRegistry()
                .createPlayableFaction(REFERENCE.VAMPIRE_PLAYER_KEY, IVampirePlayer.class, () -> VampirePlayer.CAP)
                .color(Color.MAGENTA_DARK.getRGB())
                .chatColor(ChatFormatting.DARK_PURPLE)
                .name("text.vampirism.vampire")
                .namePlural("text.vampirism.vampires")
                .hostileTowardsNeutral()
                .highestLevel(REFERENCE.HIGHEST_VAMPIRE_LEVEL)
                .lord().lordLevel(REFERENCE.HIGHEST_VAMPIRE_LORD).lordTitle(LordTitles::getVampireTitle).enableLordSkills()
                .minion(VampireMinionEntity.VampireMinionData.ID).minionData(VampireMinionEntity.VampireMinionData::new).build()
                .build()
                .village(VampireVillage::vampireVillage)
                .refinementItems(VampireRefinementItem::getItemForType)
                .addTag(ForgeRegistries.Keys.BIOMES, ModTags.Biomes.IS_VAMPIRE_BIOME)
                .addTag(ForgeRegistries.Keys.POI_TYPES, ModTags.PoiTypes.IS_VAMPIRE)
                .addTag(ForgeRegistries.Keys.VILLAGER_PROFESSIONS, ModTags.Professions.IS_VAMPIRE)
                .addTag(ForgeRegistries.Keys.ENTITY_TYPES, ModTags.Entities.VAMPIRE)
                .addTag(VampirismRegistries.TASK_ID, ModTags.Tasks.IS_VAMPIRE)
                .register();
        VReference.HUNTER_FACTION = VampirismAPI.factionRegistry()
                .createPlayableFaction(REFERENCE.HUNTER_PLAYER_KEY, IHunterPlayer.class, () -> HunterPlayer.CAP)
                .color(Color.BLUE.getRGB())
                .chatColor(ChatFormatting.BLUE)
                .name("text.vampirism.hunter")
                .namePlural("text.vampirism.hunters")
                .highestLevel(REFERENCE.HIGHEST_HUNTER_LEVEL)
                .lord().lordLevel(REFERENCE.HIGHEST_HUNTER_LORD).lordTitle(LordTitles::getHunterTitle).enableLordSkills()
                .minion(HunterMinionEntity.HunterMinionData.ID).minionData(HunterMinionEntity.HunterMinionData::new).build()
                .build()
                .village(HunterVillage::hunterVillage)
                .addTag(ForgeRegistries.Keys.BIOMES, ModTags.Biomes.IS_HUNTER_BIOME)
                .addTag(ForgeRegistries.Keys.POI_TYPES, ModTags.PoiTypes.IS_HUNTER)
                .addTag(ForgeRegistries.Keys.VILLAGER_PROFESSIONS, ModTags.Professions.IS_HUNTER)
                .addTag(ForgeRegistries.Keys.ENTITY_TYPES, ModTags.Entities.HUNTER)
                .addTag(VampirismRegistries.TASK_ID, ModTags.Tasks.IS_HUNTER)
                .register();

        VReference.vision_nightVision = VampirismAPI.vampireVisionRegistry().registerVision(new ResourceLocation(REFERENCE.MODID, "night_vision"), new NightVision());
        VReference.vision_bloodVision = VampirismAPI.vampireVisionRegistry().registerVision(new ResourceLocation(REFERENCE.MODID, "blood_vision"), new BloodVision());

        VampirismAPI.onSetupComplete();
    }

    private void processIMC(final @NotNull InterModProcessEvent event) {
        finishAPI();
        onInitStep(IInitListener.Step.PROCESS_IMC, event);
        IMCHandler.handleInterModMessage(event);
        if (inDev) {
            Tests.runBackgroundTests();
        }
        CrossbowArrowHandler.collectCrossbowArrows();
    }

    private void setup(final @NotNull FMLCommonSetupEvent event) {
        dispatcher.registerPackets();
        onInitStep(IInitListener.Step.COMMON_SETUP, event);

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());
        MinecraftForge.EVENT_BUS.addListener(ModLootTables::onLootLoad);

        SupporterManager.init();
        VampireBookManager.getInstance().init();
        ModEntitySelectors.registerSelectors();
        event.enqueueWork(TerraBlenderCompat::registerBiomeProviderIfPresentUnsafe);
//        VanillaStructureModifications.addVillageStructures(RegistryAccess.EMPTY);

        TelemetryCollector.execute();
    }

    private void setupClient(@NotNull FMLClientSetupEvent event) {
        onInitStep(IInitListener.Step.CLIENT_SETUP, event);
    }

    private void onInitStep(IInitListener.@NotNull Step step, @NotNull ParallelDispatchEvent event) {
        registryManager.onInitStep(step, event);
        proxy.onInitStep(step, event);
    }

}
