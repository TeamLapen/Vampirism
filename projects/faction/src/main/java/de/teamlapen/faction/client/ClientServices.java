package de.teamlapen.faction.client;

import de.teamlapen.faction.Services;
import de.teamlapen.faction.client.core.*;
import de.teamlapen.faction.client.entity.ClientEventHandler;
import de.teamlapen.faction.client.gui.overlay.CustomBossEventOverlay;
import de.teamlapen.faction.client.gui.screens.ScreenEventHandler;
import de.teamlapen.faction.client.world.ClientLevelEventHandler;
import de.teamlapen.faction.common.core.FactionKeys;
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
        bus.addListener(FactionScreens::registerScreens);
        bus.addListener(FactionScreens::registerScreenOverlays);
        bus.addListener(FactionItemRenderer::registerColors);
        bus.addListener(FactionBlockRenderer::registerBlockEntityRenderers);
        bus.addListener(FMLClientSetupEvent.class, x -> FactionAppearanceScreens.init());
        bus.addListener(FactionParticleFactories::registerFactories);
    }

    @Override
    public void registerGameBus(IEventBus bus) {
        bus.register(this.screenEventHandler);
        bus.register(this.modKeys);
        bus.register(this.worldEventHandler);
        bus.register(this.entityEventHandler);
    }

}
