package de.teamlapen.vampirism.api.util;

import java.util.function.Supplier;

public class CachedSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T cache;

    public CachedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.cache == null) {
            this.cache = this.supplier.get();
        }
        return this.cache;
    }
}
