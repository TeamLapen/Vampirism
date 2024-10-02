package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blockentity.PotionTableBlockEntity;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.renderer.VampirismClientEntityRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.reloadlistener.SingleJigsawReloadListener;
import de.teamlapen.vampirism.data.reloadlistener.SkillTreeReloadListener;
import de.teamlapen.vampirism.data.reloadlistener.SundamageReloadListener;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.entity.factions.FactionTags;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.entity.player.actions.ActionManager;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.vampire.BloodVision;
import de.teamlapen.vampirism.entity.player.vampire.NightVision;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.items.BloodBottleItem;
import de.teamlapen.vampirism.items.crossbow.CrossbowArrowHandler;
import de.teamlapen.vampirism.misc.SettingsProvider;
import de.teamlapen.vampirism.misc.VampirismLogger;
import de.teamlapen.vampirism.mixin.accessor.ReloadableServerResourcesAccessor;
import de.teamlapen.vampirism.mixin.accessor.TagManagerAccessor;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.modcompat.TerraBlenderCompat;
import de.teamlapen.vampirism.network.ModPacketDispatcher;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.proxy.ServerProxy;
import de.teamlapen.vampirism.recipes.ExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.util.*;
import de.teamlapen.vampirism.world.BloodConversionRegistry;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.gen.VanillaStructureModifications;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main class for Vampirism
 */
@Mod(value = REFERENCE.MODID)
public class VampirismMod {

    public static VampirismMod instance;
    public static final IProxy proxy = FMLEnvironment.dist == Dist.CLIENT ? VampirismModClient.getProxy() : new ServerProxy();
    public static boolean inDev = false;
    public static boolean inDataGen = false;

    private final @NotNull RegistryManager registryManager;
    private final IEventBus modBus;
    private final ModContainer modContainer;


    public VampirismMod(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
        checkEnv();

        this.modBus = modEventBus;
        this.modContainer = modContainer;

        this.registryManager = new RegistryManager(modEventBus);

        this.modBus.addListener(this::setup);
        this.modBus.addListener(this::enqueueIMC);
        this.modBus.addListener(this::processIMC);
        this.modBus.addListener(this::loadComplete);
        this.modBus.addListener(this::registerCapabilities);
        this.modBus.addListener(this::finalizeConfiguration);
        this.modBus.addListener(VersionUpdater::catchModVersionMismatch);
        this.modBus.register(ModPacketDispatcher.class);
        this.modBus.register(MigrationData.class);

        NeoForge.EVENT_BUS.register(Permissions.class);
        NeoForge.EVENT_BUS.register(SitHandler.class);
        NeoForge.EVENT_BUS.register(new GeneralEventHandler());
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListenerEvent);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(VersionUpdater::checkVersionUpdated);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        NeoForge.EVENT_BUS.addListener(this::onDataPackSyncEvent);

        ShapedRecipePattern.setCraftingSize(4, 4);

