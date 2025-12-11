package de.teamlapen.factions.client;

import de.teamlapen.factions.Services;
import de.teamlapen.factions.client.config.ClientConfigHelper;
import de.teamlapen.factions.client.core.FactionAppearanceScreens;
import de.teamlapen.factions.client.core.ModBlockRenderer;
import de.teamlapen.factions.client.core.ModItemRenderer;
import de.teamlapen.factions.client.core.ModScreens;
import de.teamlapen.factions.client.entity.ClientEventHandler;
import de.teamlapen.factions.client.gui.overlay.CustomBossEventOverlay;
import de.teamlapen.factions.client.gui.screens.ScreenEventHandler;
import de.teamlapen.factions.client.world.ClientLevelEventHandler;
import de.teamlapen.factions.common.core.FactionKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientServices extends Services {

    private final ScreenEventHandler screenEventHandler = new ScreenEventHandler();
    private final FactionKeys modKeys = new FactionKeys();
    private final CustomBossEventOverlay bossInfoOverlay = new CustomBossEventOverlay();
    private final ClientLevelEventHandler worldEventHandler = new ClientLevelEventHandler();
    private final ClientEventHandler entityEventHandler = new ClientEventHandler();

    public ClientServices(ModContainer container) {
        super(container);
    }

    public CustomBossEventOverlay bossInfoOverlay() {
        return bossInfoOverlay;
    }

    public FactionKeys modKeys() {
        return modKeys;
    }

    public ScreenEventHandler screenEventHandler() {
        return screenEventHandler;
    }

    @Override
    public void registerModBus(IEventBus bus) {
        bus.addListener(this.modKeys::registerKeyMapping);
        bus.addListener(ClientConfigHelper::onConfigChanged);
        bus.addListener(ModScreens::registerScreens);
        bus.addListener(ModScreens::registerScreenOverlays);
        bus.addListener(ModItemRenderer::registerColors);
        bus.addListener(ModBlockRenderer::registerBlockEntityRenderers);
        bus.addListener(FMLClientSetupEvent.class, x -> FactionAppearanceScreens.init());
    }

    @Override
    public void registerGameBus(IEventBus bus) {
        bus.register(this.screenEventHandler);
        bus.register(this.modKeys);
        bus.register(this.worldEventHandler);
        bus.register(this.entityEventHandler);
    }

}
