package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Confirms running away from one's heritage with the own heritage book on the lectern at the given position.
 */
public record ServerboundRunAwayPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundRunAwayPacket> TYPE = new Type<>(VIdentifier.mod("run_away_from_heritage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRunAwayPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundRunAwayPacket::pos,
            ServerboundRunAwayPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
