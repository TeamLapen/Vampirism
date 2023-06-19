package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.TaskManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public record ClientboundTaskPacket(int containerId,
                                    Map<UUID, TaskManager.TaskWrapper> taskWrappers,
                                    Map<UUID, Set<UUID>> completableTasks,
                                    Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements) implements IMessage.IClientBoundMessage {

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(s -> {
        try {
            return DataResult.success(UUID.fromString(s));
        } catch (Exception e){
            return DataResult.error(() -> "Invalid UUID: " + s);
        }
    }, UUID::toString).stable();
    private static final Codec<Set<UUID>> SET_CODEC = UUID_CODEC.listOf().comapFlatMap(s -> {
        try {
            return DataResult.success((Set<UUID>)new HashSet<>(s));
        } catch (Exception e) {
            return DataResult.error(() -> "Invalid List");
        }
    }, ArrayList::new).stable();
    private static final Codec<ClientboundTaskPacket> CODEC = RecordCodecBuilder.create(func -> {
        return func.group(
                Codec.INT.fieldOf("containerId").forGetter(ClientboundTaskPacket::containerId),
                Codec.unboundedMap(UUID_CODEC, TaskManager.TaskWrapper.CODEC).fieldOf("taskWrappers").forGetter(ClientboundTaskPacket::taskWrappers),
                Codec.unboundedMap(UUID_CODEC, SET_CODEC).fieldOf("completableTasks").forGetter(ClientboundTaskPacket::completableTasks),
                Codec.unboundedMap(UUID_CODEC, Codec.unboundedMap(UUID_CODEC, Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT))).fieldOf("completedRequirements").forGetter(ClientboundTaskPacket::completedRequirements)
        ).apply(func, ClientboundTaskPacket::new);
    });

    public static void encode(@NotNull ClientboundTaskPacket msg, @NotNull FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(CODEC, msg);
    }

    public static @NotNull ClientboundTaskPacket decode(@NotNull FriendlyByteBuf buffer) {
        return buffer.readJsonWithCodec(CODEC);
    }

    public static void handle(final ClientboundTaskPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskPacket(msg));
        ctx.setPacketHandled(true);
    }

}
