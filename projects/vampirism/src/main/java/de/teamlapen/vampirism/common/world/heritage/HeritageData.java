package de.teamlapen.vampirism.common.world.heritage;

import de.teamlapen.vampirism.common.core.ModAttachments;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Player-owned heritage data. A completed heritage is historical and is never replaced automatically.
 */
public final class HeritageData {
    private final Player player;
    private @Nullable HeritageMembership membership;
    private @Nullable PendingHeritage pending;
    private boolean completingPendingTransition;

    public HeritageData(Player player) {
        this.player = player;
    }

    public static HeritageData get(Player player) {
        return player.getData(ModAttachments.HERITAGE);
    }

    public Optional<HeritageMembership> getMembership() {
        return Optional.ofNullable(this.membership);
    }

    public Optional<HeritageMembership> getPendingMembership() {
        return this.pending == null ? Optional.empty() : Optional.of(this.pending.toMembership());
    }

    void ensureIndependentMembership(ServerPlayer player) {
        if (this.membership == null) {
            this.membership = PendingHeritage.independent().toMembership();
        }
        HeritageWorldData.getData(player.level().getServer()).record(player, this.membership);
        synchronize();
    }

    void beginPendingTransition() {
        this.completingPendingTransition = this.pending != null;
    }

    void completeVampireTransition(ServerPlayer player) {
        if (this.membership == null) {
            this.membership = this.completingPendingTransition && this.pending != null
                    ? this.pending.toMembership()
                    : PendingHeritage.independent().toMembership();
        }
        this.pending = null;
        this.completingPendingTransition = false;
        HeritageWorldData.getData(player.level().getServer()).record(player, this.membership);
        synchronize();
    }

    void cancelPendingTransition() {
        if (this.pending == null && !this.completingPendingTransition) {
            return;
        }
        this.pending = null;
        this.completingPendingTransition = false;
        synchronize();
    }

    void runAwayFromHeritage(ServerPlayer player) {
        this.membership = PendingHeritage.independent().toMembership();
        this.pending = null;
        this.completingPendingTransition = false;
        HeritageWorldData.getData(player.level().getServer()).record(player, this.membership);
        synchronize();
    }

    void prepare(PendingHeritage source) {
        if (this.membership != null) {
            return;
        }
        this.pending = source;
        this.completingPendingTransition = false;
        synchronize();
    }

    private void deserialize(ValueInput input) {
        this.membership = readMembership(input, "membership");
        this.pending = readPending(input, "pending");
    }

