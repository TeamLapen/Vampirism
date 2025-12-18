package de.teamlapen.factions.common;

import de.teamlapen.IFactionServices;
import de.teamlapen.factions.Services;
import de.teamlapen.factions.api.factions.IFactionPredicates;
import de.teamlapen.factions.common.config.FactionConfig;
import de.teamlapen.factions.common.core.FactionCommands;
import de.teamlapen.factions.common.core.ModRegistryManager;
import de.teamlapen.factions.common.factions.FactionPredicates;
import de.teamlapen.factions.common.factions.FactionRegistry;
import de.teamlapen.factions.common.network.packets.ModPacketDispatcher;
import de.teamlapen.factions.common.world.entities.ModPlayerEventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

public class CommonServices extends Services implements IFactionServices {

    private final FactionRegistry factionRegistry = new FactionRegistry();
    private final FactionPredicates factionPredicates = new FactionPredicates(this.factionRegistry);
    private final ModRegistryManager registryManager = new ModRegistryManager();
    private final ModPacketDispatcher packetDispatcher = new ModPacketDispatcher();
    private final ModPlayerEventHandler playerEventHandler = new ModPlayerEventHandler();

    public CommonServices(ModContainer container) {
        super(container);
        FactionConfig.register(container);
    }

    @Override
    public FactionRegistry factionRegistry() {
        return factionRegistry;
    }

    @Override
    public IFactionPredicates factionPredicates() {
        return this.factionPredicates;
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
