package de.teamlapen.vampirism.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class ForestBossManager extends WorldSavedData {

    private final static Logger LOGGER = LogManager.getLogger();
    private final static String ID = "vampirism-forest-boss-data";

    @Nonnull
    public static ForestBossManager getData(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(() -> new ForestBossManager(world), ID);
    }

    private final ServerWorld world;

    public ForestBossManager(ServerWorld world) {
        super(ID);
        this.world = world;
    }

    @Override
    public void load(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        return nbt;
    }
}
