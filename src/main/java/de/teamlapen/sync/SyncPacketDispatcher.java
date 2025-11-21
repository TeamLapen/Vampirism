package de.teamlapen.sync;

import de.teamlapen.sync.client.ClientPayloadHandler;
import de.teamlapen.sync.common.packages.ClientboundUpdateEntityPacket;
import de.teamlapen.vampirism.REFERENCE;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * PacketDispatcher implementation used by the library mod.
 * ONLY FOR INTERNAL USAGE
 */
@EventBusSubscriber(modid = REFERENCE.MODID)
public class SyncPacketDispatcher {

    private static final String VERSION = "vampirism-sync";

    @SubscribeEvent
    public static void registerHandler(RegisterPayloadHandlersEvent event) {
        registerPackets(event.registrar(VERSION));
    }

    private static void registerPackets(PayloadRegistrar registrar) {
        registrar.playToClient(ClientboundUpdateEntityPacket.TYPE, ClientboundUpdateEntityPacket.CODEC, (s, l) -> ClientPayloadHandler.getInstance().handleUpdateEntityPacket(s, l));
    }
}
