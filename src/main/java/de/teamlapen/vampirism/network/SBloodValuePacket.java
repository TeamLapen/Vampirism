package de.teamlapen.vampirism.network;

import com.google.common.collect.Maps;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.Supplier;

public class SBloodValuePacket implements IMessage {

    static void encode(SBloodValuePacket msg, PacketBuffer buf) {
        for (Map<ResourceLocation, Float> e : msg.values) {
            buf.writeVarInt(e.size());
            for (Map.Entry<ResourceLocation, Float> f : e.entrySet()) {
                buf.writeResourceLocation(f.getKey());
                buf.writeFloat(f.getValue());
            }
        }
    }

    static SBloodValuePacket decode(PacketBuffer buf) {
        @SuppressWarnings("unchecked")
        Map<ResourceLocation, Float>[] values = (Map<ResourceLocation, Float>[]) Array.newInstance(Map.class, 3);
        for (int i = 0; i < 3; i++) {
            Map<ResourceLocation, Float> map = Maps.newConcurrentMap();
            int z = buf.readVarInt();
            for (int u = 0; u < z; u++) {
                map.put(buf.readResourceLocation(), buf.readFloat());
            }
            values[i] = map;
        }
        return new SBloodValuePacket(values);
    }

    public static void handle(final SBloodValuePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleBloodValuePacket(msg));
        ctx.setPacketHandled(true);
    }

    private final Map<ResourceLocation, Float>[] values;

    public SBloodValuePacket(Map<ResourceLocation, Float>[] values) {
        this.values = values;
    }

    public Map<ResourceLocation, Float>[] getValues() {
        return values;
    }
}
