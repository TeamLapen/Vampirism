package de.teamlapen.faction.server;

import de.teamlapen.faction.api.util.REFERENCE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(value = REFERENCE.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class FactionModServer {

    public FactionModServer(IEventBus modEventBus) {
        modEventBus.addListener(FMLLoadCompleteEvent.class, e -> FactionLogger.init());
    }
}
