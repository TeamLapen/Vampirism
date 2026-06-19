package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;


public record ServerboundResetMinionStatPacket(int entityId) implements CustomPacketPayload {

    public static final Type<ServerboundResetMinionStatPacket> TYPE = new Type<>(FIdentifier.mod("reset_minion_stat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundResetMinionStatPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundResetMinionStatPacket::entityId,
            ServerboundResetMinionStatPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
