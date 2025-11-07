package de.teamlapen.sync.common.packages;

import com.mojang.logging.LogUtils;
import de.teamlapen.sync.SyncRegistry;
import de.teamlapen.sync.common.storage.IAttachedSyncable;
import de.teamlapen.sync.common.storage.ISyncable;
import de.teamlapen.sync.common.storage.UpdateParams;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Does Entity or Entity capability updates.
 * Entity capabilities that want to use this, have to be registered in {@link SyncRegistry}
 */
public class ClientboundUpdateEntityPacket implements CustomPacketPayload {

    private final static Logger LOGGER = LogUtils.getLogger();
    public static final Type<ClientboundUpdateEntityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "update_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateEntityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundUpdateEntityPacket::getId,
            ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG), s -> Optional.ofNullable(s.data),
            ByteBufCodecs.BOOL, ClientboundUpdateEntityPacket::isPlayerItself,
            ClientboundUpdateEntityPacket::new
    );


    /**
     * Create one sync packet for the given syncable entity containing firstly the data from it's {@link ISyncable} implementations and secondly all given capability instances
     *
     * @param entity EntityLiving which implements ISyncable
     * @param caps   Have to belong to the given entity
     */
    public static @NotNull ClientboundUpdateEntityPacket create(Entity entity, IAttachedSyncable... caps) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        try (var problemReported = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(problemReported, entity.registryAccess());
            saveAttachments(output.child("attachments"), caps);
            ((ISyncable) entity).serializeUpdate(output.child("data"), UpdateParams.ignoreChanged());
            return new ClientboundUpdateEntityPacket(entity.getId(), output.buildResult(), false);
        }
    }

    /**
     * Create one sync packet for all given capability instances.
     *
     * @param caps Have to belong to the same entity
     */
    public static void saveAttachments(ValueOutput output, IAttachedSyncable @NotNull ... caps) {
        for (IAttachedSyncable cap : caps) {
            cap.serializeUpdate(output.child(cap.getAttachedKey().toString()), UpdateParams.ignoreChanged());
        }
    }

    /**
     * Create a sync packet for the given capability instance containing the given data
     *
     * @param data Should be loadable by the capability instance
     */
//    public static @NotNull ClientboundUpdateEntityPacket create(@NotNull IAttachedSyncable cap, @NotNull CompoundTag data) {
//        CompoundTag tag = new CompoundTag();
//        tag.put(cap.getAttachedKey().toString(), data);
//        return new ClientboundUpdateEntityPacket(cap.asEntity().getId(), null, tag, false);
//    }

    /**
     * Create a sync packet for the given syncable entity containing the data from it's ISyncable implementation
     *
     * @param entity Has to implement ISyncable
     */
    public static @NotNull ClientboundUpdateEntityPacket create(Entity entity) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        try (var problemReported = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(problemReported, entity.registryAccess());
            ((ISyncable) entity).serializeUpdate(output.child("update"), UpdateParams.ignoreChanged());
            return new ClientboundUpdateEntityPacket(entity.getId(), output.buildResult(), false);
        }
    }

    /**
     * Create a sync packet for the given syncable entity containing the data given data
     *
     * @param entity Has to implement ISyncable
     * @param data   Should be loadable by the entity
     */
//    public static <T extends Entity & ISyncable> @NotNull ClientboundUpdateEntityPacket create(@NotNull T entity, CompoundTag data) {
//        return new ClientboundUpdateEntityPacket(entity.getId(), data, null, false);
//    }

    /**
     * Create a packet that contains all relevant information the client needs to know about a newly joined entity.
     *
     * @return If nothing to update -> null
     */
//    @Nullable
//    public static ClientboundUpdateEntityPacket createJoinWorldPacket(Entity entity) {
//        final List<IAttachedSyncable> capsToSync = new ArrayList<>();
//        Collection<AttachmentType<IAttachedSyncable>> allCaps = null;
//        if (entity instanceof PathfinderMob) {
//            allCaps = SyncRegistry.getSyncableEntityCaps().values();
//        } else if (entity instanceof Player) {
//            allCaps = SyncRegistry.getSyncablePlayerCaps().values();
//
//        }
//        if (allCaps != null && !allCaps.isEmpty()) {
//            for (AttachmentType<IAttachedSyncable> cap : allCaps) {
//                Optional.of(entity.getData(cap)).ifPresent(capsToSync::add);
//            }
//        }
//        if (!capsToSync.isEmpty()) {
//            if (entity instanceof ISyncable) {
//                return ClientboundUpdateEntityPacket.create((Mob) entity, capsToSync.toArray(new IAttachedSyncable[0]));
//            } else {
//                return ClientboundUpdateEntityPacket.create(entity.registryAccess(), capsToSync.toArray(new IAttachedSyncable[0]));
//            }
//        } else if (entity instanceof ISyncable) {
//            return ClientboundUpdateEntityPacket.create(entity);
//        } else {
//            LOGGER.warn("There is nothing to update for entity {}", entity);
//            return null;
//        }
//    }

    private final int id;
    private final @Nullable CompoundTag data;
    private boolean playerItself = false;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ClientboundUpdateEntityPacket(int id, Optional<CompoundTag> data, boolean playerItself) {
        this(id, data.orElse(null), playerItself);
    }

    private ClientboundUpdateEntityPacket(int id, @Nullable CompoundTag data, boolean playerItself) {
        this.id = id;
        this.data = data;
        this.playerItself = playerItself;
    }

    public @Nullable CompoundTag getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    public boolean isPlayerItself() {
        return playerItself;
    }

    public @NotNull ClientboundUpdateEntityPacket markAsPlayerItself() {
        playerItself = true;
        return this;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
