package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

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
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> FactionPlayerHandler.getCurrentFactionPlayer(player).map(IFactionPlayer::getTaskManager).ifPresent(m -> ((TaskManager)m).handleTaskActionMessage(msg)));
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
