package de.teamlapen.sync;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import de.teamlapen.sync.api.IStatusProvider;
import de.teamlapen.sync.api.ISyncable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PropertySync implements ValueIOSerializable, ISyncable, IStatusProvider {

    private final Map<ResourceLocation, Property> propertiesMap = new HashMap<>();
    protected final Collection<Property> properties = Collections.unmodifiableCollection(propertiesMap.values());

    public PropertySync() {
        this.registerProperties();
    }

    @Override
    public abstract void sync();

    @Override
    public int getStatus() {
        return Objects.hash(this.properties.stream().map(Property::getStatus).toList());
    }

    @ApiStatus.OverrideOnly
    protected void registerProperties() {

    }

    protected void registerProperty(Property property) {
        this.propertiesMap.put(property.key(), property);
    }

    @Override
    public void serialize(ValueOutput output) {
        for (Property syncProperty : this.properties) {
            syncProperty.storeValue(output, Property.StoreMode.FULL);
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        for (Property syncProperty : this.properties) {
            syncProperty.load(input, true);
        }

        onPropertyChanged();
    }

    @Override
    public void serializeFullUpdate(ValueOutput output) {
        for (Property property : this.properties) {
            if (property.sync()) {
                property.storeValue(output, Property.StoreMode.FULL_UPDATE);
            }
        }
    }

    @Override
    public void serializeUpdate(ValueOutput output) {
        for (Property property : this.properties) {
            if (property.sync() && property.hasChanged()) {
                property.store(output, Property.StoreMode.UPDATE);
            }
        }
    }

    @Override
    public boolean deserializeUpdate(ValueInput input) {
        boolean changed = false;
        for (Property property : this.properties) {
            if (property.sync()) {
                changed = property.load(input, false) || changed;
            }
        }

        if (changed) {
            onPropertyChanged();
            return true;
        }
        return false;
    }

    @ApiStatus.OverrideOnly
    protected void onPropertyChanged() {

    }

    protected <T> void registerProperty(ResourceLocation key, Codec<T> codec, T defaultValue, Supplier<T> valueProvider, Function<T, Boolean> valueSetter, boolean sync) {
        this.propertiesMap.put(key, new SimpleProperty<>(key, sync, codec, defaultValue, valueProvider, valueSetter));
    }

    protected <T> void registerNullableProperty(ResourceLocation key, Codec<@Nullable T> codec, @Nullable T defaultValue, Supplier<@Nullable T> valueProvider, Function<@Nullable T, Boolean> valueSetter, boolean sync) {
        this.propertiesMap.put(key, new NullableProperty<>(key, sync, codec, defaultValue, valueProvider, valueSetter));
    }

    protected <T> void registerProperty(ResourceLocation key, Codec<T> codec, T defaultValue, Supplier<T> valueProvider, Consumer<T> valueSetter, Comparator<T> comparator, boolean sync) {
        this.propertiesMap.put(key, new SimpleProperty<>(key, sync, codec, defaultValue, valueProvider, x -> {
            var old = valueProvider.get();
            valueSetter.accept(x);
            return comparator.compare(old, x) != 0;
        }));
    }

    protected <T> void registerNullableProperty(ResourceLocation key, Codec<@Nullable T> codec, @Nullable T defaultValue, Supplier<@Nullable T> valueProvider, Consumer<@Nullable T> valueSetter, Comparator<@Nullable T> comparator, boolean sync) {
        this.propertiesMap.put(key, new NullableProperty<>(key, sync, codec, defaultValue, valueProvider, x -> {
            @Nullable
            var old = valueProvider.get();
            valueSetter.accept(x);
            return comparator.compare(old, x) != 0;
        }));
    }

    @SuppressWarnings("SameParameterValue")
    protected <T extends Enum<T>> void registerEnumProperty(ResourceLocation key, Codec<T> codec, T defaultValue, Supplier<T> valueProvider, Consumer<T> valueSetter, boolean sync) {
        registerProperty(key, codec, defaultValue, valueProvider, valueSetter, Enum::compareTo, sync);
    }

    @SuppressWarnings("SameParameterValue")
    protected void registerProperty(ResourceLocation key, int defaultValue, Supplier<Integer> valueProvider, Consumer<Integer> valueSetter, boolean sync) {
        registerProperty(key, Codec.INT, defaultValue, valueProvider, valueSetter, Integer::compareTo, sync);
    }

    @SuppressWarnings("SameParameterValue")
    protected void registerProperty(ResourceLocation key, boolean defaultValue, Supplier<Boolean> valueProvider, Consumer<Boolean> valueSetter, boolean sync) {
        registerProperty(key, Codec.BOOL, defaultValue, valueProvider, valueSetter, Boolean::compareTo, sync);
    }

    @SuppressWarnings("SameParameterValue")
    protected void registerProperty(ResourceLocation key, double defaultValue, Supplier<Double> valueProvider, Consumer<Double> valueSetter, boolean sync) {
        registerProperty(key, Codec.DOUBLE, defaultValue, valueProvider, valueSetter, Double::compareTo, sync);
    }

    @SuppressWarnings("SameParameterValue")
    protected void registerProperty(ResourceLocation key, float defaultValue, Supplier<Float> valueProvider, Consumer<Float> valueSetter, boolean sync) {
        registerProperty(key, Codec.FLOAT, defaultValue, valueProvider, valueSetter, Float::compareTo, sync);
    }

    @SuppressWarnings("SameParameterValue")
    protected <T> void registerListProperty(ResourceLocation key, Codec<T> codec, Supplier<List<T>> defaultValue, Supplier<List<T>> valueProvider, Function<List<T>, Boolean> valueSetter, boolean sync) {
        this.propertiesMap.put(key, new ListProperty<>(key, sync, codec, defaultValue, valueProvider, valueSetter));
    }

    protected <T> void registerProperty(ResourceLocation key, boolean sync, Supplier<PropertySync> propertySync) {
        this.propertiesMap.put(key, new SubProperty<>(key, sync, propertySync));
    }
}
