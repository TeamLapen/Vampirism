package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TaskFinishedPacket implements IMessage {

    public final Task task;

    public TaskFinishedPacket(Task task) {
        this.task = task;
    }

    static void encode(TaskFinishedPacket msg, PacketBuffer buf) {
        buf.writeString(msg.task.getRegistryName().toString());
    }

    static TaskFinishedPacket decode(PacketBuffer buf) {
        return new TaskFinishedPacket(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString(32767))));
    }

    public static void handle(final TaskFinishedPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskFinishedPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }
}
