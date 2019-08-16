package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * open a vampire book on client
 */
public class OpenVampireBookPacket implements IMessage {
    private final ItemStack itemStack;

    public OpenVampireBookPacket(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static void handle(final OpenVampireBookPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();

        ctx.enqueueWork(() -> {
            Minecraft.getInstance().displayGuiScreen(new ReadBookScreen(new ReadBookScreen.WrittenBookInfo(msg.itemStack)));
        });
    }

    static void encode(OpenVampireBookPacket msg, PacketBuffer buf) {
        buf.writeItemStack(msg.itemStack);
    }

    static OpenVampireBookPacket decode(PacketBuffer buf) {
        return new OpenVampireBookPacket(buf.readItemStack());
    }
}
