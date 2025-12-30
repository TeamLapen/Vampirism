package de.teamlapen.sync.properties;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleProperty<T> extends Property {

    private final Codec<T> codec;
    private final Supplier<T> defaultValue;
    private final Supplier<T> valueProvider;
    private final DataApply<T> valueSetter;

    public SimpleProperty(Identifier key, Codec<T> codec, Supplier<T> defaultValue, Supplier<T> valueProvider, DataApply<T> valueSetter) {
        super(key);
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.valueProvider = valueProvider;
        this.valueSetter = valueSetter;
    }

    @Override
    public boolean hasClientSync() {
        return this.valueSetter.canLoadClient();
    }

    @Override
    public boolean hasServerLoad() {
        return this.valueSetter.canLoadServer();
    }

    @Override
    public int getStatus() {
        return this.valueProvider.get().hashCode();
    }

    public int storeValue(ValueOutput output, StoreMode mode) {
        T t = this.valueProvider.get();
        output.store(this.key.toString(), this.codec, t);

        return t.hashCode();
    }

    public boolean loadClient(ValueInput input) {
        if (!this.valueSetter.canLoadClient()) return false;

        return input.read(this.key.toString(), this.codec).map(valueSetter::loadClient).orElse(false);
    }

    public void loadServer(ValueInput input) {
        if (!this.valueSetter.canLoadServer()) return;

        var value = input.read(this.key.toString(), this.codec).orElseGet(this.defaultValue);
        this.valueSetter.loadServer(value);
    }

    public static class Builder<T> {

        private final PropertyBuilder baseBuilder;
        private final Codec<T> codec;
        @Nullable
        private Supplier<T> defaultValue;
        @Nullable
        private Supplier<T> supplier;
        @Nullable
        private Consumer<T> serverLoader;
        @Nullable
        private Function<T, Boolean> clientLoader;
        @Nullable
        private Consumer<T> clientLoaderSimple;
        @Nullable
        private Comparator<T> comparator;

        public Builder(PropertyBuilder baseBuilder, Codec<T> codec) {
            this.baseBuilder = baseBuilder;
            this.codec = codec;
        }

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = () -> defaultValue;
            return this;
        }

        public Builder<T> defaultValue(Supplier<T> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> provider(Supplier<T> supplier) {
            this.supplier = supplier;
            return this;
        }

        public Builder<T> serverLoader(Consumer<T> loader) {
            this.serverLoader = loader;
            return this;
        }

        public Builder<T> clientLoader(Function<T, Boolean> loader) {
            this.clientLoader = loader;
            return this;
        }

        public Builder<T> commonLoader(Function<T, Boolean> loader) {
            this.serverLoader = loader::apply;
            this.clientLoader = loader;
            return this;
        }

        public Builder<T> commonLoader(Consumer<T> loader, Comparator<T> comparator) {
            this.serverLoader = loader;
            this.clientLoaderSimple = loader;
            this.comparator = comparator;
            return this;
        }

        public Builder<T> comparator(Comparator<T> comparator) {
            this.comparator = comparator;
            return this;
        }

        public void register() {
            Preconditions.checkArgument( this.defaultValue != null, "Default value must be set");
            Preconditions.checkArgument( this.supplier != null, "Supplier must be set");
            Preconditions.checkArgument( this.serverLoader != null ||  this.clientLoader != null, "Server or client loaders must be set");
            Preconditions.checkArgument(this.clientLoaderSimple == null || this.clientLoader == null, "Only one of clientLoader or clientLoaderSimple can be set");
            Preconditions.checkArgument(this.clientLoader != null || (this.clientLoaderSimple == null || this.comparator != null), "Comparator must be set if clientLoaderSimple is set");

            if (this.clientLoader != null && this.comparator != null) {
                this.clientLoader = value -> {
                    T t = this.supplier.get();
                    this.clientLoader.apply(value);
                    return comparator.compare(t, value) != 0;
                };
            }

            if (this.clientLoaderSimple != null) {
                this.clientLoader = value -> {
                    T t = this.supplier.get();
                    this.clientLoaderSimple.accept(value);
                    return comparator.compare(t, value) != 0;
                };
            }

            this.baseBuilder.register(key -> new SimpleProperty<>(key, this.codec, this.defaultValue, this.supplier, DataApply.create(serverLoader, clientLoader)));
        }


    }

    public static class NullableBuilder<T> {

        private final Builder<Optional<T>> innerBuilder;

        public NullableBuilder(PropertyBuilder baseBuilder, Codec<@Nullable T> codec) {
            this.innerBuilder = new Builder<>(baseBuilder,  Codec.optionalField("value", codec, true).codec()).defaultValue(Optional.empty());
        }

        public NullableBuilder<T> defaultValue(@Nullable T defaultValue) {
            this.innerBuilder.defaultValue(Optional.ofNullable(defaultValue));
            return this;
        }

        public NullableBuilder<T> defaultValue(Supplier<@Nullable T> defaultValue) {
            this.innerBuilder.defaultValue(() -> Optional.ofNullable(defaultValue.get()));
            return this;
        }

        public NullableBuilder<T> provider(Supplier<@Nullable T> supplier) {
            this.innerBuilder.provider(() -> Optional.ofNullable(supplier.get()));
            return this;
        }

        public NullableBuilder<T> serverLoader(Consumer<@Nullable T> loader) {
            this.innerBuilder.serverLoader(x -> loader.accept(x.orElse(null)));
            return this;
        }

        public NullableBuilder<T> clientLoader(Function<@Nullable T, Boolean> loader) {
            this.innerBuilder.clientLoader(x -> loader.apply(x.orElse(null)));
            return this;
        }

        public NullableBuilder<T> commonLoader(Function<@Nullable T, Boolean> loader) {
            this.innerBuilder.commonLoader(x -> loader.apply(x.orElse(null)));
            return this;
        }

        public NullableBuilder<T> commonLoader(Consumer<@Nullable T> loader, Comparator<Optional<T>> comparator) {
            this.innerBuilder.commonLoader(x -> loader.accept(x.orElse(null)), comparator);
            return this;
        }

        public NullableBuilder<T> comparator(Comparator<@Nullable T> comparator) {
            this.innerBuilder.comparator(new Comparator<>() {
                private final Comparator<@Nullable T> delegate = comparator;

                @Override
                public int compare(Optional<T> o1, Optional<T> o2) {
                    return delegate.compare(o1.orElse(null), o2.orElse(null));
                }
            });
            return this;
        }

        public void register() {
            this.innerBuilder.register();
        }
    }
}
