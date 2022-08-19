package de.teamlapen.lib;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.util.ThreadSafeLibAPI;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Register things that should be handled by the library here
 */
public class HelperRegistry {

    private static @NotNull Map<ResourceLocation, Capability<ISyncable.ISyncableEntityCapabilityInst>> syncablePlayerCaps = new ConcurrentHashMap<>();
    private static @NotNull Map<ResourceLocation, Capability<ISyncable.ISyncableEntityCapabilityInst>> syncableEntityCaps = new ConcurrentHashMap<>();
    private static @NotNull Set<Capability<IPlayerEventListener>> playerEventListenerCaps = ConcurrentHashMap.newKeySet();
    private static Capability<IPlayerEventListener>[] playerEventListenerCapsFinal;
    /**
     * Stores syncable capabilities for {@link net.minecraft.world.entity.player.Player}
     */
    private static ImmutableMap<ResourceLocation, Capability<ISyncable.ISyncableEntityCapabilityInst>> syncablePlayerCapsFinal;
    /**
     * Stores syncable capabilities for {@link net.minecraft.world.entity.Mob}
     */
    private static ImmutableMap<ResourceLocation, Capability<ISyncable.ISyncableEntityCapabilityInst>> syncableEntityCapsFinal;

    /**
     * Return all player capabilities that should receive events
     * FOR INTERNAL USAGE ONLY
     */
    static @NotNull Capability<IPlayerEventListener>[] getEventListenerCaps() {
        return playerEventListenerCapsFinal;
    }

    /**
     * Return all player capabilities that should be synced
     * FOR INTERNAL USAGE ONLY
     */
    @ApiStatus.Internal
    public static @NotNull ImmutableMap<ResourceLocation, Capability<ISyncable.ISyncableEntityCapabilityInst>> getSyncablePlayerCaps() {
        return syncablePlayerCapsFinal;
    }

    /**
     * Return all entity capabilities that should be synced
     * FOR INTERNAL USAGE ONLY
     */
    @ApiStatus.Internal
    public static @NotNull ImmutableMap<ResourceLocation, Capability<ISyncable.ISyncableEntityCapabilityInst>> getSyncableEntityCaps() {
        return syncableEntityCapsFinal;
    }

    /**
     * Register an entity {@link Capability} which instances should be synced on world join
     * Only works for entities extending {@link net.minecraft.world.entity.PathfinderMob}
     *
     * @param clz Class of the object returned, when {@link net.minecraft.world.entity.player.Player#getCapability(Capability, Direction)} is called on the entity with the given capability
     * @param key Unique key for the capability. Preferably the key the cap was registered with.
     *            Has to be called before post init.
     */
    @ThreadSafeLibAPI
    public static void registerSyncableEntityCapability(Capability<ISyncable.ISyncableEntityCapabilityInst> capability, ResourceLocation key, Class<? extends ISyncable.ISyncableEntityCapabilityInst> clz) {
        if (syncableEntityCaps == Collections.EMPTY_MAP) {
            throw new IllegalStateException("Cannot register syncable entity capability " + clz + "("+ capability + ") after the InterModEnqueueEvent");
        }
        syncableEntityCaps.put(key, capability);
    }

    /**
     * Register a player {@link Capability} which instances should be synced on world join
     *
     * @param key Unique key for the capability. Preferably the key the cap was registered with.
     * @param clz Class of the object returned, when {@link net.minecraft.world.entity.player.Player#getCapability(Capability, Direction)} is called on the player with the given capability
     *            Has to be called before post init.
     */
    @ThreadSafeLibAPI
    public static void registerSyncablePlayerCapability(Capability<ISyncable.ISyncableEntityCapabilityInst> capability, ResourceLocation key, Class<? extends ISyncable.ISyncableEntityCapabilityInst> clz) {
        if (syncablePlayerCaps == Collections.EMPTY_MAP) {
            throw new IllegalStateException("Cannot register syncable property " + clz + "("+ capability + ") after the InterModEnqueueEvent");
        }
        syncablePlayerCaps.put(key, capability);
    }

    /**
     * Key of a {@link Capability} which implementation implements {@link IPlayerEventListener} and which instances should receive the events.
     * Has to be called before post init.
     *
     * @param clz Class of the object returned, when {@link net.minecraft.world.entity.player.Player#getCapability(Capability, Direction)} is called on the player with the given capability
     */
    @ThreadSafeLibAPI
    public static void registerPlayerEventReceivingCapability(Capability<IPlayerEventListener> capability, Class<? extends IPlayerEventListener> clz) {
        if (playerEventListenerCaps ==  Collections.EMPTY_SET) {
            throw new IllegalStateException("Cannot register PlayerEventReceiver ("+ capability + ") after the InterModEnqueueEvent");
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
        syncableEntityCaps = Collections.emptyMap();
        syncablePlayerCapsFinal = ImmutableMap.copyOf(syncablePlayerCaps);
        syncablePlayerCaps = Collections.emptyMap();
        //noinspection unchecked
        playerEventListenerCapsFinal = playerEventListenerCaps.toArray((Capability<IPlayerEventListener>[]) new Capability[0]);
        playerEventListenerCaps = Collections.emptySet();
    }
}
