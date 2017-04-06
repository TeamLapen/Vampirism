package de.teamlapen.lib.lib.network;

import de.teamlapen.lib.VampLib;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Makes Message Handling a little more comfortable.
 * Inspired by @coolAlias' tutorial
 * http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/2137055-1-7-x-1-8-customizing-packet-handling-with
 */
public abstract class AbstractMessageHandler<T extends IMessage> implements IMessageHandler<T, IMessage> {
    /**
     * Handle a message received on the client side
     *
     * @param player Client player. If NOT handled on main Thread this can be null when the game starts
     * @return a message to send back to the Server, or null if no reply is necessary
     */
    @SideOnly(Side.CLIENT)
    public abstract IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx);

    /**
     * Handle a message received on the server side
     *
     * @param player The player belonging to this message.
     * @return a message to send back to the Client, or null if no reply is necessary
     */
    public abstract IMessage handleServerMessage(EntityPlayer player, T message, MessageContext ctx);

    /**
     * Calls the respective handle method and provides the right player entity
     *
     * @return If not null the response message will be send to back to the sender using the packet dispatcher of this handler {@link AbstractMessageHandler#getDispatcher()}
     */
    @Override
    public IMessage onMessage(final T message, final MessageContext ctx) {

        if (ctx.side.isClient()) {

            if (handleOnMainThread()) {
                final AbstractPacketDispatcher dispatcher = getDispatcher();
                IThreadListener mainThread = Minecraft.getMinecraft();
                mainThread.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        EntityPlayer player = getPlayerEntityByProxy(ctx);
                        IMessage response = handleClientMessage(player, message, ctx);
                        if (response != null) {
                            dispatcher.sendToServer(response);
                        }
                    }
                });
                return null;
            }

            return handleClientMessage(getPlayerEntityByProxy(ctx), message, ctx);
        } else {
            if (handleOnMainThread()) {
                final AbstractPacketDispatcher dispatcher = getDispatcher();
                IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.getEntityWorld();
                mainThread.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        EntityPlayer player = getPlayerEntityByProxy(ctx);

                        IMessage response = handleServerMessage(player, message, ctx);
                        if (response != null) {
                            dispatcher.sendTo(response, (EntityPlayerMP) player);
                        }
                    }
                });
                return null;
            }
            return handleServerMessage(getPlayerEntityByProxy(ctx), message, ctx);
        }
    }

    protected abstract AbstractPacketDispatcher getDispatcher();

    /**
     * Return the corresponding player entity.
     * Do this via a proxy to avoid ClassNotFound Exeptions on server side
     *
     * @param ctx
     * @return
     */
    protected EntityPlayer getPlayerEntityByProxy(MessageContext ctx) {
        return VampLib.proxy.getPlayerEntity(ctx);
    }

    protected abstract boolean handleOnMainThread();
}