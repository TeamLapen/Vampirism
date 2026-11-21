package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @param notice shown instead of the tree if there are no members, e.g. because the heritage book was not researched yet
 */
public record ClientboundHeritagePacket(@Nullable String founderName, List<StaticMember> staticMembers, List<Member> members, @Nullable UUID heritageId, List<UUID> heritageIds, boolean currentHeritage, @Nullable Component notice) implements CustomPacketPayload {

    public static final Type<ClientboundHeritagePacket> TYPE = new Type<>(VIdentifier.mod("heritage"));
    private static final StreamCodec<RegistryFriendlyByteBuf, StaticMember> STATIC_MEMBER_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StaticMember::id,
            ByteBufCodecs.STRING_UTF8, StaticMember::name,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), member -> Optional.ofNullable(member.parentId()),
            ByteBufCodecs.BOOL, StaticMember::lostHistory,
            (id, name, parentId, lostHistory) -> new StaticMember(id, name, parentId.orElse(null), lostHistory)
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, Member> MEMBER_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Member::playerId,
            ByteBufCodecs.STRING_UTF8, Member::playerName,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), member -> Optional.ofNullable(member.parentPlayerId()),
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), member -> Optional.ofNullable(member.parentNpcId()),
            (playerId, playerName, parentPlayerId, parentNpcId) -> new Member(playerId, playerName, parentPlayerId.orElse(null), parentNpcId.orElse(null))
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundHeritagePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), packet -> Optional.ofNullable(packet.founderName()),
            STATIC_MEMBER_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), ClientboundHeritagePacket::staticMembers,
            MEMBER_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), ClientboundHeritagePacket::members,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), packet -> Optional.ofNullable(packet.heritageId()),
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), ClientboundHeritagePacket::heritageIds,
            ByteBufCodecs.BOOL, ClientboundHeritagePacket::currentHeritage,
            ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), packet -> Optional.ofNullable(packet.notice()),
            (founderName, staticMembers, members, heritageId, heritageIds, currentHeritage, notice) -> new ClientboundHeritagePacket(founderName.orElse(null), staticMembers, members, heritageId.orElse(null), heritageIds, currentHeritage, notice.orElse(null))
    );

    public ClientboundHeritagePacket(@Nullable String founderName, List<StaticMember> staticMembers, List<Member> members, @Nullable UUID heritageId, List<UUID> heritageIds, boolean currentHeritage) {
        this(founderName, staticMembers, members, heritageId, heritageIds, currentHeritage, null);
    }

    public ClientboundHeritagePacket {
        staticMembers = List.copyOf(staticMembers);
        members = List.copyOf(members);
        heritageIds = List.copyOf(heritageIds);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record StaticMember(String id, String name, @Nullable String parentId, boolean lostHistory) {
    }

    public record Member(UUID playerId, String playerName, @Nullable UUID parentPlayerId, @Nullable String parentNpcId) {
    }
}
