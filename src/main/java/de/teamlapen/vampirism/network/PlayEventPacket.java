package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayEventPacket implements IMessage {

    static void encode(PlayEventPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.type);
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.stateId);
    }

    static PlayEventPacket decode(PacketBuffer buf) {
        return new PlayEventPacket(buf.readVarInt(), buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(final PlayEventPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handlePlayEventPacket(msg));
        ctx.setPacketHandled(true);
    }

    public final int type;
    public final BlockPos pos;
    public final int stateId;

    public PlayEventPacket(int type, BlockPos pos, int stateId) {
        this.type = type;
        this.pos = pos;
        this.stateId = stateId;
    }
}
