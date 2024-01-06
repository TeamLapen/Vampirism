package de.teamlapen.lib.client;

import de.teamlapen.lib.entity.ClientEntityEventHandler;
import de.teamlapen.lib.proxy.ClientProxy;
import de.teamlapen.lib.proxy.IProxy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;

public class VampLibClient {

    public static IProxy getProxy() {
        return new ClientProxy();
    }

    @SubscribeEvent
    public static void processIMC(final InterModProcessEvent event) {
        NeoForge.EVENT_BUS.register(new ClientEntityEventHandler());
    }
}
