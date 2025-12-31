package de.teamlapen.vampirism.common.world.dimensions;

import com.google.common.collect.Lists;
import com.mojang.serialization.Lifecycle;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundUpdateDimensionsPacket;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraDimensionEvent;
import net.minecraft.core.*;
import net.minecraft.core.RegistryAccess.ImmutableRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * from <a href="https://github.com/Commoble/infiniverse/blob/future/src/main/java/net/commoble/infiniverse/internal/DimensionManager.java">...</a>
 */
public class DimensionManager {

    private static final RegistrationInfo DIMENSION_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.stable());
    public static final DimensionManager INSTANCE = new DimensionManager();

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Set<ResourceKey<Level>> VANILLA_LEVELS = Set.of(Level.OVERWORLD, Level.NETHER, Level.END);

    private Set<UnregistrationTicket> levelsPendingUnregistration = new HashSet<>();

    private record UnregistrationTicket(ResourceKey<Level> levelKey, boolean deleteDataFromDisk) {}

    public ServerLevel getOrCreateLevel(final MinecraftServer server, final ResourceKey<Level> levelKey, final Supplier<LevelStem> dimensionFactory)
    {
        // this is marked as deprecated but it's not called from anywhere and I'm not sure how old it is,
        // it's probably left over from forge's previous dimension api
        // in any case we need to get at the server's world field, and if we didn't use this getter,
        // then we'd just end up making a private-field-getter for it ourselves anyway
        @SuppressWarnings("deprecation")
        Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
        @Nullable ServerLevel existingLevel = map.get(levelKey);

        // if the world already exists, return it
        return existingLevel == null
                ? createAndRegisterLevel(server, map, levelKey, dimensionFactory)
                : existingLevel;
    }

    public void markDimensionForUnregistration(final MinecraftServer server, final ResourceKey<Level> levelToRemove) {
        markDimensionForUnregistration(server, levelToRemove, false);
    }

    public void markDimensionForUnregistration(final MinecraftServer server, final ResourceKey<Level> levelToRemove, boolean deleteDataFromDisk)
    {
        if (!VANILLA_LEVELS.contains(levelToRemove))
        {
            ServerLevel level = server.getLevel(levelToRemove);
            if (level != null)
            {
                level.save(null, true, false);
                levelsPendingUnregistration.add(new UnregistrationTicket(levelToRemove, deleteDataFromDisk));
            }
        }
    }

    public Set<ResourceKey<Level>> getLevelsPendingUnregistration()
    {
        return this.levelsPendingUnregistration.stream().map(UnregistrationTicket::levelKey).collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("deprecation") // markWorldsDirty is deprecated, see below
    private static ServerLevel createAndRegisterLevel(final MinecraftServer server, final Map<ResourceKey<Level>, ServerLevel> map, final ResourceKey<Level> levelKey, Supplier<LevelStem> dimensionFactory)
    {
        // get everything we need to create the dimension and the level
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        // dimension keys have a 1:1 relationship with level keys, they have the same IDs as well
        final ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, levelKey.identifier());
        final LevelStem dimension = dimensionFactory.get();

        // the int in create() here is radius of chunks to watch, 11 is what the server uses when it initializes levels
        final Executor executor = server.vampirism$executor();
        final LevelStorageAccess anvilConverter = server.vampirism$storageSource();
        final WorldData worldData = server.getWorldData();
        final DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());

        // now we have everything we need to create the dimension and the level
        // this is the same order server init creates levels:
        // the dimensions are already registered when levels are created, we'll do that first
        // then instantiate level, add border listener, add to map, fire world load event

        // register the actual dimension
        Registry<LevelStem> dimensionRegistry = server.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
        if (dimensionRegistry instanceof MappedRegistry<LevelStem> writableRegistry)
        {
            writableRegistry.unfreeze(false);
            writableRegistry.register(dimensionKey, dimension, DIMENSION_REGISTRATION_INFO);
        }
        else
        {
            throw new IllegalStateException(String.format("Unable to register dimension %s -- dimension registry not writable", dimensionKey.identifier()));
        }

        // create the level instance
        final ServerLevel newLevel = new ServerLevel(
                server,
                executor,
                anvilConverter,
                derivedLevelData,
                levelKey,
                dimension,
                worldData.isDebugWorld(),
                overworld.getSeed(), // don't need to call BiomeManager#obfuscateSeed, overworld seed is already obfuscated
                List.of(), // "special spawn list"
                // phantoms, travelling traders, patrolling/sieging raiders, and cats are overworld special spawns
                // this is always empty for non-overworld dimensions (including json dimensions)
                // these spawners are ticked when the world ticks to do their spawning logic,
                // mods that need "special spawns" for their own dimensions should implement them via tick events or other systems
                false, // "tick time", true for overworld, always false for nether, end, and json dimensions
                overworld.getRandomSequences() // as of 1.21.9 non-overworld levels share the overworld's randomSequences
        );

        newLevel.getWorldBorder().setAbsoluteMaxSize(server.getAbsoluteMaxWorldSize());
        // no, we don't need to remember the worldborder listener to remove it later
        // worldborder listeners are stored in the specific level's savedata
        // so if the level unloads it'll get gc'd with everything else
        server.getPlayerList().addWorldborderListener(newLevel);

        // register level
        map.put(levelKey, newLevel);

        // update forge's world cache so the new level can be ticked
        server.markWorldsDirty();

        // fire world load event
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(newLevel));

        // update clients' dimension lists
        server.getPlayerList().broadcastAll(new ClientboundUpdateDimensionsPacket(Set.of(levelKey), true));

        return newLevel;
    }

    @SuppressWarnings("deprecation")
    private void unregisterScheduledDimensions(final MinecraftServer server)
    {
        if (this.levelsPendingUnregistration.isEmpty())
            return;

        // flush the buffer
        final Set<UnregistrationTicket> keysToRemove = this.levelsPendingUnregistration;
        this.levelsPendingUnregistration = new HashSet<>();

        // we need to remove the dimension/level from three places:
        // the server's dimension/levelstem registry, the server's level registry, and
        // the overworld's border listener
        // the level registry is just a simple map and the border listener has a remove() method
        // the dimension registry has five sub-collections that need to be cleaned up
        // we should also eject players from removed worlds so they don't get stuck there

        final Registry<LevelStem> oldRegistry = server.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
        if (!(oldRegistry instanceof MappedRegistry<LevelStem> oldMappedRegistry))
        {
            LOGGER.warn("Cannot unload dimensions: dimension registry not an instance of MappedRegistry. There may be another mod causing incompatibility with Infiniverse, or Infiniverse may need to be updated for your version of forge/minecraft.");
            return;
        }
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess = server.vampirism$registryAccess();
        RegistryAccess.Frozen composite = layeredRegistryAccess.compositeAccess();
        if (!(composite instanceof ImmutableRegistryAccess immutableRegistryAccess))
        {
            LOGGER.warn("Cannot unload dimensions: composite registry not an instance of ImmutableRegistryAccess. There may be another mod causing incompatibility with Infiniverse, or Infiniverse may be updated for your version of forge/minecraft.");
            return;
        }

        final Set<UnregistrationTicket> removedLevelKeys = new HashSet<>();
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        for (final UnregistrationTicket levelKeyToRemove : keysToRemove)
        {
            final @Nullable ServerLevel levelToRemove = server.getLevel(levelKeyToRemove.levelKey());
            if (levelToRemove == null)
                continue;

            VelmorraDimensionEvent unregisterDimensionEvent = new VelmorraDimensionEvent(levelToRemove);
            NeoForge.EVENT_BUS.post(unregisterDimensionEvent);
            if (unregisterDimensionEvent.isCanceled())
                continue;

            // null if specified level not present
            final @Nullable ServerLevel removedLevel = server.forgeGetWorldMap().remove(levelKeyToRemove.levelKey());

            if (removedLevel != null) // if we removed the key from the map
            {
                // eject players from dead world
                // iterate over a copy as the world will remove players from the original list
                for (final ServerPlayer player : Lists.newArrayList(removedLevel.players()))
                {
                    // send players to their respawn point
                    @Nullable ServerPlayer.RespawnConfig respawnConfig = player.getRespawnConfig();
                    LevelData.RespawnData respawnData = respawnConfig == null ? server.getRespawnData() : respawnConfig.respawnData();
                    ResourceKey<Level> respawnKey = respawnData.dimension();
                    BlockPos destinationPos = respawnData.pos();
                    // if we're removing their respawn world then just send them to the overworld
                    if (keysToRemove.contains(respawnKey))
                    {
                        respawnKey = Level.OVERWORLD;
                        // make sure to wipe the player's respawn point if it was set here
                        if (respawnConfig != null && respawnConfig.respawnData().dimension() == respawnKey)
                        {
                            player.setRespawnPosition(null, false);
                        }
                    }
                    if (respawnKey == null)
                    {
                        respawnKey = Level.OVERWORLD;
                    }

                    @Nullable ServerLevel destinationLevel = server.getLevel(respawnKey);
                    if (destinationLevel == null)
                    {
                        destinationLevel = overworld;
                    }

                    // "respawning" the player via the player list schedules a task in the server to
                    // run after the post-server tick
                    // that causes some minor logspam due to the player's world no longer being
                    // loaded
                    // teleporting the player via a teleport avoids this
                    player.teleportTo(destinationLevel, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), Set.of(), respawnData.pitch(), respawnData.yaw(), false);
                }
                // save the world now or it won't be saved later and data that may be wanted to
                // be kept may be lost
                removedLevel.save(null, false, removedLevel.noSave());

                // fire world unload event -- when the server stops, this would fire after
                // worlds get saved, we'll do that here too
                NeoForge.EVENT_BUS.post(new LevelEvent.Unload(removedLevel));

                // track the removed level
                removedLevelKeys.add(levelKeyToRemove);
            }
        }

        if (!removedLevelKeys.isEmpty())
        {
            // replace the old dimension registry with a new one containing the dimensions
            // that weren't removed, in the same order
            final MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, oldMappedRegistry.registryLifecycle());

            for (final var entry : oldRegistry.entrySet())
            {
                final ResourceKey<LevelStem> oldKey = entry.getKey();
                final ResourceKey<Level> oldLevelKey = ResourceKey.create(Registries.DIMENSION, oldKey.identifier());
                final LevelStem dimension = entry.getValue();
                if (oldKey != null && dimension != null && removedLevelKeys.stream().noneMatch(x -> x.levelKey() == oldLevelKey))
                {
                    newRegistry.register(oldKey, dimension, oldRegistry.registrationInfo(oldKey).orElse(DIMENSION_REGISTRATION_INFO));
                }
            }

            // then replace the old registry with the new registry
            // as of 1.20.1 the dimension registry is stored in the server's layered registryaccess
            // this has several immutable collections of sub-registryaccesses,
            // so we'll need to recreate each of them.

            // Each ServerLevel has a reference to the layered registry access's *composite* registry access
            // so we should edit the internal fields where possible (instead of reconstructing the registry accesses)

            List<RegistryAccess.Frozen> newRegistryAccessList = new ArrayList<>();
            for (RegistryLayer layer : RegistryLayer.values())
            {
                if (layer == RegistryLayer.DIMENSIONS)
                {
                    newRegistryAccessList.add(new ImmutableRegistryAccess(List.of(newRegistry)).freeze());
                }
                else
                {
                    newRegistryAccessList.add(layeredRegistryAccess.getLayer(layer));
                }
            }
            Map<ResourceKey<? extends Registry<?>>, Registry<?>> newRegistryMap = new HashMap<>();
            for (var registryAccess : newRegistryAccessList)
            {
                var registries = registryAccess.registries().toList();
                for (var registryEntry : registries)
                {
                    newRegistryMap.put(registryEntry.key(), registryEntry.value());
                }
            }
            layeredRegistryAccess.vampirism$setValues(List.copyOf(newRegistryAccessList));
            immutableRegistryAccess.vampirism$setRegistries(newRegistryMap);

            // update the server's levels so dead levels don't get ticked
            server.markWorldsDirty();

            // notify client of the removed levels
            server.getPlayerList().broadcastAll(new ClientboundUpdateDimensionsPacket(removedLevelKeys.stream().map(UnregistrationTicket::levelKey).collect(Collectors.toUnmodifiableSet()), false));
        }

        for (UnregistrationTicket ticket : removedLevelKeys) {
            if (ticket.deleteDataFromDisk()) {
                deletePersistentData(server, ticket.levelKey());
            }
        }
    }

    private void deletePersistentData(MinecraftServer server, ResourceKey<Level> level) {
        LevelStorageAccess storageSource = server.vampirism$storageSource();
        Path dimensionPath = storageSource.getDimensionPath(level);

        if (Files.exists(dimensionPath)) {
            try (Stream<Path> files = Files.walk(dimensionPath)) {
                files.filter(x -> !x.endsWith("tmp"))
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                LOGGER.info("Deleted temporary file {}", path);
                            } catch (NoSuchFileException ignored) {
                            } catch (IOException e) {
                                LOGGER.error("Failed to delete file {}", path, e);
                            }
                        });
            } catch (IOException e) {
                LOGGER.error("Failed to delete dimension data for dimension {}", level.identifier(), e);
            }
        }
    }

    @EventBusSubscriber(modid = REFERENCE.MODID)
    private static class ForgeEventHandler
    {
        @SubscribeEvent(priority= EventPriority.LOWEST)
        public static void onServerTick(final ServerTickEvent.Post event)
        {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null)
            {
                DimensionManager.INSTANCE.unregisterScheduledDimensions(server);
            }
        }

        @SubscribeEvent
        public static void onServerStopped(final ServerStoppedEvent event)
        {
            // clear state on server exit (important for singleplayer worlds)
            DimensionManager.INSTANCE.levelsPendingUnregistration = new HashSet<>();
        }
    }
}
