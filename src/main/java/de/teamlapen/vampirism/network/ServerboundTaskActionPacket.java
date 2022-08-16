package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record ServerboundTaskActionPacket(UUID task, UUID entityId,
                                          TaskContainer.TaskAction action) implements IMessage {

    static void encode(ServerboundTaskActionPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.task);
        buf.writeUUID(msg.entityId);
        buf.writeVarInt(msg.action.ordinal());
    }

    static ServerboundTaskActionPacket decode(FriendlyByteBuf buf) {
        return new ServerboundTaskActionPacket(buf.readUUID(), buf.readUUID(), TaskContainer.TaskAction.values()[buf.readVarInt()]);
    }

    public static void handle(final ServerboundTaskActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> FactionPlayerHandler.getCurrentFactionPlayer(ctx.getSender()).map(IFactionPlayer::getTaskManager).ifPresent(m -> ((TaskManager)m).handleTaskActionMessage(msg)));
        ctx.setPacketHandled(true);
    }

}
