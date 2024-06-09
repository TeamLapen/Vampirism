package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class MinionWorldData extends SavedData {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String ID = "vampirism-minion-data";

    @NotNull
    public static MinionWorldData getData(@NotNull ServerLevel world) {
        return getData(world.getServer());
    }

    @NotNull
    public static MinionWorldData getData(final @NotNull MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new Factory<>(() -> new MinionWorldData(server), (data, provider) -> MinionWorldData.load(server, data, provider)), ID);
    }


    @NotNull
    public static Optional<MinionWorldData> getData(Level world) {
        if (world instanceof ServerLevel) {
            return Optional.of(getData(((ServerLevel) world).getServer()));
        }
        return Optional.empty();
    }

    private final MinecraftServer server;
    private final Object2ObjectOpenHashMap<UUID, PlayerMinionController> controllers = new Object2ObjectOpenHashMap<>();

    public MinionWorldData(MinecraftServer server) {
        this.server = server;
    }

    @Nullable
    public PlayerMinionController getController(UUID lordID) {
        return controllers.get(lordID);
    }

    @NotNull
    public PlayerMinionController getOrCreateController(@NotNull FactionPlayerHandler lord) {
        UUID id = lord.getPlayer().getUUID();
        if (controllers.containsKey(id)) {
            return controllers.get(id);
        } else {
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.setMaxMinions(lord.getFaction(), lord.getMaxMinions());
            controllers.put(id, c);
            return c;
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    /**
     * Only for debugging. Removes controller from saved data not from entities. Reload world afterwards
     */
    @VisibleForDebug
    public void purgeController(UUID lordID) {
        controllers.remove(lordID);
    }

    public static @NotNull MinionWorldData load(@NotNull MinecraftServer server, @NotNull CompoundTag nbt, HolderLookup.Provider provider) {
        MinionWorldData data = new MinionWorldData(server);
        ListTag all = nbt.getList("controllers", 10);
        for (Tag inbt : all) {
            CompoundTag tag = (CompoundTag) inbt;
            UUID id = tag.getUUID("uuid");
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.deserializeNBT(provider, tag);
            data.controllers.put(id, c);
        }
        return data;
    }

    /**
     * Tick server side
     */
    public void tick() {
        controllers.object2ObjectEntrySet().fastForEach(entry -> entry.getValue().tick());
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        ListTag all = new ListTag();
        controllers.object2ObjectEntrySet().fastForEach((entry) -> {
            if (entry.getValue().hasMinions()) {
                CompoundTag tag = entry.getValue().serializeNBT(provider);
                tag.putUUID("uuid", entry.getKey());
                all.add(tag);
            }
        });
        compound.put("controllers", all);
        return compound;
    }

    public Map<UUID, PlayerMinionController> getControllers() {
        return Collections.unmodifiableMap(controllers);
    }
}
