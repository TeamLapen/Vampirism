package de.teamlapen.vampirism.api.util;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * A copy of {@link Supplier} that expects only Non-null values returned.
 */
public interface NonnullSupplier<T> extends Supplier<T> {
    @Nonnull
    T getNonnull();

    @Override
    @Nonnull
    default T get() {
        return getNonnull();
    };
}
