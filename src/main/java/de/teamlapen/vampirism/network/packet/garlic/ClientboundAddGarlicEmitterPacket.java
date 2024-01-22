package de.teamlapen.vampirism.network.packet.garlic;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundAddGarlicEmitterPacket(GarlicLevel.Emitter emitter) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "add_garlic_emitter");
    public static final Codec<ClientboundAddGarlicEmitterPacket> CODEC = GarlicLevel.Emitter.CODEC.xmap(ClientboundAddGarlicEmitterPacket::new, ClientboundAddGarlicEmitterPacket::emitter);
    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
