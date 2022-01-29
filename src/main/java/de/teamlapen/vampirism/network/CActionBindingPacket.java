package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CActionBindingPacket(int actionBindingId, IAction<?> action) implements IMessage {

    static void encode(final CActionBindingPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.actionBindingId);
        buf.writeResourceLocation(RegUtil.id(msg.action));
    }

    static CActionBindingPacket decode(FriendlyByteBuf buf) {
        return new CActionBindingPacket(buf.readVarInt(), RegUtil.getAction(buf.readResourceLocation()));
    }

    public static void handle(final CActionBindingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(factionPlayerHandler -> factionPlayerHandler.setBoundAction(msg.actionBindingId, msg.action, false, false));
        });
        ctx.setPacketHandled(true);
    }

}
