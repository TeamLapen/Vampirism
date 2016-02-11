package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.AbstractMessageHandler;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.network.ISyncable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Request a update packet for the players {@link ISyncable.ISyncableExtendedProperties} (e.g. on World join)
 * TODO check if there is a better way to do this
 */
public class RequestPlayerUpdatePacket implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler extends AbstractMessageHandler<RequestPlayerUpdatePacket> {

        @Override
        public IMessage handleClientMessage(EntityPlayer player, RequestPlayerUpdatePacket message, MessageContext ctx) {
            return null;
        }

        @Override
        public IMessage handleServerMessage(EntityPlayer player, RequestPlayerUpdatePacket message, MessageContext ctx) {
            return UpdateEntityPacket.createJoinWorldPacket(player);
        }

        @Override
        protected boolean handleOnMainThread() {
            return true;
        }

        @Override
        protected AbstractPacketDispatcher getDispatcher() {
            return VampLib.dispatcher;
        }
    }
}
