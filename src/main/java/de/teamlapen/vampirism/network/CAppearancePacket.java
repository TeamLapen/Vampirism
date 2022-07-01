package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;

public class CAppearancePacket implements IMessage {

    static void encode(CAppearancePacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeUtf(msg.name);
        buf.writeVarInt(msg.data.length);
        for (int value : msg.data) {
            buf.writeVarInt(value);
        }
    }

    static CAppearancePacket decode(PacketBuffer buf) {
        int entityId = buf.readVarInt();
        String newName = buf.readUtf(MinionData.MAX_NAME_LENGTH);
        int[] data = new int[buf.readVarInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = buf.readVarInt();
        }
        return new CAppearancePacket(entityId, newName, data);
    }

    public static void handle(final CAppearancePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Entity entity = player.level.getEntity(msg.entityId);
            if (entity instanceof PlayerEntity) {
                VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.setSkinData(msg.data));
            } else if (entity instanceof MinionEntity<?>) {
                ((MinionEntity<?>) entity).getMinionData().ifPresent(minionData -> minionData.handleMinionAppearanceConfig(msg.name, msg.data));
                HelperLib.sync((MinionEntity<?>) entity);
            }
        });
        ctx.setPacketHandled(true);
    }

    public final int entityId;
    public final String name;
    public final int[] data;

    public CAppearancePacket(int entityId, String newName, int... data) {
        this.entityId = entityId;
        this.name = newName;
        this.data = data;
    }
}
