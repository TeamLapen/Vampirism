package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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


    static void encode(UpdateEntityPacket msg, FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
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
        buf.writeNbt(tag);
    }

    static UpdateEntityPacket decode(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
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
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableEntityCapabilityInst cap) {
        CompoundTag data = new CompoundTag();
        cap.writeFullUpdateToNBT(data);
        return create(cap, data);
    }

    /**
     * Create one sync packet for the given syncable entity containing firstly the data from it's {@link ISyncable} implementations and secondly all given capability instances
     *
     * @param entity EntityLiving which implements ISyncable
     * @param caps   Have to belong to the given entity
     */
    public static UpdateEntityPacket create(Mob entity, ISyncable.ISyncableEntityCapabilityInst... caps) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        UpdateEntityPacket packet = create(caps);
        packet.data = new CompoundTag();
        ((ISyncable) entity).writeFullUpdateToNBT(packet.data);
        return packet;
    }

    /**
     * Create one sync packet for all given capability instances.
     *
     * @param caps Have to belong to the same entity
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableEntityCapabilityInst... caps) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = caps[0].getTheEntityID();
        packet.caps = new CompoundTag();
        for (ISyncable.ISyncableEntityCapabilityInst cap : caps) {
            CompoundTag data = new CompoundTag();
            cap.writeFullUpdateToNBT(data);
            packet.caps.put(cap.getCapKey().toString(), data);
        }
        return packet;
    }

    /**
     * Create a sync packet for the given capability instance containing the given data
     *
     * @param data Should be loadable by the capability instance
     */
    public static UpdateEntityPacket create(ISyncable.ISyncableEntityCapabilityInst cap, CompoundTag data) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = cap.getTheEntityID();
        packet.caps = new CompoundTag();
        packet.caps.put(cap.getCapKey().toString(), data);
        return packet;
    }

    /**
     * Create a sync packet for the given syncable entity containing the data from it's ISyncable implementation
     *
     * @param entity Has to implement ISyncable
     */
    public static UpdateEntityPacket create(Entity entity) {
        if (!(entity instanceof ISyncable)) {
            throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
        }
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = entity.getId();
        packet.data = new CompoundTag();
        ((ISyncable) entity).writeFullUpdateToNBT(packet.data);
        return packet;
    }

    /**
     * Create a sync packet for the given syncable entity containing the data given data
     *
     * @param entity Has to implement ISyncable
     * @param data   Should be loadable by the entity
     */
    public static <T extends Entity & ISyncable> UpdateEntityPacket create(T entity, CompoundTag data) {
        UpdateEntityPacket packet = new UpdateEntityPacket();
        packet.id = entity.getId();
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
        Collection<Capability<ISyncable.ISyncableEntityCapabilityInst>> allCaps = null;
        if (entity instanceof PathfinderMob) {
            allCaps = HelperRegistry.getSyncableEntityCaps().values();
        } else if (entity instanceof Player) {
            allCaps = HelperRegistry.getSyncablePlayerCaps().values();

        }
        if (allCaps != null && allCaps.size() > 0) {
            for (Capability<ISyncable.ISyncableEntityCapabilityInst> cap : allCaps) {
                entity.getCapability(cap, null).ifPresent(capsToSync::add);
            }
        }
        if (capsToSync.size() > 0) {
            if (entity instanceof ISyncable) {
                return UpdateEntityPacket.create((Mob) entity, capsToSync.toArray(new ISyncable.ISyncableEntityCapabilityInst[0]));
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
    private CompoundTag data;
    private CompoundTag caps;
    private boolean playerItself = false;

    /**
     * Don't use
     */
    public UpdateEntityPacket() {

    }

    public CompoundTag getCaps() {
        return caps;
    }

    public CompoundTag getData() {
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
