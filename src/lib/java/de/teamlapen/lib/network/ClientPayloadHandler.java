package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleUpdateEntityPacket(ClientboundUpdateEntityPacket pkt, IPayloadContext context) {
        context.workHandler().execute(() -> VampLib.proxy.handleUpdateEntityPacket(pkt));
    }
}
