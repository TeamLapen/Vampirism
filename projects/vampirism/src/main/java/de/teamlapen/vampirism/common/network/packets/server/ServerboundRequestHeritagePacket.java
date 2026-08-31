package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundRequestHeritagePacket() implements CustomPacketPayload {

    public static final Type<ServerboundRequestHeritagePacket> TYPE = new Type<>(VIdentifier.mod("request_heritage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestHeritagePacket> CODEC = StreamCodec.unit(new ServerboundRequestHeritagePacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
