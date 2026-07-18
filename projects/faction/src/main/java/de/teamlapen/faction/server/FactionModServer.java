package de.teamlapen.faction.server;

import de.teamlapen.faction.api.util.REFERENCE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(value = REFERENCE.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class FactionModServer {

    public FactionModServer() {
        NeoForge.EVENT_BUS.addListener(ServerAboutToStartEvent.class, _ -> FactionLogger.setup());
        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, _ -> FactionLogger.destroy());
    }
}
