package de.teamlapen.sync.properties;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import de.teamlapen.sync.PropertySync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class for sync-able properties / attributes
 */
public abstract class Property implements IProperty {

    protected final ResourceLocation key;
    private int lastHash;

    public Property(ResourceLocation key) {
        this.key = key;
    }

    /**
     * Unique key for this property.
     * <p>
     * Used as the key for serialization / deserialization of this property
     */
    public ResourceLocation key() {
        return key;
    }

    @Override
    public abstract boolean hasClientSync();

    @Override
    public abstract boolean hasServerLoad();

    /**
     * Check if this property has changed since the last synchronization.
     * @return {@code true} if the property has changed, otherwise {@code false}
     */
    public boolean hasChanged() {
        return this.lastHash != getStatus();
    }

    /**
     * Stores this property into the output.
     * <p>
     * @implNote This automatically updates the status of this property, marking it as un-dirty.
     * @throws IllegalStateException if this method is called to store a property server side
     */
    public void store(ValueOutput output, StoreMode mode) {
        Preconditions.checkArgument(mode.client, "Can not store property server side with this method. This would modify the sync status");
        this.lastHash = storeValue(output, mode);
    }

    /**
     * Stores this property into the output.
     *
     * @implSpec This does not update the internal status of this property. For this call {@link #store(ValueOutput, StoreMode)} instead.
     *
     * @return The current status of the property
     */
    public abstract int storeValue(ValueOutput output, StoreMode mode);

    /**
     * Loads this property from the input.
     * @return {@code true} if the property has changed, otherwise {@code false}
     */
    public abstract boolean loadClient(ValueInput input);

    /**
     * Loads this property from the input.
     */
    public abstract void loadServer(ValueInput input);

    public enum StoreMode {
        /** Full storage on the server side to save the property to disk */
        FULL(true, false),
        /** Full storage of sync-able properties to send to new client players */
        FULL_UPDATE(false, true),
        /** Storage of dirty properties to send to all clients*/
        UPDATE(false, true);

        private final boolean server;
        private final boolean client;

        StoreMode(boolean server, boolean client) {
            this.server = server;
            this.client = client;
        }

        /**
         * Is allowed to store this property server side.
         */
        public boolean server() {
            return this.server;
        }

        /**
         * Is allowed to sync properties to the client.
         */
        public boolean isClient() {
            return this.client;
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

        public <T extends PropertySync> DeferredProperty.Builder<T> subProperty(Supplier<T> propertySupplier) {
            return new DeferredProperty.Builder<>(this, propertySupplier);
        }

        public <T extends PropertySync> OptionalDeferredProperty.Builder<T> optionalSubProperty(Supplier<Optional<T>> propertySupplier) {
            return new OptionalDeferredProperty.Builder<>(this, propertySupplier);
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

        public void simple(String defaultValue, Supplier<String> valueProvider, Consumer<String> valueSetter) {
            simple(Codec.STRING).defaultValue(defaultValue).provider(valueProvider).commonLoader(x -> {
                String old = valueProvider.get();
                valueSetter.accept(x);
                return Objects.equals(old, x);
            }).register();
        }

        public void register(Function<ResourceLocation,Property> property) {
            this.propertySync.registerProperty(property.apply(this.key));
        }
    }
}
