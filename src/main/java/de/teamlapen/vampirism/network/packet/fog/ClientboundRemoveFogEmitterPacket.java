package de.teamlapen.vampirism.network.packet.fog;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundRemoveFogEmitterPacket(BlockPos position, boolean tmp) implements CustomPacketPayload {
    public static final Type<ClientboundRemoveFogEmitterPacket> TYPE = new Type<>(VResourceLocation.mod("remove_fog_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRemoveFogEmitterPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundRemoveFogEmitterPacket::position,
            ByteBufCodecs.BOOL, ClientboundRemoveFogEmitterPacket::tmp,
            ClientboundRemoveFogEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
