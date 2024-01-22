package de.teamlapen.vampirism.network.packet.fog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundRemoveFogEmitterPacket(BlockPos position, boolean tmp) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "remove_fog_emitter");
    public static final Codec<ClientboundRemoveFogEmitterPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    BlockPos.CODEC.fieldOf("position").forGetter(ClientboundRemoveFogEmitterPacket::position),
                    Codec.BOOL.fieldOf("tmp").forGetter(ClientboundRemoveFogEmitterPacket::tmp)
            ).apply(inst, ClientboundRemoveFogEmitterPacket::new)
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
