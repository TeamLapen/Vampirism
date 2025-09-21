package de.teamlapen.sync.server;

import de.teamlapen.sync.common.packages.ClientboundUpdateEntityPacket;
import de.teamlapen.sync.common.packages.ServerboundRequestPlayerUpdatePacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleRequestPlayerUpdatePacket(ServerboundRequestPlayerUpdatePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientboundUpdateEntityPacket update = ClientboundUpdateEntityPacket.createJoinWorldPacket(context.player());
            if (update != null) {
                update.markAsPlayerItself();
                context.reply(update);
            }
        });
    }
}
