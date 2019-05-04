package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Request a update packet for the players {@link ISyncable.ISyncableEntityCapabilityInst} (e.g. on World join)
 * TODO check if there is a better way to do this
 */
public class RequestPlayerUpdatePacket implements IMessage {

    static void encode(RequestPlayerUpdatePacket msg, PacketBuffer buf) {

    }

    static RequestPlayerUpdatePacket decode(PacketBuffer buf) {
        return new RequestPlayerUpdatePacket();
    }


    public static void handle(final RequestPlayerUpdatePacket pkt, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> { //Execute on main thread
            EntityPlayerMP player = ctx.getSender();
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
