package de.teamlapen.vampirism.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class TaskStatusPacket implements IMessage {

    public final Set<Task> possibleTasks;
    public final Set<Task> completedTasks;
    public final Collection<Task> unlockedTask;
    public final int containerId;

    public TaskStatusPacket(Set<Task> possibleTasks, Set<Task> completedTasks, Collection<Task> unlockedTask, int containerId) {
        this.possibleTasks = possibleTasks;
        this.completedTasks = completedTasks;
        this.unlockedTask = unlockedTask;
        this.containerId = containerId;
    }

    static void encode(@Nonnull TaskStatusPacket msg, @Nonnull PacketBuffer buf) {
        buf.writeVarInt(msg.containerId);
        buf.writeVarInt(msg.possibleTasks.size());
        buf.writeVarInt(msg.completedTasks.size());
        buf.writeVarInt(msg.unlockedTask.size());
        msg.possibleTasks.forEach(res -> buf.writeString(res.getRegistryName().toString()));
        msg.completedTasks.forEach(res -> buf.writeString(res.getRegistryName().toString()));
        msg.unlockedTask.forEach(res -> buf.writeString(res.getRegistryName().toString()));
    }

    static TaskStatusPacket decode(@Nonnull PacketBuffer buf) {
        int containerId = buf.readVarInt();
        int taskSize = buf.readVarInt();
        int completeSize = buf.readVarInt();
        int unlockedSize = buf.readVarInt();
        Set<Task> possible = Sets.newHashSet();
        for (int i = 0; i < taskSize; i++) {
            possible.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        Set<Task> completed = Sets.newHashSet();
        for (int i = 0; i < completeSize; i++) {
            completed.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        List<Task> unlocked = Lists.newArrayList();
        for (int i = 0; i < unlockedSize; i++) {
            unlocked.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }

        return new TaskStatusPacket(possible, completed,unlocked, containerId);
    }

    public static void handle(final TaskStatusPacket msg, @Nonnull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskStatusPacket(msg));
        ctx.setPacketHandled(true);
    }
}
