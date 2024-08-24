package de.teamlapen.vampirism.client;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.LogBlock;
import de.teamlapen.vampirism.client.config.ModFilter;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.gui.ScreenEventHandler;
import de.teamlapen.vampirism.client.gui.overlay.CustomBossEventOverlay;
import de.teamlapen.vampirism.client.gui.overlay.VampirismHUDOverlay;
import de.teamlapen.vampirism.client.renderer.RenderHandler;
import de.teamlapen.vampirism.client.renderer.VampirismClientEntityRegistry;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.SupporterManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        this.modEventBus.register(this);

        this.modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> new ConfigurationScreen(container, parent, new ModFilter()));

        this.modEventBus.register(this);

        NeoForge.EVENT_BUS.addListener(this::onAddReloadListenerEvent);
        NeoForge.EVENT_BUS.addListener(this::onDataMapsUpdated);
        NeoForge.EVENT_BUS.register(this.overlay);
        NeoForge.EVENT_BUS.register(this.renderHandler);
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        NeoForge.EVENT_BUS.register(new ScreenEventHandler());
        NeoForge.EVENT_BUS.register(new ModKeys());
        NeoForge.EVENT_BUS.addListener(this::levelLoaded);

        if (OptifineHandler.isOptifineLoaded()) {
            LOGGER.warn("Using Optifine. Expect visual glitches and reduces blood vision functionality if using shaders.");
        }
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
        event.enqueueWork(ModItemsRender::registerItemModelPropertyUnsafe);
        event.enqueueWork(() -> {
            Sheets.addWoodType(LogBlock.DARK_SPRUCE);
            Sheets.addWoodType(LogBlock.CURSED_SPRUCE);
        });
    }

    public void levelLoaded(LevelEvent.Load load) {
        List<CompletableFuture<Optional<GameProfile>>> list = SupporterManager.getSupporter().map(s -> SkullBlockEntity.fetchGameProfile(s.texture())).toList();
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0]))
                .thenApply(v -> list.stream().map(CompletableFuture::join).filter(Optional::isPresent).map(Optional::get).toList())
                .thenAcceptAsync(profile -> profile.forEach(s -> Minecraft.getInstance().getSkinManager().getInsecureSkin(s)));
    }

    public void onDataMapsUpdated(DataMapsUpdatedEvent event) {
        ((VampirismClientEntityRegistry) VampirismAPI.entityRegistry()).syncOverlays();
    }

    public static IProxy getProxy() {
        return new ClientProxy();
    }

    public void clearBossBarOverlay() {
        this.bossInfoOverlay.clear();
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
