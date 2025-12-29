package de.teamlapen.factions.common.network.packets.client;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.factions.tasks.TaskManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ClientboundTaskPacket(int containerId,
                                    Map<UUID, TaskManager.TaskWrapper> taskWrappers,
                                    Map<UUID, Set<UUID>> completableTasks,
                                    Map<UUID, Map<UUID, Map<Identifier, Integer>>> completedRequirements) implements CustomPacketPayload {

    public static final Type<ClientboundTaskPacket> TYPE = new Type<>(FResourceLocation.mod("task"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTaskPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundTaskPacket::containerId,
            ByteBufCodecs.map(i -> new HashMap<>(), UUIDUtil.STREAM_CODEC, ByteBufCodecs.fromCodec(TaskManager.TaskWrapper.CODEC)), ClientboundTaskPacket::taskWrappers,
            ByteBufCodecs.map(i -> new HashMap<>(), UUIDUtil.STREAM_CODEC, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.collection(s -> new HashSet<>()))), ClientboundTaskPacket::completableTasks,
            ByteBufCodecs.map(i -> new HashMap<>(), UUIDUtil.STREAM_CODEC, ByteBufCodecs.map(i -> new HashMap<>(), UUIDUtil.STREAM_CODEC, ByteBufCodecs.map(i -> new HashMap<>(), Identifier.STREAM_CODEC, ByteBufCodecs.INT))), ClientboundTaskPacket::completedRequirements,
            ClientboundTaskPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
