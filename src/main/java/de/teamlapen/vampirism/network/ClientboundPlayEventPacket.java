package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundPlayEventPacket(int type, BlockPos pos, int stateId) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "play_event");
    public static final Codec<ClientboundPlayEventPacket> CODEC = RecordCodecBuilder.create(inst
            -> inst.group(
            Codec.INT.fieldOf("type").forGetter(ClientboundPlayEventPacket::type),
            BlockPos.CODEC.fieldOf("pos").forGetter(ClientboundPlayEventPacket::pos),
            Codec.INT.fieldOf("state_id").forGetter(ClientboundPlayEventPacket::stateId)
    ).apply(inst, ClientboundPlayEventPacket::new));

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
