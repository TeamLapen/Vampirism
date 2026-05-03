package de.teamlapen.faction.api.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class RegistryProvider<T> implements Supplier<Registry<T>> {

    private static final Map<ResourceKey<? extends Registry<?>>, Registry<?>> registryMap = new HashMap<>();

    private final ResourceKey<Registry<T>> key;
    private @Nullable Registry<T> registry;

    public RegistryProvider(ResourceKey<Registry<T>> key) {
        this.key = key;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Registry<T> get() {
        if (this.registry == null) {
            this.registry = Objects.requireNonNull((Registry<T>) registryMap.get(key), () -> "Registry " + key + " is not registered to this provider. Make sure that is get registered before accessing it.");
        }
        return this.registry;
    }

    public static void register(Registry<?>... registries) {
        for (Registry<?> registry : registries) {
            registryMap.put(registry.key(), registry);
        }
    }
}
