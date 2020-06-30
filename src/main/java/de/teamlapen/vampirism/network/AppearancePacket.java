package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AppearancePacket implements IMessage {

    static void encode(AppearancePacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeString(msg.name);
        buf.writeVarInt(msg.data.length);
        for (int value : msg.data) {
            buf.writeVarInt(value);
        }
    }

    static AppearancePacket decode(PacketBuffer buf) {
        int entityId = buf.readVarInt();
        String newName = buf.readString(MinionData.MAX_NAME_LENGTH);
        int[] data = new int[buf.readVarInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = buf.readVarInt();
        }
        return new AppearancePacket(entityId, newName, data);
    }

    public static void handle(final AppearancePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleAppearancePacket(ctx.getSender(), msg));
        ctx.setPacketHandled(true);
    }

    public final int entityId;
    public final String name;
    public final int[] data;

    public AppearancePacket(int entityId, String newName, int... data) {
        this.entityId = entityId;
        this.name = newName;
        this.data = data;
    }
}
