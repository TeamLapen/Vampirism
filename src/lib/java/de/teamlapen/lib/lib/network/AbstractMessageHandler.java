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
     * @return a message to send back to the Server, or null if no reply is necessary
     */
    @SideOnly(Side.CLIENT)
    public abstract IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx);

    /**
     * Handle a message received on the server side
     *
     * @return a message to send back to the Client, or null if no reply is necessary
     */
    public abstract IMessage handleServerMessage(EntityPlayer player, T message, MessageContext ctx);

    /*
     Calls the respective handle method and provides the right player entity
     */
    @Override
    public IMessage onMessage(final T message, final MessageContext ctx) {
        final EntityPlayer player = getPlayerEntityByProxy(ctx);
        if (ctx.side.isClient()) {

            if (handleOnMainThread()) {
                final AbstractPacketDispatcher dispatcher = getDispatcher();
                IThreadListener mainThread = Minecraft.getMinecraft();
                mainThread.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        IMessage response = handleClientMessage(player, message, ctx);
                        if (response != null) {
                            dispatcher.sendToServer(response);
                        }
                    }
                });
                return null;
            }
            return handleClientMessage(player, message, ctx);
        } else {
            if (handleOnMainThread()) {
                final AbstractPacketDispatcher dispatcher = getDispatcher();
                IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
                mainThread.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        IMessage response = handleServerMessage(player, message, ctx);
                        if (response != null) {
                            dispatcher.sendTo(response, (EntityPlayerMP) player);
                        }
                    }
                });
                return null;
            }
            return handleServerMessage(player, message, ctx);
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