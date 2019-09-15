package de.teamlapen.vampirism.network;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class BloodValuePacket implements IMessage {

    static void encode(BloodValuePacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.values.size());
        for (Map.Entry<ResourceLocation, Pair<Map<ResourceLocation, Integer>, Integer>> e : msg.values.entrySet()) {
            buf.writeResourceLocation(e.getKey());
            buf.writeVarInt(e.getValue().getFirst().size());
            for (Map.Entry<ResourceLocation, Integer> f : e.getValue().getFirst().entrySet()) {
                buf.writeResourceLocation(f.getKey());
                buf.writeVarInt(f.getValue());
            }
            buf.writeVarInt(e.getValue().getSecond());
        }
    }

    static BloodValuePacket decode(PacketBuffer buf) {
        Map<ResourceLocation, Pair<Map<ResourceLocation, Integer>, Integer>> values = Maps.newConcurrentMap();
        int t = buf.readVarInt();
        for (int i = 0; i < t; i++) {
            ResourceLocation resourceLocation = buf.readResourceLocation();
            Map<ResourceLocation, Integer> map = Maps.newConcurrentMap();
            int z = buf.readVarInt();
            for (int u = 0; u < z; u++) {
                map.put(buf.readResourceLocation(), buf.readVarInt());
            }
            values.put(resourceLocation, new Pair<>(map, buf.readVarInt()));
        }
        return new BloodValuePacket(values);
    }

    public static void handle(final BloodValuePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();

        ctx.enqueueWork(() -> VampirismMod.proxy.handleBloodValuePacket(msg));
    }

    private Map<ResourceLocation, Pair<Map<ResourceLocation, Integer>, Integer>> values;

    public BloodValuePacket(Map<ResourceLocation, Pair<Map<ResourceLocation, Integer>, Integer>> values) {
        this.values = values;
    }

    public Pair<Map<ResourceLocation, Integer>, Integer> getValues(ResourceLocation resourceLocation) {
        return values.get(resourceLocation);
    }
}
