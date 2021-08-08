package de.teamlapen.lib.util;

import net.minecraft.util.WeighedRandom;


public class WeightedRandomItem<T> extends WeighedRandom.WeighedRandomItem {
    private final T item;

    public WeightedRandomItem(T item, int itemWeightIn) {
        super(itemWeightIn);
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
