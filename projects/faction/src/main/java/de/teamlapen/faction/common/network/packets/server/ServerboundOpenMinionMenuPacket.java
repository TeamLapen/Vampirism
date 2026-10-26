package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Requests the minion inventory to be (re)opened, e.g. when returning from a minion sub screen
 */
public record ServerboundOpenMinionMenuPacket(int entityId) implements CustomPacketPayload {

    public static final Type<ServerboundOpenMinionMenuPacket> TYPE = new Type<>(FIdentifier.mod("open_minion_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOpenMinionMenuPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundOpenMinionMenuPacket::entityId,
            ServerboundOpenMinionMenuPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
