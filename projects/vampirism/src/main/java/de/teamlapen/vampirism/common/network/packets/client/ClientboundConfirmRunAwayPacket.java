package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Asks the player whether they want to run away from their heritage with the own heritage book on the lectern at the
 * given position.
 */
public record ClientboundConfirmRunAwayPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ClientboundConfirmRunAwayPacket> TYPE = new Type<>(VIdentifier.mod("confirm_run_away"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundConfirmRunAwayPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundConfirmRunAwayPacket::pos,
            ClientboundConfirmRunAwayPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
