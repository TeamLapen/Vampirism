package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record ServerboundAppearancePacket(int entityId, String name, int... data) implements IMessage.IServerBoundMessage {

    static void encode(@NotNull ServerboundAppearancePacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeUtf(msg.name);
        buf.writeVarInt(msg.data.length);
        for (int value : msg.data) {
            buf.writeVarInt(value);
        }
    }

    static @NotNull ServerboundAppearancePacket decode(@NotNull FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        String newName = buf.readUtf(MinionData.MAX_NAME_LENGTH);
        int[] data = new int[buf.readVarInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = buf.readVarInt();
        }
        return new ServerboundAppearancePacket(entityId, newName, data);
    }

    public static void handle(final @NotNull ServerboundAppearancePacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            Entity entity = ctx.getSender().level().getEntity(msg.entityId);
            if (entity instanceof Player player) {
                VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.setSkinData(msg.data));
            } else if (entity instanceof MinionEntity<?> minion) {
                minion.getMinionData().ifPresent(minionData -> minionData.handleMinionAppearanceConfig(msg.name, msg.data));
                HelperLib.sync(minion);
            }
        });
        ctx.setPacketHandled(true);
    }
}
