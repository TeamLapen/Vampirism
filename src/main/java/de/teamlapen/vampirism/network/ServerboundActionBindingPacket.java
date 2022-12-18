package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record ServerboundActionBindingPacket(int actionBindingId, IAction<?> action) implements IMessage.IServerBoundMessage {

    static void encode(final @NotNull ServerboundActionBindingPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(msg.actionBindingId);
        buf.writeResourceLocation(RegUtil.id(msg.action));
    }

    static @NotNull ServerboundActionBindingPacket decode(@NotNull FriendlyByteBuf buf) {
        return new ServerboundActionBindingPacket(buf.readVarInt(), RegUtil.getAction(buf.readResourceLocation()));
    }

    public static void handle(final @NotNull ServerboundActionBindingPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(factionPlayerHandler -> factionPlayerHandler.setBoundAction(msg.actionBindingId, msg.action, false, false));
        });
        ctx.setPacketHandled(true);
    }

}
