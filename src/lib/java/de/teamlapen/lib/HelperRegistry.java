package de.teamlapen.lib;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.util.ThreadSafeLibAPI;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Register things that should be handled by the library here
 */
public class HelperRegistry {
    private final static Logger LOGGER = LogManager.getLogger();

    private final static String TAG = "HelperRegistry";
    private static Map<ResourceLocation, Capability> syncablePlayerCaps = new ConcurrentHashMap<>();
    private static Map<ResourceLocation, Capability> syncableEntityCaps = new ConcurrentHashMap<>();
    private static Set<Capability> playerEventListenerCaps = new ConcurrentSet<>();
    private static Capability[] playerEventListenerCapsFinal;
    /**
     * Stores syncable capabilities for {@link EntityPlayer}
     */
    private static ImmutableMap<ResourceLocation, Capability> syncablePlayerCapsFinal;
    /**
     * Stores syncable capabilities for {@link EntityLiving}
     */
    private static ImmutableMap<ResourceLocation, Capability> syncableEntityCapsFinal;

    /**
     * Return all player capabilities that should receive events
     * FOR INTERNAL USAGE ONLY
     */
    static Capability[] getEventListenerCaps() {
        return playerEventListenerCapsFinal;
    }

    /**
     * Return all player capabilities that should be synced
     * FOR INTERNAL USAGE ONLY
     */
    public static ImmutableMap<ResourceLocation, Capability> getSyncablePlayerCaps() {
        return syncablePlayerCapsFinal;
    }

    /**
     * Return all entity capabilities that should be synced
     * FOR INTERNAL USAGE ONLY
     */
    public static ImmutableMap<ResourceLocation, Capability> getSyncableEntityCaps() {
        return syncableEntityCapsFinal;
    }

    /**
     * Register a entity {@link Capability} which instances should be synced on world join
     * Only works for entities extending {@link net.minecraft.entity.EntityCreature}
     *
     * @param clz Class of the object returned, when {@link EntityPlayer#getCapability(Capability, EnumFacing)} is called on the entity with the given capability
     * @param key Unique key for the capability. Preferably the key the cap was registered with.
     *            Has to be called before post init.
     */
    @ThreadSafeLibAPI
    public static void registerSyncableEntityCapability(Capability capability, ResourceLocation key, Class<? extends ISyncable.ISyncableEntityCapabilityInst> clz) {
        if (syncableEntityCaps == null) {
            LOGGER.error("You have to register the syncable property {} ({}) during InterModEnqueueEvent", clz, capability);
            return;
        }
        syncableEntityCaps.put(key, capability);
    }

    /**
     * Register a player {@link Capability} which instances should be synced on world join
     *
     * @param key Unique key for the capability. Preferably the key the cap was registered with.
     * @param clz Class of the object returned, when {@link EntityPlayer#getCapability(Capability, EnumFacing)} is called on the player with the given capability
     *            Has to be called before post init.
     */
    @ThreadSafeLibAPI
    public static void registerSyncablePlayerCapability(Capability capability, ResourceLocation key, Class<? extends ISyncable.ISyncableEntityCapabilityInst> clz) {
        if (syncablePlayerCaps == null) {
            LOGGER.error("You have to register the syncable property {} ({}) before post init", clz, capability);
            return;
        }
        syncablePlayerCaps.put(key, capability);
    }

    /**
     * Key of a {@link Capability} which implementation implements {@link IPlayerEventListener} and which instances should receive the events.
     * Has to be called before post init.
     *
     * @param capability
     * @param clz        Class of the object returned, when {@link EntityPlayer#getCapability(Capability, EnumFacing)} is called on the player with the given capability
     */
    @ThreadSafeLibAPI
    public static void registerPlayerEventReceivingCapability(Capability capability, Class<? extends IPlayerEventListener> clz) {
        if (playerEventListenerCaps == null) {
            LOGGER.error("You have to register PlayerEventReceiver BEFORE post init. (" + capability + ")");
        } else {
            playerEventListenerCaps.add(capability);
        }
    }

    /**
     * Finishes registration.
     * FOR INTERNAL USAGE ONLY
     */
    static void finish() {
        syncableEntityCapsFinal = ImmutableMap.copyOf(syncableEntityCaps);
        syncableEntityCaps = null;
        syncablePlayerCapsFinal = ImmutableMap.copyOf(syncablePlayerCaps);
        syncablePlayerCaps = null;
        playerEventListenerCapsFinal = playerEventListenerCaps.toArray(new Capability[0]);
        playerEventListenerCaps = null;
    }
}
