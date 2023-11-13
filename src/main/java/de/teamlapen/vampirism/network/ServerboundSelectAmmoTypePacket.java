package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.client.gui.screens.SelectAmmoScreen;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public record ServerboundSelectAmmoTypePacket(boolean hasRestriction, @Nullable ResourceLocation ammoId) implements IMessage.IServerBoundMessage {

    public ServerboundSelectAmmoTypePacket(boolean hasRestriction, @Nullable ResourceLocation ammoId) {
        this.hasRestriction = hasRestriction;
        this.ammoId = ammoId;
        if (hasRestriction) {
            Objects.requireNonNull(ammoId);
        }
    }
    static void encode(@NotNull ServerboundSelectAmmoTypePacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(msg.hasRestriction);
        if (msg.hasRestriction) {
            //noinspection DataFlowIssue
            buf.writeResourceLocation(msg.ammoId);
        }
    }

    static ServerboundSelectAmmoTypePacket decode(@NotNull FriendlyByteBuf buf) {
        boolean hasRestrictions = buf.readBoolean();
        return new ServerboundSelectAmmoTypePacket(hasRestrictions, hasRestrictions ? buf.readResourceLocation() : null);
    }

    static void handle(@NotNull ServerboundSelectAmmoTypePacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IVampirismCrossbow crossbow && crossbow.canSelectAmmunition(stack)) {
                crossbow.setAmmunition(stack, msg.ammoId);
            }
        });
        ctx.setPacketHandled(true);
    }

    public static ServerboundSelectAmmoTypePacket of(SelectAmmoScreen.AmmoType ammoType) {
        if (ammoType.renderStack == null)  {
            return new ServerboundSelectAmmoTypePacket(false, null);
        } else {
            return new ServerboundSelectAmmoTypePacket(true, RegUtil.id(ammoType.renderStack.getItem()));
        }
    }
}
