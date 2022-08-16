package de.teamlapen.vampirism.network;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.Supplier;

public record ClientboundBloodValuePacket(Map<ResourceLocation, Float>[] values) implements IMessage {

    static void encode(ClientboundBloodValuePacket msg, FriendlyByteBuf buf) {
        for (Map<ResourceLocation, Float> e : msg.values) {
            buf.writeVarInt(e.size());
            for (Map.Entry<ResourceLocation, Float> f : e.entrySet()) {
                buf.writeResourceLocation(f.getKey());
                buf.writeFloat(f.getValue());
            }
        }
    }

    static ClientboundBloodValuePacket decode(FriendlyByteBuf buf) {
        @SuppressWarnings("unchecked")
        Map<ResourceLocation, Float>[] values = (Map<ResourceLocation, Float>[]) Array.newInstance(Map.class, 3);
        for (int i = 0; i < 3; i++) {
            ImmutableMap.Builder<ResourceLocation, Float> builder = ImmutableMap.builder();
            int z = buf.readVarInt();
            for (int u = 0; u < z; u++) {
                builder.put(buf.readResourceLocation(), buf.readFloat());
            }
            values[i] = builder.build();
        }
        return new ClientboundBloodValuePacket(values);
    }

    public static void handle(final ClientboundBloodValuePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleBloodValuePacket(msg));
        ctx.setPacketHandled(true);
    }

    public Map<ResourceLocation, Float>[] getValues() {
        return values;
    }
}
