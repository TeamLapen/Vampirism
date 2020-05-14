package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AppearancePacket implements IMessage {

    public final int fangType;
    public final int eyeType;

    public AppearancePacket(int fangType, int eyeType) {
        this.fangType = fangType;
        this.eyeType = eyeType;
    }

    static void encode(AppearancePacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.fangType);
        buf.writeVarInt(msg.eyeType);
    }

    static AppearancePacket decode(PacketBuffer buf) {
        return new AppearancePacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(final AppearancePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleAppearancePacket(ctx.getSender(), msg));
        ctx.setPacketHandled(true);
    }
}
