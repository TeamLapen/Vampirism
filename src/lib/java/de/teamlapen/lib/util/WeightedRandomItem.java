package de.teamlapen.lib.util;

import net.minecraft.util.WeightedRandom;


public class WeightedRandomItem<T> extends WeightedRandom.Item {
    private final T item;

    public WeightedRandomItem(T item, int itemWeightIn) {
        super(itemWeightIn);
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
