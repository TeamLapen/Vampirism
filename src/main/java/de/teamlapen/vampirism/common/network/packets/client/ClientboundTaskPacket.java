package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.entity.player.TaskManager;
import de.teamlapen.vampirism.common.serialization.ModStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ClientboundTaskPacket(int containerId,
                                    Map<UUID, TaskManager.TaskWrapper> taskWrappers,
                                    Map<UUID, Set<UUID>> completableTasks,
                                    Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements) implements CustomPacketPayload {

    public static final Type<ClientboundTaskPacket> TYPE = new Type<>(VResourceLocation.mod("task"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTaskPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundTaskPacket::containerId,
            ByteBufCodecs.map(i -> new HashMap<>(), ModStreamCodecs.UUID, ByteBufCodecs.fromCodec(TaskManager.TaskWrapper.CODEC)), ClientboundTaskPacket::taskWrappers,
            ByteBufCodecs.map(i -> new HashMap<>(), ModStreamCodecs.UUID, ModStreamCodecs.UUID.apply(ByteBufCodecs.collection(s -> new HashSet<>()))), ClientboundTaskPacket::completableTasks,
            ByteBufCodecs.map(i -> new HashMap<>(), ModStreamCodecs.UUID, ByteBufCodecs.map(i -> new HashMap<>(), ModStreamCodecs.UUID, ByteBufCodecs.map(i -> new HashMap<>(), ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT))), ClientboundTaskPacket::completedRequirements,
            ClientboundTaskPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
