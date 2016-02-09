package de.teamlapen.lib.network;

import de.teamlapen.lib.LIBREFERENCE;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;

/**
 * PacketDispatcher implementation used by the library mod.
 * ONLY FOR INTERNAL USAGE
 */
public class LibraryPacketDispatcher extends AbstractPacketDispatcher {
    public LibraryPacketDispatcher() {
        super(LIBREFERENCE.MODID);
    }

    @Override
    public void registerPackets() {

    }
}
