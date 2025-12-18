package de.teamlapen.sync.properties;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import de.teamlapen.sync.PropertySync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OptionalDeferredProperty<T extends PropertySync> extends Property {

    private final Supplier<Optional<T>> property;
    private final Supplier<T> factory;
    private final Consumer<Optional<T>> onSet;

    public OptionalDeferredProperty(ResourceLocation key, Supplier<Optional<T>> property, Supplier<T> factory, Consumer<Optional<T>> onSet) {
        super(key);
        this.property = property;
        this.factory = factory;
        this.onSet = onSet;
    }

    @Override
    public boolean hasClientSync() {
        return true;
    }

    @Override
    public boolean hasServerLoad() {
        return false;
    }

    @Override
    public int storeValue(ValueOutput output, StoreMode mode) {
        ValueOutput main = output.child(this.key.toString());
        var sub = property.get();
        if (sub.isPresent()) {
            main.store("present", Codec.BOOL, true);
            T value = sub.get();
            switch (mode) {
                case FULL_UPDATE -> value.serializeFullUpdate(main.child("optional"));
                case UPDATE -> value.serializeUpdate(main.child("optional"));
                default -> throw new IllegalStateException("Update type is not supported: " + mode);
            }
            return value.hashCode();
        } else {
            main.store("present", Codec.BOOL, false);
            return 0;
        }
    }

    @Override
    public boolean loadClient(ValueInput input) {
        return input.child(this.key.toString()).filter(x -> x.read("present", Codec.BOOL).map(present -> {
            if (present) {
                T t = property.get().orElse(null);
                if (t == null) {
                    t = factory.get();
                    onSet.accept(Optional.of(t));
                }
                return t.deserializeUpdate(x.childOrEmpty("optional"));
            } else {
                onSet.accept(Optional.empty());
                return true;
            }
        }).orElse(false)).isPresent();
    }

    @Override
    public void loadServer(ValueInput input) {
        throw new UnsupportedOperationException("Server side loading is not supported");
    }

    @Override
    public int getStatus() {
        return this.property.get().map(PropertySync::getStatus).orElse(0);
    }

    public static class Builder<T extends PropertySync> {

        private final PropertyBuilder propertySync;
        private final Supplier<Optional<T>> property;
        private @Nullable Supplier<T> factory;
        private @Nullable Consumer<Optional<T>> consumer;

        public Builder(PropertyBuilder propertySync, Supplier<Optional<T>> property) {
            this.propertySync = propertySync;
            this.property = property;
        }

        public Builder<T> withFactory(Supplier<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> withNewInstanceSetter(Consumer<Optional<T>> consumer) {
            this.consumer = consumer;
            return this;
        }

        public void register() {
            Preconditions.checkNotNull(this.factory, "Factory must be set");
            Preconditions.checkNotNull(this.consumer, "Consumer must be set");
            this.propertySync.register(key -> new OptionalDeferredProperty<>(key, this.property, this.factory, this.consumer));
        }
    }
}
