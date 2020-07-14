package de.teamlapen.vampirism.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class TaskStatusPacket implements IMessage {

    public final Set<Task> completableTasks;
    public final Collection<Task> visibleTasks;
    public final Set<Task> notAcceptedTasks;
    public final Map<Task, Map<ResourceLocation, Integer>> completedRequirements;
    public final int containerId;
    public final UUID taskBoardId;

    /**
     * @param completableTasks      all visible task that are completable
     * @param visibleTasks          all visible tasks except completable or not accepted ones
     * @param notAcceptedTasks      all visible tasks that are not accepted
     * @param completedRequirements all requirements of the visible tasks that are already completed
     * @param containerId           the id of the {@link de.teamlapen.vampirism.inventory.container.TaskBoardContainer}
     * @param taskBoardId           the task board id
     */
    public TaskStatusPacket(Set<Task> completableTasks, Collection<Task> visibleTasks, Set<Task> notAcceptedTasks, Map<Task, Map<ResourceLocation, Integer>> completedRequirements, int containerId, UUID taskBoardId) {
        this.completableTasks = completableTasks;
        this.visibleTasks = visibleTasks;
        this.notAcceptedTasks = notAcceptedTasks;
        this.completedRequirements = completedRequirements;
        this.containerId = containerId;
        this.taskBoardId = taskBoardId;
    }

    static void encode(@Nonnull TaskStatusPacket msg, @Nonnull PacketBuffer buf) {
        buf.writeString(msg.taskBoardId.toString());
        buf.writeVarInt(msg.containerId);
        buf.writeVarInt(msg.notAcceptedTasks.size());
        buf.writeVarInt(msg.completableTasks.size());
        buf.writeVarInt(msg.visibleTasks.size());
        buf.writeVarInt(msg.completedRequirements.size());
        msg.completableTasks.forEach(res -> buf.writeString(Objects.requireNonNull(res.getRegistryName()).toString()));
        msg.visibleTasks.forEach(res -> buf.writeString(Objects.requireNonNull(res.getRegistryName()).toString()));
        msg.notAcceptedTasks.forEach(res -> buf.writeString(Objects.requireNonNull(res.getRegistryName()).toString()));
        msg.completedRequirements.forEach(((task, resourceLocations) -> {
            buf.writeVarInt(resourceLocations.size());
            buf.writeString(Objects.requireNonNull(task.getRegistryName()).toString());
            resourceLocations.forEach(((resourceLocation, integer) -> {
                buf.writeString(resourceLocation.toString());
                buf.writeVarInt(integer);
            }));
        }));
    }

    static TaskStatusPacket decode(@Nonnull PacketBuffer buf) {
        UUID taskBoardId = UUID.fromString(buf.readString());
        int containerId = buf.readVarInt();
        int notAcceptedSize = buf.readVarInt();
        int completableSize = buf.readVarInt();
        int visibleSize = buf.readVarInt();
        int completedReqSize = buf.readVarInt();
        Set<Task> completable = Sets.newHashSet();
        for (int i = 0; i < completableSize; i++) {
            completable.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        List<Task> visible = Lists.newArrayList();
        for (int i = 0; i < visibleSize; i++) {
            visible.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        Set<Task> notAccepted = Sets.newHashSet();
        for (int i = 0; i < notAcceptedSize; i++) {
            notAccepted.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        Map<Task, Map<ResourceLocation, Integer>> completedRequirements = Maps.newHashMapWithExpectedSize(completedReqSize);
        for(int i = 0; i < completedReqSize;++i) {
            int l = buf.readVarInt();
            Task task = ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString()));
            Map<ResourceLocation, Integer> req = Maps.newHashMapWithExpectedSize(l);
            for(; l>0;--l) {
                req.put(new ResourceLocation(buf.readString()), buf.readVarInt());
            }
            completedRequirements.put(task,req);
        }
        return new TaskStatusPacket(completable,visible, notAccepted,completedRequirements, containerId, taskBoardId);
    }

    public static void handle(final TaskStatusPacket msg, @Nonnull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskStatusPacket(msg));
        ctx.setPacketHandled(true);
    }
}
