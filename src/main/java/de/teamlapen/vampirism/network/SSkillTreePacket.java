package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.player.skills.SkillNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SSkillTreePacket implements IMessage {
    static void encode(SSkillTreePacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.nodes.size());
        for (Map.Entry<ResourceLocation, SkillNode.Builder> e : msg.nodes.entrySet()) {
            buf.writeResourceLocation(e.getKey());
            e.getValue().writeTo(buf);
        }
    }


    static SSkillTreePacket decode(FriendlyByteBuf buf) {
        SSkillTreePacket pkt = new SSkillTreePacket();
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {
            ResourceLocation id = buf.readResourceLocation();
            SkillNode.Builder b = SkillNode.Builder.readFrom(buf);
            pkt.nodes.put(id, b);
        }
        return pkt;
    }


    public static void handle(final SSkillTreePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleSkillTreePacket(msg));
        ctx.setPacketHandled(true);
    }

    private final Map<ResourceLocation, SkillNode.Builder> nodes;

    public SSkillTreePacket(Map<ResourceLocation, SkillNode.Builder> nodes) {
        this.nodes = nodes;
    }

    private SSkillTreePacket() {
        nodes = new HashMap<>();
    }

    public Map<ResourceLocation, SkillNode.Builder> getNodes() {
        return nodes;
    }
}
