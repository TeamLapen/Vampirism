package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * open a vampire book on client
 */
public record SOpenVampireBookPacket(ItemStack itemStack) implements IMessage {
    public static void handle(final SOpenVampireBookPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleVampireBookPacket(msg));
        ctx.setPacketHandled(true);
    }

    static void encode(SOpenVampireBookPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.itemStack);
    }

    static SOpenVampireBookPacket decode(FriendlyByteBuf buf) {
        return new SOpenVampireBookPacket(buf.readItem());
    }

}
