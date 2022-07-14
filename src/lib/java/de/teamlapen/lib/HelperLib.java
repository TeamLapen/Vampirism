package de.teamlapen.lib;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.lib.network.UpdateEntityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * General Helper library
 */
public class HelperLib {

    /**
     * Syncs the entity to players tracking this entity.
     * Entity has to implement {@link ISyncable}
     */
    public static <T extends Entity & ISyncable> void sync(T entity) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            IMessage m = UpdateEntityPacket.create(entity);
            VampLib.dispatcher.sendToAllTrackingPlayers(m, entity);
        }

    }

    /**
     * Syncs the entity to players tracking this entity using the given data
     * Entity has to implement {@link ISyncable}
     */
    public static <T extends Entity & ISyncable> void sync(T entity, CompoundTag data) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            IMessage m = UpdateEntityPacket.create(entity, data);
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
    public static void sync(ISyncable.ISyncableEntityCapabilityInst cap, Entity entity, boolean all) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            IMessage m = UpdateEntityPacket.create(cap);
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
    public static void sync(ISyncable.ISyncableEntityCapabilityInst cap, CompoundTag data, Entity entity, boolean all) {
        if (!entity.getCommandSenderWorld().isClientSide) {
            IMessage m = UpdateEntityPacket.create(cap, data);
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
