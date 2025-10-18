package de.teamlapen.vampirism;

import de.teamlapen.lib.common.ILifecycleListener;
import de.teamlapen.sync.SyncRegistry;
import de.teamlapen.sync.common.entities.IPlayerEventListener;
import de.teamlapen.sync.common.storage.IAttachedSyncable;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.renderer.VampirismClientEntityRegistry;
import de.teamlapen.vampirism.common.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.common.blockentity.PotionTableBlockEntity;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.common.entity.SundamageRegistry;
import de.teamlapen.vampirism.common.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.common.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.common.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.common.entity.player.actions.ActionManager;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.entity.player.vampire.BloodVision;
import de.teamlapen.vampirism.common.entity.player.vampire.NightVision;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.integration.IMCHandler;
import de.teamlapen.vampirism.common.integration.TerraBlenderCompat;
import de.teamlapen.vampirism.common.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.common.items.BloodBottleItem;
import de.teamlapen.vampirism.common.items.BloodSyringeFluidHandler;
import de.teamlapen.vampirism.common.items.crossbow.CrossbowArrowHandler;
import de.teamlapen.vampirism.common.proxy.IProxy;
import de.teamlapen.vampirism.common.recipes.ExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.common.serialization.CodecModifications;
import de.teamlapen.vampirism.common.util.SupporterManager;
import de.teamlapen.vampirism.common.util.TelemetryCollector;
import de.teamlapen.vampirism.common.util.VampireVisionRegistry;
import de.teamlapen.vampirism.common.util.VersionUpdater;
import de.teamlapen.vampirism.common.world.biomes.OverworldModifications;
import de.teamlapen.vampirism.common.world.structures.VanillaStructureModifications;
import de.teamlapen.vampirism.data.BloodConversionRegistry;
import de.teamlapen.vampirism.data.reloadlistener.SingleJigsawReloadListener;
import de.teamlapen.vampirism.data.reloadlistener.SundamageReloadListener;
import de.teamlapen.vampirism.data.reloadlistener.skills.SkillTreeReloadListener;
import de.teamlapen.vampirism.data.remote.SettingsProvider;
import de.teamlapen.vampirism.server.ServerProxy;
import de.teamlapen.vampirism.server.VampirismLogger;
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
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
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

    private final @NotNull ModRegistryManager registryManager;
    private final IEventBus modBus;
    private final ModContainer modContainer;


    public VampirismMod(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
        checkEnv();

        this.modBus = modEventBus;
        this.modContainer = modContainer;

        this.registryManager = new ModRegistryManager(modEventBus);

        this.modBus.addListener(this::setup);
        this.modBus.addListener(this::enqueueIMC);
        this.modBus.addListener(this::processIMC);
        this.modBus.addListener(this::loadComplete);
        this.modBus.addListener(this::registerCapabilities);
        this.modBus.addListener(this::finalizeConfiguration);
        this.modBus.addListener(VersionUpdater::catchModVersionMismatch);

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
        NeoForgeMod.enableMergedAttributeTooltips();
    }

    public void onAddReloadListenerEvent(@NotNull AddServerReloadListenersEvent event) {
        event.addListener(SingleJigsawReloadListener.SINGLE_JIGSAW_ID, new SingleJigsawReloadListener());
        event.addListener(SundamageReloadListener.SUNDAMAGE_ID, new SundamageReloadListener(event.getRegistryAccess()));
        event.addListener(SkillTreeReloadListener.SKILL_TREE_ID, new SkillTreeReloadListener());
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
        onInitStep(ILifecycleListener.Step.ENQUEUE_IMC, event);
        SyncRegistry.registerPlayerEventReceivingCapability((AttachmentType<IPlayerEventListener>) (Object) ModAttachments.VAMPIRE_PLAYER.get(), VampirePlayer.class);
        SyncRegistry.registerPlayerEventReceivingCapability((AttachmentType<IPlayerEventListener>) (Object) ModAttachments.HUNTER_PLAYER.get(), HunterPlayer.class);
        SyncRegistry.registerSyncableEntityCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.EXTENDED_CREATURE.get(), ExtendedCreature.class);
        SyncRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.VAMPIRE_PLAYER.get(), VampirePlayer.class);
        SyncRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.HUNTER_PLAYER.get(), HunterPlayer.class);
        SyncRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.FACTION_PLAYER_HANDLER.get(), FactionPlayerHandler.class);
    }

    private void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        // Items
        event.registerItem(Capabilities.FluidHandler.ITEM, (item, b) -> new BloodBottleFluidHandler(item, BloodBottleItem.CAPACITY), ModItems.BLOOD_BOTTLE.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (item, b) -> new FluidHandlerItemStack(ModDataComponents.BLOOD_CONTAINER, item, BloodContainerBlockEntity.CAPACITY), ModBlocks.BLOOD_CONTAINER.asItem());
        event.registerItem(Capabilities.FluidHandler.ITEM, (item, b) -> new BloodSyringeFluidHandler(item), ModItems.SYRINGE_EMPTY.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (item, b) -> new BloodSyringeFluidHandler(item), ModItems.SYRINGE_BLOOD.get());

        // Blocks
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BLOOD_CONTAINER.get(), (blockEntity, side) -> blockEntity.fluidInventory);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> {
            if (side == Direction.DOWN) return null;
            return blockEntity.itemHandler;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> {
            if (side == Direction.UP) return null;
            return blockEntity.fluidInventory;
        });
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BLOOD_SIEVE.get(), (blockEntity, side) -> {
            if (side != null && side.getAxis().isHorizontal()) return blockEntity.filterItemHandler;
            return null;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BLOOD_SIEVE.get(), (blockEntity, side) -> {
            if (side == Direction.UP) return blockEntity.inputFluidInventory;
            if (side == Direction.DOWN) return blockEntity.outputFluidInventory;
            return null;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.ALTAR_INSPIRATION.get(), (blockEntity, side) -> blockEntity.fluidInventory);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BLOOD_PEDESTAL.get(), (o, side) -> o);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.POTION_TABLE.get(), new ICapabilityProvider<>() {
            @Override
            public @Nullable IItemHandler getCapability(@NotNull PotionTableBlockEntity object, @NotNull Direction context) {
                return object.getCapability(object, context);
            }
        });
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ALTAR_INFUSION.get(), (blockEntity, side) -> new InvWrapper(blockEntity));
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
        ModConfig.buildBalanceConfig();
        ModConfig.register(this.modContainer);
    }

    private void loadComplete(final @NotNull FMLLoadCompleteEvent event) {
        onInitStep(ILifecycleListener.Step.LOAD_COMPLETE, event);
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
        onInitStep(ILifecycleListener.Step.PROCESS_IMC, event);
        IMCHandler.handleInterModMessage(event);
        CrossbowArrowHandler.collectCrossbowArrows();
    }

    private void setup(final @NotNull FMLCommonSetupEvent event) {
        onInitStep(ILifecycleListener.Step.COMMON_SETUP, event);

        NeoForge.EVENT_BUS.register(new ModPlayerEventHandler());
        NeoForge.EVENT_BUS.register(new ModEntityEventHandler());

        SupporterManager.init();
        ModEntitySelectors.registerSelectors();
        event.enqueueWork(TerraBlenderCompat::registerBiomeProviderIfPresentUnsafe);
        event.enqueueWork(ModStats::registerFormatter);
        event.enqueueWork(CodecModifications::changeMobEffectCodec);
        event.enqueueWork(ModVillage::villagerTradeSetup);
        event.enqueueWork(ModItems::registerDispenserBehaviour);
        event.enqueueWork(ModBlocks::registerStrippables);
        event.enqueueWork(ModBlocks::registerFlammables);
        event.enqueueWork(ModFluids::registerFluidInteractions);
        TelemetryCollector.execute();
    }

    private void onInitStep(ILifecycleListener.@NotNull Step step, @NotNull ParallelDispatchEvent event) {
        proxy.onInitStep(step, event);
    }
}
