package de.teamlapen.sync;

import com.mojang.serialization.Codec;
import de.teamlapen.sync.api.IPropertySync;
import de.teamlapen.sync.api.IStatusProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Property implements IStatusProvider, IPropertySync {

    protected final ResourceLocation key;
    private int lastHash;

    public Property(ResourceLocation key) {
        this.key = key;
    }

    public ResourceLocation key() {
        return key;
    }

    @Override
    public abstract boolean hasClientSync();

    @Override
    public abstract boolean hasServerLoad();

    public boolean hasChanged() {
        return this.lastHash != getStatus();
    }

    public void store(ValueOutput output, StoreMode mode) {
        this.lastHash = storeValue(output, mode);
    }

    protected abstract int storeValue(ValueOutput output, StoreMode mode);

    public abstract boolean loadClient(ValueInput input);

    public abstract void loadServer(ValueInput input);

    public enum StoreMode {
        FULL(true, false),
        FULL_UPDATE(false, true),
        UPDATE(false, true);

        public final boolean server;
        public final boolean client;

        StoreMode(boolean server, boolean client) {
            this.server = server;
            this.client = client;
        }
    }

    public record PropertyBuilder(PropertySync propertySync, ResourceLocation key) {

        public <T> SimpleProperty.Builder<T> simple(Codec<T> codec) {
            return new SimpleProperty.Builder<>(this, codec);
        }

        public <T> SimpleProperty.NullableBuilder<T> nullable(Codec<T> codec) {
            return new SimpleProperty.NullableBuilder<>(this, codec);
        }

        public <T> SimpleProperty.Builder<List<T>> list(Codec<T> codec) {
            return new SimpleProperty.Builder<>(this, codec.listOf()).defaultValue(ArrayList::new);
        }

        public <T, Z> SimpleProperty.Builder<Map<T,Z>> map(Codec<Map<T,Z>> codec) {
            return new SimpleProperty.Builder<>(this, codec).defaultValue(HashMap::new);
        }

        public <T> SimpleProperty.Builder<Set<T>> set(Codec<T> codec) {
            return new SimpleProperty.Builder<>(this, codec.listOf().xmap((Function<? super List<T>, Set<T>>) HashSet::new, ArrayList::new)).defaultValue(HashSet::new);
        }

        public <T extends PropertySync> SubProperty.Builder<T> subProperty(Supplier<T> propertySupplier) {
            return new SubProperty.Builder<>(this, propertySupplier);
        }

        public void simple(int defaultValue, Supplier<Integer> valueProvider, Consumer<Integer> valueSetter) {
            simple(Codec.INT).defaultValue(defaultValue).provider(valueProvider).commonLoader(x -> {
                int old = valueProvider.get();
                valueSetter.accept(x);
                return old != x;
            }).register();
        }

        public void simple(double defaultValue, Supplier<Double> valueProvider, Consumer<Double> valueSetter) {
            simple(Codec.DOUBLE).defaultValue(defaultValue).provider(valueProvider).commonLoader(x -> {
                double old = valueProvider.get();
                valueSetter.accept(x);
                return old != x;
            }).register();
        }

        public void simple(float defaultValue, Supplier<Float> valueProvider, Consumer<Float> valueSetter) {
            simple(Codec.FLOAT).defaultValue(defaultValue).provider(valueProvider).commonLoader(x -> {
                float old = valueProvider.get();
                valueSetter.accept(x);
                return old != x;
            }).register();
        }

        public void simple(boolean defaultValue, Supplier<Boolean> valueProvider, Consumer<Boolean> valueSetter) {
            simple(Codec.BOOL).defaultValue(defaultValue).provider(valueProvider).commonLoader(x -> {
                boolean old = valueProvider.get();
                valueSetter.accept(x);
                return old != x;
            }).register();
        }

        public void register(Function<ResourceLocation,Property> property) {
            this.propertySync.registerProperty(property.apply(this.key));
        }
    }
}
