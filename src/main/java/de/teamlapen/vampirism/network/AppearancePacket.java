package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AppearancePacket implements IMessage {

    static void encode(AppearancePacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeVarInt(msg.data.length);
        for (int value : msg.data) {
            buf.writeVarInt(value);
        }
    }

    static AppearancePacket decode(PacketBuffer buf) {
        int entityId = buf.readVarInt();
        int[] data = new int[buf.readVarInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = buf.readVarInt();
        }
        return new AppearancePacket(entityId, data);
    }

    public static void handle(final AppearancePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleAppearancePacket(ctx.getSender(), msg));
        ctx.setPacketHandled(true);
    }

    public final int entityId;
    public final int[] data;

    public AppearancePacket(int entityId, int... data) {
        this.entityId = entityId;
        this.data = data;
    }
}
