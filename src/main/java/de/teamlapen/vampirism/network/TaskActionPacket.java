package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class TaskActionPacket implements IMessage {

    public final Task task;
    public final UUID entityId;
    public final TaskBoardContainer.TaskAction action;

    public TaskActionPacket(Task task, UUID entityId, TaskBoardContainer.TaskAction action) {
        this.task = task;
        this.entityId = entityId;
        this.action = action;
    }

    static void encode(TaskActionPacket msg, PacketBuffer buf) {
        buf.writeString(Objects.requireNonNull(msg.task.getRegistryName()).toString());
        buf.writeUniqueId(msg.entityId);
        buf.writeVarInt(msg.action.ordinal());
    }

    static TaskActionPacket decode(PacketBuffer buf) {
        return new TaskActionPacket(ModRegistries.TASKS.getValue(new ResourceLocation(buf.readString(32767))), buf.readUniqueId(), TaskBoardContainer.TaskAction.values()[buf.readVarInt()]);
    }

    public static void handle(final TaskActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskActionPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }
}
