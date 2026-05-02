package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IMinecraftServer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.concurrent.Executor;

@Deprecated
public interface IMinecraftServerVampirismMock extends IMinecraftServer {

    @Override
    default Executor vampirism$executor() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default LevelStorageSource.LevelStorageAccess vampirism$storageSource() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default LayeredRegistryAccess<RegistryLayer> vampirism$registryAccess() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
