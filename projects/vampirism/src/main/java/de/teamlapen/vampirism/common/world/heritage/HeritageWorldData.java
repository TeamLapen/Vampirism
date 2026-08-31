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
import java.util.Map;
import java.util.UUID;

/**
 * Server-wide heritage membership store. It keeps player nodes available when the players are offline.
 */
public final class HeritageWorldData extends SavedData implements ValueIOSerializable {
    public static final SavedDataType<HeritageWorldData> TYPE = new SavedDataType<>(VIdentifier.mod("heritage"), HeritageWorldData::new, HeritageWorldData::makeCodec);
    private final MinecraftServer server;
    private final Map<UUID, HeritageRecord> records = new HashMap<>();

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

    void record(ServerPlayer player, HeritageMembership membership) {
        UUID playerId = player.getUUID();
        this.records.forEach((heritageId, record) -> {
            if (!heritageId.equals(membership.heritageId())) {
                record.members.remove(playerId);
            }
        });
        HeritageRecord record = this.records.computeIfAbsent(membership.heritageId(), _ -> new HeritageRecord(membership.namedNpc()));
        record.members.put(playerId, new HeritageMember(playerId, player.getGameProfile().name(), membership.parentPlayerId(), membership.origin()));
        setDirty();
    }

    @Override
    public void deserialize(ValueInput input) {
        this.records.clear();
        input.childrenList("records").stream().flatMap(ValueInput.ValueInputList::stream).forEach(recordInput ->
                recordInput.read("id", UUIDUtil.CODEC).ifPresent(id -> {
                    HeritageRecord record = new HeritageRecord(recordInput.getString("named_npc").orElse(null));
                    record.deserialize(recordInput);
                    this.records.put(id, record);
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

    public record HeritageMember(UUID playerId, String playerName, @Nullable UUID parentPlayerId, HeritageOrigin origin) {
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
                            memberInput.read("origin", HeritageOrigin.CODEC).orElse(HeritageOrigin.UNKNOWN)
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
                memberOutput.store("origin", HeritageOrigin.CODEC, member.origin());
            });
        }
    }
}
