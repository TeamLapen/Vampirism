package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public record ServerboundUpgradeMinionStatPacket(int entityId, int statId) implements IMessage.IServerBoundMessage {

    public static void handle(final @NotNull ServerboundUpgradeMinionStatPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if (player != null) {
                Entity entity = player.level().getEntity(msg.entityId);
                if (entity instanceof MinionEntity) {
                    if (((MinionEntity<?>) entity).getMinionData().map(d -> d.upgradeStat(msg.statId, (MinionEntity<?>) entity)).orElse(false)) {
                        HelperLib.sync((MinionEntity<?>) entity);
                    }
                }
            }
        })
        ;
        ctx.setPacketHandled(true);
    }

    static void encode(@NotNull ServerboundUpgradeMinionStatPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeVarInt(msg.statId);
    }

    static @NotNull ServerboundUpgradeMinionStatPacket decode(@NotNull FriendlyByteBuf buf) {
        return new ServerboundUpgradeMinionStatPacket(buf.readVarInt(), buf.readVarInt());
    }

}
