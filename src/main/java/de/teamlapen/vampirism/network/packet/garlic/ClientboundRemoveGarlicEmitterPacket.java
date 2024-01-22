package de.teamlapen.vampirism.network.packet.garlic;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundRemoveGarlicEmitterPacket(int emitterId) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "remove_garlic_emitter");
    public static final Codec<ClientboundRemoveGarlicEmitterPacket> CODEC = Codec.INT.xmap(ClientboundRemoveGarlicEmitterPacket::new, ClientboundRemoveGarlicEmitterPacket::emitterId);
    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
