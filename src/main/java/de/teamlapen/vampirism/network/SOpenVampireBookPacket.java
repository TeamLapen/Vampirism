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
public record SOpenVampireBookPacket(String bookId) implements IMessage {
    public static void handle(final SOpenVampireBookPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleVampireBookPacket(VampireBookManager.getInstance().getBookById(msg.bookId)));
        ctx.setPacketHandled(true);
    }

    static void encode(SOpenVampireBookPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.bookId);
    }

    static SOpenVampireBookPacket decode(FriendlyByteBuf buf) {
        return new SOpenVampireBookPacket(buf.readUtf());
    }

}
