package de.teamlapen.sync.client;

import de.teamlapen.sync.SyncRegistry;
import de.teamlapen.sync.common.packages.ServerboundRequestPlayerUpdatePacket;
import de.teamlapen.vampirism.REFERENCE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = REFERENCE.MODID, value = Dist.CLIENT)
public class ClientEntityEventHandler {

    @SubscribeEvent
    public static void onPlayerLoggedInClient(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!SyncRegistry.getSyncablePlayerCaps().isEmpty()) {
            event.getPlayer().connection.send(ServerboundRequestPlayerUpdatePacket.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawnedClient(ClientPlayerNetworkEvent.Clone event) {
        if (!SyncRegistry.getSyncablePlayerCaps().isEmpty()) {
            event.getPlayer().connection.send(ServerboundRequestPlayerUpdatePacket.INSTANCE);
        }
    }
}