    private static @Nullable HeritageMembership readMembership(ValueInput input, String key) {
        return input.child(key).map(HeritageData::readMembership).orElse(null);
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

    private static @Nullable PendingHeritage readPending(ValueInput input, String key) {
        return input.child(key).map(child -> readMembership(child)).map(PendingHeritage::fromMembership).orElse(null);
    }

    private void serialize(ValueOutput output) {
        if (this.membership != null) {
            writeMembership(output.child("membership"), this.membership);
        }
        if (this.pending != null) {
            writeMembership(output.child("pending"), this.pending.toMembership());
        }
    }

    private static void writeMembership(ValueOutput output, HeritageMembership membership) {
        output.store("id", UUIDUtil.CODEC, membership.heritageId());
        output.store("origin", HeritageOrigin.CODEC, membership.origin());
        output.storeNullable("parent", UUIDUtil.CODEC, membership.parentPlayerId());
        if (membership.namedNpc() != null) {
            output.putString("named_npc", membership.namedNpc());
        }
        if (membership.parentNpcId() != null) {
            output.putString("parent_npc", membership.parentNpcId());
        }
    }

    private void synchronize() {
        if (!this.player.level().isClientSide()) {
            this.player.syncData(ModAttachments.HERITAGE);
        }
    }

    private void writeNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.membership != null);
        if (this.membership == null) {
            return;
        }
        buffer.writeUUID(this.membership.heritageId());
        buffer.writeEnum(this.membership.origin());
        writeNullableUuid(buffer, this.membership.parentPlayerId());
        writeNullableString(buffer, this.membership.namedNpc());
        writeNullableString(buffer, this.membership.parentNpcId());
    }

    private void readNetwork(RegistryFriendlyByteBuf buffer) {
        if (!buffer.readBoolean()) {
            this.membership = null;
            return;
        }
        UUID heritageId = buffer.readUUID();
        HeritageOrigin origin = buffer.readEnum(HeritageOrigin.class);
        UUID parent = readNullableUuid(buffer);
        String namedNpc = readNullableString(buffer);
        String parentNpc = readNullableString(buffer);
        this.membership = new HeritageMembership(heritageId, origin, parent, namedNpc, parentNpc);
    }

    private static void writeNullableUuid(RegistryFriendlyByteBuf buffer, @Nullable UUID value) {
        buffer.writeBoolean(value != null);
        if (value != null) {
            buffer.writeUUID(value);
        }
    }

    private static @Nullable UUID readNullableUuid(RegistryFriendlyByteBuf buffer) {
        return buffer.readBoolean() ? buffer.readUUID() : null;
    }

    private static void writeNullableString(RegistryFriendlyByteBuf buffer, @Nullable String value) {
        buffer.writeBoolean(value != null);
        if (value != null) {
            buffer.writeUtf(value);
        }
    }

    private static @Nullable String readNullableString(RegistryFriendlyByteBuf buffer) {
        return buffer.readBoolean() ? buffer.readUtf() : null;
    }

    static final class PendingHeritage {
        private final HeritageMembership membership;

        private PendingHeritage(HeritageMembership membership) {
            this.membership = membership;
        }

        static PendingHeritage fromMembership(HeritageMembership membership) {
            return new PendingHeritage(membership);
        }

        static PendingHeritage independent() {
            return new PendingHeritage(new HeritageMembership(UUID.randomUUID(), HeritageOrigin.INDEPENDENT, null, null, null));
        }

        static PendingHeritage named(String namedNpc, @Nullable String parentNpcId) {
            return new PendingHeritage(new HeritageMembership(HeritageWorldData.idForNamedNpc(namedNpc), HeritageOrigin.INHERITED, null, namedNpc, parentNpcId));
        }

        static PendingHeritage player(HeritageMembership parent, UUID parentPlayerId) {
            return new PendingHeritage(new HeritageMembership(parent.heritageId(), HeritageOrigin.INHERITED, parentPlayerId, parent.namedNpc(), null));
        }

        HeritageMembership toMembership() {
            return this.membership;
        }
    }

    public static final class Factory implements Function<IAttachmentHolder, HeritageData> {
        @Override
        public HeritageData apply(IAttachmentHolder holder) {
            if (holder instanceof Player player) {
                return new HeritageData(player);
            }
            throw new IllegalArgumentException("Cannot create heritage attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }

    public static final class Serializer implements IAttachmentSerializer<HeritageData> {
        @Override
        public HeritageData read(IAttachmentHolder holder, ValueInput input) {
            HeritageData data = new Factory().apply(holder);
            data.deserialize(input);
            return data;
        }

        @Override
        public boolean write(HeritageData attachment, ValueOutput output) {
            attachment.serialize(output);
            return attachment.membership != null || attachment.pending != null;
        }
    }

    public static final class Sync implements AttachmentSyncHandler<HeritageData> {
        @Override
        public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
            return holder == to;
        }

        @Override
        public void write(RegistryFriendlyByteBuf buffer, HeritageData attachment, boolean initialSync) {
            attachment.writeNetwork(buffer);
        }

        @Override
        public @Nullable HeritageData read(IAttachmentHolder holder, RegistryFriendlyByteBuf buffer, @Nullable HeritageData previousValue) {
            HeritageData data = previousValue == null ? new Factory().apply(holder) : previousValue;
            data.readNetwork(buffer);
            return data;
        }
    }
}
