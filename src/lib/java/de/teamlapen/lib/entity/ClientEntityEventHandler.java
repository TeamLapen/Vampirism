package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.network.RequestPlayerUpdatePacket;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Library's entity event handler to do client-sided stuff
 */
public class ClientEntityEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedInClient(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (HelperRegistry.getSyncablePlayerCaps().size() > 0) {
            VampLib.dispatcher.sendToServer(new RequestPlayerUpdatePacket());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawnedClient(ClientPlayerNetworkEvent.RespawnEvent event) {
        if (HelperRegistry.getSyncablePlayerCaps().size() > 0) {
            VampLib.dispatcher.sendToServer(new RequestPlayerUpdatePacket());
        }
    }
}
