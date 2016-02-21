package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.AbstractMessageHandler;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.network.ISyncable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Do Entity or IExtendedEntityProperty Update
 */
public class UpdateEntityPacket implements IMessage {

    private final static String TAG = "UpdateEntityPacket";

    /**
     * Create a sync packet for the given property.
     *
     * @param prop
     * @return
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableExtendedProperties prop) {
        NBTTagCompound data = new NBTTagCompound();
        prop.writeFullUpdateToNBT(data);
        return create(prop, data);
    }

    /**
     * Create one sync packet for the given syncable entity containing firstly the data from it's {@link ISyncable} implementations and secondly all given properties
     *
     * @param entity EntityLiving which implements ISyncable
     * @param props  Have to belong to the given entity
     * @return
     */
    public static UpdateEntityPacket create(EntityLiving entity, ISyncable.ISyncableExtendedProperties... props) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        UpdateEntityPacket packet = create(props);
        packet.data = new NBTTagCompound();
        ((ISyncable) entity).writeFullUpdateToNBT(packet.data);
        return packet;
    }

    /**
     * Create one sync packet for all given properties.
     *
     * @param props Have to belong to the same entity
     * @return
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableExtendedProperties... props) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = props[0].getTheEntityID();
        packet.props = new NBTTagCompound();
        for (int i = 0; i < props.length; i++) {
            NBTTagCompound data = new NBTTagCompound();
            props[i].writeFullUpdateToNBT(data);
            packet.props.setTag(props[i].getPropertyKey(), data);
        }
        return packet;
    }

    /**
     * Create a sync packet for the given property containing the given data
     *
     * @param prop
     * @param data Should be loadable by the property
     * @return
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableExtendedProperties prop, NBTTagCompound data) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = prop.getTheEntityID();
        packet.props = new NBTTagCompound();
        packet.props.setTag(prop.getPropertyKey(), data);
        return packet;
    }

    /**
     * Create a sync packet for the given syncable entity containing the data from it's ISyncable implementation
     *
     * @param entity Has to implement ISyncable
     * @return
     */
    public static UpdateEntityPacket create(Entity entity) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = entity.getEntityId();
        packet.data = new NBTTagCompound();
        ((ISyncable) entity).writeFullUpdateToNBT(packet.data);
        return packet;
    }

    /**
     * Create a sync packet for the given syncable entity containing the data given data
     *
     * @param entity Has to implement ISyncable
     * @param data   Should be loadable by the entity
     * @return
     */
    public static UpdateEntityPacket create(Entity entity, NBTTagCompound data) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = entity.getEntityId();
        packet.data = data;
        return packet;
    }

    public static UpdateEntityPacket createJoinWorldPacket(Entity entity) {
        List<ISyncable.ISyncableExtendedProperties> props = null;
        String[] keys = null;
        if (entity instanceof EntityLiving) {
            keys = HelperRegistry.getSyncableEntityProperties();
        } else if (entity instanceof EntityPlayer) {
            keys = HelperRegistry.getSyncablePlayerProperties();

        }
        if (keys != null && keys.length > 0) {
            props = new ArrayList<>();
            for (String prop : keys) {
                ISyncable.ISyncableExtendedProperties p = (ISyncable.ISyncableExtendedProperties) entity.getExtendedProperties(prop);
                if (p != null) {
                    props.add(p);
                }
            }
        }
        if (props != null) {
            if (entity instanceof ISyncable) {
                return UpdateEntityPacket.create((EntityLiving) entity, props.toArray(new ISyncable.ISyncableExtendedProperties[props.size()]));
            } else {
                return UpdateEntityPacket.create(props.toArray(new ISyncable.ISyncableExtendedProperties[props.size()]));
            }
        } else if (entity instanceof ISyncable) {
            return UpdateEntityPacket.create(entity);
        } else {
            VampLib.log.w("RequestUpdatePacket", "There is nothing to update for entity %s", entity);
            return null;
        }


    }

    private int id;
    private NBTTagCompound data;
    private NBTTagCompound props;

    /**
     * Dont use
     */
    public UpdateEntityPacket() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        id = tag.getInteger("id");
        if (tag.hasKey("data")) {
            data = tag.getCompoundTag("data");
        }
        if (tag.hasKey("props")) {
            props = tag.getCompoundTag("props");
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", id);
        if (data != null) {
            tag.setTag("data", data);
        }
        if (props != null) {
            tag.setTag("props", props);
        }
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler extends AbstractMessageHandler<UpdateEntityPacket> {

        @Override
        public IMessage handleClientMessage(EntityPlayer player, UpdateEntityPacket message, MessageContext ctx) {
            VampLib.log.t("Received %s %s %s", player, message.data, message.props);
            if (player.worldObj == null) {
                VampLib.log.w(TAG, "World not loaded yet");
                return null;
            }
            Entity e = player.worldObj.getEntityByID(message.id);
            if (e == null) {
                VampLib.log.e(TAG, "Did not find entity %s", message.id);
                return null;
            }

            if (message.data != null) {
                ISyncable syncable;
                try {
                    syncable = (ISyncable) e;

                } catch (ClassCastException ex) {
                    VampLib.log.w(TAG, "Target entity %s does not implement ISyncable (%s)", e, ex);
                    return null;
                }
                syncable.loadUpdateFromNBT(message.data);
            }
            if (message.props != null) {

                for (String key : message.props.getKeySet()) {
                    handleProperty(e, key, message.props.getCompoundTag(key));
                }


            }
            return null;
        }

        @Override
        public IMessage handleServerMessage(EntityPlayer player, UpdateEntityPacket message, MessageContext ctx) {
            return null;
        }

        @Override
        protected AbstractPacketDispatcher getDispatcher() {
            return VampLib.dispatcher;
        }

        @Override
        protected boolean handleOnMainThread() {
            return true;
        }

        private void handleProperty(Entity e, String prop, NBTTagCompound data) {
            ISyncable syncable;
            try {
                syncable = (ISyncable) e.getExtendedProperties(prop);
            } catch (ClassCastException ex) {
                VampLib.log.w(TAG, "Target entity's property %s does not implement ISyncable (%s)", e.getExtendedProperties(prop), ex);
                return;
            }
            if (syncable == null) {
                VampLib.log.w(TAG, "Target entity %s does not have property %s", e, prop);
            } else {
                syncable.loadUpdateFromNBT(data);
            }
        }

    }
}
