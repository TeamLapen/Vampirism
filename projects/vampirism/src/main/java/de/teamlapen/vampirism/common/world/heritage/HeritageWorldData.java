package de.teamlapen.vampirism.common.world.heritage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Server-wide heritage store. It keeps player state and heritage nodes available when players are offline.
 */
public final class HeritageWorldData extends SavedData implements ValueIOSerializable {
    public static final SavedDataType<HeritageWorldData> TYPE = new SavedDataType<>(VIdentifier.mod("heritage"), HeritageWorldData::new, HeritageWorldData::makeCodec);
    private final MinecraftServer server;
    private final Map<UUID, HeritageRecord> records = new HashMap<>();
    private final Map<UUID, PlayerHeritage> playerHeritages = new HashMap<>();

    public HeritageWorldData(ServerLevel level) {
        this.server = level.getServer();
    }

    public static @NotNull HeritageWorldData getData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    static UUID idForNamedNpc(String namedNpc) {
        return UUID.nameUUIDFromBytes(("vampirism:heritage:named:" + namedNpc).getBytes(StandardCharsets.UTF_8));
    }

    public Map<UUID, HeritageMember> getMembers(UUID heritageId) {
        HeritageRecord record = this.records.get(heritageId);
        return record == null ? Map.of() : Collections.unmodifiableMap(record.members);
    }

    public List<HeritageHistory> getHeritagesForPlayer(UUID playerId) {
        return this.records.entrySet().stream()
                .flatMap(entry -> Optional.ofNullable(entry.getValue().members.get(playerId))
                        .stream()
                        .map(member -> new HeritageHistory(entry.getKey(), entry.getValue().namedNpc, member)))
                .toList();
    }

    Optional<HeritageMembership> getMembership(UUID playerId) {
        PlayerHeritage playerHeritage = this.playerHeritages.get(playerId);
        return playerHeritage == null ? Optional.empty() : Optional.ofNullable(playerHeritage.membership);
    }

    Optional<HeritageMembership> getPendingMembership(UUID playerId) {
        PlayerHeritage playerHeritage = this.playerHeritages.get(playerId);
        return playerHeritage == null ? Optional.empty() : Optional.ofNullable(playerHeritage.pending);
    }

    void prepare(UUID playerId, HeritageMembership pending) {
        PlayerHeritage playerHeritage = this.playerHeritages.computeIfAbsent(playerId, _ -> new PlayerHeritage());
        if (playerHeritage.membership != null) {
            return;
        }
        playerHeritage.pending = pending;
        playerHeritage.completingPendingTransition = false;
        setDirty();
    }

    void beginPendingTransition(UUID playerId) {
        PlayerHeritage playerHeritage = this.playerHeritages.get(playerId);
        if (playerHeritage != null) {
            playerHeritage.completingPendingTransition = playerHeritage.pending != null;
        }
    }

    void cancelPendingTransition(UUID playerId) {
        PlayerHeritage playerHeritage = this.playerHeritages.get(playerId);
        if (playerHeritage == null || (playerHeritage.pending == null && !playerHeritage.completingPendingTransition)) {
            return;
        }
        playerHeritage.pending = null;
        playerHeritage.completingPendingTransition = false;
        removeIfEmpty(playerId, playerHeritage);
        setDirty();
    }

    void ensureIndependentMembership(ServerPlayer player) {
        PlayerHeritage playerHeritage = this.playerHeritages.computeIfAbsent(player.getUUID(), _ -> new PlayerHeritage());
        if (playerHeritage.membership == null) {
            playerHeritage.membership = independentMembership();
        }
        record(player, playerHeritage.membership);
    }

    void completeVampireTransition(ServerPlayer player) {
        PlayerHeritage playerHeritage = this.playerHeritages.computeIfAbsent(player.getUUID(), _ -> new PlayerHeritage());
        if (playerHeritage.membership == null) {
            playerHeritage.membership = playerHeritage.completingPendingTransition && playerHeritage.pending != null
                    ? playerHeritage.pending
                    : independentMembership();
        }
        playerHeritage.pending = null;
        playerHeritage.completingPendingTransition = false;
        record(player, playerHeritage.membership);
    }

