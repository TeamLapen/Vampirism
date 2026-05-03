package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IMinecraftServer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor extends IMinecraftServer {

    @Accessor("executor")
    @Override
    Executor vampirism$executor();

    @Accessor("storageSource")
    @Override
    LevelStorageSource.LevelStorageAccess vampirism$storageSource();

    @Accessor("registries")
    @Override
    LayeredRegistryAccess<RegistryLayer> vampirism$registryAccess();
}
