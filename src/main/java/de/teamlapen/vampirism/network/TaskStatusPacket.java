package de.teamlapen.vampirism.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.player.tasks.TaskInstance;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class TaskStatusPacket implements IMessage {

    public final int containerId;
    public final UUID taskBoardId;
    public final Set<ITaskInstance> available;
    public final Set<UUID> completableTasks;
    public final Map<UUID, Map<ResourceLocation, Integer>> completedRequirements;

    /**
     * @param completedRequirements all requirements of the visible tasks that are already completed
     * @param containerId           the id of the {@link de.teamlapen.vampirism.inventory.container.TaskBoardContainer}
     * @param taskBoardId           the task board id
     */
    public TaskStatusPacket(@Nonnull Set<ITaskInstance> available, Set<UUID> completableTasks ,@Nonnull Map<UUID, Map<ResourceLocation, Integer>> completedRequirements, int containerId, UUID taskBoardId) {
        this.available = available;
        this.completableTasks = completableTasks;
        this.completedRequirements = completedRequirements;
        this.containerId = containerId;
        this.taskBoardId = taskBoardId;
    }

    static void encode(@Nonnull TaskStatusPacket msg, @Nonnull PacketBuffer buf) {
        buf.writeString(msg.taskBoardId.toString());
        buf.writeVarInt(msg.containerId);
        buf.writeVarInt(msg.completableTasks.size());
        msg.completableTasks.forEach(buf::writeUniqueId);
        buf.writeVarInt(msg.available.size());
        msg.available.forEach(ins -> ins.encode(buf));
        buf.writeVarInt(msg.completedRequirements.size());
        msg.completedRequirements.forEach(((id, resourceLocations) -> {
            buf.writeVarInt(resourceLocations.size());
            buf.writeUniqueId(id);
            resourceLocations.forEach((loc, val) -> {
                buf.writeResourceLocation(loc);
                buf.writeVarInt(val);
            });
        }));
    }

    static TaskStatusPacket decode(@Nonnull PacketBuffer buf) {
        UUID taskBoardId = UUID.fromString(buf.readString());
        int containerId = buf.readVarInt();
        int completableTaskSize = buf.readVarInt();
        Set<UUID> completableTasks = Sets.newHashSetWithExpectedSize(completableTaskSize);
        for (int i = 0; i < completableTaskSize; i++) {
            completableTasks.add(buf.readUniqueId());
        }
        int taskSize = buf.readVarInt();
        Set<ITaskInstance> taskInstances = Sets.newHashSetWithExpectedSize(taskSize);
        for (int i = 0; i < taskSize; i++) {
            taskInstances.add(TaskInstance.decode(buf));
        }
        int completedReqSize = buf.readVarInt();
        Map<UUID, Map<ResourceLocation, Integer>> completedRequirements = Maps.newHashMapWithExpectedSize(completedReqSize);
        for (int i = 0; i < completedReqSize; ++i) {
            int l = buf.readVarInt();
            UUID id = buf.readUniqueId();
            Map<ResourceLocation, Integer> req = Maps.newHashMapWithExpectedSize(l);
            for (; l > 0; --l) {
                req.put(buf.readResourceLocation(), buf.readVarInt());
            }
            completedRequirements.put(id, req);
        }
        return new TaskStatusPacket(taskInstances, completableTasks, completedRequirements, containerId, taskBoardId);
    }

    public static void handle(final TaskStatusPacket msg, @Nonnull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskStatusPacket(msg));
        ctx.setPacketHandled(true);
    }
}
