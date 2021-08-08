package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public record ActionBindingPacket(int actionBindingId,
                                  IAction action) implements IMessage {

    static void encode(final ActionBindingPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.actionBindingId);
        buf.writeUtf(msg.action.getRegistryName().toString());
    }

    static ActionBindingPacket decode(FriendlyByteBuf buf) {
        return new ActionBindingPacket(buf.readVarInt(), ModRegistries.ACTIONS.getValue(new ResourceLocation(buf.readUtf(32767))));
    }

    public static void handle(final ActionBindingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleActionBindingPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

}
