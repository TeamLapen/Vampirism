package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayEventPacket(int type, BlockPos pos, int stateId) implements IMessage {

    static void encode(PlayEventPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.type);
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.stateId);
    }

    static PlayEventPacket decode(FriendlyByteBuf buf) {
        return new PlayEventPacket(buf.readVarInt(), buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(final PlayEventPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handlePlayEventPacket(msg));
        ctx.setPacketHandled(true);
    }

}
