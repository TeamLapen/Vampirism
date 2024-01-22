package de.teamlapen.vampirism.network.packet.fog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.fog.FogLevel;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateFogEmitterPacket(List<FogLevel.Emitter> emitters, List<FogLevel.Emitter> emittersTmp) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "update_fog_emitter");
    public static final Codec<ClientboundUpdateFogEmitterPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.list(FogLevel.Emitter.CODEC).fieldOf("emitters").forGetter(ClientboundUpdateFogEmitterPacket::emitters),
                    Codec.list(FogLevel.Emitter.CODEC).fieldOf("emitters").forGetter(ClientboundUpdateFogEmitterPacket::emittersTmp)
            ).apply(inst, ClientboundUpdateFogEmitterPacket::new)
    );
    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
