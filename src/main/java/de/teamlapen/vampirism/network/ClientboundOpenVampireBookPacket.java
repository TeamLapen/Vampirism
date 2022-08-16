package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * open a vampire book on client
 */
public record ClientboundOpenVampireBookPacket(String bookId) implements IMessage {
    public static void handle(final ClientboundOpenVampireBookPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleVampireBookPacket(VampireBookManager.getInstance().getBookById(msg.bookId)));
        ctx.setPacketHandled(true);
    }

    static void encode(ClientboundOpenVampireBookPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.bookId);
    }

    static ClientboundOpenVampireBookPacket decode(FriendlyByteBuf buf) {
        return new ClientboundOpenVampireBookPacket(buf.readUtf());
    }

}
