package de.teamlapen.sync.api;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public interface MutableDataComponentMap extends DataComponentMap {

    <T> @Nullable T set(DataComponentType<T> type, @Nullable T value);

    <T> @Nullable T set(Supplier<DataComponentType<T>> type, @Nullable T value);

    <T> @Nullable T remove(DataComponentType<? extends T> type);

    <T> @Nullable T remove(Supplier<DataComponentType<? extends T>> type);

    void set(Supplier<DataComponentType<Unit>> type, boolean value);
}
