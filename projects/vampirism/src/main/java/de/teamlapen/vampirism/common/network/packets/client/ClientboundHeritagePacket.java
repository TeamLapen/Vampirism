package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ClientboundHeritagePacket(@Nullable String founderName, List<Member> members) implements CustomPacketPayload {

    public static final Type<ClientboundHeritagePacket> TYPE = new Type<>(VIdentifier.mod("heritage"));
    private static final StreamCodec<RegistryFriendlyByteBuf, Member> MEMBER_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Member::playerId,
            ByteBufCodecs.STRING_UTF8, Member::playerName,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), member -> Optional.ofNullable(member.parentPlayerId()),
            (playerId, playerName, parentPlayerId) -> new Member(playerId, playerName, parentPlayerId.orElse(null))
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundHeritagePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), packet -> Optional.ofNullable(packet.founderName()),
            MEMBER_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), ClientboundHeritagePacket::members,
            (founderName, members) -> new ClientboundHeritagePacket(founderName.orElse(null), members)
    );

    public ClientboundHeritagePacket {
        members = List.copyOf(members);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record Member(UUID playerId, String playerName, @Nullable UUID parentPlayerId) {
    }
}
