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
    public final Map<Task, List<ResourceLocation>> completedRequirements;
    public final int containerId;
    public final int entityId;

    public TaskStatusPacket(Set<Task> completableTasks, Collection<Task> visibleTasks, Set<Task> notAcceptedTasks, Map<Task, List<ResourceLocation>> completedRequirements, int containerId, int entityId) {
        this.completableTasks = completableTasks;
        this.visibleTasks = visibleTasks;
        this.notAcceptedTasks = notAcceptedTasks;
        this.completedRequirements = completedRequirements;
        this.containerId = containerId;
        this.entityId = entityId;
    }

    static void encode(@Nonnull TaskStatusPacket msg, @Nonnull PacketBuffer buf) {
        buf.writeVarInt(msg.entityId);
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
            resourceLocations.forEach(res -> buf.writeString(res.toString()));
        }));
    }

    static TaskStatusPacket decode(@Nonnull PacketBuffer buf) {
        int entityId = buf.readVarInt();
        int containerId = buf.readVarInt();
        int notAcceptedSize = buf.readVarInt();
        int taskSize = buf.readVarInt();
        int unlockedSize = buf.readVarInt();
        int completedReqSize = buf.readVarInt();
        Set<Task> possible = Sets.newHashSet();
        for (int i = 0; i < taskSize; i++) {
            possible.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        List<Task> unlocked = Lists.newArrayList();
        for (int i = 0; i < unlockedSize; i++) {
            unlocked.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        Set<Task> notAccepted = Sets.newHashSet();
        for (int i = 0; i < notAcceptedSize; i++) {
            notAccepted.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        Map<Task, List<ResourceLocation>> completedRequirements = Maps.newHashMapWithExpectedSize(completedReqSize);
        for(int i = 0; i < completedReqSize;++i) {
            int l = buf.readVarInt();
            Task task = ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString()));
            List<ResourceLocation> req = Lists.newArrayListWithCapacity(l);
            for(; l>0;--l) {
                req.add(new ResourceLocation(buf.readString()));
            }
            completedRequirements.put(task,req);
        }

        return new TaskStatusPacket(possible,unlocked, notAccepted,completedRequirements, containerId, entityId);
    }

    public static void handle(final TaskStatusPacket msg, @Nonnull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskStatusPacket(msg));
        ctx.setPacketHandled(true);
    }
}
