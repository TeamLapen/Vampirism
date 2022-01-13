package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

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
        ctx.enqueueWork(() -> VampirismMod.proxy.handleActionBindingPacket(msg, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

    public final int actionBindingId;
    public final IAction action;

    public CActionBindingPacket(int actionBindingId, IAction action) {
        this.actionBindingId = actionBindingId;
        this.action = action;
    }
}
