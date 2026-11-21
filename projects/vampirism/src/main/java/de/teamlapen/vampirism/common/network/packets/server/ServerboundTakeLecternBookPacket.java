package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Takes the heritage book from the lectern at the given position, like the "Take Book" button of the vanilla lectern.
 */
public record ServerboundTakeLecternBookPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundTakeLecternBookPacket> TYPE = new Type<>(VIdentifier.mod("take_lectern_book"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTakeLecternBookPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundTakeLecternBookPacket::pos,
            ServerboundTakeLecternBookPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
