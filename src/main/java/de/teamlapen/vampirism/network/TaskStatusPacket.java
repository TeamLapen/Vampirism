package de.teamlapen.vampirism.network;

import com.google.common.collect.Sets;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Supplier;

public class TaskStatusPacket implements IMessage {

    public final Set<Task> possibleTasks;
    public final Set<Task> completedTasks;
    public final int containerId;

    public TaskStatusPacket(Set<Task> possibleTasks, Set<Task> completedTasks, int containerId) {
        this.possibleTasks = possibleTasks;
        this.completedTasks = completedTasks;
        this.containerId = containerId;
    }

    static void encode(@Nonnull TaskStatusPacket msg, @Nonnull PacketBuffer buf) {
        buf.writeVarInt(msg.containerId);
        buf.writeVarInt(msg.possibleTasks.size());
        buf.writeVarInt(msg.completedTasks.size());
        msg.possibleTasks.forEach(res -> buf.writeString(res.getRegistryName().toString()));
        msg.completedTasks.forEach(res -> buf.writeString(res.getRegistryName().toString()));
    }

    static TaskStatusPacket decode(@Nonnull PacketBuffer buf) {
        int containerId = buf.readVarInt();
        int taskSize = buf.readVarInt();
        int completeSize = buf.readVarInt();
        Set<Task> res = Sets.newHashSet();
        for (int i = 0; i < taskSize; i++) {
            res.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }
        Set<Task> res2 = Sets.newHashSet();
        for (int i = 0; i < completeSize; i++) {
            res2.add(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())));
        }

        return new TaskStatusPacket(res, res2, containerId);
    }

    public static void handle(final TaskStatusPacket msg, @Nonnull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskStatusPacket(msg));
        ctx.setPacketHandled(true);
    }
}
