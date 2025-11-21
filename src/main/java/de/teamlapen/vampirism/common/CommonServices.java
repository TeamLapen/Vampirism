package de.teamlapen.vampirism.common;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.IVampirismServices;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEntitySelectors;
import de.teamlapen.vampirism.common.core.ModRegistryManager;
import de.teamlapen.vampirism.common.data.BloodConversionRegistry;
import de.teamlapen.vampirism.common.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.common.entity.SundamageRegistry;
import de.teamlapen.vampirism.common.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.common.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.common.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.common.entity.player.actions.ActionManager;
import de.teamlapen.vampirism.common.entity.player.vampire.ModVampireVisions;
import de.teamlapen.vampirism.common.integration.InterModHandler;
import de.teamlapen.vampirism.common.recipes.ExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.common.recipes.RecipesSync;
import de.teamlapen.vampirism.common.server.ServerEventHandler;
import de.teamlapen.vampirism.common.util.*;
import de.teamlapen.vampirism.common.world.biomes.OverworldModifications;
import de.teamlapen.vampirism.data.reloadlistener.ModReloadListeners;
import de.teamlapen.vampirism.data.remote.SettingsProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class CommonServices extends Services implements IVampirismServices {

    private final RecipesSync recipes = new RecipesSync();
    private final ModRegistryManager registryManager = new ModRegistryManager();
    private final VersionUpdater versionUpdater = new VersionUpdater();
    private final ModPlayerEventHandler playerEventHandler = new ModPlayerEventHandler();
    private final ModEntityEventHandler entityEventHandler = new ModEntityEventHandler();
    private final ServerEventHandler serverEventHandler = new ServerEventHandler();
    private final SupporterManager supporterManager = new SupporterManager();
    private final ModReloadListeners reloadListeners = new ModReloadListeners();
    private final FactionRegistry factionRegistry = new FactionRegistry();
    private final SundamageRegistry sundamageRegistry = new SundamageRegistry();
    private final ActionManager actionManager = new ActionManager();
    private final VampireVisionRegistry vampireVisionRegistry = new VampireVisionRegistry();
    private final ExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry = new ExtendedBrewingRecipeRegistry();
    private final SettingsProvider settingsProvider = new SettingsProvider(REFERENCE.SETTINGS_API);
    private final BloodConversionRegistry bloodConversionRegistry = new BloodConversionRegistry();
    private final InterModHandler interModCommunicationHandler = new InterModHandler();
    private final IVampirismEntityRegistry entityRegistry = new VampirismEntityRegistry();


    public CommonServices(ModContainer container) {
        super(container);
        VampirismAPI.setUpRegistries(this);
    }

    //<editor-fold desc="Getters" >

    public RecipesSync recipes() {
        return this.recipes;
    }

    public SupporterManager supporterManager() {
        return this.supporterManager;
    }

    @Override
    public ActionManager actionManager() {
        return this.actionManager;
    }

    @Override
    public VampireVisionRegistry visionRegistry() {
        return this.vampireVisionRegistry;
    }

    @Override
    public FactionRegistry factionRegistry() {
        return this.factionRegistry;
    }

    @Override
    public SundamageRegistry sundamageRegistry() {
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

    //</editor-fold>

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.register(this.registryManager);
        this.registryManager.setupRegistries(bus);
        this.registryManager.registerModEventHandler(bus);
        bus.register(this.interModCommunicationHandler);
        bus.addListener(ModCapabilities::registerCapabilities);
        bus.addListener(NewRegistryEvent.class, (event) -> {
            ModConfig.buildBalanceConfig();
            ModConfig.register(this.container());
        });
        bus.addListener(this.versionUpdater::catchModVersionMismatch);
        bus.addListener(FMLCommonSetupEvent.class, e -> e.enqueueWork(ModEntitySelectors::registerSelectors));
        bus.addListener(FMLLoadCompleteEvent.class, e -> e.enqueueWork(OverworldModifications::addBiomesToOverworldUnsafe));
        bus.addListener(FMLCommonSetupEvent.class, e -> this.supporterManager.init());
        bus.addListener(FMLCommonSetupEvent.class, e -> this.settingsProvider.syncSettingsCache());
        bus.addListener(FMLCommonSetupEvent.class, e -> ModVampireVisions.registerVisions(this.vampireVisionRegistry));
        bus.addListener(FMLLoadCompleteEvent.class, e -> TelemetryCollector.execute());
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
    }
}
