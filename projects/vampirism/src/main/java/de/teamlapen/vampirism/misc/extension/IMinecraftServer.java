package de.teamlapen.vampirism.misc.extension;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.concurrent.Executor;

public interface IMinecraftServer {

    Executor vampirism$executor();

    LevelStorageSource.LevelStorageAccess vampirism$storageSource();

    LayeredRegistryAccess<RegistryLayer> vampirism$registryAccess();
}
