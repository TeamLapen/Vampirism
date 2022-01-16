package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;

public class CActionBindingPacket implements IMessage {

    static void encode(final CActionBindingPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.actionBindingId);
        buf.writeUtf(msg.action.getRegistryName().toString());
    }

    static CActionBindingPacket decode(PacketBuffer buf) {
        return new CActionBindingPacket(buf.readVarInt(), ModRegistries.ACTIONS.getValue(new ResourceLocation(buf.readUtf(32767))));
    }

    public static void handle(final CActionBindingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            FactionPlayerHandler.getOpt(player).ifPresent(factionPlayerHandler -> factionPlayerHandler.setBoundAction(msg.actionBindingId, msg.action, false, false));
        });
        ctx.setPacketHandled(true);
    }

    public final int actionBindingId;
    public final IAction action;

    public CActionBindingPacket(int actionBindingId, IAction action) {
        this.actionBindingId = actionBindingId;
        this.action = action;
    }
}
