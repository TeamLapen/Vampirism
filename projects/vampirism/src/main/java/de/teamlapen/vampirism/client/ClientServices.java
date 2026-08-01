package de.teamlapen.vampirism.client;

import de.teamlapen.faction.Services;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import de.teamlapen.vampirism.client.gui.ScreenEventHandler;
import de.teamlapen.vampirism.client.gui.overlay.DraculaEventOverlay;
import de.teamlapen.vampirism.client.gui.overlay.FullScreenOverlay;
import de.teamlapen.vampirism.client.gui.overlay.SunBlindOverlay;
import de.teamlapen.vampirism.client.gui.overlay.VampirismHUDOverlay;
import de.teamlapen.vampirism.client.renderer.bloodvision.BloodVisionRenderer;
import de.teamlapen.vampirism.client.renderer.MistStateTracker;
import de.teamlapen.vampirism.client.renderer.MistRenderer;
import de.teamlapen.vampirism.client.renderer.RenderHandler;
import de.teamlapen.vampirism.client.renderer.VelmorraCollapseHandler;
import de.teamlapen.vampirism.common.util.PlayerSkinHelper;
import de.teamlapen.vampirism.data.reloadlistener.vampirebook.VampireBooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class ClientServices extends Services {

    //<editor-fold desc="Services" >

    private final VampirismHUDOverlay vampirismHUDOverlay = new VampirismHUDOverlay();
    private final RenderHandler renderHandler = new RenderHandler();
    private final BloodVisionRenderer bloodVisionRenderer = new BloodVisionRenderer();
    private final VampireBooks vampireBooks = new VampireBooks();
    private final ModKeys modKeys = new ModKeys();
    private final ScreenEventHandler screenEventHandler = new ScreenEventHandler();
    private final ClientEventHandler clientEventHandler = new ClientEventHandler();
    private final PlayerSkinHelper playerSkinHelper = new PlayerSkinHelper();
    private final ClientTooltips clientTooltips = new ClientTooltips();
    private final FullScreenOverlay fullScreenOverlay = new FullScreenOverlay();
    private final SunBlindOverlay sunBlindOverlay = new SunBlindOverlay();
    private final DraculaEventOverlay draculaEventOverlay = new DraculaEventOverlay();
    private final VelmorraCollapseHandler velmorraCollapseHandler = new VelmorraCollapseHandler();
    private final MistRenderer mistRenderer = new MistRenderer();
    private final MistStateTracker mistStateTracker = new MistStateTracker();

    //</editor-fold>

    public ClientServices(ModContainer container) {
        super(container);
    }

    //<editor-fold desc="Getters" >

    public VampirismHUDOverlay hud() {
        return this.vampirismHUDOverlay;
    }

    public FullScreenOverlay fullScreenOverlay() {
        return this.fullScreenOverlay;
    }

    public SunBlindOverlay sunBlindOverlay() {
        return this.sunBlindOverlay;
    }

    public RenderHandler renderHandler() {
        return this.renderHandler;
    }

    public BloodVisionRenderer bloodVisionRenderer() {
        return this.bloodVisionRenderer;
    }

    public MistStateTracker mistStateTracker() {
        return this.mistStateTracker;
    }

    public VampireBooks vampireBooks() {
        return this.vampireBooks;
    }

    public ModKeys modKeys() {
        return this.modKeys;
    }

    public ScreenEventHandler screenEventHandler() {
        return this.screenEventHandler;
    }

    public ClientEventHandler clientEventHandler() {
        return this.clientEventHandler;
    }

    public PlayerSkinHelper playerSkinHelper() {
        return this.playerSkinHelper;
    }

    public DraculaEventOverlay draculaEventOverlay() {
        return this.draculaEventOverlay;
    }

    public VelmorraCollapseHandler velmorraCollapseHandler() {
        return this.velmorraCollapseHandler;
    }

    //</editor-fold>

    //<editor-fold desc="Register" >

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.register(this.vampireBooks);
        bus.addListener(this.clientTooltips::registerTooltipRenderer);
        bus.addListener(this.modKeys::registerKeyMapping);
        bus.addListener(ModEntitiesRender::onRegisterRenderers);
        bus.addListener(ModEntitiesRender::onRegisterLayers);
        bus.addListener(ModEntitiesRender::onAddLayers);
        bus.addListener(ModBlocksRender::registerBlockEntityRenderers);
        bus.addListener(ModScreens::registerScreenOverlays);
        bus.addListener(ModScreens::registerScreens);
        bus.addListener(ModScreens::registerAppearanceScreens);
        bus.addListener(ModBlocksRender::registerBlockColors);
        bus.addListener(ModItemsRender::registerColors);
        bus.addListener(ModItemsRender::registerRangeSelector);
        bus.addListener(ModItemsRender::registerConditional);
        bus.addListener(ModParticleFactories::registerFactories);
        bus.addListener(ClientEventHandler::onModelRegistry);
        bus.addListener(ModDebugEntries::registerDebugEntries);
        bus.addListener(ModItemsRender::registerItemDecorator);
        bus.addListener(ModClientEffects::registerClientExtensions);
        bus.addListener(ModBlocksRender::registerClientExtensions);
        bus.addListener(ModClientFluids::registerClientExtensions);
        bus.addListener(ModClientFluids::registerFluidModels);
        bus.addListener(ModItemsRender::registerClientExtensions);
        bus.addListener(ModRenderPipelines::registerRenderPipelines);
        bus.addListener(this.bloodVisionRenderer::create);
        bus.addListener(ModEntityRenderStates::addRenderStateModifier);
        bus.register(ItemExtensions.VampireArmorItemExtension.class);
    }

    @Override
    protected void registerGameBus(IEventBus bus) {
        bus.register(this.vampirismHUDOverlay);
        bus.register(this.renderHandler);
        bus.register(this.velmorraCollapseHandler);
        bus.register(this.bloodVisionRenderer);
        bus.register(this.mistRenderer);
        bus.register(this.mistStateTracker);
        bus.register(this.modKeys);
        bus.register(this.screenEventHandler);
        bus.register(this.clientEventHandler);
        bus.register(this.playerSkinHelper);
        bus.register(this.clientTooltips);
        bus.addListener(ClientTickEvent.Pre.class, _ -> {
            this.fullScreenOverlay.update();
            this.sunBlindOverlay.update();
        });
    }

    //</editor-fold>
}
