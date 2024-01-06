package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.entity.player.tasks.TaskInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ClientboundTaskStatusPacket(Set<? extends ITaskInstance> available,
                                          Set<UUID> completableTasks,
                                          Map<UUID, Map<ResourceLocation, Integer>> completedRequirements,
                                          int containerId, UUID taskBoardId) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "task_status");
    public static final Codec<ClientboundTaskStatusPacket> CODEC = RecordCodecBuilder.create(inst->
            inst.group(
                    Codec.list(TaskInstance.CODEC).xmap(c -> (Set<TaskInstance>) new HashSet<>(c), ArrayList::new).fieldOf("available").forGetter(s -> (Set<TaskInstance>) s.available),
                    Codec.list(Codec.STRING.xmap(UUID::fromString, UUID::toString)).xmap(x -> (Set<UUID>)new HashSet<>(x), ArrayList::new).fieldOf("completableTasks").forGetter(ClientboundTaskStatusPacket::completableTasks),
                    Codec.unboundedMap(Codec.STRING.xmap(UUID::fromString, UUID::toString), Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)).fieldOf("completedRequirements").forGetter(ClientboundTaskStatusPacket::completedRequirements),
                    Codec.INT.fieldOf("containerId").forGetter(ClientboundTaskStatusPacket::containerId),
                    Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("taskBoardId").forGetter(ClientboundTaskStatusPacket::taskBoardId)
            ).apply(inst, ClientboundTaskStatusPacket::new)
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
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
