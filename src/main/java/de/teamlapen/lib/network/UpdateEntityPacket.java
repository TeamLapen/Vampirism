package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.AbstractMessageHandler;
import de.teamlapen.lib.lib.network.ISyncable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Do Entity or IExtendedEntityProperty Update
 */
public class UpdateEntityPacket implements IMessage {

    private final static String TAG = "UpdateEntityPacket";
    private boolean entity;
    private String propKey = "";
    private int id;
    private NBTTagCompound data;

    /**
     * Dont use
     */
    public UpdateEntityPacket() {

    }

    public UpdateEntityPacket(Entity entity) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        this.entity = true;
        this.id = entity.getEntityId();
        this.data = new NBTTagCompound();
        ((ISyncable) entity).writeFullUpdateToNBT(data);
    }

    public UpdateEntityPacket(Entity entity, NBTTagCompound data) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        this.entity = true;
        this.id = entity.getEntityId();
        this.data = data;
    }

    public UpdateEntityPacket(ISyncable.ISyncableExtendedProperties prop) {
        this(prop, new NBTTagCompound());
        prop.writeFullUpdateToNBT(data);
    }

    public UpdateEntityPacket(ISyncable.ISyncableExtendedProperties prop, NBTTagCompound nbt) {
        this.entity = false;
        this.id = prop.getTheEntityID();
        this.data = nbt;
        this.propKey = prop.getPropertyKey();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        id = tag.getInteger("id");
        data = tag.getCompoundTag("data");
        entity = tag.getBoolean("entity");
        if (!entity) {
            propKey = tag.getString("propKey");
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", id);
        tag.setBoolean("entity", entity);
        if (!entity) {
            tag.setString("propKey", propKey);
        }
        tag.setTag("data", data);
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler extends AbstractMessageHandler<UpdateEntityPacket> {

        @Override
        public IMessage handleClientMessage(EntityPlayer player, UpdateEntityPacket message, MessageContext ctx) {
            if (player.worldObj == null) {
                VampLib.log.w(TAG, "World not loaded yet");
                return null;
            }
            Entity e = player.worldObj.getEntityByID(message.id);
            if (e == null) {
                return null;
            }
            ISyncable syncable;
            if (message.entity) {
                try {
                    syncable = (ISyncable) e;
                } catch (ClassCastException ex) {
                    VampLib.log.w(TAG, "Target entity %s does not implement ISyncable (%s)", e, ex);
                    return null;
                }
            } else {
                try {
                    syncable = (ISyncable) e.getExtendedProperties(message.propKey);
                } catch (ClassCastException ex) {
                    VampLib.log.w(TAG, "Target entity's property %s does not implement ISyncable (%s)", e.getExtendedProperties(message.propKey), ex);
                    return null;
                }
                if (syncable == null) {
                    VampLib.log.w(TAG, "Target entity %s does not have property %s", e, message.propKey);
                    return null;
                }
            }
            syncable.loadUpdateFromNBT(message.data);

            return null;
        }

        @Override
        public IMessage handleServerMessage(EntityPlayer player, UpdateEntityPacket message, MessageContext ctx) {
            return null;
        }

        @Override
        protected EntityPlayer getPlayerEntityByProxy(MessageContext ctx) {
            return VampLib.proxy.getPlayerEntity(ctx);
        }
    }
}