    void runAwayFromHeritage(ServerPlayer player) {
        PlayerHeritage playerHeritage = this.playerHeritages.computeIfAbsent(player.getUUID(), _ -> new PlayerHeritage());
        playerHeritage.membership = independentMembership();
        playerHeritage.pending = null;
        playerHeritage.completingPendingTransition = false;
        record(player, playerHeritage.membership);
    }

    void record(ServerPlayer player, HeritageMembership membership) {
        UUID playerId = player.getUUID();
        HeritageRecord record = this.records.computeIfAbsent(membership.heritageId(), _ -> new HeritageRecord(membership.namedNpc()));
        record.members.put(playerId, new HeritageMember(playerId, player.getGameProfile().name(), membership.parentPlayerId(), membership.parentNpcId(), membership.origin()));
        setDirty();
    }

    @Override
    public void deserialize(ValueInput input) {
        this.records.clear();
        this.playerHeritages.clear();
        input.childrenList("records").stream().flatMap(ValueInput.ValueInputList::stream).forEach(recordInput ->
                recordInput.read("id", UUIDUtil.CODEC).ifPresent(id -> {
                    HeritageRecord record = new HeritageRecord(recordInput.getString("named_npc").orElse(null));
                    record.deserialize(recordInput);
                    this.records.put(id, record);
                })
        );
        input.childrenList("players").stream().flatMap(ValueInput.ValueInputList::stream).forEach(playerInput ->
                playerInput.read("id", UUIDUtil.CODEC).ifPresent(id -> {
                    PlayerHeritage playerHeritage = new PlayerHeritage();
                    playerHeritage.membership = readMembership(playerInput, "membership");
                    playerHeritage.pending = readMembership(playerInput, "pending");
                    if (playerHeritage.membership != null || playerHeritage.pending != null) {
                        this.playerHeritages.put(id, playerHeritage);
                    }
                })
        );
    }

    @Override
    public void serialize(ValueOutput output) {
        var recordsOutput = output.childrenList("records");
        this.records.forEach((id, record) -> {
            ValueOutput recordOutput = recordsOutput.addChild();
            recordOutput.store("id", UUIDUtil.CODEC, id);
            record.serialize(recordOutput);
        });
        var playersOutput = output.childrenList("players");
        this.playerHeritages.forEach((id, playerHeritage) -> {
            if (playerHeritage.membership == null && playerHeritage.pending == null) {
                return;
            }
            ValueOutput playerOutput = playersOutput.addChild();
            playerOutput.store("id", UUIDUtil.CODEC, id);
            if (playerHeritage.membership != null) {
                writeMembership(playerOutput, "membership", playerHeritage.membership);
            }
            if (playerHeritage.pending != null) {
                writeMembership(playerOutput, "pending", playerHeritage.pending);
            }
        });
    }

    private static @Nullable HeritageMembership readMembership(ValueInput input, String key) {
        return input.child(key).map(HeritageWorldData::readMembership).orElse(null);
    }

    private static @Nullable HeritageMembership readMembership(ValueInput input) {
        return input.read("id", UUIDUtil.CODEC).map(id -> new HeritageMembership(
                id,
                input.read("origin", HeritageOrigin.CODEC).orElse(HeritageOrigin.INDEPENDENT),
                input.read("parent", UUIDUtil.CODEC).orElse(null),
                input.getString("named_npc").orElse(null),
                input.getString("parent_npc").orElse(null)
        )).orElse(null);
    }

