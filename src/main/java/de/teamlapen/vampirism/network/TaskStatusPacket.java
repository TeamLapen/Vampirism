package de.teamlapen.vampirism.network;

import com.google.common.collect.Lists;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class TaskStatusPacket implements IMessage {

    public final List<ResourceLocation> possibleTasks;
    public final int containerId;

    public TaskStatusPacket(List<ResourceLocation> possibleTasks, int containerId) {
        this.possibleTasks = possibleTasks;
        this.containerId = containerId;
    }

    static void encode(TaskStatusPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.containerId);
        buf.writeInt(msg.possibleTasks.size());
        msg.possibleTasks.forEach(res -> buf.writeString(res.toString()));
    }

    static TaskStatusPacket decode(PacketBuffer buf) {
        int containerId = buf.readInt();
        List<ResourceLocation> res = Lists.newArrayList();
        for (int i = 0; i < buf.readInt(); i++) {
            res.add(new ResourceLocation(buf.readString()));
        }
        return new TaskStatusPacket(res, containerId);
    }

    public static void handle(final TaskStatusPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskStatusPacket(msg));
        ctx.setPacketHandled(true);
    }
}
