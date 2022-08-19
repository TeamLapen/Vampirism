package de.teamlapen.lib.network;

import de.teamlapen.lib.LIBREFERENCE;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;

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
        dispatcher.registerMessage(nextID(), ServerboundRequestPlayerUpdatePacket.class, ServerboundRequestPlayerUpdatePacket::encode, ServerboundRequestPlayerUpdatePacket::decode, ServerboundRequestPlayerUpdatePacket::handle);
        dispatcher.registerMessage(nextID(), ClientboundUpdateEntityPacket.class, ClientboundUpdateEntityPacket::encode, ClientboundUpdateEntityPacket::decode, ClientboundUpdateEntityPacket::handle);
    }
}
