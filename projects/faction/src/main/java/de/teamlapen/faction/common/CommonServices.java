package de.teamlapen.faction.common;

import de.teamlapen.faction.api.IFactionServices;
import de.teamlapen.faction.Services;
import de.teamlapen.faction.api.factions.IFactionPredicates;
import de.teamlapen.faction.api.factions.IFactionSpecificTags;
import de.teamlapen.faction.common.core.FactionCommands;
import de.teamlapen.faction.common.core.ModRegistryManager;
import de.teamlapen.faction.common.factions.FactionPredicates;
import de.teamlapen.faction.common.factions.FactionHelper;
import de.teamlapen.faction.common.factions.FactionSpecificTags;
import de.teamlapen.faction.common.factions.skills.SkillCallbacks;
import de.teamlapen.faction.common.network.packets.ModPacketDispatcher;
import de.teamlapen.faction.common.world.entities.ModPlayerEventHandler;
import de.teamlapen.faction.common.world.entities.PlayerListenerEventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonServices extends Services implements IFactionServices {

    private final FactionHelper factionRegistry = new FactionHelper();
    private final FactionPredicates factionPredicates = new FactionPredicates(this.factionRegistry);
    private final ModRegistryManager registryManager = new ModRegistryManager();
    private final ModPacketDispatcher packetDispatcher = new ModPacketDispatcher();
    private final ModPlayerEventHandler playerEventHandler = new ModPlayerEventHandler();
    private final FactionSpecificTags factionSpecificTags = new FactionSpecificTags();
    private final PlayerListenerEventHandler playerListenerEventHandler = new PlayerListenerEventHandler();

    public CommonServices(ModContainer container) {
        super(container);
    }

    @Override
    public FactionHelper factionHelper() {
        return factionRegistry;
    }

    @Override
    public IFactionPredicates factionPredicates() {
        return this.factionPredicates;
    }

    @Override
    public IFactionSpecificTags factionTags() {
        return this.factionSpecificTags;
    }

    @Override
    protected void registerModBus(IEventBus bus) {
        this.registryManager.setupRegistries(bus);
        bus.register(this.packetDispatcher);
        bus.addListener(FMLCommonSetupEvent.class, x -> this.factionSpecificTags.collectTags());
        bus.addListener(this.playerListenerEventHandler::collect);
    }

    @Override
    protected void registerGameBus(IEventBus bus) {
        bus.addListener(FactionCommands::registerCommands);
        bus.addListener(Permissions::registerNodes);
        bus.register(this.playerEventHandler);
        bus.register(this.playerListenerEventHandler);
        bus.addListener(SkillCallbacks::onBound);
    }
}
