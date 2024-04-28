package de.teamlapen.vampirism.api.util;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemOrdering<T> {
    private final List<T> ordering = new ArrayList<>();
    private final List<T> excluded = new ArrayList<>();
    private final Supplier<Collection<T>> allItems;

    public ItemOrdering(Collection<T> ordering, Collection<T> excluded, Supplier<Collection<T>> allItems) {
        this.allItems = allItems;
        this.ordering.addAll(ordering);
        this.excluded.addAll(excluded);
        testAllItems();
    }

    private void testAllItems() {
        this.allItems.get().stream().filter(i -> !this.ordering.contains(i) && !this.excluded.contains(i)).forEach(this.excluded::add);
    }

    public List<T> getOrdering() {
        return Collections.unmodifiableList(ordering);
    }

    public List<T> getExcluded() {
        return Collections.unmodifiableList(excluded);
    }

    public void reset() {
        this.ordering.clear();
        this.excluded.clear();
        this.excluded.addAll(this.allItems.get());
    }

    public void applyOrdering(List<T> list) {
        this.ordering.clear();
        this.ordering.addAll(list.stream().distinct().toList());
        this.excluded.removeAll(list);
    }

    public void setOrder(int index, T item) {
        Preconditions.checkArgument(index >= 0 && index <= this.ordering.size());
        int i = this.ordering.indexOf(item);
        if (i >= 0 && i < index) {
            this.ordering.add(index, item);
            this.ordering.remove(item);
        } else if (i > index) {
            this.ordering.remove(item);
            this.ordering.add(index, item);
        } else if (i < 0) {
            this.ordering.add(index, item);
        }
        this.excluded.remove(item);
    }

    public void exclude(Collection<T> items) {
        this.ordering.removeAll(items);
        for (T item : items) {
            if (!this.excluded.contains(item)) {
                this.excluded.add(item);
            }
        }
    }

    public void exclude(T item) {
        this.ordering.remove(item);
        if (!this.excluded.contains(item)) {
            this.excluded.add(item);
        }
    }

    public void remove(T item) {
        this.ordering.remove(item);
        this.excluded.remove(item);
    }

}