        prepareAPI();
        this.registryManager.setupRegistries();
        this.registryManager.registerModEventHandler();
        this.registryManager.registerForgeEventHandler();
    }

    public void onAddReloadListenerEvent(@NotNull AddReloadListenerEvent event) {
        event.addListener(new SingleJigsawReloadListener());
        event.addListener(new SundamageReloadListener(((TagManagerAccessor) ((ReloadableServerResourcesAccessor) event.getServerResources()).getTagManager()).getRegistryAccess()));
        event.addListener(new SkillTreeReloadListener());
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
        HelperRegistry.registerPlayerEventReceivingCapability((AttachmentType<IPlayerEventListener>) (Object) ModAttachments.VAMPIRE_PLAYER.get(), VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability((AttachmentType<IPlayerEventListener>) (Object) ModAttachments.HUNTER_PLAYER.get(), HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.EXTENDED_CREATURE.get(), ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.VAMPIRE_PLAYER.get(), VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.HUNTER_PLAYER.get(), HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.FACTION_PLAYER_HANDLER.get(), FactionPlayerHandler.class);
    }

    private void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (item, b) -> new BloodBottleFluidHandler(item, BloodBottleItem.CAPACITY), ModItems.BLOOD_BOTTLE.get());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModTiles.BLOOD_CONTAINER.get(), (o, side) -> o.getTank());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModTiles.ALTAR_INSPIRATION.get(), (o, side) -> o.getTank());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTiles.GRINDER.get(), (o, side) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTiles.BLOOD_PEDESTAL.get(), (o, side) -> o);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModTiles.SIEVE.get(), (o, side) -> o.getTank());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTiles.POTION_TABLE.get(), new ICapabilityProvider<>() {
            @Override
            public @Nullable IItemHandler getCapability(@NotNull PotionTableBlockEntity object, @NotNull Direction context) {
                return object.getCapability(object, context);
            }
        });
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTiles.ALTAR_INFUSION.get(), (o, side) -> new InvWrapper(o));
    }

    private void onServerStarting(@NotNull ServerAboutToStartEvent event) {
        VanillaStructureModifications.addVillageStructures(event.getServer().registryAccess());
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).initServer(event.getServer().registryAccess());
    }

    private void onServerStopped(ServerStoppedEvent event) {
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).removeServer();
    }

    private void onDataPackSyncEvent(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            ((SundamageRegistry) VampirismAPI.sundamageRegistry()).updateClient(event.getPlayer());
        } else {
            event.getPlayerList().getPlayers().forEach(player -> ((SundamageRegistry) VampirismAPI.sundamageRegistry()).updateClient(player));
        }
    }

    private void finalizeConfiguration(NewRegistryEvent event) {
        VampirismConfig.buildBalanceConfig();
        VampirismConfig.register(this.modContainer);
    }

    private void loadComplete(final @NotNull FMLLoadCompleteEvent event) {
        onInitStep(IInitListener.Step.LOAD_COMPLETE, event);
        event.enqueueWork(OverworldModifications::addBiomesToOverworldUnsafe);
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            VampirismLogger.init();
        }
    }


    /**
     * Called during constructor to set up the API as well as VReference
     */
    private void prepareAPI() {
        VampirismAPI.setUpRegistries(new FactionRegistry(), new SundamageRegistry(), FMLEnvironment.dist == Dist.CLIENT ? new VampirismClientEntityRegistry() : new VampirismEntityRegistry(), new ActionManager(), new VampireVisionRegistry(), new ExtendedBrewingRecipeRegistry(), new SettingsProvider(REFERENCE.SETTINGS_API), new BloodConversionRegistry());
        VReference.vision_nightVision = VampirismAPI.vampireVisionRegistry().registerVision(VResourceLocation.mod("night_vision"), new NightVision());
        VReference.vision_bloodVision = VampirismAPI.vampireVisionRegistry().registerVision(VResourceLocation.mod("blood_vision"), new BloodVision());
        VampirismAPI.onSetupComplete();
    }

    private void processIMC(final @NotNull InterModProcessEvent event) {
        onInitStep(IInitListener.Step.PROCESS_IMC, event);
        IMCHandler.handleInterModMessage(event);
        CrossbowArrowHandler.collectCrossbowArrows();
    }

    private void setup(final @NotNull FMLCommonSetupEvent event) {
        FactionTags.collectTags();
        onInitStep(IInitListener.Step.COMMON_SETUP, event);

        NeoForge.EVENT_BUS.register(new ModPlayerEventHandler());
        NeoForge.EVENT_BUS.register(new ModEntityEventHandler());

        SupporterManager.init();
        VampireBookManager.getInstance().init();
        ModEntitySelectors.registerSelectors();
        event.enqueueWork(TerraBlenderCompat::registerBiomeProviderIfPresentUnsafe);
        event.enqueueWork(ModStats::registerFormatter);
        event.enqueueWork(CodecModifications::changeMobEffectCodec);
        event.enqueueWork(ModVillage::villagerTradeSetup);
        event.enqueueWork(ModItems::registerDispenserBehaviourUnsafe);
        TelemetryCollector.execute();
    }

    private void onInitStep(IInitListener.@NotNull Step step, @NotNull ParallelDispatchEvent event) {
        proxy.onInitStep(step, event);
    }

}
