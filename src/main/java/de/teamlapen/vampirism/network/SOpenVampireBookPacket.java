package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * open a vampire book on client
 */
public class SOpenVampireBookPacket implements IMessage {
    public static void handle(final SOpenVampireBookPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleVampireBookPacket(msg));
        ctx.setPacketHandled(true);
    }

    static void encode(SOpenVampireBookPacket msg, PacketBuffer buf) {
        buf.writeItem(msg.itemStack);
    }

    static SOpenVampireBookPacket decode(PacketBuffer buf) {
        return new SOpenVampireBookPacket(buf.readItem());
    }

    public final ItemStack itemStack;

    public SOpenVampireBookPacket(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
