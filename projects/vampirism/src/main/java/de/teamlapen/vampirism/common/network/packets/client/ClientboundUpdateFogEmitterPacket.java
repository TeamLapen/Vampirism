package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateFogEmitterPacket(List<LevelFog.Emitter> emitters, List<LevelFog.Emitter> emittersTmp) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateFogEmitterPacket> TYPE = new Type<>(VIdentifier.mod("update_fog_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateFogEmitterPacket> CODEC = StreamCodec.composite(
            LevelFog.Emitter.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundUpdateFogEmitterPacket::emitters,
            LevelFog.Emitter.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundUpdateFogEmitterPacket::emittersTmp,
            ClientboundUpdateFogEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
