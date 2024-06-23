package de.teamlapen.vampirism.network.packet.garlic;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundRemoveGarlicEmitterPacket(int emitterId) implements CustomPacketPayload {
    public static final Type<ClientboundRemoveGarlicEmitterPacket> TYPE = new Type<>(VResourceLocation.mod("remove_garlic_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRemoveGarlicEmitterPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundRemoveGarlicEmitterPacket::emitterId,
            ClientboundRemoveGarlicEmitterPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
