package de.teamlapen.lib;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.UpdateEntityPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * General Helper library
 */
public class Helper {
    private final static int PACKET_DISTANCE = 100;

    /**
     * Syncs the entity to players in {@link Helper#PACKET_DISTANCE} radius.
     * Entity has to implement {@link ISyncable}
     *
     * @param entity
     */
    public static void sync(Entity entity) {
        IMessage m = new UpdateEntityPacket(entity);
        VampLib.dispatcher.sendToAllAround(m, createTargetPoint(entity));
    }

    /**
     * Syncs the entity to players in {@link Helper#PACKET_DISTANCE} radius using the given data
     * Entity has to implement {@link ISyncable}
     *
     * @param entity
     */
    public static void sync(Entity entity, NBTTagCompound data) {
        IMessage m = new UpdateEntityPacket(entity, data);
        VampLib.dispatcher.sendToAllAround(m, createTargetPoint(entity));
    }

    /**
     * Syncs the extended properties.
     * If the entity is a player and "all" is false it will only be send to the respective player
     * Otherwise it will we send to all players in {@link Helper#PACKET_DISTANCE} radius using the given data
     *
     * @param entity
     */
    public static void sync(ISyncable.ISyncableExtendedProperties prop, Entity entity, boolean all) {
        IMessage m = new UpdateEntityPacket(prop);
        if (entity instanceof EntityPlayerMP && !all) {
            VampLib.dispatcher.sendTo(m, (EntityPlayerMP) entity);
        } else {
            VampLib.dispatcher.sendToAllAround(m, createTargetPoint(entity));
        }
    }

    /**
     * Syncs the extended properties using the given data.
     * If the entity is a player and "all" is false it will only be send to the respective player
     * Otherwise it will we send to all players in {@link Helper#PACKET_DISTANCE} radius using the given data
     *
     * @param entity
     */
    public static void sync(ISyncable.ISyncableExtendedProperties prop, NBTTagCompound data, Entity entity, boolean all) {
        IMessage m = new UpdateEntityPacket(prop, data);
        if (entity instanceof EntityPlayerMP && !all) {
            VampLib.dispatcher.sendTo(m, (EntityPlayerMP) entity);
        } else {
            VampLib.dispatcher.sendToAllAround(m, createTargetPoint(entity));
        }
    }

    private static NetworkRegistry.TargetPoint createTargetPoint(Entity e) {
        return new NetworkRegistry.TargetPoint(e.dimension, e.posX, e.posY, e.posZ, PACKET_DISTANCE);
    }


}
