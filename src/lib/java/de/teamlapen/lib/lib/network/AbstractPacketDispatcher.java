package de.teamlapen.lib.lib.network;

import com.mojang.serialization.Codec;
import de.teamlapen.lib.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handles packet registration and provides utility methods.
 * Subclass has to register packets.
 * <p>
 * Inspired by @coolAlias <a href="http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/2137055-1-7-x-1-8-customizing-packet-handling-with">tutorial</a>
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
    public final void sendTo(@NotNull IMessage.IClientBoundMessage message, ServerPlayer player) {
        Objects.requireNonNull(message);
        dispatcher.send(PacketDistributor.PLAYER.with(() -> player), message);
        //dispatcher.sendTo(message, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public final void sendToAll(IMessage.IClientBoundMessage message) {
        dispatcher.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Sends a message to everyone within a certain range of the coordinates in the same dimension.
     */
    public final void sendToAllAround(IMessage.IClientBoundMessage message, ResourceKey<Level> dimension, double x, double y, double z,

                                      double range) {
        sendToAllAround(message, new PacketDistributor.TargetPoint(x, y, z,

                range, dimension));
    }

    /**
     * Send this message to everyone within a certain range of a point.
     */
    public final void sendToAllAround(IMessage.IClientBoundMessage message, PacketDistributor.TargetPoint point) {
        dispatcher.send(PacketDistributor.NEAR.with(() -> point), message);
    }

    public final void sendToAllTrackingPlayers(IMessage.IClientBoundMessage message, Entity target) {
        dispatcher.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), message);
    }

    /**
     * Send this message to the server.
     */
    public final void sendToServer(IMessage.IServerBoundMessage message) {
        dispatcher.sendToServer(message);
    }

    protected int nextID() {
        return packetId++;
    }

    protected <MSG extends IMessage.IClientBoundMessage> void registerClientBound(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        dispatcher.messageBuilder(messageType, nextID(), NetworkDirection.PLAY_TO_CLIENT).encoder(encoder).decoder(decoder).consumerNetworkThread(messageConsumer).add();
    }

    protected <MSG extends IMessage.IServerBoundMessage> void registerServerBound(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        dispatcher.messageBuilder(messageType, nextID(), NetworkDirection.PLAY_TO_SERVER).encoder(encoder).decoder(decoder).consumerNetworkThread(messageConsumer).add();
    }

}
