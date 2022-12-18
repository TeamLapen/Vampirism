package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Request an update packet for the players {@link ISyncable.ISyncableEntityCapabilityInst} (e.g. on World join)
 */
public class ServerboundRequestPlayerUpdatePacket implements IMessage.IServerBoundMessage {

    @SuppressWarnings("EmptyMethod")
    static void encode(ServerboundRequestPlayerUpdatePacket msg, FriendlyByteBuf buf) {

    }

    static @NotNull ServerboundRequestPlayerUpdatePacket decode(FriendlyByteBuf buf) {
        return new ServerboundRequestPlayerUpdatePacket();
    }


    public static void handle(final ServerboundRequestPlayerUpdatePacket pkt, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> { //Execute on main thread
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                ClientboundUpdateEntityPacket update = ClientboundUpdateEntityPacket.createJoinWorldPacket(player);
                if (update != null) {
                    update.markAsPlayerItself();
                    VampLib.dispatcher.sendTo(update, player);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
