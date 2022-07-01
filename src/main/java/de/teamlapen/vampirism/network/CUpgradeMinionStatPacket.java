package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class CUpgradeMinionStatPacket implements IMessage {


    public static void handle(final CUpgradeMinionStatPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity player = ctx.getSender();
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

    static void encode(CUpgradeMinionStatPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeVarInt(msg.statId);
    }

    static CUpgradeMinionStatPacket decode(PacketBuffer buf) {
        return new CUpgradeMinionStatPacket(buf.readVarInt(), buf.readVarInt());
    }

    public final int entityId;
    public final int statId;

    public CUpgradeMinionStatPacket(int entityId, int statId) {
        this.entityId = entityId;
        this.statId = statId;
    }
}
