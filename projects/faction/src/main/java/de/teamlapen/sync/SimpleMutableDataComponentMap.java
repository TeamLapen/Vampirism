package de.teamlapen.sync;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.util.collections.CollectionUtil;
import de.teamlapen.sync.api.ISyncable;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.CommonHooks;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A freely mutable {@link DataComponentMap} with no prototype and no default values.
 * <p>
 * Unlike {@link net.minecraft.core.component.PatchedDataComponentMap}, values are not diffed against a prototype,
 * so a component is simply either present or absent.
 */
public class SimpleMutableDataComponentMap extends PropertySync implements DataComponentMap {

    private Reference2ObjectArrayMap<DataComponentType<?>, Object> map = new Reference2ObjectArrayMap<>();
    private final ISyncable syncable;

    public SimpleMutableDataComponentMap(ISyncable syncable) {
        this.syncable = syncable;
    }

    @Override
    public <T> @Nullable T get(DataComponentType<? extends T> type) {
        //noinspection unchecked
        return (T) this.map.get(type);
    }

    @Override
    public void sync() {
        this.syncable.sync();
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(FIdentifier.mod("data")).map(DataComponentType.VALUE_MAP_CODEC).commonLoader(x -> map = new Reference2ObjectArrayMap<>(x), new CollectionUtil.MapHashComparator<>()).provider(() -> map).register();
    }

    @Override
    public boolean has(DataComponentType<?> type) {
        return this.map.containsKey(type);
    }

    @Override
    public Set<DataComponentType<?>> keySet() {
        return this.map.keySet();
    }

    @Override
    public Iterator<TypedDataComponent<?>> iterator() {
        return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), entry -> TypedDataComponent.createUnchecked(entry.getKey(), entry.getValue()));
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @SuppressWarnings("UnstableApiUsage")
    public <T> @Nullable T set(DataComponentType<T> type, @Nullable T value) {
        CommonHooks.validateComponent(value);
        //noinspection unchecked
        return (T) (value == null ? this.map.remove(type) : this.map.put(type, value));
    }

    public <T> @Nullable T remove(DataComponentType<? extends T> type) {
        //noinspection unchecked
        return (T) this.map.remove(type);
    }

    public void applyPatch(DataComponentPatch patch) {
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : patch.entrySet()) {
            DataComponentType<?> type = entry.getKey();
            Optional<?> value = entry.getValue();
            if (value.isPresent()) {
                this.setUnchecked(type, value.get());
            } else {
                this.map.remove(type);
            }
        }
    }

    private <T> void setUnchecked(DataComponentType<T> type, Object value) {
        //noinspection unchecked
        this.set(type, (T) value);
    }

    public void setAll(DataComponentMap components) {
        for (TypedDataComponent<?> entry : components) {
            this.setUnchecked(entry.type(), entry.value());
        }
    }

    @Override
    public String toString() {
        return "{" + this.stream().map(TypedDataComponent::toString).collect(Collectors.joining(", ")) + "}";
    }
}
