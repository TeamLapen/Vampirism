package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.skills.SkillNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record ClientboundSkillTreePacket(Map<ResourceLocation, SkillNode.Builder> nodes) implements IMessage.IClientBoundMessage {
    static void encode(@NotNull ClientboundSkillTreePacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(msg.nodes.size());
        for (Map.Entry<ResourceLocation, SkillNode.Builder> e : msg.nodes.entrySet()) {
            buf.writeResourceLocation(e.getKey());
            e.getValue().writeTo(buf);
        }
    }


    static @NotNull ClientboundSkillTreePacket decode(@NotNull FriendlyByteBuf buf) {
        HashMap<ResourceLocation, SkillNode.Builder> nodes = new HashMap<>();
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {
            nodes.put(buf.readResourceLocation(), SkillNode.Builder.readFrom(buf));
        }
        return new ClientboundSkillTreePacket(nodes);
    }


    public static void handle(final ClientboundSkillTreePacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleSkillTreePacket(msg));
        ctx.setPacketHandled(true);
    }

}
