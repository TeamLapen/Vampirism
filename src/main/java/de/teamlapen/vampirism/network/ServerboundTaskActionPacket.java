package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.TaskManager;
import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

public record ServerboundTaskActionPacket(UUID task, UUID entityId,
                                          TaskMenu.TaskAction action) implements IMessage {

    static void encode(@NotNull ServerboundTaskActionPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeUUID(msg.task);
        buf.writeUUID(msg.entityId);
        buf.writeVarInt(msg.action.ordinal());
    }

    static @NotNull ServerboundTaskActionPacket decode(@NotNull FriendlyByteBuf buf) {
        return new ServerboundTaskActionPacket(buf.readUUID(), buf.readUUID(), TaskMenu.TaskAction.values()[buf.readVarInt()]);
    }

    public static void handle(final @NotNull ServerboundTaskActionPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> FactionPlayerHandler.getCurrentFactionPlayer(ctx.getSender()).map(IFactionPlayer::getTaskManager).ifPresent(m -> ((TaskManager) m).handleTaskActionMessage(msg)));
        ctx.setPacketHandled(true);
    }

}
