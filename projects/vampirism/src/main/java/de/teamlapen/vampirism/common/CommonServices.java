package de.teamlapen.vampirism.common;

import de.teamlapen.faction.Services;
import de.teamlapen.faction.api.event.AddFactionTagEvent;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.IVampirismServices;
import de.teamlapen.vampirism.api.world.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.common.core.ModCreativeTabs;
import de.teamlapen.vampirism.common.core.ModEntitySelectors;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModRegistryManager;
import de.teamlapen.vampirism.common.integration.InterModHandler;
import de.teamlapen.vampirism.common.network.ModPacketDispatcher;
import de.teamlapen.vampirism.common.server.ServerEventHandler;
import de.teamlapen.vampirism.common.util.*;
import de.teamlapen.vampirism.common.world.LevelEventHandler;
import de.teamlapen.vampirism.common.util.wings.WingsManager;
import de.teamlapen.vampirism.common.world.VillageEventHandler;
import de.teamlapen.vampirism.common.world.biomes.OverworldModifications;
import de.teamlapen.vampirism.common.world.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.common.world.entity.SundamageRegistry;
import de.teamlapen.vampirism.common.world.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.common.world.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.common.world.items.ItemEventHandler;
import de.teamlapen.vampirism.common.world.items.recipes.ExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.common.world.items.recipes.RecipesSync;
import de.teamlapen.vampirism.data.BloodConversionRegistry;
import de.teamlapen.vampirism.data.reloadlistener.ModReloadListeners;
import de.teamlapen.vampirism.data.remote.SettingsProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

public class CommonServices extends Services implements IVampirismServices {

    private final RecipesSync recipes = new RecipesSync();
    private final ModRegistryManager registryManager = new ModRegistryManager();
    private final VersionUpdater versionUpdater = new VersionUpdater();
    private final ModPlayerEventHandler playerEventHandler = new ModPlayerEventHandler();
    private final ModEntityEventHandler entityEventHandler = new ModEntityEventHandler();
    private final ServerEventHandler serverEventHandler = new ServerEventHandler();
    private final SupporterManager supporterManager = new SupporterManager();
    private final ModReloadListeners reloadListeners = new ModReloadListeners();
    private final SundamageRegistry sundamageRegistry = new SundamageRegistry();
    private final ExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry = new ExtendedBrewingRecipeRegistry();
    private final SettingsProvider settingsProvider = new SettingsProvider(REFERENCE.SETTINGS_API);
    private final BloodConversionRegistry bloodConversionRegistry = new BloodConversionRegistry();
    private final InterModHandler interModCommunicationHandler = new InterModHandler();
    private final IVampirismEntityRegistry entityRegistry = new VampirismEntityRegistry();
    private final VillageEventHandler villageEventHandler = new VillageEventHandler();
    private final LevelEventHandler levelEventHandler = new LevelEventHandler();
    private final ItemEventHandler itemEventHandler = new ItemEventHandler();
    private final WingsManager wingsManager = new WingsManager();


    public CommonServices(ModContainer container) {
        super(container);
    }

    //<editor-fold desc="Getters" >

    public RecipesSync recipes() {
        return this.recipes;
    }

    public SupporterManager supporterManager() {
        return this.supporterManager;
    }

    @Override
    public SundamageRegistry sunDamageRegistry() {
        return this.sundamageRegistry;
    }

    @Override
    public IVampirismEntityRegistry entityRegistry() {
        return this.entityRegistry;
    }

    @Override
    public ExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry() {
        return this.extendedBrewingRecipeRegistry;
    }

    @Override
    public SettingsProvider settings() {
        return this.settingsProvider;
    }

    @Override
    public BloodConversionRegistry bloodConversionRegistry() {
        return this.bloodConversionRegistry;
    }

    public InterModHandler imc() {
        return this.interModCommunicationHandler;
    }

    public WingsManager wingsManager() {
        return this.wingsManager;
    }

    //</editor-fold>

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.register(this.registryManager);
        this.registryManager.setupRegistries(bus);
        this.registryManager.registerModEventHandler(bus);
        bus.register(this.interModCommunicationHandler);
        bus.addListener(ModCapabilities::registerCapabilities);
        bus.addListener(this.versionUpdater::catchModVersionMismatch);
        bus.addListener(FMLCommonSetupEvent.class, e -> e.enqueueWork(ModEntitySelectors::registerSelectors));
        bus.addListener(FMLLoadCompleteEvent.class, e -> e.enqueueWork(OverworldModifications::addBiomesToOverworldUnsafe));
        bus.addListener(FMLCommonSetupEvent.class, e -> this.supporterManager.init());
        bus.addListener(FMLCommonSetupEvent.class, e -> this.settingsProvider.syncSettingsCache());
        bus.addListener(FMLLoadCompleteEvent.class, e -> TelemetryCollector.execute());
        bus.addListener(AddFactionTagEvent.class, ModFactions::registerFactionTags);
        bus.addListener(ModCreativeTabs::addToExistingCreativeTabs);
        bus.addListener(ModPacketDispatcher::registerHandler);
        bus.addListener(MigrationData::fix);
        bus.register(this.wingsManager);
    }

    @Override
    protected void registerGameBus(IEventBus bus) {
        bus.register(this.recipes);
        this.registryManager.registerForgeEventHandler(bus);
        bus.addListener(this.versionUpdater::checkVersionUpdated);
        bus.register(this.playerEventHandler);
        bus.register(this.entityEventHandler);
        bus.register(this.reloadListeners);
        bus.register(this.serverEventHandler);
        bus.register(this.villageEventHandler);
        bus.addListener(Permissions::registerNodes);
        bus.register(this.itemEventHandler);
        bus.register(this.levelEventHandler);
    }
}
