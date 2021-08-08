package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;


public record UpgradeMinionStatPacket(int entityId, int statId) implements IMessage {

    public static void handle(final UpgradeMinionStatPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if (player != null) {
                Entity entity = player.level.getEntity(msg.entityId);
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

    static void encode(UpgradeMinionStatPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeVarInt(msg.statId);
    }

    static UpgradeMinionStatPacket decode(FriendlyByteBuf buf) {
        return new UpgradeMinionStatPacket(buf.readVarInt(), buf.readVarInt());
    }

}
