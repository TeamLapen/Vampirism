package de.teamlapen.faction.api.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class RegistryProvider<T> implements Supplier<Registry<T>> {

    private final ResourceKey<Registry<T>> key;
    private @Nullable Registry<T> registry;

    public RegistryProvider(ResourceKey<Registry<T>> key) {
        this.key = key;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Registry<T> get() {
        if (this.registry == null) {
            this.registry = Objects.requireNonNull((Registry<T>) BuiltInRegistries.REGISTRY.getValue((ResourceKey) key));
        }
        return this.registry;
    }
}
