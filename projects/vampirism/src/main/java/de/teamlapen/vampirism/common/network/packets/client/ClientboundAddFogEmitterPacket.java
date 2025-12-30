package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundAddFogEmitterPacket(LevelFog.Emitter emitter) implements CustomPacketPayload {
    public static final Type<ClientboundAddFogEmitterPacket> TYPE = new Type<>(VIdentifier.mod("add_fog_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddFogEmitterPacket> CODEC = StreamCodec.composite(
            LevelFog.Emitter.STREAM_CODEC, ClientboundAddFogEmitterPacket::emitter,
            ClientboundAddFogEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
