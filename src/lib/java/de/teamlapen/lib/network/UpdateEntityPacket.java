package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final static Logger LOGGER = LogManager.getLogger();


    static void encode(UpdateEntityPacket msg, PacketBuffer buf) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("id", msg.id);
        if (msg.data != null) {
            tag.put("data", msg.data);
        }
        if (msg.caps != null) {
            tag.put("caps", msg.caps);
        }
        if (msg.playerItself) {
            tag.putBoolean("itself", true);
        }
        buf.writeCompoundTag(tag);
    }

    static UpdateEntityPacket decode(PacketBuffer buf) {
        CompoundNBT tag = buf.readCompoundTag();
        UpdateEntityPacket pkt = new UpdateEntityPacket();
        pkt.id = tag.getInt("id");
        if (tag.contains("data")) {
            pkt.data = tag.getCompound("data");
        }
        if (tag.contains("caps")) {
            pkt.caps = tag.getCompound("caps");
        }
        if (tag.contains("itself")) {
            pkt.playerItself = tag.getBoolean("itself");
        }
        return pkt;
    }

    public static void handle(final UpdateEntityPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> { //Execute on main thread
            VampLib.proxy.handleUpdateEntityPacket(message);
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
        CompoundNBT data = new CompoundNBT();
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
    public static UpdateEntityPacket create(MobEntity entity, ISyncable.ISyncableEntityCapabilityInst... caps) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        UpdateEntityPacket packet = create(caps);
        packet.data = new CompoundNBT();
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
        packet.caps = new CompoundNBT();
        for (ISyncable.ISyncableEntityCapabilityInst cap : caps) {
            CompoundNBT data = new CompoundNBT();
            cap.writeFullUpdateToNBT(data);
            packet.caps.put(cap.getCapKey().toString(), data);
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
    public static UpdateEntityPacket create(ISyncable.ISyncableEntityCapabilityInst cap, CompoundNBT data) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = cap.getTheEntityID();
        packet.caps = new CompoundNBT();
        packet.caps.put(cap.getCapKey().toString(), data);
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
        packet.data = new CompoundNBT();
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
    public static UpdateEntityPacket create(Entity entity, CompoundNBT data) {
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
        final List<ISyncable.ISyncableEntityCapabilityInst> capsToSync = new ArrayList<>();
        Collection<Capability> allCaps = null;
        if (entity instanceof CreatureEntity) {
            allCaps = HelperRegistry.getSyncableEntityCaps().values();
        } else if (entity instanceof PlayerEntity) {
            allCaps = HelperRegistry.getSyncablePlayerCaps().values();

        }
        if (allCaps != null && allCaps.size() > 0) {
            for (Capability cap : allCaps) {
                entity.getCapability(cap, null).ifPresent(inst -> capsToSync.add((ISyncable.ISyncableEntityCapabilityInst) inst));
            }
        }
        if (capsToSync.size() > 0) {
            if (entity instanceof ISyncable) {
                return UpdateEntityPacket.create((MobEntity) entity, capsToSync.toArray(new ISyncable.ISyncableEntityCapabilityInst[0]));
            } else {
                return UpdateEntityPacket.create(capsToSync.toArray(new ISyncable.ISyncableEntityCapabilityInst[0]));
            }
        } else if (entity instanceof ISyncable) {
            return UpdateEntityPacket.create(entity);
        } else {
            LOGGER.warn("There is nothing to update for entity {}", entity);
            return null;
        }


    }

    private int id;
    private CompoundNBT data;
    private CompoundNBT caps;
    private boolean playerItself = false;

    /**
     * Dont use
     */
    public UpdateEntityPacket() {

    }

    public CompoundNBT getCaps() {
        return caps;
    }

    public CompoundNBT getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    public boolean isPlayerItself() {
        return playerItself;
    }

    public UpdateEntityPacket markAsPlayerItself() {
        playerItself = true;
        return this;
    }

}
