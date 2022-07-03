package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ActionBindingPacket(int actionBindingId, IAction<?> action) implements IMessage {

    static void encode(final ActionBindingPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.actionBindingId);
        buf.writeResourceLocation(RegUtil.id(msg.action));
    }

    static ActionBindingPacket decode(FriendlyByteBuf buf) {
        return new ActionBindingPacket(buf.readVarInt(), RegUtil.getAction(buf.readResourceLocation()));
    }

    public static void handle(final ActionBindingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleActionBindingPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

}
