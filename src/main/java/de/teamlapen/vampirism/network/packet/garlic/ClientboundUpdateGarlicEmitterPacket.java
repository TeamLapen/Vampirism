package de.teamlapen.vampirism.network.packet.garlic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateGarlicEmitterPacket(List<GarlicLevel.Emitter> emitters) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateGarlicEmitterPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "update_garlic_emitter"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateGarlicEmitterPacket> CODEC = StreamCodec.composite(
            GarlicLevel.Emitter.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundUpdateGarlicEmitterPacket::emitters,
            ClientboundUpdateGarlicEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
