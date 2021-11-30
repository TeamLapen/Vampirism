package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public record RequestMinionSelectPacket(de.teamlapen.vampirism.network.RequestMinionSelectPacket.Action action,
                                        List<Pair<Integer, Component>> minions) implements IMessage {

    public static void handle(final RequestMinionSelectPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleRequestMinionSelect(msg.action, msg.minions));
        ctx.setPacketHandled(true);
    }

    static void encode(RequestMinionSelectPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.action.ordinal());
        buf.writeVarInt(msg.minions.size());
        for (Pair<Integer, Component> minion : msg.minions) {
            buf.writeVarInt(minion.getLeft());
            buf.writeComponent(minion.getRight());
        }

    }

    static RequestMinionSelectPacket decode(FriendlyByteBuf buf) {
        Action a = Action.values()[buf.readVarInt()];
        int count = buf.readVarInt();
        List<Pair<Integer, Component>> minions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            minions.add(Pair.of(buf.readVarInt(), buf.readComponent()));
        }
        return new RequestMinionSelectPacket(a, minions);
    }

    public enum Action {
        CALL
    }
}
