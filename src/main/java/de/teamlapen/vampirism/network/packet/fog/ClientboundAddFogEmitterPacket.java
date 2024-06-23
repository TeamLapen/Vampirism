package de.teamlapen.vampirism.network.packet.fog;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.world.fog.FogLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundAddFogEmitterPacket(FogLevel.Emitter emitter) implements CustomPacketPayload {
    public static final Type<ClientboundAddFogEmitterPacket> TYPE = new Type<>(VResourceLocation.mod("add_fog_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddFogEmitterPacket> CODEC = StreamCodec.composite(
            FogLevel.Emitter.STREAM_CODEC, ClientboundAddFogEmitterPacket::emitter,
            ClientboundAddFogEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
