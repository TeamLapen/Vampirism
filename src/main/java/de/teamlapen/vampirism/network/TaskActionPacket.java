package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record TaskActionPacket(UUID task, UUID entityId,
                               TaskContainer.TaskAction action) implements IMessage {

    static void encode(TaskActionPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.task);
        buf.writeUUID(msg.entityId);
        buf.writeVarInt(msg.action.ordinal());
    }

    static TaskActionPacket decode(FriendlyByteBuf buf) {
        return new TaskActionPacket(buf.readUUID(), buf.readUUID(), TaskContainer.TaskAction.values()[buf.readVarInt()]);
    }

    public static void handle(final TaskActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskActionPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

}
