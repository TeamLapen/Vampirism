package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Request an update packet for the players {@link ISyncable.ISyncableEntityCapabilityInst} (e.g. on World join)
 */
public class RequestPlayerUpdatePacket implements IMessage {

    @SuppressWarnings("EmptyMethod")
    static void encode(RequestPlayerUpdatePacket msg, FriendlyByteBuf buf) {

    }

    static RequestPlayerUpdatePacket decode(FriendlyByteBuf buf) {
        return new RequestPlayerUpdatePacket();
    }


    public static void handle(final RequestPlayerUpdatePacket pkt, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> { //Execute on main thread
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                UpdateEntityPacket update = UpdateEntityPacket.createJoinWorldPacket(player);
                if (update != null) {
                    update.markAsPlayerItself();
                    VampLib.dispatcher.sendTo(update, player);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
