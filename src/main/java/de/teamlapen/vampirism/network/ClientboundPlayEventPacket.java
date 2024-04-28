package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundPlayEventPacket(int event, BlockPos pos, int stateId) implements CustomPacketPayload {

    public static final Type<ClientboundPlayEventPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "play_event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayEventPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundPlayEventPacket::event,
            BlockPos.STREAM_CODEC, ClientboundPlayEventPacket::pos,
            ByteBufCodecs.VAR_INT, ClientboundPlayEventPacket::stateId,
            ClientboundPlayEventPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
