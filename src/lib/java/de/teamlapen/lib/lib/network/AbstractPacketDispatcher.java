package de.teamlapen.lib.lib.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Handles packet registration and provides utility methods.
 * Subclass has to register packets.
 * <p>
 * Inspired by @coolAlias tutorial
 * http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/2137055-1-7-x-1-8-customizing-packet-handling-with
 */
public abstract class AbstractPacketDispatcher {
    private final SimpleNetworkWrapper dispatcher;
    private byte packetId = 0;


    protected AbstractPacketDispatcher(String channelName) {
        dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
    }

    public abstract void registerPackets();

    /**
     * Send this message to the specified player.
     * See {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
     */
    public final void sendTo(IMessage message, EntityPlayerMP player) {
        dispatcher.sendTo(message, player);
    }

    public final void sendToAll(IMessage message) {
        dispatcher.sendToAll(message);
    }

    /**
     * Sends a message to everyone within a certain range of the coordinates in the same dimension.
     * See {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
     */
    public final void sendToAllAround(IMessage message, int dimension, double x, double y, double z,

                                      double range) {
        sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z,

                range));
    }

    /**
     * Send this message to everyone within a certain range of a point.
     * See {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
     */
    public final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        dispatcher.sendToAllAround(message, point);
    }

    /**
     * Sends a message to everyone within a certain range of the player provided.
     */
    public final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        sendToAllAround(message, player.getEntityWorld().provider.getDimension(), player.posX,

                player.posY, player.posZ, range);
    }

    public final void sendToAllTrackingPlayers(IMessage message, Entity target) {
        EntityTracker et = ((WorldServer) target.getEntityWorld()).getEntityTracker();

        //Send the message to the target itself if it is a player
        if (target instanceof EntityPlayerMP) {
            sendTo(message, (EntityPlayerMP) target);
        }
        for (EntityPlayer player : et.getTrackingPlayers(target)) {
            if (player instanceof EntityPlayerMP) {
                sendTo(message, (EntityPlayerMP) player);
            }
        }

        //This approach creates a separate package for each player, but this is required to some weird netty stuff related to LAN and FMLProxyPackets #268
        //Vanilla would use, but this does not work
        /*
            Packet pkt = dispatcher.getPacketFrom(message);
            et.sendToTrackingAndSelf(target, pkt);
        */
        //Alternative, would need some more testing and improvements
        /*
            FMLProxyPacket pkt = dispatcher.getPacketFrom(message);
            for(EntityPlayerMP player : players){
                 pkt.payload().retain();
                player.connection.sendPacket(pkt);
            }
            pkt.payload().release();
         */
    }

    /**
     * Send this message to everyone within the supplied dimension.
     * See {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
     */
    public final void sendToDimension(IMessage message, int dimensionId) {
        dispatcher.sendToDimension(message, dimensionId);
    }

    /**
     * Send this message to the server.
     * See {@link SimpleNetworkWrapper#sendToServer(IMessage)}
     */
    public final void sendToServer(IMessage message) {
        dispatcher.sendToServer(message);
    }

    /**
     * Registers a message and message handler
     *
     * @param side Side this message should be received
     */
    protected final void registerMessage(Class handlerClass, Class messageClass, Side side) {
        dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
    }
}
