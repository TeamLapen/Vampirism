package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class MinionWorldData extends WorldSavedData {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String ID = "vampirism-minion-data";

    @Nonnull
    public static MinionWorldData getData(ServerWorld world) {
        return getData(world.getServer());
    }

    @Nonnull
    public static MinionWorldData getData(final MinecraftServer server) {
        return server.getLevel(World.OVERWORLD).getDataStorage().computeIfAbsent(() -> new MinionWorldData(server), ID);
    }


    @Nonnull
    public static Optional<MinionWorldData> getData(World world) {
        if (world instanceof ServerWorld) {
            return Optional.of(getData(((ServerWorld) world).getWorldServer()));
        }
        return Optional.empty();
    }

    private final MinecraftServer server;
    private final Object2ObjectOpenHashMap<UUID, PlayerMinionController> controllers = new Object2ObjectOpenHashMap<>();

    public MinionWorldData(MinecraftServer server) {
        super(ID);
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

    @Override
    public void load(CompoundNBT nbt) {

        controllers.clear();
        ListNBT all = nbt.getList("controllers", 10);
        for (INBT inbt : all) {
            CompoundNBT tag = (CompoundNBT) inbt;
            UUID id = tag.getUUID("uuid");
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.deserializeNBT(tag);
            controllers.put(id, c);
        }
    }

    /**
     * Tick server side
     */
    public void tick() {
        controllers.object2ObjectEntrySet().fastForEach(entry -> entry.getValue().tick());
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT all = new ListNBT();
        controllers.object2ObjectEntrySet().fastForEach((entry) -> {
            if (entry.getValue().hasMinions()) {
                CompoundNBT tag = entry.getValue().serializeNBT();
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
