package de.teamlapen.lib.util;

import net.minecraft.util.random.WeightedEntry;


public class WeightedRandomItem<T> extends WeightedEntry.IntrusiveBase {
    private final T item;

    public WeightedRandomItem(T item, int itemWeightIn) {
        super(itemWeightIn);
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
