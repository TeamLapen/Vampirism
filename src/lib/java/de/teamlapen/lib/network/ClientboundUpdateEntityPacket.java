package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.LIBREFERENCE;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import de.teamlapen.lib.lib.storage.ISyncable;
import de.teamlapen.lib.lib.storage.UpdateParams;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Does Entity or Entity capability updates.
 * Entity capabilities that want to use this, have to be registered in {@link HelperRegistry}
 */
public class ClientboundUpdateEntityPacket implements CustomPacketPayload {

    private final static Logger LOGGER = LogManager.getLogger();
    public static final Type<ClientboundUpdateEntityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LIBREFERENCE.MODID, "update_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateEntityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundUpdateEntityPacket::getId,
            ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG), s -> Optional.ofNullable(s.data),
            ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG), s -> Optional.ofNullable(s.attachments),
            ByteBufCodecs.BOOL, ClientboundUpdateEntityPacket::isPlayerItself,
            ClientboundUpdateEntityPacket::new
    );


    /**
     * Create a sync packet for the given capability instance.
     */
    public static @NotNull ClientboundUpdateEntityPacket create(HolderLookup.Provider provider, @NotNull IAttachedSyncable cap) {
        return create(cap, cap.serializeUpdateNBT(provider, UpdateParams.ignoreChanged()));
    }

    /**
     * Create one sync packet for the given syncable entity containing firstly the data from it's {@link ISyncable} implementations and secondly all given capability instances
     *
     * @param entity EntityLiving which implements ISyncable
     * @param caps   Have to belong to the given entity
     */
    public static @NotNull ClientboundUpdateEntityPacket create(Mob entity, IAttachedSyncable... caps) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        ClientboundUpdateEntityPacket packet = create(entity.registryAccess(), caps);
        packet.data = ((ISyncable) entity).serializeUpdateNBT(entity.registryAccess(), UpdateParams.ignoreChanged());
        return packet;
    }

    /**
     * Create one sync packet for all given capability instances.
     *
     * @param caps Have to belong to the same entity
     */
    public static @NotNull ClientboundUpdateEntityPacket create(HolderLookup.Provider provider, IAttachedSyncable @NotNull ... caps) {
        CompoundTag capsTag = new CompoundTag();
        for (IAttachedSyncable cap : caps) {
            capsTag.put(cap.getAttachedKey().toString(), cap.serializeUpdateNBT(provider, UpdateParams.ignoreChanged()));
        }
        return new ClientboundUpdateEntityPacket(caps[0].asEntity().getId(), null, capsTag, false);
    }

    /**
     * Create a sync packet for the given capability instance containing the given data
     *
     * @param data Should be loadable by the capability instance
     */
    public static @NotNull ClientboundUpdateEntityPacket create(@NotNull IAttachedSyncable cap, @NotNull CompoundTag data) {
        CompoundTag tag = new CompoundTag();
        tag.put(cap.getAttachedKey().toString(), data);
        return new ClientboundUpdateEntityPacket(cap.asEntity().getId(), null, tag, false);
    }

    /**
     * Create a sync packet for the given syncable entity containing the data from it's ISyncable implementation
     *
     * @param entity Has to implement ISyncable
     */
    public static @NotNull ClientboundUpdateEntityPacket create(Entity entity) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        return new ClientboundUpdateEntityPacket(entity.getId(), ((ISyncable) entity).serializeUpdateNBT(entity.registryAccess(), UpdateParams.ignoreChanged()), null, false);
    }

    /**
     * Create a sync packet for the given syncable entity containing the data given data
     *
     * @param entity Has to implement ISyncable
     * @param data   Should be loadable by the entity
     */
    public static <T extends Entity & ISyncable> @NotNull ClientboundUpdateEntityPacket create(@NotNull T entity, CompoundTag data) {
        return new ClientboundUpdateEntityPacket(entity.getId(), data, null, false);
    }

    /**
     * Create a packet that contains all relevant information the client needs to know about a newly joined entity.
     *
     * @return If nothing to update -> null
     */
    @Nullable
    public static ClientboundUpdateEntityPacket createJoinWorldPacket(Entity entity) {
        final List<IAttachedSyncable> capsToSync = new ArrayList<>();
        Collection<AttachmentType<IAttachedSyncable>> allCaps = null;
        if (entity instanceof PathfinderMob) {
            allCaps = HelperRegistry.getSyncableEntityCaps().values();
        } else if (entity instanceof Player) {
            allCaps = HelperRegistry.getSyncablePlayerCaps().values();

        }
        if (allCaps != null && !allCaps.isEmpty()) {
            for (AttachmentType<IAttachedSyncable> cap : allCaps) {
                Optional.of(entity.getData(cap)).ifPresent(capsToSync::add);
            }
        }
        if (!capsToSync.isEmpty()) {
            if (entity instanceof ISyncable) {
                return ClientboundUpdateEntityPacket.create((Mob) entity, capsToSync.toArray(new IAttachedSyncable[0]));
            } else {
                return ClientboundUpdateEntityPacket.create(entity.registryAccess(), capsToSync.toArray(new IAttachedSyncable[0]));
            }
        } else if (entity instanceof ISyncable) {
            return ClientboundUpdateEntityPacket.create(entity);
        } else {
            LOGGER.warn("There is nothing to update for entity {}", entity);
            return null;
        }
    }

    private final int id;
    private @Nullable CompoundTag data;
    private final @Nullable CompoundTag attachments;
    private boolean playerItself = false;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ClientboundUpdateEntityPacket(int id, Optional<CompoundTag> data, Optional<CompoundTag> attachments, boolean playerItself) {
        this(id, data.orElse(null), attachments.orElse(null), playerItself);
    }

    private ClientboundUpdateEntityPacket(int id, @Nullable CompoundTag data, @Nullable CompoundTag attackments, boolean playerItself) {
        this.id = id;
        this.data = data;
        this.attachments = attackments;
        this.playerItself = playerItself;
    }

    public @Nullable CompoundTag getAttachments() {
        return attachments;
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
