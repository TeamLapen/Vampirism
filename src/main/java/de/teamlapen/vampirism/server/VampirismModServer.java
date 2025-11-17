package de.teamlapen.vampirism.server;

import de.teamlapen.vampirism.REFERENCE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(value = REFERENCE.MODID, dist = Dist.DEDICATED_SERVER)
public class VampirismModServer {

    public VampirismModServer(IEventBus modEventBus) {
        modEventBus.addListener(FMLLoadCompleteEvent.class, e -> VampirismLogger.init());
    }
}
