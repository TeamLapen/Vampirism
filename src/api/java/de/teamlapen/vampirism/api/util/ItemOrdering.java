package de.teamlapen.vampirism.api.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemOrdering<T> {
    private final List<T> ordering;
    private final List<T> excluded;
    private final Supplier<Collection<T>> allItems;

    public ItemOrdering(List<T> ordering, List<T> excluded, Supplier<Collection<T>> allItems) {
        this.allItems = allItems;
        this.ordering = ordering;
        this.excluded = excluded;
        testAllItems();
    }

    private void testAllItems() {
        this.allItems.get().stream().filter(i -> !this.ordering.contains(i) && !this.excluded.contains(i)).forEach(this.excluded::add);
    }
    public List<T> getOrdering() {
        return ordering;
    }

    public List<T> getExcluded() {
        return excluded;
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