    private static void writeMembership(ValueOutput output, String key, HeritageMembership membership) {
        ValueOutput membershipOutput = output.child(key);
        membershipOutput.store("id", UUIDUtil.CODEC, membership.heritageId());
        membershipOutput.store("origin", HeritageOrigin.CODEC, membership.origin());
        membershipOutput.storeNullable("parent", UUIDUtil.CODEC, membership.parentPlayerId());
        if (membership.namedNpc() != null) {
            membershipOutput.putString("named_npc", membership.namedNpc());
        }
        if (membership.parentNpcId() != null) {
            membershipOutput.putString("parent_npc", membership.parentNpcId());
        }
    }

    private static HeritageMembership independentMembership() {
        return new HeritageMembership(UUID.randomUUID(), HeritageOrigin.INDEPENDENT, null, null, null);
    }

    private void removeIfEmpty(UUID playerId, PlayerHeritage playerHeritage) {
        if (playerHeritage.membership == null && playerHeritage.pending == null) {
            this.playerHeritages.remove(playerId);
        }
    }

    private static Codec<HeritageWorldData> makeCodec(ServerLevel level) {
        return CompoundTag.CODEC.flatXmap(tag -> {
            HeritageWorldData data = new HeritageWorldData(level);
            ProblemReporter.Collector reporter = new ProblemReporter.Collector();
            data.deserialize(TagValueInput.create(reporter, level.registryAccess(), tag));
            return reporter.isEmpty() ? DataResult.success(data) : DataResult.error(() -> "Deserialization error in heritage data: " + reporter.getReport());
        }, data -> {
            ProblemReporter.Collector reporter = new ProblemReporter.Collector();
            TagValueOutput output = TagValueOutput.createWithContext(reporter, data.server.registryAccess());
            data.serialize(output);
            CompoundTag tag = output.buildResult();
            return reporter.isEmpty() ? DataResult.success(tag) : DataResult.error(() -> "Serialization error in heritage data: " + reporter.getReport());
        });
    }

    public record HeritageMember(UUID playerId, String playerName, @Nullable UUID parentPlayerId, @Nullable String parentNpcId, HeritageOrigin origin) {
    }

    public record HeritageHistory(UUID heritageId, @Nullable String namedNpc, HeritageMember member) {
        public HeritageMembership membership() {
            return new HeritageMembership(this.heritageId, this.member.origin(), this.member.parentPlayerId(), this.namedNpc, this.member.parentNpcId());
        }
    }

    private static final class PlayerHeritage {
        private @Nullable HeritageMembership membership;
        private @Nullable HeritageMembership pending;
        private boolean completingPendingTransition;
    }

    private static final class HeritageRecord {
        private final @Nullable String namedNpc;
        private final Map<UUID, HeritageMember> members = new HashMap<>();

        private HeritageRecord(@Nullable String namedNpc) {
            this.namedNpc = namedNpc;
        }

        private void deserialize(ValueInput input) {
            input.childrenList("members").stream().flatMap(ValueInput.ValueInputList::stream).forEach(memberInput ->
                    memberInput.read("id", UUIDUtil.CODEC).ifPresent(id -> members.put(id, new HeritageMember(
                            id,
                            memberInput.getString("name").orElse(""),
                            memberInput.read("parent", UUIDUtil.CODEC).orElse(null),
                            memberInput.getString("parent_npc").orElse(null),
                            memberInput.read("origin", HeritageOrigin.CODEC).orElse(HeritageOrigin.INDEPENDENT)
                    )))
            );
        }

        private void serialize(ValueOutput output) {
            if (this.namedNpc != null) {
                output.putString("named_npc", this.namedNpc);
            }
            var membersOutput = output.childrenList("members");
            this.members.forEach((id, member) -> {
                ValueOutput memberOutput = membersOutput.addChild();
                memberOutput.store("id", UUIDUtil.CODEC, id);
                memberOutput.putString("name", member.playerName());
                memberOutput.storeNullable("parent", UUIDUtil.CODEC, member.parentPlayerId());
                if (member.parentNpcId() != null) {
                    memberOutput.putString("parent_npc", member.parentNpcId());
                }
                memberOutput.store("origin", HeritageOrigin.CODEC, member.origin());
            });
        }
    }
}
