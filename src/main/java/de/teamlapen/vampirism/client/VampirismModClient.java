package de.teamlapen.vampirism.client;

import de.teamlapen.lib.client.OptifineHandler;
import de.teamlapen.lib.common.ILifecycleListener;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.config.ModFilter;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.client.core.ClientRegistryHandler;
import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.ScreenEventHandler;
import de.teamlapen.vampirism.client.gui.overlay.CustomBossEventOverlay;
import de.teamlapen.vampirism.client.gui.overlay.VampirismHUDOverlay;
import de.teamlapen.vampirism.client.models.armor.ArmorModels;
import de.teamlapen.vampirism.client.renderer.BloodVisionRenderer;
import de.teamlapen.vampirism.client.renderer.RenderHandler;
import de.teamlapen.vampirism.client.renderer.VampirismClientEntityRegistry;
import de.teamlapen.vampirism.client.renderer.items.BloodContainerRenderer;
import de.teamlapen.vampirism.client.renderer.items.CoffinRenderer;
import de.teamlapen.vampirism.client.renderer.items.MotherTrophyRenderer;
import de.teamlapen.vampirism.common.blocks.IDescriptionProvider;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.proxy.IProxy;
import de.teamlapen.vampirism.common.util.BlockDescription;
import de.teamlapen.vampirism.common.util.PlayerSkinHelper;
import de.teamlapen.vampirism.common.util.ShiftDescription;
import de.teamlapen.vampirism.data.reloadlistener.vampirebook.VampireBooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(value = REFERENCE.MODID, dist = Dist.CLIENT)
public class VampirismModClient {

    private static final Logger LOGGER = LogManager.getLogger();
    private static VampirismModClient INSTANCE;
    private static ClientServices SERVICES;

    private final VampirismHUDOverlay overlay;
    private final CustomBossEventOverlay bossInfoOverlay = new CustomBossEventOverlay();
    private final RenderHandler renderHandler;
    private final BloodVisionRenderer bloodVisionRenderer;
    private final ArmorModels armorModels = new ArmorModels();
    private final VampireBooks vampireBooks;

    public VampirismModClient(IEventBus modEventBus, ModContainer modContainer) {
        INSTANCE = this;
        ClientRegistryHandler.init(modEventBus);
        this.overlay = new VampirismHUDOverlay(Minecraft.getInstance());
        this.renderHandler = new RenderHandler(Minecraft.getInstance());
        this.bloodVisionRenderer = new BloodVisionRenderer(Minecraft.getInstance());
        this.vampireBooks = new VampireBooks();

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> new ConfigurationScreen(container, parent, new ModFilter()));

        modEventBus.register(this);
        modEventBus.register(this.armorModels);
//        modEventBus.addListener(BloodVisionRenderer::onRegisterStage);
        modEventBus.addListener(this.bloodVisionRenderer::onClientSetup);

        NeoForge.EVENT_BUS.addListener(this::onDataMapsUpdated);
        NeoForge.EVENT_BUS.register(this.overlay);
        NeoForge.EVENT_BUS.register(this.renderHandler);
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        NeoForge.EVENT_BUS.register(new ScreenEventHandler());
        NeoForge.EVENT_BUS.register(new ModKeys());
        NeoForge.EVENT_BUS.addListener(this::levelLoaded);
        NeoForge.EVENT_BUS.register(this.bloodVisionRenderer);
        NeoForge.EVENT_BUS.addListener(ModItems::registerShiftTooltips);
        NeoForge.EVENT_BUS.addListener(this::onToolTip);

        if (OptifineHandler.isOptifineLoaded()) {
            LOGGER.warn("Using Optifine. Expect visual glitches and reduces blood vision functionality if using shaders.");
        }

        SERVICES = new ClientServices();
    }

    public static ClientServices getServices() {
        return SERVICES;
    }

    @SubscribeEvent
    public void onAddReloadListenerEvent(@NotNull AddClientReloadListenersEvent event) {
        this.vampireBooks.register(event);
    }

    public static VampirismModClient getInstance() {
        return INSTANCE;
    }

    public static VampireBooks getBookContent() {
        return INSTANCE.vampireBooks;
    }

    @SubscribeEvent
    public void setupClient(@NotNull FMLClientSetupEvent event) {
        VampirismMod.proxy.onInitStep(ILifecycleListener.Step.CLIENT_SETUP, event);
        event.enqueueWork(ModBlocksRender::register);
        event.enqueueWork(() -> {
            Sheets.addWoodType(ModBlocks.WoodTypes.DARK_SPRUCE);
            Sheets.addWoodType(ModBlocks.WoodTypes.CURSED_SPRUCE);
        });
    }

    public void levelLoaded(LevelEvent.Load load) {
        PlayerSkinHelper.loadPlayerSkins();
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

    public ArmorModels getArmorModels() {
        return this.armorModels;
    }

    public BloodVisionRenderer getBloodVisionRenderer() {
        return this.bloodVisionRenderer;
    }

    @SubscribeEvent
    public void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        event.register(MotherTrophyRenderer.ID, MotherTrophyRenderer.Unbaked.MAP_CODEC);
        event.register(BloodContainerRenderer.ID, BloodContainerRenderer.Unbaked.MAP_CODEC);
        event.register(CoffinRenderer.ID, CoffinRenderer.Unbaked.MAP_CODEC);
    }

    public void onToolTip(ItemTooltipEvent event) {
        if (event.getItemStack().get(ModDataComponents.SHIFT_DESCRIPTION) instanceof ShiftDescription shiftDescription) {
            TooltipDisplay orDefault = event.getItemStack().getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
            shiftDescription.addTooltips(event.getItemStack(), event.getEntity(), event.getContext(), orDefault, event.getFlags(), event.getToolTip()::add, event.getItemStack().getItem() instanceof IDescriptionProvider s ? s.getDescriptionParameters() : new Object[0]);
        }

        if (event.getItemStack().get(ModDataComponents.BLOCK_DESCRIPTION) instanceof BlockDescription blockDescription) {
            TooltipDisplay orDefault = event.getItemStack().getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
            blockDescription.addTooltips(event.getItemStack(), event.getContext(), orDefault, event.getFlags(), event.getToolTip()::add);
        }
    }
}
