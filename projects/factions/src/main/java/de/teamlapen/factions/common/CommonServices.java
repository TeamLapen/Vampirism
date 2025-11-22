package de.teamlapen.factions.common;

import de.teamlapen.factions.Services;
import de.teamlapen.factions.common.config.ModConfig;
import de.teamlapen.factions.common.core.FactionCommands;
import de.teamlapen.factions.common.core.ModRegistryManager;
import de.teamlapen.factions.common.entities.ModPlayerEventHandler;
import de.teamlapen.factions.common.factions.FactionRegistry;
import de.teamlapen.factions.common.network.packets.ModPacketDispatcher;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

public class CommonServices extends Services {

    private final FactionRegistry factionRegistry = new FactionRegistry();
    private final ModRegistryManager registryManager = new ModRegistryManager();
    private final ModPacketDispatcher packetDispatcher = new ModPacketDispatcher();
    private final ModPlayerEventHandler playerEventHandler = new ModPlayerEventHandler();

    public CommonServices(ModContainer container) {
        super(container);
        ModConfig.register(container);
    }

    public FactionRegistry factionRegistry() {
        return factionRegistry;
    }

    @Override
    protected void registerModBus(IEventBus bus) {
        this.registryManager.setupRegistries(bus);
        bus.register(this.packetDispatcher);
    }

    @Override
    protected void registerGameBus(IEventBus bus) {
        bus.addListener(FactionCommands::registerCommands);
        bus.addListener(Permissions::registerNodes);
        bus.register(this.playerEventHandler);
    }
}
