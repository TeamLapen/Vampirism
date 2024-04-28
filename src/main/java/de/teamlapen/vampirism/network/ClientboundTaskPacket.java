package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.player.TaskManager;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import net.minecraft.network.FriendlyByteBuf;
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

    public static final Type<ClientboundTaskPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "task"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTaskPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundTaskPacket::containerId,
            ByteBufCodecs.map(i -> new HashMap<>(), ByteBufferCodecUtil.UUID, ByteBufCodecs.fromCodec(TaskManager.TaskWrapper.CODEC)), ClientboundTaskPacket::taskWrappers,
            ByteBufCodecs.map(i -> new HashMap<>(), ByteBufferCodecUtil.UUID, ByteBufferCodecUtil.UUID.apply(ByteBufCodecs.collection(s -> new HashSet<>()))), ClientboundTaskPacket::completableTasks,
            ByteBufCodecs.map(i -> new HashMap<>(), ByteBufferCodecUtil.UUID, ByteBufCodecs.map(i -> new HashMap<>(), ByteBufferCodecUtil.UUID, ByteBufCodecs.map(i -> new HashMap<>(), ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT))), ClientboundTaskPacket::completedRequirements,
            ClientboundTaskPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
