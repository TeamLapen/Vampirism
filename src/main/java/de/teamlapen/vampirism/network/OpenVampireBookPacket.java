package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * open a vampire book on client
 */
public record OpenVampireBookPacket(ItemStack itemStack) implements IMessage {
    public static void handle(final OpenVampireBookPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleVampireBookPacket(msg));
        ctx.setPacketHandled(true);
    }

    static void encode(OpenVampireBookPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.itemStack);
    }

    static OpenVampireBookPacket decode(FriendlyByteBuf buf) {
        return new OpenVampireBookPacket(buf.readItem());
    }

}
