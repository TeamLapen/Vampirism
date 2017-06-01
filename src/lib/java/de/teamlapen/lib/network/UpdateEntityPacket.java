package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.network.AbstractClientMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Does Entity or Entity capability updates.
 * Entity capabilities that want to use this, have to be registered in {@link HelperRegistry}
 */
public class UpdateEntityPacket implements IMessage {

    private final static String TAG = "UpdateEntityPacket";

    /**
     * Create a sync packet for the given capability instance.
     *
     * @param cap
     * @return
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableEntityCapabilityInst cap) {
        NBTTagCompound data = new NBTTagCompound();
        cap.writeFullUpdateToNBT(data);
        return create(cap, data);
    }

    /**
     * Create one sync packet for the given syncable entity containing firstly the data from it's {@link ISyncable} implementations and secondly all given capability instances
     *
     * @param entity EntityLiving which implements ISyncable
     * @param caps   Have to belong to the given entity
     * @return
     */
    public static UpdateEntityPacket create(EntityLiving entity, ISyncable.ISyncableEntityCapabilityInst... caps) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        UpdateEntityPacket packet = create(caps);
        packet.data = new NBTTagCompound();
        ((ISyncable) entity).writeFullUpdateToNBT(packet.data);
        return packet;
    }

    /**
     * Create one sync packet for all given capability instances.
     *
     * @param caps Have to belong to the same entity
     * @return
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableEntityCapabilityInst... caps) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = caps[0].getTheEntityID();
        packet.caps = new NBTTagCompound();
        for (int i = 0; i < caps.length; i++) {
            NBTTagCompound data = new NBTTagCompound();
            caps[i].writeFullUpdateToNBT(data);
            packet.caps.setTag(caps[i].getCapKey().toString(), data);
        }
        return packet;
    }

    /**
     * Create a sync packet for the given capability instance containing the given data
     *
     * @param cap
     * @param data Should be loadable by the capability instance
     * @return
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableEntityCapabilityInst cap, NBTTagCompound data) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = cap.getTheEntityID();
        packet.caps = new NBTTagCompound();
        packet.caps.setTag(cap.getCapKey().toString(), data);
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

    /**
     * Create a packet that contains all relevant information the client needs to know about a newly joined entity.
     *
     * @return If nothing to update -> null
     */
    public static @Nullable
    UpdateEntityPacket createJoinWorldPacket(Entity entity) {
        List<ISyncable.ISyncableEntityCapabilityInst> capsToSync = null;
        Collection<Capability> allCaps = null;
        if (entity instanceof EntityCreature) {
            allCaps = HelperRegistry.getSyncableEntityCaps().values();
        } else if (entity instanceof EntityPlayer) {
            allCaps = HelperRegistry.getSyncablePlayerCaps().values();

        }
        if (allCaps != null && allCaps.size() > 0) {
            capsToSync = new ArrayList<>();
            for (Capability cap : allCaps) {
                ISyncable.ISyncableEntityCapabilityInst p = (ISyncable.ISyncableEntityCapabilityInst) entity.getCapability(cap, null);
                if (p != null) {
                    capsToSync.add(p);
                }
            }
        }
        if (capsToSync != null) {
            if (entity instanceof ISyncable) {
                return UpdateEntityPacket.create((EntityLiving) entity, capsToSync.toArray(new ISyncable.ISyncableEntityCapabilityInst[capsToSync.size()]));
            } else {
                return UpdateEntityPacket.create(capsToSync.toArray(new ISyncable.ISyncableEntityCapabilityInst[capsToSync.size()]));
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
    private NBTTagCompound caps;
    private boolean playerItself = false;

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
        if (tag.hasKey("caps")) {
            caps = tag.getCompoundTag("caps");
        }
        if (tag.hasKey("itself")) {
            playerItself = tag.getBoolean("itself");
        }
    }

    public UpdateEntityPacket markAsPlayerItself() {
        playerItself = true;
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", id);
        if (data != null) {
            tag.setTag("data", data);
        }
        if (caps != null) {
            tag.setTag("caps", caps);
        }
        if (playerItself) {
            tag.setBoolean("itself", true);
        }
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler extends AbstractClientMessageHandler<UpdateEntityPacket> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage handleClientMessage(EntityPlayer player, UpdateEntityPacket message, MessageContext ctx) {
//            if (player.getRNG().nextInt(10) == 0)
//                VampLib.log.t("Received %s %s %s", message.id, message.data, message.caps);//Log a few random message, just to see if everything is alright.

            Entity e = player.getEntityWorld().getEntityByID(message.id);
            if (e == null) {
                VampLib.log.e(TAG, "Did not find entity %s", message.id);
                if (message.playerItself) {
                    VampLib.log.e(TAG, "Message is meant for player itself, but id mismatch %s %s. Loading anyway.", player.getEntityId(), message.id);
                    e = player;
                } else {
                    return null;
                }
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
            if (message.caps != null) {

                for (String key : message.caps.getKeySet()) {
                    handleCapability(e, new ResourceLocation(key), message.caps.getCompoundTag(key));
                }


            }
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

        @SideOnly(Side.CLIENT)
        private void handleCapability(Entity e, ResourceLocation key, NBTTagCompound data) {
            ISyncable syncable;
            Capability cap = HelperRegistry.getSyncableEntityCaps().get(key);
            if (cap == null && e instanceof EntityPlayer) {
                cap = HelperRegistry.getSyncablePlayerCaps().get(key);
            }
            if (cap == null) {
                VampLib.log.w(TAG, "Capability with key %s is not registered in the HelperRegistry", key);
            }
            try {
                syncable = (ISyncable) e.getCapability(cap, null);
            } catch (ClassCastException ex) {
                VampLib.log.w(TAG, "Target entity's capability %s (%s)does not implement ISyncable (%s)", e.getCapability(cap, null), cap, ex);
                return;
            }
            if (syncable == null) {
                VampLib.log.w(TAG, "Target entity %s does not have capability %s", e, cap);
            } else {
                syncable.loadUpdateFromNBT(data);
            }
        }

    }
}
