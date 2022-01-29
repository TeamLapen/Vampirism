package de.teamlapen.vampirism.network;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.Supplier;

public record SBloodValuePacket(
        Pair<Map<ResourceLocation, Integer>, Integer>[] values) implements IMessage {

    static void encode(SBloodValuePacket msg, FriendlyByteBuf buf) {
        for (Pair<Map<ResourceLocation, Integer>, Integer> e : msg.values) {
            buf.writeVarInt(e.getFirst().size());
            for (Map.Entry<ResourceLocation, Integer> f : e.getFirst().entrySet()) {
                buf.writeResourceLocation(f.getKey());
                buf.writeVarInt(f.getValue());
            }
            buf.writeVarInt(e.getSecond());
        }
    }

    static SBloodValuePacket decode(FriendlyByteBuf buf) {
        @SuppressWarnings("unchecked")
        Pair<Map<ResourceLocation, Integer>, Integer>[] values = (Pair<Map<ResourceLocation, Integer>, Integer>[]) Array.newInstance(Pair.class, 3);
        for (int i = 0; i < 3; i++) {
            Map<ResourceLocation, Integer> map = Maps.newConcurrentMap();
            int z = buf.readVarInt();
            for (int u = 0; u < z; u++) {
                map.put(buf.readResourceLocation(), buf.readVarInt());
            }
            values[i] = new Pair<>(map, buf.readVarInt());
        }
        return new SBloodValuePacket(values);
    }

    public static void handle(final SBloodValuePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleBloodValuePacket(msg));
        ctx.setPacketHandled(true);
    }

    public Pair<Map<ResourceLocation, Integer>, Integer>[] getValues() {
        return values;
    }
}
