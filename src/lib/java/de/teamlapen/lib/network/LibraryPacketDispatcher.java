package de.teamlapen.lib.network;

import de.teamlapen.lib.LIBREFERENCE;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * PacketDispatcher implementation used by the library mod.
 * ONLY FOR INTERNAL USAGE
 */
public class LibraryPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    @SubscribeEvent
    public void registerHandler(RegisterPayloadHandlersEvent event) {
        registerPackets(event.registrar(LIBREFERENCE.MODID).versioned(PROTOCOL_VERSION));
    }

    public void registerPackets(PayloadRegistrar registrar) {
        registrar.playToClient(ClientboundUpdateEntityPacket.TYPE, ClientboundUpdateEntityPacket.CODEC, (s, l) -> ClientPayloadHandler.getInstance().handleUpdateEntityPacket(s, l));
        registrar.playToServer(ServerboundRequestPlayerUpdatePacket.TYPE, ServerboundRequestPlayerUpdatePacket.CODEC, (msg, context) -> ServerPayloadHandler.getInstance().handleRequestPlayerUpdatePacket(msg, context));
    }
}
