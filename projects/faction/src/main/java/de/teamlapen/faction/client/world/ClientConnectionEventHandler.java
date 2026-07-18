package de.teamlapen.faction.client.world;

import de.teamlapen.faction.client.config.preferences.UserPreferences;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public class ClientConnectionEventHandler {

    @SubscribeEvent
    public void onClientStart(ClientPlayerNetworkEvent.LoggingIn event) {
        UserPreferences.init(event.getPlayer().registryAccess());
    }

    @SubscribeEvent
    public void onClientStart(ClientPlayerNetworkEvent.LoggingOut event) {
        UserPreferences.clear();
    }
}
