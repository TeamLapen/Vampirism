package de.teamlapen.vampirism.network.packet.garlic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateGarlicEmitterPacket(List<GarlicLevel.Emitter> emitters) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "update_garlic_emitter");
    public static final Codec<ClientboundUpdateGarlicEmitterPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.list(GarlicLevel.Emitter.CODEC).fieldOf("emitters").forGetter(ClientboundUpdateGarlicEmitterPacket::emitters)
            ).apply(inst, ClientboundUpdateGarlicEmitterPacket::new)
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
