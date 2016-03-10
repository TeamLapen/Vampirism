package de.teamlapen.lib.lib.network;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

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
        VampirismMod.log.t("Sending %s to %s", message, player);
        dispatcher.sendTo(message, player);
    }

    /**
     * Send this message to everyone within a certain range of a point.
     * See {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
     */
    public final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        dispatcher.sendToAllAround(message, point);
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
     * Sends a message to everyone within a certain range of the player provided.
     */
    public final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        sendToAllAround(message, player.worldObj.provider.getDimensionId(), player.posX,

                player.posY, player.posZ, range);
    }

    public final void sendToAllTrackingPlayers(IMessage message, Entity target) {
        EntityTracker et = ((WorldServer) target.worldObj).getEntityTracker();
        // does not send it to the player himself it target is a player et.sendToAllTrackingEntity(target, dispatcher.getPacketFrom(message));
        et.func_151248_b(target, dispatcher.getPacketFrom(message));
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
