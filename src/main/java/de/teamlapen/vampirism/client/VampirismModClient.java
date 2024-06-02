package de.teamlapen.vampirism.client;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.client.VIngameOverlays;
import de.teamlapen.vampirism.blocks.LogBlock;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.gui.ScreenEventHandler;
import de.teamlapen.vampirism.client.gui.overlay.*;
import de.teamlapen.vampirism.client.renderer.RenderHandler;
import de.teamlapen.vampirism.client.renderer.VampirismClientEntityRegistry;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(value = REFERENCE.MODID, dist = Dist.CLIENT)
public class VampirismModClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private static VampirismModClient INSTANCE;

    private final IEventBus modEventBus;
    private final ModContainer modContainer;
    private final VampirismHUDOverlay overlay;
    private final CustomBossEventOverlay bossInfoOverlay = new CustomBossEventOverlay();
    private final RenderHandler renderHandler;

    public VampirismModClient(IEventBus modEventBus, ModContainer modContainer) {
        INSTANCE = this;
        this.modEventBus = modEventBus;
        this.modContainer = modContainer;
        ClientRegistryHandler.init(modEventBus);
        this.overlay = new VampirismHUDOverlay(Minecraft.getInstance());
        this.renderHandler = new RenderHandler(Minecraft.getInstance());

        NeoForge.EVENT_BUS.addListener(this::onAddReloadListenerEvent);
        NeoForge.EVENT_BUS.addListener(this::onDataMapsUpdated);
        NeoForge.EVENT_BUS.register(this.overlay);
        NeoForge.EVENT_BUS.register(this.renderHandler);
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        NeoForge.EVENT_BUS.register(new ScreenEventHandler());
        NeoForge.EVENT_BUS.register(new ModKeys());

        if (OptifineHandler.isOptifineLoaded()) {
            LOGGER.warn("Using Optifine. Expect visual glitches and reduces blood vision functionality if using shaders.");
        }

        setupApi();
    }

    public void onAddReloadListenerEvent(@NotNull AddReloadListenerEvent event) {
        event.addListener(this.renderHandler);
    }

    public static VampirismModClient getINSTANCE() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void setupClient(@NotNull FMLClientSetupEvent event) {
        VampirismMod.proxy.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
        event.enqueueWork(ModBlocksRender::register);
        event.enqueueWork(ModEffects::modifyNightVisionRenderer);
        event.enqueueWork(ModItemsRender::registerItemModelPropertyUnsafe);
        event.enqueueWork(() -> {
            Sheets.addWoodType(LogBlock.DARK_SPRUCE);
            Sheets.addWoodType(LogBlock.CURSED_SPRUCE);
        });
    }

    @SubscribeEvent
    public void commonEvent(FMLCommonSetupEvent event) {
        ModRecipes.Categories.init();
    }

    public void onDataMapsUpdated(DataMapsUpdatedEvent event) {
        ((VampirismClientEntityRegistry)VampirismAPI.entityRegistry()).syncOverlays();
    }

    public static IProxy getProxy() {
        return new ClientProxy();
    }

    public void clearBossBarOverlay() {
        this.bossInfoOverlay.clear();
    }

    private void setupApi() {
        VIngameOverlays.FACTION_RAID_BAR_ELEMENT = this.bossInfoOverlay;
        VIngameOverlays.BLOOD_BAR_ELEMENT = new BloodBarOverlay();
        VIngameOverlays.FACTION_LEVEL_ELEMENT = new FactionLevelOverlay();
        VIngameOverlays.ACTION_COOLDOWN_ELEMENT = new ActionCooldownOverlay();
        VIngameOverlays.ACTION_DURATION_ELEMENT = new ActionDurationOverlay();
    }

    public VampirismHUDOverlay getOverlay() {
        return this.overlay;
    }

    public CustomBossEventOverlay getBossInfoOverlay() {
        return bossInfoOverlay;
    }

    public RenderHandler getRenderHandler() {
        return renderHandler;
    }
}
