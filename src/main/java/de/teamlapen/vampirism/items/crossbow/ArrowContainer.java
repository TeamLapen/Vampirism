package de.teamlapen.vampirism.items.crossbow;

import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ArrowContainer extends Item {

    private final Supplier<?extends ArrowItem> item;
    private final int count;

    public ArrowContainer(Properties properties, Supplier<? extends ArrowItem> item, int count) {
        super(properties);
        this.item = item;
        this.count = count;
    }

    public Collection<ArrowItem> getArrows() {
        List<ArrowItem> arrows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            arrows.add(item.get());
        }
        return arrows;
    }
}
