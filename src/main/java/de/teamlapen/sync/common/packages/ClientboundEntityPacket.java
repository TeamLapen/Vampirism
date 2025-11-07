package de.teamlapen.sync.common.packages;

import com.mojang.logging.LogUtils;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public record ClientboundEntityPacket(int id, CompoundTag data, boolean isPlayerItself) implements CustomPacketPayload {

    private final static Logger LOGGER = LogUtils.getLogger();
    public static final Type<ClientboundEntityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "data_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEntityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundEntityPacket::id,
            ByteBufCodecs.COMPOUND_TAG, ClientboundEntityPacket::data,
            ByteBufCodecs.BOOL, ClientboundEntityPacket::isPlayerItself,
            ClientboundEntityPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

//    @Nullable
//    public static ClientboundEntityPacket createPacket(Entity entity) {
//        final List<IAttachedSyncable> capsToSync = new ArrayList<>();
//        Collection<AttachmentType<IAttachedSyncable>> caps = switch (entity) {
//            case PathfinderMob mob -> SyncRegistry.getSyncableEntityCaps().values();
//            case Player player -> SyncRegistry.getSyncablePlayerCaps().values();
//            default -> List.of();
//        };
//        for (AttachmentType<IAttachedSyncable> cap : caps) {
//            Optional.of(entity.getData(cap)).ifPresent(capsToSync::add);
//        }
//        if (!capsToSync.isEmpty()) {
//            return ClientboundEntityPacket.create(entity, capsToSync.toArray(new IAttachedSyncable[0]));
//        } else if (entity instanceof ISyncable syncable) {
//            return ClientboundEntityPacket.create(syncable);
//        } else {
//            return null;
//        }
//    }

//    public static <T extends Entity & ISyncable> ClientboundEntityPacket create(T entity) {
//        try (var reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
//            TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
//
//        }
//    }

//    public static ClientboundEntityPacket create(Entity entity, IAttachedSyncable... caps) {
//
//    }

}
