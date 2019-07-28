package de.teamlapen.lib.network;

import de.teamlapen.lib.LIBREFERENCE;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;

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
        dispatcher.registerMessage(nextID(), RequestPlayerUpdatePacket.class, RequestPlayerUpdatePacket::encode, RequestPlayerUpdatePacket::decode, RequestPlayerUpdatePacket::handle);
        dispatcher.registerMessage(nextID(), UpdateEntityPacket.class, UpdateEntityPacket::encode, UpdateEntityPacket::decode, UpdateEntityPacket::handle);
    }
}
