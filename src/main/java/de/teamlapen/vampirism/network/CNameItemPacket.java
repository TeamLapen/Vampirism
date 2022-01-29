package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;


public class CNameItemPacket implements IMessage {
    static void encode(CNameItemPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name);
    }

    static CNameItemPacket decode(FriendlyByteBuf buf) {
        return new CNameItemPacket(buf.readUtf(35));
    }

    static void handle(CNameItemPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            if (VampirismVampireSword.DO_NOT_NAME_STRING.equals(msg.name)) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof VampirismVampireSword) {
                    ((VampirismVampireSword) stack.getItem()).doNotName(stack);
                }
            } else if (!org.apache.commons.lang3.StringUtils.isBlank(msg.name)) {
                ItemStack stack = player.getMainHandItem();
                stack.setHoverName(Component.literal(msg.name).withStyle(ChatFormatting.AQUA));
            }
        });
        ctx.setPacketHandled(true);
    }

    private final String name;

    public CNameItemPacket(String name) {
        this.name = name;
    }
}
