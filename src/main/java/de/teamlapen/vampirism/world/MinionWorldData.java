package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


public class MinionWorldData extends SavedData {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String ID = "vampirism-minion-data";

    @Nonnull
    public static MinionWorldData getData(ServerLevel world) {
        return getData(world.getServer());
    }

    /**
     * only call if server is running, not during startup
     */
    @Nonnull
    public static MinionWorldData getData(final MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent((data) -> MinionWorldData.load(server, data), () -> new MinionWorldData(server), ID);
    }

    @Nonnull
    public static Optional<MinionWorldData> getDataSafe(final MinecraftServer server) {
        return Optional.ofNullable(server.getLevel(Level.OVERWORLD)).map(level -> level.getDataStorage().computeIfAbsent((data) -> MinionWorldData.load(server, data), () -> new MinionWorldData(server), ID));
    }


    @Nonnull
    public static Optional<MinionWorldData> getData(Level world) {
        if (world instanceof ServerLevel) {
            return Optional.of(getData(((ServerLevel) world).getServer()));
        }
        return Optional.empty();
    }

    private final MinecraftServer server;
    private final Object2ObjectOpenHashMap<UUID, PlayerMinionController> controllers = new Object2ObjectOpenHashMap<>();

    public MinionWorldData(MinecraftServer server) {
        super();
        this.server = server;
    }

    @Nullable
    public PlayerMinionController getController(UUID lordID) {
        return controllers.get(lordID);
    }

    @Nonnull
    public PlayerMinionController getOrCreateController(FactionPlayerHandler lord) {
        UUID id = lord.getPlayer().getUUID();
        if (controllers.containsKey(id)) {
            return controllers.get(id);
        } else {
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.setMaxMinions(lord.getCurrentFaction(), lord.getMaxMinions());
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
    @Deprecated
    public void purgeController(UUID lordID) {
        controllers.remove(lordID);
    }

    public static MinionWorldData load(MinecraftServer server, CompoundTag nbt) {
        MinionWorldData data = new MinionWorldData(server);
        ListTag all = nbt.getList("controllers", 10);
        for (Tag inbt : all) {
            CompoundTag tag = (CompoundTag) inbt;
            UUID id = tag.getUUID("uuid");
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.deserializeNBT(tag);
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

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag all = new ListTag();
        controllers.object2ObjectEntrySet().fastForEach((entry) -> {
            if (entry.getValue().hasMinions()) {
                CompoundTag tag = entry.getValue().serializeNBT();
                tag.putUUID("uuid", entry.getKey());
                all.add(tag);
            }
        });
        compound.put("controllers", all);
        return compound;
    }
}
