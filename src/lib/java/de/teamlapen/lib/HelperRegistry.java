package de.teamlapen.lib;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import de.teamlapen.lib.util.ThreadSafeLibAPI;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
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

    private static @NotNull Map<ResourceLocation, AttachmentType<IAttachedSyncable>> syncablePlayerCaps = new ConcurrentHashMap<>();
    private static @NotNull Map<ResourceLocation, AttachmentType<IAttachedSyncable>> syncableEntityCaps = new ConcurrentHashMap<>();
    private static @NotNull Set<AttachmentType<IPlayerEventListener>> playerEventListenerCaps = ConcurrentHashMap.newKeySet();
    private static AttachmentType<IPlayerEventListener>[] playerEventListenerCapsFinal;
    /**
     * Stores syncable capabilities for {@link net.minecraft.world.entity.player.Player}
     */
    private static ImmutableMap<ResourceLocation, AttachmentType<IAttachedSyncable>> syncablePlayerCapsFinal;
    /**
     * Stores syncable capabilities for {@link net.minecraft.world.entity.Mob}
     */
    private static ImmutableMap<ResourceLocation, AttachmentType<IAttachedSyncable>> syncableEntityCapsFinal;

    /**
     * Return all player capabilities that should receive events
     * FOR INTERNAL USAGE ONLY
     */
    static @NotNull AttachmentType<IPlayerEventListener>[] getEventListenerCaps() {
        return playerEventListenerCapsFinal;
    }

    /**
     * Return all player capabilities that should be synced
     * FOR INTERNAL USAGE ONLY
     */
    @ApiStatus.Internal
    public static @NotNull ImmutableMap<ResourceLocation, AttachmentType<IAttachedSyncable>> getSyncablePlayerCaps() {
        return syncablePlayerCapsFinal;
    }

    /**
     * Return all entity capabilities that should be synced
     * FOR INTERNAL USAGE ONLY
     */
    @ApiStatus.Internal
    public static @NotNull ImmutableMap<ResourceLocation, AttachmentType<IAttachedSyncable>> getSyncableEntityCaps() {
        return syncableEntityCapsFinal;
    }

    /**
     * Register an entity {@link net.neoforged.neoforge.capabilities.EntityCapability} which instances should be synced on world join
     * Only works for entities extending {@link net.minecraft.world.entity.PathfinderMob}
     *
     * @param clz Class of the object returned, when {@link net.minecraft.world.entity.player.Player#getCapability(net.neoforged.neoforge.capabilities.EntityCapability)} is called on the entity with the given capability
     */
    @ThreadSafeLibAPI
    public static void registerSyncableEntityCapability(AttachmentType<IAttachedSyncable> capability, Class<? extends IAttachedSyncable> clz) {
        if (syncableEntityCaps == Collections.EMPTY_MAP) {
            throw new IllegalStateException("Cannot register syncable entity capability " + clz + "("+ capability + ") after the InterModEnqueueEvent");
        }
        syncableEntityCaps.put(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(capability), capability);
    }

    /**
     * Register a player {@link net.neoforged.neoforge.capabilities.EntityCapability} which instances should be synced on world join
     *
     * @param clz Class of the object returned, when {@link net.minecraft.world.entity.player.Player#getCapability(net.neoforged.neoforge.capabilities.EntityCapability)} is called on the player with the given capability
     *            Has to be called before post init.
     */
    @ThreadSafeLibAPI
    public static void registerSyncablePlayerCapability(AttachmentType<IAttachedSyncable> capability, Class<? extends IAttachedSyncable> clz) {
        if (syncablePlayerCaps == Collections.EMPTY_MAP) {
            throw new IllegalStateException("Cannot register syncable property " + clz + "("+ capability + ") after the InterModEnqueueEvent");
        }
        syncablePlayerCaps.put(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(capability), capability);
    }

    /**
     * Key of a {@link net.neoforged.neoforge.capabilities.EntityCapability} which implementation implements {@link IPlayerEventListener} and which instances should receive the events.
     * Has to be called before post init.
     *
     * @param clz Class of the object returned, when {@link net.minecraft.world.entity.player.Player#getCapability(net.neoforged.neoforge.capabilities.EntityCapability)} is called on the player with the given capability
     */
    @ThreadSafeLibAPI
    public static void registerPlayerEventReceivingCapability(AttachmentType<IPlayerEventListener> capability, Class<? extends IPlayerEventListener> clz) {
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
        playerEventListenerCapsFinal = playerEventListenerCaps.toArray(AttachmentType[]::new);
        playerEventListenerCaps = Collections.emptySet();
    }
}
