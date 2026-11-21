package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Opens the heritage screen for the heritage book on the lectern at the given position, or allows taking the book if
 * that screen is already open.
 */
public record ClientboundOpenLecternHeritagePacket(BlockPos pos, boolean canTakeBook) implements CustomPacketPayload {

    public static final Type<ClientboundOpenLecternHeritagePacket> TYPE = new Type<>(VIdentifier.mod("open_lectern_heritage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenLecternHeritagePacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundOpenLecternHeritagePacket::pos,
            ByteBufCodecs.BOOL, ClientboundOpenLecternHeritagePacket::canTakeBook,
            ClientboundOpenLecternHeritagePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
