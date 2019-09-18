package de.teamlapen.lib.lib.network;

import de.teamlapen.lib.network.IMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Handles packet registration and provides utility methods.
 * Subclass has to register packets.
 * <p>
 * Inspired by @coolAlias tutorial
 * http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/2137055-1-7-x-1-8-customizing-packet-handling-with
 */
public abstract class AbstractPacketDispatcher {
    protected final SimpleChannel dispatcher;
    private byte packetId = 0;

    protected AbstractPacketDispatcher(SimpleChannel channel) {
        dispatcher = channel;
    }

    /**
     * Template for packets:
     * <p>
     * <p>
     * public static void encode(PACKET msg, PacketBuffer buf){
     * <p>
     * }
     * <p>
     * public static PACKET decode(PacketBuffer buf){
     * return new
     * }
     * <p>
     * <p>
     * public static void handle(final PACKET pkt, Supplier<NetworkEvent.Context> contextSupplier){
     * final NetworkEvent.Context ctx = contextSupplier.get();
     * ctx.enqueueWork( () -> { //Execute on main thread
     * <p>
     * });
     * ctx.setPacketHandled(true);
     * }
     */
    public abstract void registerPackets();

    /**
     * Send this message to the specified player.
     */
    public final void sendTo(IMessage message, ServerPlayerEntity player) {
        dispatcher.send(PacketDistributor.PLAYER.with(() -> player), message);
        dispatcher.sendTo(message, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public final void sendToAll(IMessage message) {
        dispatcher.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Sends a message to everyone within a certain range of the coordinates in the same dimension.
     */
    public final void sendToAllAround(IMessage message, DimensionType dimension, double x, double y, double z,

                                      double range) {
        sendToAllAround(message, new PacketDistributor.TargetPoint(x, y, z,

                range, dimension));
    }

    /**
     * Send this message to everyone within a certain range of a point.
     */
    public final void sendToAllAround(IMessage message, PacketDistributor.TargetPoint point) {
        dispatcher.send(PacketDistributor.NEAR.with(() -> point), message);
    }

    public final void sendToAllTrackingPlayers(IMessage message, Entity target) {
        dispatcher.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), message);
    }

    /**
     * Send this message to the server.
     */
    public final void sendToServer(IMessage message) {
        dispatcher.sendToServer(message);
    }

    protected int nextID() {
        return packetId++;
    }

}
