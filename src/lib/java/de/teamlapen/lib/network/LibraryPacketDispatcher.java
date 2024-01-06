package de.teamlapen.lib.network;

import com.mojang.serialization.Codec;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

/**
 * PacketDispatcher implementation used by the library mod.
 * ONLY FOR INTERNAL USAGE
 */
public class LibraryPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    @SubscribeEvent
    public void registerHandler(RegisterPayloadHandlerEvent event) {
        registerPackets(event.registrar(LIBREFERENCE.MODID).versioned(PROTOCOL_VERSION));
    }

    public void registerPackets(IPayloadRegistrar registrar) {
        registrar.common(ClientboundUpdateEntityPacket.ID, reader(ClientboundUpdateEntityPacket.CODEC), handler -> handler.client(ClientPayloadHandler.getInstance()::handleUpdateEntityPacket));
        registrar.common(ServerboundRequestPlayerUpdatePacket.ID, reader(ServerboundRequestPlayerUpdatePacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleRequestPlayerUpdatePacket));
    }

    protected <T> FriendlyByteBuf.Reader<T> reader(Codec<T> codec) {
        return buf -> buf.readJsonWithCodec(codec);
    }
}
