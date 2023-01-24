package de.teamlapen.vampirism.client.gui.screens.radial;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemWrapper<T> {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<T> item;

    public ItemWrapper(@Nullable T item) {
        this.item = Optional.ofNullable(item);
    }


    public ItemWrapper() {
        this(null);
    }

    @Nullable
    public T get() {
        return this.item.orElse(null);
    }

    @NotNull
    public Optional<T> getOptional() {
        return this.item;
    }

    public void run(Consumer<T> consumer) {
        this.item.ifPresent(consumer);
    }

    public T swapItem(T item) {
        T old = this.item.orElse(null);
        this.item = Optional.ofNullable(item);
        return old;
    }
}
