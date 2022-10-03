package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.network.ServerboundRequestPlayerUpdatePacket;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Library's entity event handler to do client-sided stuff
 */
public class ClientEntityEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedInClient(ClientPlayerNetworkEvent.LoggingIn event) {
        if (HelperRegistry.getSyncablePlayerCaps().size() > 0) {
            VampLib.dispatcher.sendToServer(new ServerboundRequestPlayerUpdatePacket());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawnedClient(ClientPlayerNetworkEvent.Clone event) {
        if (HelperRegistry.getSyncablePlayerCaps().size() > 0) {
            VampLib.dispatcher.sendToServer(new ServerboundRequestPlayerUpdatePacket());
        }
    }
}
