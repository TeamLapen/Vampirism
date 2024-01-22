package de.teamlapen.vampirism.network.packet.fog;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.fog.FogLevel;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundAddFogEmitterPacket(FogLevel.Emitter emitter) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "add_fog_emitter");
    public static final Codec<ClientboundAddFogEmitterPacket> CODEC = FogLevel.Emitter.CODEC.xmap(ClientboundAddFogEmitterPacket::new, ClientboundAddFogEmitterPacket::emitter);
    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
