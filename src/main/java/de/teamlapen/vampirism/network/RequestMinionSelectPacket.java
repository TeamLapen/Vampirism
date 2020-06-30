package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class RequestMinionSelectPacket implements IMessage {

    public static void handle(final RequestMinionSelectPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleRequestMinionSelect(msg.action, msg.minions));
        ctx.setPacketHandled(true);
    }

    static void encode(RequestMinionSelectPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.action.ordinal());
        buf.writeVarInt(msg.minions.size());
        for (Pair<Integer, ITextComponent> minion : msg.minions) {
            buf.writeVarInt(minion.getLeft());
            buf.writeTextComponent(minion.getRight());
        }

    }

    static RequestMinionSelectPacket decode(PacketBuffer buf) {
        Action a = Action.values()[buf.readVarInt()];
        int count = buf.readVarInt();
        List<Pair<Integer, ITextComponent>> minions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            minions.add(Pair.of(buf.readVarInt(), buf.readTextComponent()));
        }
        return new RequestMinionSelectPacket(a, minions);
    }

    public final List<Pair<Integer, ITextComponent>> minions;
    public final Action action;

    public RequestMinionSelectPacket(Action action, List<Pair<Integer, ITextComponent>> minions) {
        this.action = action;
        this.minions = minions;
    }

    public enum Action {
        CALL
    }
}
