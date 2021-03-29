package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ActionBindingPacket implements IMessage {

    static void encode(final ActionBindingPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.actionBindingId);
        buf.writeString(msg.action.getRegistryName().toString());
    }

    static ActionBindingPacket decode(PacketBuffer buf) {
        return new ActionBindingPacket(buf.readVarInt(), ModRegistries.ACTIONS.getValue(new ResourceLocation(buf.readString(32767))));
    }

    public static void handle(final ActionBindingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            if(msg.actionBindingId==1){
                FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(fph->fph.setBoundAction1(msg.action,true));
            }
            else{
                FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(fph->fph.setBoundAction2(msg.action,true));
            }
        });
        ctx.setPacketHandled(true);
    }

    public final int actionBindingId;
    public final IAction action;

    public ActionBindingPacket(int actionBindingId, IAction action) {
        this.actionBindingId = actionBindingId;
        this.action = action;
    }
}