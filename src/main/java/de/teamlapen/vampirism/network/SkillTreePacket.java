package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.player.skills.SkillNode;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SkillTreePacket implements IMessage {
    private final static Logger LOGGER = LogManager.getLogger();

    static void encode(SkillTreePacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.nodes.size());
        for (Map.Entry<ResourceLocation, SkillNode.Builder> e : msg.nodes.entrySet()) {
            buf.writeResourceLocation(e.getKey());
            e.getValue().writeTo(buf);
        }
    }


    static SkillTreePacket decode(PacketBuffer buf) {
        SkillTreePacket pkt = new SkillTreePacket();
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {
            ResourceLocation id = buf.readResourceLocation();
            SkillNode.Builder b = SkillNode.Builder.readFrom(buf);
            pkt.nodes.put(id, b);
        }
        return pkt;
    }


    public static void handle(final SkillTreePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();

        ctx.enqueueWork(() -> {
            VampirismMod.proxy.handleSkillTree(msg);
        });
    }

    private Map<ResourceLocation, SkillNode.Builder> nodes;

    public SkillTreePacket(Map<ResourceLocation, SkillNode.Builder> nodes) {
        this.nodes = nodes;
    }

    private SkillTreePacket() {
        nodes = new HashMap<>();
    }

    public Map<ResourceLocation, SkillNode.Builder> getNodes() {
        return nodes;
    }
}
