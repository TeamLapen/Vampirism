package de.teamlapen.sync;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.teamlapen.lib.util.ThreadSafeLibAPI;
import de.teamlapen.sync.common.entities.IPlayerEventListener;
import de.teamlapen.sync.common.storage.IAttachedSyncable;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Register things that should be handled by the library here
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class SyncRegistry {

    private static @NotNull Map<ResourceLocation, AttachmentType<IAttachedSyncable>> syncablePlayerCaps = new ConcurrentHashMap<>();
    private static @NotNull Map<ResourceLocation, AttachmentType<IAttachedSyncable>> syncableEntityCaps = new ConcurrentHashMap<>();
    private static @NotNull Set<AttachmentType<IPlayerEventListener>> playerEventListenerCaps = ConcurrentHashMap.newKeySet();


    private static ImmutableList<AttachmentType<IPlayerEventListener>> playerEventListenerCapsFinal = ImmutableList.of();
    private static ImmutableMap<ResourceLocation, AttachmentType<IAttachedSyncable>> syncablePlayerCapsFinal = ImmutableMap.of();
    private static ImmutableMap<ResourceLocation, AttachmentType<IAttachedSyncable>> syncableEntityCapsFinal = ImmutableMap.of();

    /**
     * Return all player capabilities that should receive events
     */
    @ApiStatus.Internal
    @Unmodifiable
    public static @NotNull List<AttachmentType<IPlayerEventListener>> getEventListenerCaps() {
        return playerEventListenerCapsFinal;
    }

    /**
     * Return all player capabilities that should be synced
     */
    @ApiStatus.Internal
    public static @NotNull ImmutableMap<ResourceLocation, AttachmentType<IAttachedSyncable>> getSyncablePlayerCaps() {
        return syncablePlayerCapsFinal;
    }

    /**
     * Return all entity capabilities that should be synced
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
            throw new IllegalStateException("Cannot register syncable entity capability " + clz + "(" + capability + ") after the InterModEnqueueEvent");
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
            throw new IllegalStateException("Cannot register syncable property " + clz + "(" + capability + ") after the InterModEnqueueEvent");
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
        if (playerEventListenerCaps == Collections.EMPTY_SET) {
            throw new IllegalStateException("Cannot register PlayerEventReceiver (" + capability + ") after the InterModEnqueueEvent");
        } else {
            playerEventListenerCaps.add(capability);
        }
    }

    @SubscribeEvent
    public static void onRegistered(InterModProcessEvent event) {
        finish();
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
        playerEventListenerCapsFinal = ImmutableList.copyOf(playerEventListenerCaps);
        playerEventListenerCaps = Collections.emptySet();
    }
}
