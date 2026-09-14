package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record ServerboundRequestHeritagePacket(@Nullable UUID heritageId, @Nullable String targetName) implements CustomPacketPayload {

    public static final Type<ServerboundRequestHeritagePacket> TYPE = new Type<>(VIdentifier.mod("request_heritage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestHeritagePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), packet -> Optional.ofNullable(packet.heritageId()),
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), packet -> Optional.ofNullable(packet.targetName()),
            (heritageId, targetName) -> new ServerboundRequestHeritagePacket(heritageId.orElse(null), targetName.orElse(null))
    );

    public ServerboundRequestHeritagePacket() {
        this(null, null);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
