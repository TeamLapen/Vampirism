package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class TaskAcceptedPacket implements IMessage {

    public final Task task;
    public final int entityId;

    public TaskAcceptedPacket(Task task, int entityId) {
        this.task = task;
        this.entityId = entityId;
    }

    static void encode(TaskAcceptedPacket msg, PacketBuffer buf) {
        buf.writeString(Objects.requireNonNull(msg.task.getRegistryName()).toString());
        buf.writeVarInt(msg.entityId);
    }

    static TaskAcceptedPacket decode(PacketBuffer buf) {
        return new TaskAcceptedPacket(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString())), buf.readVarInt());
    }

    public static void handle(final TaskAcceptedPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskAcceptedPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }
}
