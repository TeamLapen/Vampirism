package de.teamlapen.vampirism.misc.extension;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public interface IImmutableRegistryAccess {

    void vampirism$setRegistries(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries);
}
