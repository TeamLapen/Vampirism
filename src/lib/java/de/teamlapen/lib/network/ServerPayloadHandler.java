package de.teamlapen.lib.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleRequestPlayerUpdatePacket(ServerboundRequestPlayerUpdatePacket msg, IPayloadContext context) {
        context.player().ifPresent(player -> {
            ClientboundUpdateEntityPacket update = ClientboundUpdateEntityPacket.createJoinWorldPacket(player);
            if (update != null) {
                update.markAsPlayerItself();
                context.replyHandler().send(update);
            }
        });
    }
}
