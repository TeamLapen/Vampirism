package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.entity.player.tasks.TaskInstance;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ClientboundTaskStatusPacket(Set<? extends ITaskInstance> available,
                                          Set<UUID> completableTasks,
                                          Map<UUID, Map<ResourceLocation, Integer>> completedRequirements,
                                          int containerId, UUID taskBoardId) implements CustomPacketPayload {

    public static final Type<ClientboundTaskStatusPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "task_status"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTaskStatusPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(TaskInstance.CODEC).apply(ByteBufCodecs.collection(i -> new HashSet<>())).map(d -> d, l -> new HashSet<>((Set<TaskInstance>) l)), ClientboundTaskStatusPacket::available,
            ByteBufferCodecUtil.UUID.apply(ByteBufCodecs.collection(i -> new HashSet<>())), ClientboundTaskStatusPacket::completableTasks,
            ByteBufCodecs.map(l -> new HashMap<>(),ByteBufferCodecUtil.UUID, ByteBufCodecs.map(l -> new HashMap<>(), ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT)), ClientboundTaskStatusPacket::completedRequirements,
            ByteBufCodecs.INT, ClientboundTaskStatusPacket::containerId,
            ByteBufferCodecUtil.UUID, ClientboundTaskStatusPacket::taskBoardId,
            ClientboundTaskStatusPacket::new
    );

    /**
     * @param completedRequirements all requirements of the visible tasks that are already completed
     * @param containerId           the id of the {@link de.teamlapen.vampirism.inventory.TaskBoardMenu}
     * @param taskBoardId           the task board id
     */
    public ClientboundTaskStatusPacket(@NotNull Set<? extends ITaskInstance> available, Set<UUID> completableTasks, @NotNull Map<UUID, Map<ResourceLocation, Integer>> completedRequirements, int containerId, UUID taskBoardId) {
        this.available = available;
        this.completableTasks = completableTasks;
        this.completedRequirements = completedRequirements;
        this.containerId = containerId;
        this.taskBoardId = taskBoardId;
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
