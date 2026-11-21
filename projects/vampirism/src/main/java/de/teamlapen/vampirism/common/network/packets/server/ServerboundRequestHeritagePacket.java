package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Requests heritage data for the heritage screen.
 *
 * @param heritageId  a specific heritage of the player, or null for the current one
 * @param targetName  the name of a heritage book held by the player
 * @param lecternPos  a lectern holding a heritage book. Takes precedence over targetName
 */
public record ServerboundRequestHeritagePacket(@Nullable UUID heritageId, @Nullable String targetName, @Nullable BlockPos lecternPos) implements CustomPacketPayload {

    public static final Type<ServerboundRequestHeritagePacket> TYPE = new Type<>(VIdentifier.mod("request_heritage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestHeritagePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), packet -> Optional.ofNullable(packet.heritageId()),
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), packet -> Optional.ofNullable(packet.targetName()),
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), packet -> Optional.ofNullable(packet.lecternPos()),
            (heritageId, targetName, lecternPos) -> new ServerboundRequestHeritagePacket(heritageId.orElse(null), targetName.orElse(null), lecternPos.orElse(null))
    );

    public ServerboundRequestHeritagePacket() {
        this(null, null, null);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
