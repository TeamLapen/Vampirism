package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Does Entity or Entity capability updates.
 * Entity capabilities that want to use this, have to be registered in {@link HelperRegistry}
 */
public class UpdateEntityPacket implements IMessage {

    private final static String TAG = "UpdateEntityPacket";


    static void encode(UpdateEntityPacket msg, PacketBuffer buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInt("id", msg.id);
        if (msg.data != null) {
            tag.setTag("data", msg.data);
        }
        if (msg.caps != null) {
            tag.setTag("caps", msg.caps);
        }
        if (msg.playerItself) {
            tag.setBoolean("itself", true);
        }
        buf.writeCompoundTag(tag);
    }

    static UpdateEntityPacket decode(PacketBuffer buf) {
        NBTTagCompound tag = buf.readCompoundTag();
        UpdateEntityPacket pkt = new UpdateEntityPacket();
        pkt.id = tag.getInt("id");
        if (tag.hasKey("data")) {
            pkt.data = tag.getCompound("data");
        }
        if (tag.hasKey("caps")) {
            pkt.caps = tag.getCompound("caps");
        }
        if (tag.hasKey("itself")) {
            pkt.playerItself = tag.getBoolean("itself");
        }
        return new UpdateEntityPacket();
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleCapability(Entity e, ResourceLocation key, NBTTagCompound data) {
        ISyncable syncable;
        Capability cap = HelperRegistry.getSyncableEntityCaps().get(key);
        if (cap == null && e instanceof EntityPlayer) {
            cap = HelperRegistry.getSyncablePlayerCaps().get(key);
        }
        if (cap == null) {
            LOGGER.warn("Capability with key %s is not registered in the HelperRegistry", key);
        }
        try {
            syncable = (ISyncable) e.getCapability(cap, null);
        } catch (ClassCastException ex) {
            LOGGER.warn("Target entity's capability %s (%s)does not implement ISyncable (%s)", e.getCapability(cap, null), cap, ex);
            return;
        }
        if (syncable == null) {
            LOGGER.warn("Target entity %s does not have capability %s", e, cap);
        } else {
            syncable.loadUpdateFromNBT(data);
        }
    }

    public static void handle(final UpdateEntityPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        EntityPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> { //Execute on main thread
            Entity e = player.getEntityWorld().getEntityByID(message.id);
            if (e == null) {
                LOGGER.error("Did not find entity %s", message.id);
                if (message.playerItself) {
                    LOGGER.error("Message is meant for player itself, but id mismatch %s %s. Loading anyway.", player.getEntityId(), message.id);
                    e = player;
                }
            }
            if (e != null) {
                if (message.data != null) {
                    ISyncable syncable;
                    try {
                        syncable = (ISyncable) e;
                        syncable.loadUpdateFromNBT(message.data);

                    } catch (ClassCastException ex) {
                        LOGGER.warn("Target entity %s does not implement ISyncable (%s)", e, ex);
                    }
                }
                if (message.caps != null) {

                    for (String key : message.caps.keySet()) {
                        handleCapability(e, new ResourceLocation(key), message.caps.getCompound(key));
                    }


                }
            }

        });
        ctx.setPacketHandled(true);
    }

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
        for (ISyncable.ISyncableEntityCapabilityInst cap : caps) {
            NBTTagCompound data = new NBTTagCompound();
            cap.writeFullUpdateToNBT(data);
            packet.caps.setTag(cap.getCapKey().toString(), data);
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

    public UpdateEntityPacket markAsPlayerItself() {
        playerItself = true;
        return this;
    }


}
