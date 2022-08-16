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

public record ClientboundSkillTreePacket(Map<ResourceLocation, SkillNode.Builder> nodes) implements IMessage {
    static void encode(ClientboundSkillTreePacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.nodes.size());
        for (Map.Entry<ResourceLocation, SkillNode.Builder> e : msg.nodes.entrySet()) {
            buf.writeResourceLocation(e.getKey());
            e.getValue().writeTo(buf);
        }
    }


    static ClientboundSkillTreePacket decode(FriendlyByteBuf buf) {
        HashMap<ResourceLocation, SkillNode.Builder> nodes = new HashMap<>();
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {
            nodes.put(buf.readResourceLocation(), SkillNode.Builder.readFrom(buf));
        }
        return new ClientboundSkillTreePacket(nodes);
    }


    public static void handle(final ClientboundSkillTreePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleSkillTreePacket(msg));
        ctx.setPacketHandled(true);
    }

}
