package de.teamlapen.lib.network;

import de.teamlapen.lib.LIBREFERENCE;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;

import java.util.Optional;

/**
 * PacketDispatcher implementation used by the library mod.
 * ONLY FOR INTERNAL USAGE
 */
public class LibraryPacketDispatcher extends AbstractPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public LibraryPacketDispatcher() {
        super(NetworkRegistry.ChannelBuilder.named(new ResourceLocation(LIBREFERENCE.MODID, "main")).clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel());
    }

    @Override
    public void registerPackets() {
        registerServerBound(ServerboundRequestPlayerUpdatePacket.class, ServerboundRequestPlayerUpdatePacket::encode, ServerboundRequestPlayerUpdatePacket::decode, ServerboundRequestPlayerUpdatePacket::handle);
        registerClientBound(ClientboundUpdateEntityPacket.class, ClientboundUpdateEntityPacket::encode, ClientboundUpdateEntityPacket::decode, ClientboundUpdateEntityPacket::handle);
    }
}
