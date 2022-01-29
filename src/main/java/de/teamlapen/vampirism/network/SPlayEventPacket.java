package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SPlayEventPacket(int type, BlockPos pos, int stateId) implements IMessage {

    static void encode(SPlayEventPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.type);
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.stateId);
    }

    static SPlayEventPacket decode(FriendlyByteBuf buf) {
        return new SPlayEventPacket(buf.readVarInt(), buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(final SPlayEventPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handlePlayEventPacket(msg));
        ctx.setPacketHandled(true);
    }

}
