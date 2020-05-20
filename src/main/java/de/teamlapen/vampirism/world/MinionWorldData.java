package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        return server.getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(() -> new MinionWorldData(server), ID);
    }

    @Nullable
    public static MinionWorldData getData(World world) {
        if (world instanceof ServerWorld) {
            return getData(((ServerWorld) world).getWorldServer());
        }
        return null;
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
        UUID id = lord.getPlayer().getUniqueID();
        if (controllers.containsKey(id)) {
            return controllers.get(id);
        } else {
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.setMaxMinions(lord.getMaxMinions());
            controllers.put(id, c);
            return c;
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void read(CompoundNBT nbt) {
        LOGGER.info("Deserializing");

        controllers.clear();
        ListNBT all = nbt.getList("controllers", 10);
        for (INBT inbt : all) {
            CompoundNBT tag = (CompoundNBT) inbt;
            UUID id = tag.getUniqueId("uuid");
            PlayerMinionController c = new PlayerMinionController(server, id);
            c.deserializeNBT(tag);
            controllers.put(id, c);
        }
    }

    public void tick() {
        controllers.object2ObjectEntrySet().fastForEach(entry -> entry.getValue().tick());
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        LOGGER.info("Serializing");
        ListNBT all = new ListNBT();
        controllers.object2ObjectEntrySet().fastForEach((entry) -> {
            if (entry.getValue().hasMinions()) {
                CompoundNBT tag = entry.getValue().serializeNBT();
                tag.putUniqueId("uuid", entry.getKey());
                all.add(tag);
            }
        });
        compound.put("controllers", all);
        return compound;
    }
}
