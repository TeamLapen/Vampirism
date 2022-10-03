package de.teamlapen.vampirism.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A copy of {@link Supplier} that expects only Non-null values returned.
 */
public interface NonnullSupplier<T> extends Supplier<T> {
    @NotNull
    T getNonnull();

    @Override
    @NotNull
    default T get() {
        return getNonnull();
    }
}
