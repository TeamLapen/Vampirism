package de.teamlapen.vampirism.network.packet.fog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.fog.FogLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateFogEmitterPacket(List<FogLevel.Emitter> emitters, List<FogLevel.Emitter> emittersTmp) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateFogEmitterPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "update_fog_emitter"));
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
