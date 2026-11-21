package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Sent on every right-click on a lectern with a book. The server opens the heritage screen if the book is a heritage
 * book, even if protection mods or plugins block the normal interaction.
 */
public record ServerboundReadLecternPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundReadLecternPacket> TYPE = new Type<>(VIdentifier.mod("read_lectern"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundReadLecternPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundReadLecternPacket::pos,
            ServerboundReadLecternPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
