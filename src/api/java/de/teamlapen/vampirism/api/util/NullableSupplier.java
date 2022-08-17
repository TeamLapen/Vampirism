package de.teamlapen.vampirism.api.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface NullableSupplier<T> extends Supplier<T> {

    @Nullable
    T getNullable();

    @Nullable
    @Override
    default T get() {
        return getNullable();
    }
}
