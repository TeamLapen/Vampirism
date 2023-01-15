package de.teamlapen.lib;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.ClientboundUpdateEntityPacket;
import de.teamlapen.lib.network.IMessage;
import net.minecraft.nbt.CompoundTag;
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
            IMessage.IClientBoundMessage m = ClientboundUpdateEntityPacket.create(entity);
            VampLib.dispatcher.sendToAllTrackingPlayers(m, entity);
        }

    }

    /**
     * Syncs the entity to players tracking this entity using the given data
     * Entity has to implement {@link ISyncable}
     */
    public static <T extends Entity & ISyncable> void sync(@NotNull T entity, CompoundTag data) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            IMessage.IClientBoundMessage m = ClientboundUpdateEntityPacket.create(entity, data);
            VampLib.dispatcher.sendToAllTrackingPlayers(m, entity);
        }

    }

    /**
     * Syncs the given capability instance.
     * If the entity is a player and "all" is false it will only be sent to the respective player
     * Otherwise it will we send to all players tracking the entity radius using the given data
     * <p>
     * CAREFUL: If this is a player, and it is not connected yet, no message is sent, but no exception is thrown.
     */
    public static void sync(ISyncable.@NotNull ISyncableEntityCapabilityInst cap, @NotNull Entity entity, boolean all) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            IMessage.IClientBoundMessage m = ClientboundUpdateEntityPacket.create(cap);
            if (entity instanceof ServerPlayer player && !all) {
                //noinspection ConstantConditions
                if (player.connection != null) {
                    VampLib.dispatcher.sendTo(m, player);
                }

            } else {
                VampLib.dispatcher.sendToAllTrackingPlayers(m, entity);
            }
        }

    }

    /**
     * Syncs the given capability instance using the given data.
     * If the entity is a player and "all" is false it will only be sent to the respective player
     * Otherwise it will we send to all players tracking this entity using the given data
     * <p>
     * CAREFUL: If this is a player, and it is not connected yet, no message is sent, but no exception is thrown.
     */
    public static void sync(ISyncable.@NotNull ISyncableEntityCapabilityInst cap, @NotNull CompoundTag data, @NotNull Entity entity, boolean all) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            IMessage.IClientBoundMessage m = ClientboundUpdateEntityPacket.create(cap, data);
            if (entity instanceof ServerPlayer player && !all) {
                //noinspection ConstantConditions
                if (player.connection != null) {
                    VampLib.dispatcher.sendTo(m, player);
                }
            } else {
                VampLib.dispatcher.sendToAllTrackingPlayers(m, entity);
            }
        }

    }


}
