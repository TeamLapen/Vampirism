package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TaskFinishedPacket {

    public final Task task;
    public final UUID playerId;

    public TaskFinishedPacket(Task task, UUID playerId) {
        this.task = task;
        this.playerId = playerId;
    }

    static void encode(TaskFinishedPacket msg, PacketBuffer buf) {
        buf.writeString(msg.task.getRegistryName().toString());
        buf.writeUniqueId(msg.playerId);
    }

    static TaskFinishedPacket decode(PacketBuffer buf) {
        return new TaskFinishedPacket(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())), buf.readUniqueId());
    }

    public static void handle(final TaskFinishedPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskFinishedPacket(msg));
        ctx.setPacketHandled(true);
    }
}
