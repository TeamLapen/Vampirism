package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CTaskActionPacket implements IMessage {

    static void encode(CTaskActionPacket msg, PacketBuffer buf) {
        buf.writeUUID(msg.task);
        buf.writeUUID(msg.entityId);
        buf.writeVarInt(msg.action.ordinal());
    }

    static CTaskActionPacket decode(PacketBuffer buf) {
        return new CTaskActionPacket(buf.readUUID(), buf.readUUID(), TaskContainer.TaskAction.values()[buf.readVarInt()]);
    }

    public static void handle(final CTaskActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskActionPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }
    public final UUID task;
    public final UUID entityId;
    public final TaskContainer.TaskAction action;

    public CTaskActionPacket(UUID task, UUID entityId, TaskContainer.TaskAction action) {
        this.task = task;
        this.entityId = entityId;
        this.action = action;
    }
}
