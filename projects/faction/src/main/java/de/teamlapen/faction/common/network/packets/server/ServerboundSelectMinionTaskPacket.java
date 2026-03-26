package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public record ServerboundSelectMinionTaskPacket(int minionID, Identifier taskID) implements CustomPacketPayload {
    public static final Type<ServerboundSelectMinionTaskPacket> TYPE = new Type<>(FIdentifier.mod("select_minion_task"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSelectMinionTaskPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundSelectMinionTaskPacket::minionID,
            Identifier.STREAM_CODEC, ServerboundSelectMinionTaskPacket::taskID,
            ServerboundSelectMinionTaskPacket::new
    );
    public final static Identifier RECALL = FIdentifier.mod("recall");
    public final static Identifier RESPAWN = FIdentifier.mod("respawn");

    public static void printRecoveringMinions(@NotNull ServerPlayer player, @NotNull List<MutableComponent> recoveringMinions) {
        if (recoveringMinions.size() == 1) {
            player.displayClientMessage(Component.translatable("message.factionapi.minion.still_recovering", recoveringMinions.getFirst()), true);
        } else if (recoveringMinions.size() > 1) {
            player.displayClientMessage(Component.translatable("message.factionapi.minion.minions_recovering", recoveringMinions.size()), true);
        }
    }


    public ServerboundSelectMinionTaskPacket {
        assert minionID >= -1;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
