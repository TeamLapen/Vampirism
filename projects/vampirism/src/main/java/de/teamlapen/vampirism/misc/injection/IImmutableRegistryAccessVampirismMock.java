package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IImmutableRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

@Deprecated
public interface IImmutableRegistryAccessVampirismMock extends IImmutableRegistryAccess {

    @Override
    default void vampirism$setRegistries(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
