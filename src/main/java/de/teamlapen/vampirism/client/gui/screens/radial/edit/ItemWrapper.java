package de.teamlapen.vampirism.client.gui.screens.radial.edit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemWrapper<T> {

    private T item;

    public ItemWrapper(@Nullable T item) {
        this.item = item;
    }


    public ItemWrapper() {
        this(null);
    }

    @Nullable
    public T get() {
        return this.item;
    }

    @NotNull
    public Optional<T> getOptional() {
        return Optional.ofNullable(this.item);
    }

    public void run(Consumer<T> consumer) {
        if (this.item != null) {
            consumer.accept(this.item);
        }
    }

    public void swapItem(ItemWrapper<T> item) {
        T old = this.item;
        T oldOther = item.item;
        item.item = old;
        this.item = oldOther;
    }

    public void selectItem(T item) {
        this.item = item;
    }

    public void clear() {
        this.item = null;
    }
}
