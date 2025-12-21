package de.teamlapen.factions.common.network.packets.client;

import de.teamlapen.factions.api.factions.tasks.ITaskInstance;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.factions.tasks.TaskInstance;
import de.teamlapen.factions.common.world.inventory.TaskBoardMenu;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ClientboundTaskStatusPacket(Set<ITaskInstance> available,
                                          Set<UUID> completableTasks,
                                          Map<UUID, Map<Identifier, Integer>> completedRequirements,
                                          int containerId, UUID taskBoardId) implements CustomPacketPayload {

    public static final Type<ClientboundTaskStatusPacket> TYPE = new Type<>(FResourceLocation.mod("task_status"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTaskStatusPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(TaskInstance.CODEC).apply(ByteBufCodecs.collection(i -> new HashSet<>())), ClientboundTaskStatusPacket::available,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.collection(i -> new HashSet<>())), ClientboundTaskStatusPacket::completableTasks,
            ByteBufCodecs.map(l -> new HashMap<>(), UUIDUtil.STREAM_CODEC, ByteBufCodecs.map(l -> new HashMap<>(), Identifier.STREAM_CODEC, ByteBufCodecs.INT)), ClientboundTaskStatusPacket::completedRequirements,
            ByteBufCodecs.INT, ClientboundTaskStatusPacket::containerId,
            UUIDUtil.STREAM_CODEC, ClientboundTaskStatusPacket::taskBoardId,
            ClientboundTaskStatusPacket::new
    );

    /**
     * @param completedRequirements all requirements of the visible tasks that are already completed
     * @param containerId           the id of the {@link TaskBoardMenu}
     * @param taskBoardId           the task board id
     */
    public ClientboundTaskStatusPacket(@NotNull Set<ITaskInstance> available, Set<UUID> completableTasks, @NotNull Map<UUID, Map<Identifier, Integer>> completedRequirements, int containerId, UUID taskBoardId) {
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
