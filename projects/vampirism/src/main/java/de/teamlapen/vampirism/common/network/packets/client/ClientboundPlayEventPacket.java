package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ClientboundPlayEventPacket(Event event, Optional<BlockPos> pos) implements CustomPacketPayload {

    public static final Type<ClientboundPlayEventPacket> TYPE = new Type<>(VIdentifier.mod("play_event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayEventPacket> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Event.class), ClientboundPlayEventPacket::event,
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), ClientboundPlayEventPacket::pos,
            ClientboundPlayEventPacket::new
    );

    public ClientboundPlayEventPacket(Event event, BlockPos pos) {
        this(event, Optional.of(pos));
    }

    public ClientboundPlayEventPacket(Event event) {
        this(event, Optional.empty());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Event {
        STOP_MUSIC,
    }
}
