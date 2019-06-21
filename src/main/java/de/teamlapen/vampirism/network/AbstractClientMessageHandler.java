package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.network.AbstractMessageHandler;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Extend this to easily handle messages on client side
 */
public abstract class AbstractClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {//TODO @maxanier

    public final IMessage handleServerMessage(EntityPlayer player, T message, MessageContext ctx) {
        return null;
    }

    @Override
    protected AbstractPacketDispatcher getDispatcher() {
        return VampirismMod.dispatcher;
    }
}
