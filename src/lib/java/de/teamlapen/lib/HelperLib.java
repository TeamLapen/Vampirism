package de.teamlapen.lib;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.ClientboundUpdateEntityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * General Helper library
 */
public class HelperLib {

    /**
     * Syncs the entity to players tracking this entity.
     * Entity has to implement {@link ISyncable}
     */
    public static <T extends Entity & ISyncable> void sync(@NotNull T entity) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            ClientboundUpdateEntityPacket m = ClientboundUpdateEntityPacket.create(entity);
            sendToAll(entity, m);
        }

    }

    /**
     * Syncs the entity to players tracking this entity using the given data
     * Entity has to implement {@link ISyncable}
     */
    public static <T extends Entity & ISyncable> void sync(@NotNull T entity, CompoundTag data) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            ClientboundUpdateEntityPacket m = ClientboundUpdateEntityPacket.create(entity, data);
            sendToAll(entity, m);
        }

    }

    /**
     * Syncs the given capability instance.
     * If the entity is a player and "all" is false it will only be sent to the respective player
     * Otherwise it will we send to all players tracking the entity radius using the given data
     * <p>
     * CAREFUL: If this is a player, and it is not connected yet, no message is sent, but no exception is thrown.
     */
    public static void sync(ISyncable.@NotNull ISyncableAttachment cap, @NotNull Entity entity, boolean all) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            ClientboundUpdateEntityPacket m = ClientboundUpdateEntityPacket.create(cap);
            if (entity instanceof ServerPlayer player) {
                //noinspection ConstantConditions
                if (player.connection != null) {
                    player.connection.send(m);
                }
            }
            if (all) {
                sendToAll(entity, m);
            }
        }

    }

    public static void sync(Object object, Entity entity, boolean all) {
        if (!entity.level().isClientSide() && object instanceof ISyncable.ISyncableAttachment cap) {
            ClientboundUpdateEntityPacket m = ClientboundUpdateEntityPacket.create(cap);
            if (entity instanceof ServerPlayer player) {
                //noinspection ConstantConditions
                if (player.connection != null) {
                    player.connection.send(m);
                }
            }
            if (all) {
                sendToAll(entity, m);
            }
        }
    }

    private static void sendToAll(@NotNull Entity entity, CustomPacketPayload packetPayload) {
        if (entity.level() instanceof ServerLevel level) {
            ServerChunkCache serverchunkcache = level.getChunkSource();
            serverchunkcache.broadcast(entity, packetPayload);
        }
    }

    /**
     * Syncs the given capability instance using the given data.
     * If the entity is a player and "all" is false it will only be sent to the respective player
     * Otherwise it will we send to all players tracking this entity using the given data
     * <p>
     * CAREFUL: If this is a player, and it is not connected yet, no message is sent, but no exception is thrown.
     */
    public static void sync(ISyncable.@NotNull ISyncableAttachment cap, @NotNull CompoundTag data, @NotNull Entity entity, boolean all) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            ClientboundUpdateEntityPacket m = ClientboundUpdateEntityPacket.create(cap, data);
            if (entity instanceof ServerPlayer player && !all) {
                //noinspection ConstantConditions
                if (player.connection != null) {
                    player.connection.send(m);
                }
            } else {
                sendToAll(entity, m);
            }
        }

    }


}
