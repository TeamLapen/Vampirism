package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.network.ServerboundRequestPlayerUpdatePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * Library's entity event handler to do client-sided stuff
 */
public class ClientEntityEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedInClient(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!HelperRegistry.getSyncablePlayerCaps().isEmpty()) {
            event.getPlayer().connection.send(new ServerboundRequestPlayerUpdatePacket());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawnedClient(ClientPlayerNetworkEvent.Clone event) {
        if (!HelperRegistry.getSyncablePlayerCaps().isEmpty()) {
            event.getPlayer().connection.send(new ServerboundRequestPlayerUpdatePacket());
        }
    }
}
