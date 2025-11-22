package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateGarlicEmitterPacket(List<LevelGarlic.Emitter> emitters) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateGarlicEmitterPacket> TYPE = new Type<>(VResourceLocation.mod("update_garlic_emitter"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateGarlicEmitterPacket> CODEC = StreamCodec.composite(
            LevelGarlic.Emitter.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundUpdateGarlicEmitterPacket::emitters,
            ClientboundUpdateGarlicEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
