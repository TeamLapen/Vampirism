package de.teamlapen.vampirism.network.packet.fog;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.world.fog.FogLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateFogEmitterPacket(List<FogLevel.Emitter> emitters, List<FogLevel.Emitter> emittersTmp) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateFogEmitterPacket> TYPE = new Type<>(VResourceLocation.mod("update_fog_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateFogEmitterPacket> CODEC = StreamCodec.composite(
            FogLevel.Emitter.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundUpdateFogEmitterPacket::emitters,
            FogLevel.Emitter.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundUpdateFogEmitterPacket::emittersTmp,
            ClientboundUpdateFogEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
