package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;


public class CNameItemPacket implements IMessage {
    static void encode(CNameItemPacket msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
    }

    static CNameItemPacket decode(PacketBuffer buf) {
        return new CNameItemPacket(buf.readUtf(35));
    }

    static void handle(CNameItemPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            if (VampirismVampireSword.DO_NOT_NAME_STRING.equals(msg.name)) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof VampirismVampireSword) {
                    ((VampirismVampireSword) stack.getItem()).doNotName(stack);
                }
            } else if (!org.apache.commons.lang3.StringUtils.isBlank(msg.name)) {
                ItemStack stack = player.getMainHandItem();
                stack.setHoverName(new StringTextComponent(msg.name).withStyle(TextFormatting.AQUA));
            }
        });
        ctx.setPacketHandled(true);
    }

    private final String name;

    public CNameItemPacket(String name) {
        this.name = name;
    }
}
