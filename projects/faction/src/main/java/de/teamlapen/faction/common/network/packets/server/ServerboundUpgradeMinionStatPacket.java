package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;


public record ServerboundUpgradeMinionStatPacket(int entityId, Identifier stat) implements CustomPacketPayload {

    public static final Type<ServerboundUpgradeMinionStatPacket> TYPE = new Type<>(FIdentifier.mod("upgrade_minion_stat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUpgradeMinionStatPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundUpgradeMinionStatPacket::entityId,
            Identifier.STREAM_CODEC, ServerboundUpgradeMinionStatPacket::stat,
            ServerboundUpgradeMinionStatPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
